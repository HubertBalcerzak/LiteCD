package me.hubertus248.deployer.instance.controller

import me.hubertus248.deployer.data.entity.InstanceKey
import me.hubertus248.deployer.security.IsAdmin
import me.hubertus248.deployer.service.InstanceManagerService
import me.hubertus248.deployer.service.InstanceService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.view.RedirectView

//todo rewrite
@Controller
class InstanceController(
    private val instanceManagerService: InstanceManagerService,
    private val instanceService: InstanceService
) {

    @IsAdmin
    @GetMapping("/newApp")
    fun newApp(model: Model): String {
        model.addAttribute("managers", instanceManagerService.getAvailableManagers())
        return "newApp"
    }

    @IsAdmin
    @PostMapping("/app/{appId}/create")
    fun create(@PathVariable appId: Long, @RequestParam key: String): RedirectView {
        instanceService.createAndStart(appId, InstanceKey(key))
        return RedirectView("/app/$appId")
    }

    @IsAdmin
    @PostMapping("/app/{appId}/start")
    fun start(@PathVariable appId: Long, @RequestParam key: String): RedirectView {
        instanceService.start(appId, InstanceKey(key))
        return RedirectView("/app/$appId")
    }

    @IsAdmin
    @PostMapping("/app/{appId}/stop")
    fun stop(@PathVariable appId: Long, @RequestParam key: String): RedirectView {
        instanceService.stop(appId, InstanceKey(key))
        return RedirectView("/app/$appId")
    }


    @IsAdmin
    @PostMapping("/app/{appId}/delete")
    fun delete(@PathVariable appId: Long, @RequestParam key: String): RedirectView {
        instanceService.delete(appId, InstanceKey(key))
        return RedirectView("/app/$appId")
    }


    /**
     * delete and create instance again, preserve instance configuration
     */
    @IsAdmin
    @PostMapping("/app/{appId}/recreate")
    fun recreate(@PathVariable appId: Long, @RequestParam key: String): RedirectView {
        instanceService.recreate(appId, InstanceKey(key))
        return RedirectView("/app/$appId")
    }

    @IsAdmin
    @PostMapping("/app/{appId}/deleteAvailableInstance")
    fun deleteAvailableInstance(@PathVariable appId: Long, @RequestParam key: String): RedirectView {
        instanceService.deleteAvailableInstance(appId, InstanceKey(key))
        return RedirectView("/app/$appId")
    }
}