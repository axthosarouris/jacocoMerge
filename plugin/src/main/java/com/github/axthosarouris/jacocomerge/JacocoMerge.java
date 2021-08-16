package com.github.axthosarouris.jacocomerge;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import javax.inject.Inject;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

abstract class JacocoMerge extends DefaultTask {

    @Inject
    public JacocoMerge(){

    }

    @TaskAction
    public void taskAction() throws IOException {
        File execFile = new ExecFileMerger(this.getProject().getRootProject()).createMergedExecFile();
        Set<File> sourceFiles = new SourceFilesRetriever(this.getProject().getRootProject()).getAllSourceFiles();
        Set<File> classFiles = new ClassFilesRetriever(this.getProject().getRootProject()).listAllMainClassFiles();
        new ReportBuilder(sourceFiles, classFiles).writeReports(execFile);
    }
}
