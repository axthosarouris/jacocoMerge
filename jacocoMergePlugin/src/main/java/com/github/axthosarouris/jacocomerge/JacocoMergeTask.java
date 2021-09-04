package com.github.axthosarouris.jacocomerge;

import java.util.Objects;
import javax.inject.Inject;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;

/**
 * This is the class that implements the functionality of the "jacocoMerge" class.
 */
public abstract class JacocoMergeTask extends DefaultTask {

    public static final String ROOT = null;

    @Inject
    public JacocoMergeTask() {
    }

    @Input
    @Optional
    public abstract Property<String> getProjectName();

    @TaskAction
    public void taskAction() {
        Project project = extractProjectNameFromTaskConfig();
        JacocoMerge jacocoMerge = new JacocoMerge();
        jacocoMerge.createReport(project);
    }

    private Project extractProjectNameFromTaskConfig() {
        String projectName = getProjectName().getOrElse(ROOT);
        return isRootProject(projectName)
            ? getRootProject()
            : locateSubProject(projectName);
    }

    private Project getRootProject() {
        return this.getProject().getRootProject();
    }

    private Project locateSubProject(String projectName) {
        return getRootProject()
            .getSubprojects()
            .stream()
            .filter(subproject -> subproject.getName().equals(projectName))
            .findAny()
            .orElseThrow(() -> new RuntimeException(reportMissingProject(projectName)));
    }

    private String reportMissingProject(String projectName) {
        return String.format("Module %s does not exist", projectName);
    }

    private boolean isRootProject(String projectName) {
        return Objects.isNull(projectName) || projectName.isBlank() || projectName.equalsIgnoreCase("root")
               || getRootProject().getName().equals(projectName);
    }
}