package io.fries.wiremock.demo.wiremock.cucumber.wiremock.record;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.Response;

public class RecordBodyTransformer extends ResponseTransformer {

    private final String pattern;
    private final String replacement;

    public RecordBodyTransformer(final String pattern, final String replacement) {
        this.pattern = pattern;
        this.replacement = replacement;
    }

    @Override
    public Response transform(final Request request, final Response response, final FileSource files, final Parameters parameters) {
        return Response.Builder.like(response)
                .body(response.getBodyAsString().replaceAll(pattern, replacement))
                .build();
    }

    @Override
    public String getName() {
        return "record-body-transformer";
    }
}
