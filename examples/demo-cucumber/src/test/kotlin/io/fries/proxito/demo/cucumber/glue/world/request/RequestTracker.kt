package io.fries.proxito.demo.cucumber.glue.world.request

import com.github.tomakehurst.wiremock.http.Request
import com.github.tomakehurst.wiremock.http.Response
import org.assertj.core.api.Assertions.assertThat

class RequestTracker(
    private val responseByUrl: MutableMap<String, Response> = mutableMapOf()
) {

    fun track(request: Request, response: Response) {
        responseByUrl[request.url] = response
    }

    fun assertThatAllRequestsWereRecorded() {
        val unrecordedRequests = responseByUrl.entries
            .filterNot { (_, response) -> response.wasConfigured() }
            .map { (url, _) -> url }
            .toSet()

        assertThat(unrecordedRequests)
            .describedAs("Some unrecorded requests were tracked")
            .isEmpty()
    }
}