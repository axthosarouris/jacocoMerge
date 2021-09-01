package com.github.axthosarouris.jacocomerge;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jacoco.core.tools.ExecFileLoader;

public class Merge {

    private List<File> execfiles = new ArrayList<>();
    private File destfile;
    
    public void addExecFiles(Collection<File> execFiles) {
        this.execfiles.addAll(execFiles);
    }


    public void setDestfile(File destfile) {
        this.destfile = destfile;
    }

    public void createMergedFile()
        throws IOException {
        final ExecFileLoader loader = loadExecutionData();
        System.out.printf("[INFO] Writing execution data to %s.%n",
                          destfile.getAbsolutePath());
        loader.save(destfile, true);
    }


    private ExecFileLoader loadExecutionData()
        throws IOException {
        final ExecFileLoader loader = new ExecFileLoader();
        if (execfiles.isEmpty()) {
            System.out.println("[WARN] No execution data files provided.");
        } else {
            for (final File file : execfiles) {
                System.out.printf("[INFO] Loading execution data file %s.%n",
                                  file.getAbsolutePath());
                loader.load(file);
            }
        }
        return loader;
    }
}