package io.fries.proxito.demo.cucumber

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
    glue = ["io.fries.proxito.demo.cucumber.glue"],
    features = ["src/test/resources/features"],
    plugin = ["pretty", "html:target/cucumber/cucumber.html", "json:target/cucumber/cucumber.json"]
)
class CucumberTestRunner