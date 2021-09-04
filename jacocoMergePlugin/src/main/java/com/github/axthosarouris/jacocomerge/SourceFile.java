package com.github.axthosarouris.jacocomerge;

import java.io.File;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SourceFile {

    public static final Pattern SOURCE_FILE_PATTERN = Pattern.compile(".*?/src/.*?/java/(.*)");
    /**
     * The location of the source file in the disk. The absolute path.
     */
    private final File physicalLocation;
    /**
     * The location of the source file relative to the source folder.
     */
    private final File logicalLocation;

    private SourceFile(File physicalLocation, File classLocation) {
        this.physicalLocation = physicalLocation;
        this.logicalLocation = classLocation;
    }

    public static SourceFile fromPhysicalLocation(File file) {
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

    @Override
    public int hashCode() {
        return Objects.hash(getPhysicalLocation(), getLogicalLocation());
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
        return Objects.equals(getPhysicalLocation(), that.getPhysicalLocation())
               && Objects.equals(getLogicalLocation(), that.getLogicalLocation());
    }

    public File getPhysicalLocation() {
        return physicalLocation;
    }

    public File getLogicalLocation() {
        return logicalLocation;
    }
}
