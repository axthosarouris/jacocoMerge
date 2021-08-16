package com.github.axthosarouris.jacocomerge;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import org.gradle.api.Project;

public class SourceFilesRetriever {

    private final Project project;

    public SourceFilesRetriever(Project rootProject){
        this.project = rootProject;
    }


    public Set<File> getAllSourceFiles() {
        File rootDirectory = project.getRootDir();
        Set<File> sourceFiles = new HashSet<>();
        Stack<File> currentFolderStack = new Stack<>();
        currentFolderStack.push(rootDirectory);
        while (!currentFolderStack.isEmpty()) {
            File currentFolder = currentFolderStack.pop();
            File[] currentFolderFiles = currentFolder.listFiles();
            if (currentFolderFiles != null) {
                for (File file : currentFolderFiles) {
                    if (isSourceFile(file)) {
                        sourceFiles.add(file);
                    } else if (file.exists() && file.isDirectory()) {
                        currentFolderStack.push(file);
                    }
                }
            }
        }
        return sourceFiles;
    }

    private boolean isSourceFile(File file) {
        return file.exists()
               && file.isFile()
               && file.getAbsolutePath().contains("src/main/java")
               && file.getName().endsWith(".java");
    }

}
