package com.github.axthosarouris.testingutils.filelisting;

import static com.github.axthosarouris.testingutils.filelisting.BuildFilesListing.CLASS_FILE_ENDING;
import static com.github.axthosarouris.testingutils.filelisting.BuildFilesListing.MAIN_SOURCE_FOLDERS;
import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

public class ClassDetails {

    private static final String PACKAGE_DELIMITER = ".";
    private static final String PACKAGE_DELIMITER_REGEX = "\\.";
    private final File classFile;

    public ClassDetails(File classFile) {
        this.classFile = classFile;
    }

    public String getCanonicalName() {
        String className = extractClassNameAsFolderStructure(classFile);
        className = className.replaceAll(File.separator, PACKAGE_DELIMITER);
        className = className.replaceAll(CLASS_FILE_ENDING, "");

        return className;
    }

    public String getClassNameAsPath() {
        return getCanonicalName().replaceAll(PACKAGE_DELIMITER_REGEX, File.separator);
    }

    private String extractClassNameAsFolderStructure(File classFile) {
        String absolutePath = classFile.getAbsolutePath();
        int indexOfMainSourceFolders = absolutePath.indexOf(MAIN_SOURCE_FOLDERS);
        int beginIndexOfPackageName = indexOfMainSourceFolders + MAIN_SOURCE_FOLDERS.length();
        return absolutePath.substring(beginIndexOfPackageName);
    }

    public Class<?> getInstance() {
        try {
            URL[] url = new URL[]{classFile.toURI().toURL()};
            return URLClassLoader.newInstance(url).loadClass(getCanonicalName());
        } catch (ClassNotFoundException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
    public Stream<Method> getDeclaredMethods(){
        return Arrays.stream(getInstance().getDeclaredMethods());
    }


}
