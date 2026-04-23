package com.example;

import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import net.masterthought.cucumber.Reportable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CucumberReportGenerator {
    public static void generateReport(String projectName, String targetDir, ArrayList<String> jsonFiles) {
        File reportOutputDirectory = new File(targetDir);
        Configuration configuration = new Configuration(reportOutputDirectory, projectName);
        configuration.addClassifications("Platform", "Windows");
        configuration.addClassifications("Browser", "Chrome");

        ReportBuilder reportBuilder = new ReportBuilder(jsonFiles, configuration);
        Reportable result = reportBuilder.generateReports();

        if (result != null && (result.getFailedFeatures() > 0 || result.getFailedScenarios() > 0)) {
            throw new RuntimeException("Cucumber report generation completed, but there are test failures!");
}
    }
}

