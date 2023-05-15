package com.github.manosbatsis.lib.core.rest

import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

open class ResourceSizeAdvice : ResponseBodyAdvice<Collection<*>> {
    override fun supports(
        returnType: MethodParameter,
        converterType: Class<out HttpMessageConverter<*>>,
    ): Boolean {
        // Checks if this advice is applicable.
        // In this case it applies to any endpoint which returns a collection.
        return MutableCollection::class.java.isAssignableFrom(returnType.parameterType)
    }

    override fun beforeBodyWrite(
        body: Collection<*>,
        returnType: MethodParameter,
        selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>?>?,
        request: ServerHttpRequest,
        response: ServerHttpResponse,
    ): Collection<*> {
        response.headers.add("X-Total-Count", body.size.toString())
        return body
    }
}
