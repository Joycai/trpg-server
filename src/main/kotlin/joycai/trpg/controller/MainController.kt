package joycai.trpg.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MainController {

    @GetMapping("index")
    fun index():Any{
        return "ok"
    }
}