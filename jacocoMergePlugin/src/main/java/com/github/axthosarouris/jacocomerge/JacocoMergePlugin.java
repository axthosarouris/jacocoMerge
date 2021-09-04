package com.github.axthosarouris.jacocomerge;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class JacocoMergePlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getTasks().register("jacocoMergeReport", JacocoMergeTask.class);
    }
}


