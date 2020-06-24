package me.hubertus248.deployer.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class IndexController {

    @GetMapping("/")
    fun index(): String = "index"

    @GetMapping("/apps")
    fun appList(): String = "apps"
}