package com.github.axthosarouris.jacocomerge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jacoco.report.ISourceFileLocator;

/**
 * Class necessary for {@link org.jacoco.report.IReportVisitor}. Its functionality is to provide the contents of a
 * source file when Jacoco is creating the report, in order for the source code to be visible in the HTML reports.
 * <p>
 * In this particular implementation the SourceFileLocator is a view of a {@link SourceFile} collection as a {@link Map}
 * where the key is the logical location of the file.
 */
public class SourceFileLocatorImpl implements ISourceFileLocator {

    public static final int TAB_WTDTH = 4;
    private final Map<String, SourceFile> sourceFileMap;

    public SourceFileLocatorImpl(List<SourceFile> sourceFiles) {
        this.sourceFileMap = sourceFiles.stream()
            .collect(Collectors.toMap(e -> e.getLogicalLocation().toString(), e -> e));
    }

    @Override
    public Reader getSourceFile(String packageName, String fileName) throws IOException {
        String key = packageName + File.separator + fileName;
        if (sourceFileMap.containsKey(key)) {
            SourceFile sourceFile = sourceFileMap.get(key);
            return new BufferedReader(new FileReader(sourceFile.getPhysicalLocation()));
        }
        return null;
    }

    @Override
    public int getTabWidth() {
        return TAB_WTDTH;
    }
}
