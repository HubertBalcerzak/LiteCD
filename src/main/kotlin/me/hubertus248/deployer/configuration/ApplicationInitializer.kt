package me.hubertus248.deployer.configuration

import org.springframework.web.filter.DelegatingFilterProxy
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer
import javax.servlet.Filter

class ApplicationInitializer : AbstractAnnotationConfigDispatcherServletInitializer() {
    override fun getServletMappings(): Array<out String> = emptyArray()

    override fun getRootConfigClasses(): Array<Class<*>>? = null

    override fun getServletConfigClasses(): Array<Class<*>>? = null

    override fun getServletFilters(): Array<Filter>? {
        return arrayOf(
                DelegatingFilterProxy().apply { setTargetBeanName("headerRewriteFilter") }
        )
    }
}