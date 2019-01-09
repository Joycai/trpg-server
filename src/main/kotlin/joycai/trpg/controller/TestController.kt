package joycai.trpg.controller

import joycai.trpg.service.BaseDataService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController(
        val baseDataService: BaseDataService
) {

    @GetMapping("getSkillList")
    fun getSkillList(): Any {
//        val fio = FileInputStream(File("excels/数据集.xlsx"))
//        val result = readXlsx(fio, arrayOf("code", "name", "creditRange", "mainAttr", "professionPerk"))
        return baseDataService.listAllSkill()
    }

    @GetMapping("getJobList")
    fun getJobList(): Any{
        return baseDataService.listAllJob();
    }
}