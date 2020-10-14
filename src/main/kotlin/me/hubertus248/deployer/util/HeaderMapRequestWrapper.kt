package me.hubertus248.deployer.util

import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper

class HeaderMapRequestWrapper(request: HttpServletRequest) : HttpServletRequestWrapper(request) {

    private val headerNameMap = mutableMapOf<String, String>()


    fun rewriteHeaderName(oldName: String, newName: String) {
        headerNameMap[oldName] = newName
    }

    override fun getHeader(name: String?): String? {
        if (headerNameMap.containsKey(name)) return super.getHeader(headerNameMap[name])
        return super.getHeader(name)
    }

    override fun getHeaders(name: String?): Enumeration<String>? {
        if (headerNameMap.containsKey(name)) return super.getHeaders(headerNameMap[name])
        return super.getHeaders(name)
    }

    override fun getHeaderNames(): Enumeration<String>? {
        val names: MutableSet<String> = super.getHeaderNames().toList().toMutableSet()
        headerNameMap.keys.filter { key: String -> names.contains(key) }
                .also { oldNames: Collection<String> -> names.removeAll(oldNames) }
                .map { oldName: String -> headerNameMap[oldName]!! }
                .let { newNames: List<String> -> names.addAll(newNames) }

        return Collections.enumeration(names)
    }
}