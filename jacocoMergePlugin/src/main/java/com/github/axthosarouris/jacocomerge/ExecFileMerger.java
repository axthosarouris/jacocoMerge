package com.github.axthosarouris.jacocomerge;

import static com.github.axthosarouris.jacocomerge.Constants.BUILD_FOLDER_NAME;
import static com.github.axthosarouris.jacocomerge.Constants.DEFAULT_JACOCO_EXEC_REPORT_FILE;
import static com.github.axthosarouris.jacocomerge.Constants.JACOCO_RESULTS_FOLDER_NAME;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import no.unit.nva.s3.UnixPath;
import org.gradle.api.Project;

public class ExecFileMerger {

    private final Project project;
    private final File jacocoMergeLocation;

    public ExecFileMerger(Project project) {
        this.project = project;
        File projectFolder = this.project.getProjectDir().getAbsoluteFile();
        File buildFolder = new File(projectFolder, BUILD_FOLDER_NAME).getAbsoluteFile();
        File jacocoReportsFolder = new File(buildFolder, JACOCO_RESULTS_FOLDER_NAME).getAbsoluteFile();
        this.jacocoMergeLocation = new File(jacocoReportsFolder, DEFAULT_JACOCO_EXEC_REPORT_FILE).getAbsoluteFile();
    }

    public File createMergedExecFile() throws IOException {
        Set<File> execFiles = listSubprojectExecFiles();
        return mergeFiles(execFiles);
    }

    private File mergeFiles(Set<File> execFiles) throws IOException {
        ExecFilesMerge merge = new ExecFilesMerge();
        merge.addExecFiles(execFiles);
        merge.setDestinationFile(jacocoMergeLocation);
        merge.createMergedFile();
        return jacocoMergeLocation;
    }

    private Set<File> listSubprojectExecFiles() {
        return listSubProjectBuildDirs()
            .map(buildDir -> buildDir.addChild(JACOCO_RESULTS_FOLDER_NAME))
            .map(jacocoFolder -> jacocoFolder.addChild(DEFAULT_JACOCO_EXEC_REPORT_FILE))
            .map(execFile -> new File(execFile.toString()))
            .filter(File::exists)
            .collect(Collectors.toSet());
    }

    private Stream<UnixPath> listSubProjectBuildDirs() {
        return Optional.ofNullable(project).stream()
            .map(Project::getSubprojects)
            .flatMap(Collection::stream)
            .map(Project::getBuildDir)
            .map(File::getAbsolutePath)
            .map(UnixPath::fromString);
    }
}
