package com.github.axthosarouris.jacocomerge;

import java.io.File;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.gradle.api.Project;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;

public class SourceFilesRetriever {

    private final Project project;

    public SourceFilesRetriever(Project project) {
        this.project = project;
    }

    public Set<File> getAllSourceFiles() {
        return listSourceDirs()
            .map(FileUtils::listAllFiles)
            .flatMap(Collection::stream)
            .map(File::getAbsoluteFile)
            .collect(Collectors.toSet());
    }

    private Stream<File> listSourceDirs() {
        return project.getAllprojects().stream()
            .map(p -> p.getExtensions().getByType(JavaPluginExtension.class))
            .flatMap(plugin -> plugin.getSourceSets().stream())
            .map(SourceSet::getAllJava)
            .map(SourceDirectorySet::getSrcDirs)
            .flatMap(Collection::stream)
            .map(File::getAbsoluteFile);
    }
}
