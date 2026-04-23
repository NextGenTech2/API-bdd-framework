package com.example.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class) 
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "com.example.steps",
    tags = "@auth or @categories",
    plugin = {
        "pretty",
        "html:target/cucumber-reports/cucumber.html",
        "json:target/cucumber-reports/report.json"
    }
)
public class ApiRunnerTest { // This must match the filename exactly!
    // class body
}