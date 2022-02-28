package io.fries.wiremock.demo.wiremock.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import io.fries.wiremock.demo.api.DemoApi;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

@SpringBootTest(
        classes = DemoApi.class,
        webEnvironment = DEFINED_PORT
)
@ComponentScan
@AutoConfigureMockMvc
@CucumberContextConfiguration
public class CucumberConfiguration {
}
