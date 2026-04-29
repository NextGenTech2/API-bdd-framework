package com.example.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
import org.junit.AfterClass;
import com.example.CucumberReportGenerator;
import java.util.ArrayList;
import java.util.Arrays;

@RunWith(Cucumber.class) 
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "com.example.steps",
    tags = "@notes",
    plugin = {
        "pretty",
        "html:target/cucumber-reports/cucumber.html",
        "json:target/cucumber-reports/report.json"
    }
)
public class ApiRunnerTest {

    @AfterClass
    public static void generateAdvancedReport() {
        ArrayList<String> jsonFiles = new ArrayList<>(Arrays.asList("target/cucumber-reports/report.json"));
        CucumberReportGenerator.generateReport("API Automation Framework", "target/cucumber-reports/advanced", jsonFiles);
    }
}