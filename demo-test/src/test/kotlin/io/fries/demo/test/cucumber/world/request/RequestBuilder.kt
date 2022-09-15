package io.fries.demo.test.cucumber.world.request

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.web.util.UriComponentsBuilder

class RequestBuilder(
    serverProperties: ServerProperties,
    private val uri: UriComponentsBuilder = UriComponentsBuilder.fromUriString("http://127.0.0.1:${serverProperties.port}"),
    private val headers: HttpHeaders = HttpHeaders()
) {

    fun path(path: String) = apply {
        uri.replacePath(path)
    }

    fun parameter(queryParameter: QueryParameter) = apply {
        uri.queryParam(queryParameter.name, queryParameter.encodedValue)
    }

    fun header(name: String, value: String) = apply {
        headers[name] = value
    }

    fun build(httpMethod: HttpMethod): RequestEntity<Any> {
        return RequestEntity(headers, httpMethod, uri.build(true).toUri())
    }
}