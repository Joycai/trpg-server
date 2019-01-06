package joycai.trpg.controller

import joycai.trpg.model.excel.JobDto
import joycai.trpg.model.json.JobInfo
import joycai.trpg.service.BaseDataService
import joycai.utils.sheet.excel.ExcelReader
import joycai.utils.sheet.excel.ExcelType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

@RestController
class TestController(
        val baseDataService: BaseDataService
) {

    @GetMapping("test/readExcel")
    fun readExcel(): Any {
//        val fio = FileInputStream(File("excels/数据集.xlsx"))
//        val result = readXlsx(fio, arrayOf("code", "name", "creditRange", "mainAttr", "professionPerk"))
        return baseDataService.listAllSkill()
    }
}