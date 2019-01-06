package joycai.trpg.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import joycai.trpg.model.excel.JobDto
import joycai.trpg.model.json.SkillDto
import joycai.trpg.service.BaseDataService
import joycai.utils.sheet.excel.ExcelReader
import joycai.utils.sheet.excel.ExcelType
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

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

    init {
        val fio = FileInputStream(File("excels/数据集.xlsx"))
        val reader = ExcelReader(fio, ExcelType.XLSX)
        val sheetHeader = arrayOf<String?>("name", "base", "code")
        //读取手艺
        val artList = readSheet(reader, 0, 1, sheetHeader, SkillDto::class.java)
        if (artList.isNotEmpty()) {
            artList.forEach { artMap["技艺(${it.name})"] = it }
        }
        //读取科学
        val scList = readSheet(reader, 0, 2, sheetHeader, SkillDto::class.java)
        if (scList.isNotEmpty()) {
            scList.forEach { scMap["科学(${it.name})"] = it }
        }

        //读取驾驶
        val driveList = readSheet(reader, 0, 3, sheetHeader, SkillDto::class.java)
        if (driveList.isNotEmpty()) {
            driveList.forEach { driveMap["驾驶(${it.name})"] = it }
        }

        //读取求生
        val surviveList = readSheet(reader, 0, 4, sheetHeader, SkillDto::class.java)
        if (surviveList.isNotEmpty()) {
            surviveList.forEach { surviveMap["生存(${it.name})"] = it }
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
                baseMap["${it.name}"] = it
            }
        }

        //读取格斗
        val fightList = readSheet(reader, 0, 6, sheetHeader, SkillDto::class.java)
        if (fightList.isNotEmpty()) {
            fightList.forEach { fightMap["格斗(${it.name})"] = it }
        }

        //读取射击
        val shootList = readSheet(reader, 0, 7, sheetHeader, SkillDto::class.java)
        if (shootList.isNotEmpty()) {
            shootList.forEach { shootMap["射击(${it.name})"] = it }
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

    override fun listAllSkill(): Any {
        return mapOf(
                "base" to baseMap,
                "sc" to scMap,
                "sv" to surviveMap,
                "art" to artMap,
                "dr" to driveMap,
                "ft" to fightMap,
                "st" to shootMap
        )
    }
}