package io.fries.demo.test.cucumber.wiremock.record.transformer;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;

import static org.apache.http.HttpHeaders.CONNECTION;

public class ConnectionCloseResponseTransformer extends ResponseDefinitionTransformer {

    @Override
    public ResponseDefinition transform(final Request request, final ResponseDefinition responseDefinition, final FileSource files, final Parameters parameters) {
        return ResponseDefinitionBuilder.like(responseDefinition)
                .withHeader(CONNECTION, "close")
                .build();
    }

    @Override
    public String getName() {
        return "connection-close-response-transformer";
    }
}
