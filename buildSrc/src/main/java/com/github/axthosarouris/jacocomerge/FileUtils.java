package com.github.axthosarouris.jacocomerge;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

public final class FileUtils {

    private FileUtils() {

    }

    public static Collection<File> listAllFiles(File root) {
        List<File> result = new ArrayList<>();
        Stack<File> unvisitedFiles = new Stack<>();
        unvisitedFiles.add(root);
        while (!unvisitedFiles.isEmpty()) {
            File currentFile = unvisitedFiles.pop();
            if(currentFile.exists()){
                if (currentFile.isDirectory()) {
                    addChildrenFilesToUnvisitedFiles(unvisitedFiles, currentFile);
                } else if (currentFile.isFile()) {
                    result.add(currentFile);
                }
            }
        }
        return result;
    }

    private static void addChildrenFilesToUnvisitedFiles(Stack<File> unvisitedFiles, File currentFile) {
        List<File> children = Arrays.asList(Objects.requireNonNull(currentFile.listFiles()));
        unvisitedFiles.addAll(children);
    }
}
