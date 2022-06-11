package io.fries.demo.test.cucumber.wiremock.record;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.StubMappingTransformer;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;

public class RecordStubTransformer extends StubMappingTransformer {

    private final String pattern;
    private final String replacement;

    public RecordStubTransformer(final String pattern, final String replacement) {
        this.pattern = pattern;
        this.replacement = replacement;
    }

    @Override
    public StubMapping transform(final StubMapping stubMapping, final FileSource fileSource, final Parameters parameters) {
        // TODO: replace pattern in stub's response body.
        return stubMapping;
    }

    @Override
    public String getName() {
        return "record-stub-transformer";
    }
}
