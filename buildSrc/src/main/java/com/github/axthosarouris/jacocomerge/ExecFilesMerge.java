package com.github.axthosarouris.jacocomerge;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jacoco.core.tools.ExecFileLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecFilesMerge {

    public static final Logger logger = LoggerFactory.getLogger(ExecFilesMerge.class);
    private final List<File> execFiles = new ArrayList<>();
    private File destinationFile;
    
    public void addExecFiles(Collection<File> execFiles) {
        this.execFiles.addAll(execFiles);
    }

    public void setDestinationFile(File destinationFile) {
        this.destinationFile = destinationFile;
    }

    public void createMergedFile()
        throws IOException {
        final ExecFileLoader loader = loadExecutionData();
        logger.info("Writing execution data to {}",destinationFile.getAbsolutePath());
        loader.save(destinationFile, true);
    }


    private ExecFileLoader loadExecutionData()
        throws IOException {
        final ExecFileLoader loader = new ExecFileLoader();
        if (execFiles.isEmpty()) {
            logger.warn("No execution data files provided.");
        } else {
            loadExecFiles(loader);
        }
        return loader;
    }

    private void loadExecFiles(ExecFileLoader loader) throws IOException {
        for (final File file : execFiles) {
            logger.info("Loading execution data file {}",file.getAbsolutePath());
            loader.load(file);
        }
    }
}