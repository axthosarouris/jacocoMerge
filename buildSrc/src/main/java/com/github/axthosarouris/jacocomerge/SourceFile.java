package com.github.axthosarouris.jacocomerge;

import java.io.File;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SourceFile {

    public static final Pattern SOURCE_FILE_PATTERN = Pattern.compile(".*?/src/.*?/java/(.*)");
    private final File physicalLocation;
    private final File classLocation;


    public static SourceFile fromPhysicalLocation(File file){
        return fromPhysicalLocation(file.getAbsolutePath());
    }

    public static SourceFile fromPhysicalLocation(String location) {
        Matcher matcher = SOURCE_FILE_PATTERN.matcher(location);
        if (matcher.find()) {
            String logicalLocation = matcher.group(1);
            return new SourceFile(new File(location), new File(logicalLocation));
        }

        throw new RuntimeException("Not a source file:" + location);
    }

    public SourceFile(File physicalLocation, File classLocation) {
        this.physicalLocation = physicalLocation;
        this.classLocation = classLocation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPhysicalLocation(), getClassLocation());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SourceFile)) {
            return false;
        }
        SourceFile that = (SourceFile) o;
        return Objects.equals(getPhysicalLocation(), that.getPhysicalLocation()) && Objects.equals(
            getClassLocation(), that.getClassLocation());
    }

    public File getPhysicalLocation() {
        return physicalLocation;
    }

    public File getClassLocation() {
        return classLocation;
    }
}
