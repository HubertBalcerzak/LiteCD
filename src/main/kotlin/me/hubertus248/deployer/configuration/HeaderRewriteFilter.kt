package me.hubertus248.deployer.configuration

import me.hubertus248.deployer.util.HeaderMapRequestWrapper
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

const val AUTHORIZATION_PROXIED_HEADER_NAME = "Authorization_Proxied"

//TODO validate if this is still necessary (#12)
//@Component("headerRewriteFilter")
//@Order(-200)
class HeaderRewriteFilter : Filter {

    //TODO run only when needed
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val wrapper = HeaderMapRequestWrapper(request as HttpServletRequest)
        wrapper.rewriteHeaderName("Authorization", AUTHORIZATION_PROXIED_HEADER_NAME)
        chain.doFilter(wrapper, response)
    }
}