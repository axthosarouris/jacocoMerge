package com.github.axthosarouris.jacocomerge;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.gradle.api.Project;

public class ClassFilesRetriever {

    private final Project project;

    public ClassFilesRetriever(Project rootProject){
        this.project = rootProject;
    }

    public Set<File> listAllMainClassFiles() {
        Set<Project> subprojects = project.getSubprojects();
        return subprojects.stream()
            .flatMap(project -> listAllMainClassFiles(project.getBuildDir()))
            .collect(Collectors.toSet());
    }


    private Stream<File> listAllMainClassFiles(File buildDir) {
        final ArrayList<File> classFiles = new ArrayList<>();
        return Optional.of(buildDir)
            .map(this::getClassesFolderInBuildDir)
            .stream()
            .filter(File::exists)
            .flatMap(file -> listAllClassFilesRecursive(file, classFiles));
    }


    private File getClassesFolderInBuildDir(File b) {
        return new File(b, "classes");
    }

    private Stream<File> listAllClassFilesRecursive(File topFolder, ArrayList<File> classFiles) {
        Stack<File> unvisitedFolders = new Stack<>();
        unvisitedFolders.add(topFolder);
        while (!unvisitedFolders.isEmpty()) {
            File currentFolder = unvisitedFolders.pop();
            if (currentFolder.exists() && currentFolder.isDirectory()) {
                List<File> currentClassFiles = listClassFilesInFolder(currentFolder);
                classFiles.addAll(currentClassFiles);
                unvisitedFolders.addAll(listSubFolders(currentFolder));
            } else if (currentFolder.exists() && currentFolder.isFile()) {
                throw new IllegalStateException("Unexpected file in folders stack:" + currentFolder.getAbsolutePath());
            }
        }

        return classFiles.stream();
    }


    private List<File> listSubFolders(File currentFolder) {
        return Optional.ofNullable(currentFolder)
            .map(folder -> folder.listFiles(File::isDirectory))
            .stream()
            .flatMap(Arrays::stream)
            .collect(Collectors.toList());
    }

    private List<File> listClassFilesInFolder(File currentFolder) {
        return Optional.ofNullable(currentFolder)
            .map(folder -> folder.listFiles(this::isMainAndClassFile))
            .stream()
            .flatMap(Arrays::stream)
            .collect(Collectors.toList());
    }

    private boolean isMainAndClassFile(File file) {
        return file.getName().endsWith(".class") && file.getAbsolutePath().contains("java/main");
    }



}
