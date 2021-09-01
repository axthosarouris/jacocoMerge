package com.github.axthosarouris.jacocomerge;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jacoco.report.ISourceFileLocator;

public class SourceFileLocatorImpl implements ISourceFileLocator {

    public static final int TAB_WTDTH = 4;
    private final Map<String, SourceFile> sourceFileMap;

    public SourceFileLocatorImpl(List<SourceFile> sourceFiles){

        Map<String, SourceFile> map = sourceFiles.stream()
            .collect(Collectors.toMap(e -> e.getClassLocation().toString(), e -> e));
        this.sourceFileMap = map;
    }

    @Override
    public Reader getSourceFile(String packageName, String fileName) throws IOException {
        String key = packageName+"/"+ fileName;
        if(sourceFileMap.containsKey(key)){
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
