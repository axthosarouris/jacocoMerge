package com.github.axthosarouris.testingutils.filelisting;

import static java.util.function.Predicate.not;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;

public class BuildFilesListing {

    public static final String MAIN_SOURCE_FOLDERS = "build/classes/java/main/";
    public static final String CLASS_FILE_ENDING = ".class";
    private final File projectFolder;
    private final File pluginSrcFolder;
    private final File functionalTestingFolder;

    public BuildFilesListing(File projectFolder){
        this.projectFolder = projectFolder;
        this.pluginSrcFolder = new File(projectFolder,"buildSrc");
        this.functionalTestingFolder = new File(projectFolder, "functionaltesting");

    }

    public List<File> listAllClassFiles() {
        ArrayList<File> discoveredClassFiles = new ArrayList<>();
        Stack<File> unvisitedFiles = initializeUnvisitedFiles();
        while (thereAreMoreFiles(unvisitedFiles)) {
            File currentFile = unvisitedFiles.pop();
            if (currentFile.isDirectory()) {
                discoveredClassFiles.addAll(extractClassFiles(currentFile));
                unvisitedFiles.addAll(listRestFiles(currentFile));
            }
        }
        return filterOutBuildSrcAndTestClasses(discoveredClassFiles);
    }

    public List<String> listClassNames() {
        return listAllClassFiles()
            .stream()
            .map(ClassDetails::new)
            .map(ClassDetails::getClassNameAsPath)
            .collect(Collectors.toList());
    }

    public List<String> listAllMethodNames() {
        return listAllClassFiles().stream()
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
        return !file.getAbsolutePath().startsWith(functionalTestingFolder.getAbsolutePath());
    }

    private boolean excludeBuildSrcFiles(File file) {
        return !file.getAbsolutePath().startsWith(pluginSrcFolder.getAbsolutePath());
    }

    private boolean thereAreMoreFiles(Stack<File> unvisitedFiles) {
        return !unvisitedFiles.isEmpty();
    }

    private Stack<File> initializeUnvisitedFiles() {
        Stack<File> unvisitedFiles = new Stack<>();
        unvisitedFiles.push(projectFolder);
        return unvisitedFiles;
    }

    private List<File> listRestFiles(File folder) {
        return Arrays.stream(Objects.requireNonNull(folder.listFiles()))
            .filter(not(this::fileIsBuildClassFile))
            .collect(Collectors.toList());
    }

    private List<File> extractClassFiles(File folder) {
        return Arrays.stream(Objects.requireNonNull(folder.listFiles()))
            .filter(this::fileIsBuildClassFile)
            .collect(Collectors.toList());
    }

    private boolean fileIsBuildClassFile(File file) {
        return file.getAbsolutePath().endsWith(CLASS_FILE_ENDING);
    }
}
