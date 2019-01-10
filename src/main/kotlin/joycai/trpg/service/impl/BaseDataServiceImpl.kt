package joycai.trpg.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import joycai.trpg.model.excel.ExcelJob
import joycai.trpg.model.json.CustomJobGroup
import joycai.trpg.model.json.JobDto
import joycai.trpg.model.json.SkillDto
import joycai.trpg.service.BaseDataService
import joycai.utils.sheet.excel.ExcelReader
import joycai.utils.sheet.excel.ExcelType
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream

@Service("BaseDataService")
class BaseDataServiceImpl(
        val objectMapper: ObjectMapper
) : BaseDataService {

    private val artMap = mutableMapOf<String, SkillDto>()
    private val scMap = mutableMapOf<String, SkillDto>()
    private val driveMap = mutableMapOf<String, SkillDto>()
    private val surviveMap = mutableMapOf<String, SkillDto>()
    private val lanMap = mutableMapOf<String, SkillDto>()

    private val baseMap = mutableMapOf<String, SkillDto>()
    private val fightMap = mutableMapOf<String, SkillDto>()
    private val shootMap = mutableMapOf<String, SkillDto>()

    private val jobListMap = mutableMapOf<String, JobDto>();

    init {
        val fio = FileInputStream(File("excels/数据集.xlsx"))
        val reader = ExcelReader(fio, ExcelType.XLSX)
        val sheetHeader = arrayOf<String?>("name", "base", "code")
        //读取手艺
        val artList = readSheet(reader, 0, 1, sheetHeader, SkillDto::class.java)
        if (artList.isNotEmpty()) {
            artList.forEach { artMap["${it.code}"] = it }
        }
        //读取科学
        val scList = readSheet(reader, 0, 2, sheetHeader, SkillDto::class.java)
        if (scList.isNotEmpty()) {
            scList.forEach { scMap["${it.code}"] = it }
        }

        //读取驾驶
        val driveList = readSheet(reader, 0, 3, sheetHeader, SkillDto::class.java)
        if (driveList.isNotEmpty()) {
            driveList.forEach { driveMap["${it.code}"] = it }
        }

        //读取求生
        val surviveList = readSheet(reader, 0, 4, sheetHeader, SkillDto::class.java)
        if (surviveList.isNotEmpty()) {
            surviveList.forEach { surviveMap["${it.code}"] = it }
        }

        //读取基础技能
        val baseList = readSheet(reader, 0, 5, sheetHeader, SkillDto::class.java)
        if (baseList.isNotEmpty()) {
            baseList.forEach {
                if (it.code == "107") {
                    it.isJobPerk = true
                    it.enableJobPerk = true
                    it.enableHobPerk = false
                }
                if (it.code == "108") {
                    it.enableHobPerk = false
                }
                baseMap["${it.code}"] = it
            }
        }

        //读取格斗
        val fightList = readSheet(reader, 0, 6, sheetHeader, SkillDto::class.java)
        if (fightList.isNotEmpty()) {
            fightList.forEach { fightMap["${it.code}"] = it }
        }

        //读取射击
        val shootList = readSheet(reader, 0, 7, sheetHeader, SkillDto::class.java)
        if (shootList.isNotEmpty()) {
            shootList.forEach { shootMap["${it.code}"] = it }
        }

        //语言
        val lanList = readSheet(reader, 0, 8, sheetHeader, SkillDto::class.java)
        if (lanList.isNotEmpty()) {
            lanList.forEach { lanMap["${it.code}"] = it }
        }

        //读取职业列表
        val jobHeader = arrayOf<String?>("code", "name", "creditRange", "mainAttr", "jobSkillStr", "skillMsg")
        val jobList = readSheet(reader, 0, 0, jobHeader, ExcelJob::class.java)

        if (jobList.isNotEmpty()) {
            jobList.forEach {
                jobListMap[it.code!!] = it.convert()
            }
        }
        reader.close()
    }

    fun <T> readSheet(reader: ExcelReader, startRow: Int, sheetIdx: Int, headerMapper: Array<String?>, clazz: Class<T>): List<T> {
        val row_num = reader.getLineCount(sheetIdx)
        val result = mutableListOf<T>()
        for (i in startRow..row_num) {

            val obj = reader.readLineToObject(sheetIdx, i, headerMapper, clazz) as T

            result.add(obj)
        }
        return result
    }

    fun ExcelJob.convert(): JobDto {

        //信誉
        val credArray = if (this.creditRange.isNullOrBlank()) {
            arrayOf(0, 99)
        } else {
            val str = this.creditRange?.split("-")!!
            arrayOf(str[0].toInt(), str[1].toInt())
        }

        //主要属性
        val mainAttr = if (this.mainAttr.isNullOrBlank()) {
            listOf<Int>()
        } else {
            if (this.mainAttr!!.contains(",")) {
                val ta = this.mainAttr!!.split(",")
                ta.map { it.toInt() }
            } else {
                listOf(this.mainAttr!!.toInt())
            }
        }

        val skillMap = processRawSkillStr(this.jobSkillStr)

        return JobDto(
                code = this.code,
                name = this.name,
                mainAttr = mainAttr,
                credMin = credArray[0],
                credMax = credArray[1],
                jobSkill = skillMap["base"] as List<String>? ?: listOf<String>(),
                exJobNum = skillMap["extNum"] as Int? ?: 0,
                customJobGroup = skillMap["group"] as List<CustomJobGroup>? ?: listOf()
        )

    }

    /**
     * 处理技能描述文本
     *
     * 一共处理成3个内容
     * "base" 基本技能组
     * "exNum" 自由技能
     * "group" n选m List<CustomJobGroup>
     */
    private fun processRawSkillStr(jobSkillStr: String?): Map<String, Any> {
        val complexReg = "^[1-9]x（[\\S\\s]+）\$".toRegex()

        /**
         * 任意专精
         */
        val proSkillReg = "^([1-9]x)?((格斗)|(技艺)|(射击)|(生存)|(科学)|(外语)|(驾驶))A?\$".toRegex()
        val proSkillReg2 = "^([1-9]x)?((格斗)|(技艺)|(射击)|(生存)|(科学)|(外语)|(驾驶))（(E[\\S\\s]+）)\$".toRegex()

        val result = mutableMapOf<String, Any>()
        val baseArr = mutableListOf<String>()

        if (jobSkillStr.isNullOrBlank()) {
            return mapOf()
        } else {
            //拆分描述单词
            val descWords = jobSkillStr.split("，")

            for (word in descWords) {
                if (word == null) {
                    println("NULL word")
                } else if (word.startsWith("EX")) {
                    //自由点数
                    result["extNum"] = word.substring(2).toInt()

                } else if (complexReg.matches(word)) {
                    //标准格式nx(aaa、bbb、ccc)
                    println("标准格式 $word")
                } else if (proSkillReg.matches(word)) {
                    //专精n选1
//                    println("专精配置 $word")
                } else if (proSkillReg2.matches(word)) {
//                    println("专精配置(补集) $word")
                } else {
                    val code = findSkillCodeByName(word)
                    if (code == null) {
                        //未知词
                        println("UNKNOW: $word")
                    } else {
                        //正常
                        baseArr.add(code)
                    }
                }

            }
            result["base"] = baseArr
            return result
        }

        return mapOf()
    }


    /**
     * 按照名称寻找对应的代码（单个技能）
     */
    private fun findSkillCodeByName(name: String): String? {
        val proSkillReg = "^((格斗)|(技艺)|(射击)|(生存)|(科学)|(外语)|(驾驶))（[\\S\\s]+）\$".toRegex()

        baseMap.values.forEach {
            if (it.name == name) {
                return it.code
            }
        }
        if (proSkillReg.matches(name)) {
            //是专精技能
            val catName = name.substring(0, 2)
            val proName = name.substring(name.indexOf("（") + 1, name.lastIndexOf("）"))

            val collection = when (catName) {
                "格斗" -> fightMap.values
                "技艺" -> artMap.values
                "射击" -> shootMap.values
                "生存" -> surviveMap.values
                "科学" -> scMap.values
                "外语" -> lanMap.values
                "驾驶" -> driveMap.values
                else -> {
                    arrayListOf()
                }
            }
            collection.forEach {
                if (it.name!!.contains(proName)) {
                    return it.code
                }
            }
        }

        return null
    }

    override fun listAllSkill(): Any {
        return mapOf(
                "base" to baseMap,
                "fight" to fightMap,
                "shoot" to shootMap,
                "science" to scMap,
                "survive" to surviveMap,
                "craft" to artMap,
                "drive" to driveMap,
                "lang" to lanMap
        )
    }

    override fun listAllJob(): Any {
        return mapOf("joblist" to jobListMap)
    }
}