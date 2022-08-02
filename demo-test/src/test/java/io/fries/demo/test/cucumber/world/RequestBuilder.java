package io.fries.demo.test.cucumber.world;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.web.util.UriComponentsBuilder;

public class RequestBuilder {

    private final UriComponentsBuilder uri;
    private final HttpHeaders headers;

    private RequestBuilder(final UriComponentsBuilder uri, final HttpHeaders headers) {
        this.uri = uri;
        this.headers = headers;
    }

    public static RequestBuilder empty() {
        return new RequestBuilder(
                UriComponentsBuilder.fromUriString("http://127.0.0.1:8080"),
                new HttpHeaders()
        );
    }

    public RequestBuilder path(final String path) {
        uri.replacePath(path);
        return this;
    }

    public RequestBuilder parameter(final QueryParameter queryParameter) {
        uri.queryParam(queryParameter.name(), queryParameter.value());
        return this;
    }

    public RequestBuilder header(final String name, final String value) {
        headers.set(name, value);
        return this;
    }

    public RequestBuilder removeHeader(final String name) {
        headers.remove(name);
        return this;
    }

    public RequestEntity<Object> build(final HttpMethod httpMethod) {
        return new RequestEntity<>(headers, httpMethod, uri.build(true).toUri());
    }
}
