package com.github.axthosarouris.jacocomerge;

import java.io.File;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.gradle.api.Project;

/**
 * Finds all class files in the project.
 */
public class ClassFilesRetriever {

    public static final String CLASSES_FOLDER = "classes";
    public static final String CLASS_FILE_ENDING = ".class";
    public static final String MAIN_FOLDER = "java/main";
    private final Project project;

    public ClassFilesRetriever(Project rootProject) {
        this.project = rootProject;
    }

    public Set<File> listAllMainClassFiles() {
        return project.getSubprojects()
            .stream()
            .map(Project::getBuildDir)
            .flatMap(this::listAllMainClassFiles)
            .collect(Collectors.toSet());
    }

    private Stream<File> listAllMainClassFiles(File buildDir) {
        return Optional.of(buildDir)
            .map(this::getClassesFolderInBuildDir)
            .stream()
            .filter(File::exists)
            .flatMap(this::listAllClassFiles);
    }

    private File getClassesFolderInBuildDir(File buildFolder) {
        return new File(buildFolder, CLASSES_FOLDER);
    }

    private Stream<File> listAllClassFiles(File topFolder) {
        Collection<File> allFiles = FileUtils.listAllFiles(topFolder);
        return allFiles.stream().filter(this::isMainClassFile);
    }

    private boolean isMainClassFile(File file) {
        return file.getName().endsWith(CLASS_FILE_ENDING) && file.getAbsolutePath().contains(MAIN_FOLDER);
    }
}
