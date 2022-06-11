package io.fries.demo.test.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import io.fries.demo.api.DemoApi;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = DemoApi.class, webEnvironment = RANDOM_PORT)
@ComponentScan
@AutoConfigureMockMvc
@CucumberContextConfiguration
public class CucumberConfiguration {
}
