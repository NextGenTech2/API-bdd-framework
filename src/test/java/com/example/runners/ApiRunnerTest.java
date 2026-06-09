package com.example.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.AfterClass;
import com.example.CucumberReportGenerator;
import java.util.ArrayList;
import java.util.Arrays;

@CucumberOptions(
    features = "src/test/resources/features/expandtesting.feature",
    glue = "com.example.steps",
    tags = "@proxy",
    plugin = {
        "pretty",
        "html:target/cucumber-reports/cucumber.html",
        "json:target/cucumber-reports/report.json"
    }
)
public class ApiRunnerTest extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }

    @AfterClass(alwaysRun = true)
    public void generateAdvancedReport() {
        ArrayList<String> jsonFiles = new ArrayList<>(Arrays.asList("target/cucumber-reports/report.json"));
        CucumberReportGenerator.generateReport("API Automation Framework", "target/cucumber-reports/advanced", jsonFiles);
    }
}