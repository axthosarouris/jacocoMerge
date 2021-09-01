package com.github.axthosarouris.jacocomerge;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import no.unit.nva.s3.UnixPath;
import org.gradle.api.Project;

public class ExecFileMerger {

    public static final String JACOCO_RESULTS_FOLDER_NAME = "jacoco";
    public static final String DEFAULT_JACOCO_EXEC_REPORT_FILE = "test.exec";
    public static File ROOT_BUILD_FOLDER =  new File("build");
    public static File JACOCO_REPORT_FILES_FOLDER = new File(ROOT_BUILD_FOLDER, JACOCO_RESULTS_FOLDER_NAME);
    public static final File JACOCO_MERGE_REPORT_LOCATION =
        new File(JACOCO_REPORT_FILES_FOLDER,DEFAULT_JACOCO_EXEC_REPORT_FILE);

    private final Project project;

    public ExecFileMerger(Project project) {
        this.project = project;
    }

    public File createMergedExecFile() throws IOException {
        Set<File> execFiles = listAllExecFiles();
        return mergeFiles(execFiles);
    }

    private File mergeFiles(Set<File> execFiles) throws IOException {
        Merge merge = new Merge();
        merge.addExecFiles(execFiles);
        File destfile = new File(JACOCO_MERGE_REPORT_LOCATION.toString());
        merge.setDestfile(destfile);
        merge.createMergedFile();
        return destfile;
    }

    private Set<File> listAllExecFiles() {
        return Optional.ofNullable(project).stream()
            .map(Project::getSubprojects)
            .flatMap(Collection::stream)
            .map(Project::getBuildDir)
            .map(File::getAbsolutePath)
            .map(UnixPath::fromString)
            .map(buildDir -> buildDir.addChild(JACOCO_RESULTS_FOLDER_NAME))
            .map(jacocoFolder -> jacocoFolder.addChild(DEFAULT_JACOCO_EXEC_REPORT_FILE))
            .map(execFile -> new File(execFile.toString()))
            .filter(File::exists)
            .collect(Collectors.toSet());
    }
}
