# WireMock issue demo: using a `ResponseTemplateTrasnformer` in record mode
> * WireMock version: 2.31.0 
> * wiremock/wiremock issue: https://github.com/wiremock/wiremock/issues/1823

This demo project helps reproduce a problematic (but maybe intended?) behavior of [WireMock](https://github.com/wiremock/wiremock): 
`ResponseTemplateTransformer` cannot be used to replace templates in response body while the WireMock server is running in record mode.

## Use-case & expected behavior

We are using WireMock in our test stack to proxy HTTP partners. When writing a new test case, the WireMock servers are
started in record mode, effectively saving stub mappings locally to be replayed later in the CI pipelines.

Our business is heavily reliant on *dates*, and we would like our test cases to be as much as possible time-agnostic.
Therefore, when a partner's response contains a date (or any time related information), we replace it with a templated 
pattern that is then replaced when the request is replayed by WireMock. 

At record time, we use both a `ResponseTransformer` and a `ResponseTemplateTransformer` to (hopefully) template the
persisted response body but still get a valued response from WireMock's stub mapping : see
[WireMockConfiguration](demo-wiremock/src/test/java/io/fries/wiremock/demo/wiremock/cucumber/wiremock/WireMockConfiguration.java#L22) 
and [RecordConfiguration.java](demo-wiremock/src/test/java/io/fries/wiremock/demo/wiremock/cucumber/wiremock/record/RecordConfiguration.java#L54).
As explained later, this does _not_ work as intended.

At replay time, we use the same `ResponseTemplateTransformer` instance to get a valued response from WireMock's stub mapping :
see [ReplayConfiguration.java](demo-wiremock/src/test/java/io/fries/wiremock/demo/wiremock/cucumber/wiremock/replay/ReplayConfiguration.java#L43).
This, however, is working like a charm.

## Issue
> IntelliJ run configurations are provided in the [`.run`](.run) directory.

* Run the `demo-wiremock [record]` run configuration (launches the [CucumberTestRunner.java](demo-wiremock/src/test/java/io/fries/wiremock/demo/wiremock/CucumberTestRunner.java) class with `spring.profiles.active=mock,record`):
  * Partner stub mapping is successfully recorded by WireMock under the [`wiremock`](demo-wiremock/src/test/resources/wiremock) test resource directory (ignored by Git), and the response body is successfully templated.
  * **Issue:** the test case _fails_ as the templated body is not valued by the `ResponseTemplateTransformer` before being forwarded to the caller. This is the main culprit.
* Then run the `demo-wiremock [replay]` run configuration (launches the [CucumberTestRunner.java](demo-wiremock/src/test/java/io/fries/wiremock/demo/wiremock/CucumberTestRunner.java) class with `spring.profiles.active=mock,replay`):
  * The test case _succeeds_ as the templated body _is_ correctly valued by the `ResponseTemplateTransformer`.

## Analysis

What we understood so far:
* Because `ResponseTemplateTransformer` extends `ResponseDefinitionTransformer`, it is applied by [`InMemoryStubMappings#serveFor`](https://github.com/wiremock/wiremock/blob/2.31.0/src/main/java/com/github/tomakehurst/wiremock/stubbing/InMemoryStubMappings.java#L80).
* However, when WireMock is in the process of recording the new stub mapping, the body content is not yet valued inside the `ResponseDefinition` instance. Thus, the `ResponseTemplateTransformer` does nothing.
* Afterward, the `ResponseTransformer` is applied by the [`StubResponseRenderer`](https://github.com/wiremock/wiremock/blob/2.31.0/src/main/java/com/github/tomakehurst/wiremock/http/StubResponseRenderer.java#L57), itself called by the [WireMockHandlerDispatchingServlet](https://github.com/wiremock/wiremock/blob/2.31.0/src/main/java/com/github/tomakehurst/wiremock/servlet/WireMockHandlerDispatchingServlet.java#L127) through an [AbstractRequestHandler](https://github.com/wiremock/wiremock/blob/2.31.0/src/main/java/com/github/tomakehurst/wiremock/http/AbstractRequestHandler.java#L69). We do not really understand at what point the response body is updated in the stub mapping so that [WireMockApp#saveMappings](https://github.com/wiremock/wiremock/blob/2.31.0/src/main/java/com/github/tomakehurst/wiremock/core/WireMockApp.java#L265) can be aware of this transformation.
* The body updated by the `ResponseTransformer` is persisted on the file system when the recording stops, but the `ResponseTemplateTransformer` never gets to see it.

## Feature request

From our perspective, the expected behavior should be: 
* **After** the `ResponseTransformer` execution, the `ResponseTemplateTransformer` should be applied on the stub mapping responses so that the caller does not get a templated body as the response, but a valued one.
* **However**, the recorded stub mapping response body should remain templated.
