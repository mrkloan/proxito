package io.fries.demo.test;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;

@RunWith(Cucumber.class)
@CucumberOptions(
        glue = "io.fries.demo.test.cucumber",
        features = "src/test/resources/features",
        plugin = {"pretty", "html:target/cucumber/cucumber.html", "json:target/cucumber/cucumber.json"}
)
public class CucumberTestRunner {
}
