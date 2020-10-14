package me.hubertus248.deployer.util

import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext
import me.hubertus248.deployer.configuration.AUTHORIZATION_PROXIED_HEADER_NAME
import org.springframework.stereotype.Component

@Component
class ZuulHeaderRewriteFilter: ZuulFilter(){
    override fun shouldFilter(): Boolean = true

    override fun run(): Any? {
        val requestContext = RequestContext.getCurrentContext()
        val headers = requestContext.zuulRequestHeaders
        if(headers.containsKey(AUTHORIZATION_PROXIED_HEADER_NAME)){
            headers["Authorization"] = headers[AUTHORIZATION_PROXIED_HEADER_NAME]
            headers.remove(AUTHORIZATION_PROXIED_HEADER_NAME)
        }
        return null
    }

    override fun filterType(): String {
        return "post"
    }

    override fun filterOrder(): Int {
        return 1
    }

}