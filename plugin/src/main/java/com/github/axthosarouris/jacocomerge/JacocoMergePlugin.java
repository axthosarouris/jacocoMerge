package com.github.axthosarouris.jacocomerge;


import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;

public class JacocoMergePlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        TaskProvider<JacocoMerge> task = project.getTasks().register("jacocoMergeReport", JacocoMerge.class);

    }


}


