package me.hubertus248.deployer.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import java.security.Principal
import javax.servlet.http.HttpServletRequest

@Controller
class IndexController {

    @GetMapping("/")
    fun index(principal: Principal?): String {
        if (principal != null) return "redirect:/apps"
        return "index"
    }

//    @GetMapping("/apps")
//    fun appList(): String = "apps"

    //TODO use POST /sso/logout instead
    @GetMapping("/logout")
    fun logout(request: HttpServletRequest): String {
        request.logout()
        return "index"
    }
}