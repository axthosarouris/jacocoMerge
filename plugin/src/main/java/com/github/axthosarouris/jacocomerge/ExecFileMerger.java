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

    private final Project project;

    public ExecFileMerger(Project project) {
        this.project = project;
    }

    public File createMergedExecFile() throws IOException {
        Set<File> execFiles = listAllExectFiles();
        return mergeFiles(execFiles);
    }

    private File mergeFiles(Set<File> execFiles) throws IOException {
        Merge merge = new Merge();
        merge.addExecFiles(execFiles);
        File destfile = new File(UnixPath.of("build", "jacoco", "test.exec").toString());
        merge.setDestfile(new File(UnixPath.of("build", "jacoco", "test.exec").toString()));
        merge.createMergedFile();
        return destfile;
    }

    private Set<File> listAllExectFiles() {
        return Optional.ofNullable(project).stream()
            .map(Project::getSubprojects)
            .flatMap(Collection::stream)
            .map(Project::getBuildDir)
            .map(File::getAbsolutePath)
            .map(UnixPath::fromString)
            .map(buildDir -> buildDir.addChild("jacoco"))
            .map(jacocoFolder -> jacocoFolder.addChild("test.exec"))
            .map(execFile -> new File(execFile.toString()))
            .filter(File::exists)
            .collect(Collectors.toSet());
    }
}
