package io.fries.demo.test.cucumber.world.request;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.web.util.UriComponentsBuilder;

import static java.lang.String.format;

public class RequestBuilder {

    private final UriComponentsBuilder uri;
    private final HttpHeaders headers;

    private RequestBuilder(final UriComponentsBuilder uri, final HttpHeaders headers) {
        this.uri = uri;
        this.headers = headers;
    }

    public static RequestBuilder from(final ServerProperties serverProperties) {
        final var uri = format("http://127.0.0.1:%d", serverProperties.port());

        return new RequestBuilder(
                UriComponentsBuilder.fromUriString(uri),
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

    public RequestEntity<Object> build(final HttpMethod httpMethod) {
        return new RequestEntity<>(headers, httpMethod, uri.build(true).toUri());
    }
}
