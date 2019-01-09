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
    private val baseMap = mutableMapOf<String, SkillDto>()

    private val fightMap = mutableMapOf<String, SkillDto>()
    private val shootMap = mutableMapOf<String, SkillDto>()

    private val jobListMap = mutableMapOf<String,JobDto>();

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

        //读取职业列表
        val jobHeader = arrayOf<String?>("code", "name", "creditRange", "mainAttr", "jobSkillStr", "skillMsg")
        val jobList = readSheet(reader, 0, 0, jobHeader, ExcelJob::class.java)

        if( jobList.isNotEmpty()){
            jobList.forEach {
                jobListMap[it.code!!] = it.convert()
            }
        }
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
        val credArray = if(this.creditRange.isNullOrBlank()){
            arrayOf(0,99)
        }else{
            val str = this.creditRange?.split("-")!!
            arrayOf(str[0].toInt(),str[1].toInt())
        }
        //主要属性
        val mainAttr = if(this.mainAttr.isNullOrBlank()){
            listOf<Int>()
        }else{
            if (this.mainAttr!!.contains(",")){
                val ta =this.mainAttr!!.split(",")
                ta.map { it.toInt() }
            }else{
                listOf(this.mainAttr!!.toInt())
            }
        }

        val skillMap = processSkillStr(this.jobSkillStr)

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
    fun processSkillStr(jobSkillStr: String?): Map<String, Any> {

        if(jobSkillStr.isNullOrBlank()){
            return mapOf()
        }else{
            //拆分成描述字符串
            val descStr = jobSkillStr.split("，")

        }

        return mapOf()
    }

    override fun listAllSkill(): Any {
        return mapOf(
                "base" to baseMap,
                "fight" to fightMap,
                "shoot" to shootMap,
                "science" to scMap,
                "survive" to surviveMap,
                "craft" to artMap,
                "drive" to driveMap
        )
    }

    override fun listAllJob(): Any{
        return mapOf("joblist" to jobListMap)
    }
}