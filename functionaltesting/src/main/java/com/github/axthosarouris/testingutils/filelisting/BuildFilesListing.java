package com.github.axthosarouris.testingutils.filelisting;

import static java.util.function.Predicate.not;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class BuildFilesListing {

    public static final File PROJECT_FOLDER = new File(new File("").getAbsolutePath()).getParentFile();
    public static final File BUILD_SRC_FOLDER = new File(PROJECT_FOLDER, "buildSrc");
    public static final File FUNCTIONAL_TESTING_FOLDER = new File(PROJECT_FOLDER, "functionaltesting");
    public static final String MAIN_SOURCE_FOLDERS = "build/classes/java/main/";
    public static final String CLASS_FILE_ENDING = ".class";

    public List<File> listAllClassFiles(File folder) {
        ArrayList<File> result = new ArrayList<>();
        Stack<File> unvisitedFiles = initializeUnvisitedFiles(folder);
        while (thereAreMoreFiles(unvisitedFiles)) {
            File currentFile = unvisitedFiles.pop();
            if (currentFile.isDirectory()) {
                File[] files = currentFile.listFiles();
                addClassFilesToResult(result, files);
                addRestFilesToUnvisitedFiles(unvisitedFiles, files);
            }
        }
        return filterOutBuildSrcAndTestClasses(result);
    }

    public List<String> listClassNames(File folder) {

        return listAllClassFiles(folder)
            .stream()
            .map(ClassDetails::new)
            .map(ClassDetails::getClassNameAsPath)
            .collect(Collectors.toList());
    }

    public List<String> listAllMethodNames(File folder) {
        return listAllClassFiles(folder).stream()
            .map(ClassDetails::new)
            .flatMap(ClassDetails::getDeclaredMethods)
            .map(Method::getName)
            .map(this::mapJacocoInternalNameForConstructorsToXmlReportName)
            .collect(Collectors.toList());
    }

    private String mapJacocoInternalNameForConstructorsToXmlReportName(String methodName) {
        if (methodName.contains("$jacocoInit")) {
            return "<init>";
        }
        return methodName;
    }

    private List<File> filterOutBuildSrcAndTestClasses(ArrayList<File> result) {
        return result
            .stream()
            .filter(this::excludeBuildSrcFiles)
            .filter(this::excludeFunctionalTestingFiles)
            .filter(this::excludeTestClasses)
            .collect(Collectors.toList());
    }

    private boolean excludeTestClasses(File file) {
        return file.getAbsolutePath().contains(MAIN_SOURCE_FOLDERS);
    }

    private boolean excludeFunctionalTestingFiles(File file) {
        return !file.getAbsolutePath().startsWith(FUNCTIONAL_TESTING_FOLDER.getAbsolutePath());
    }

    private boolean excludeBuildSrcFiles(File file) {
        return !file.getAbsolutePath().startsWith(BUILD_SRC_FOLDER.getAbsolutePath());
    }

    private boolean thereAreMoreFiles(Stack<File> unvisitedFiles) {
        return !unvisitedFiles.isEmpty();
    }

    private void addRestFilesToUnvisitedFiles(Stack<File> unvisitedFiles, File[] files) {
        List<File> restFiles = listRestFiles(files);
        unvisitedFiles.addAll(restFiles);
    }

    private Stack<File> initializeUnvisitedFiles(File folder) {
        Stack<File> unvisitedFiles = new Stack<>();
        unvisitedFiles.push(folder);
        return unvisitedFiles;
    }

    private void addClassFilesToResult(ArrayList<File> result, File[] files) {
        List<File> buildClassFiles = extractClassFiles(files);
        result.addAll(buildClassFiles);
    }

    private List<File> listRestFiles(File[] files) {
        return Arrays.stream(files)
            .filter(not(this::fileIsBuildClassFile))
            .collect(Collectors.toList());
    }

    private List<File> extractClassFiles(File[] listFiles) {
        return Arrays.stream(listFiles)
            .filter(this::fileIsBuildClassFile)
            .collect(Collectors.toList());
    }

    private boolean fileIsBuildClassFile(File file) {
        return file.getAbsolutePath().endsWith(CLASS_FILE_ENDING);
    }
}
