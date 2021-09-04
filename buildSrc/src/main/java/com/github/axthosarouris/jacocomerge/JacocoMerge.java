package com.github.axthosarouris.jacocomerge;

import static nva.commons.core.attempt.Try.attempt;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import org.gradle.api.Project;

public class JacocoMerge {

    public void createReport(Project project) {
        File execFile = attempt(()->new ExecFileMerger(project).createMergedExecFile()).orElseThrow();
        Set<File> sourceFiles = new SourceFilesRetriever(project).getAllSourceFiles();
        Set<File> classFiles = new ClassFilesRetriever(project).listAllMainClassFiles();
        ReportBuilder reportBuilder = new ReportBuilder(project,sourceFiles, classFiles);
        attempt(()-> writeReports(execFile, reportBuilder)).orElseThrow();
    }

    private Void writeReports(File execFile, ReportBuilder reportBuilder) throws IOException {
        reportBuilder.writeReports(execFile);
        return null;
    }


}
