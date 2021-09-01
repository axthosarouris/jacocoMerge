package com.github.axthosarouris.jacocomerge;

import java.util.Objects;
import javax.inject.Inject;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;

public abstract class JacocoMergeTask extends DefaultTask {

    @Inject
    public JacocoMergeTask() {
    }

    @Input
    @Optional
    public abstract Property<String> getProjectName();

    @TaskAction
    public void taskAction() {
        Project project = findProject();
        JacocoMerge jacocoMerge = new JacocoMerge();
        jacocoMerge.createReport(project);
    }

    private Project findProject() {
        String projectName = getProjectName().getOrElse(null);
        Project project = null;
        if (isRootProject(projectName)) {
            project = this.getProject().getRootProject();
        } else {
            project = this.getProject()
                .getRootProject()
                .getSubprojects()
                .stream()
                .filter(p -> p.getName().equals(projectName))
                .findAny()
                .orElseThrow(() -> new RuntimeException(String.format("Module %s does not exist", projectName)));
        }
        return project;
    }

    private boolean isRootProject(String projectName) {
        return Objects.isNull(projectName) || projectName.isBlank() || projectName.equalsIgnoreCase("root")
            || this.getProject().getRootProject().getName().equals(projectName);
    }
}