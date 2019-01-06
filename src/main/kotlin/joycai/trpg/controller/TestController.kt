package joycai.trpg.controller

import joycai.trpg.model.excel.JobDto
import joycai.trpg.model.json.JobInfo
import joycai.utils.sheet.excel.ExcelReader
import joycai.utils.sheet.excel.ExcelType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

@RestController
class TestController {

    @GetMapping("test/readExcel")
    fun readExcel(): Any {
        val fio = FileInputStream(File("excels/数据集.xlsx"))
        val result = readXlsx(fio, arrayOf("code", "name", "creditRange", "mainAttr", "professionPerk"))
        return result
    }

    fun JobDto.convertToJsonDto(): JobInfo{
        val obj = JobInfo(code = this.code, info=this.professionPerk)


        return obj
    }

    fun readXlsx(ism: InputStream, headerMapper: Array<String?>): List<JobDto> {
        val reader = ExcelReader(ism, ExcelType.XLSX)
        val row_num = reader.getLineCount(0)
        val first_num = reader.firstLineIdx(0)
        val result = mutableListOf<JobDto>()
        for (i in first_num..row_num) {
            val obj = reader.readLineToObject(0, i, headerMapper, JobDto::class.java) as JobDto
            result.add(obj)
        }
        reader.close()
        return result
    }
}