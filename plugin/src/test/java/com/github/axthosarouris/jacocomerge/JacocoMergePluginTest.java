package com.github.axthosarouris.jacocomerge;

import static org.assertj.core.api.Assertions.assertThat;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

public class JacocoMergePluginTest {

    @Test
    public void JacocoMergePluginTest() {
        // Create a test project and apply the plugin
        Project project = ProjectBuilder.builder().build();
        project.getPlugins().apply("com.github.axthosarouris.jacocoMerge");
        assertThat(project.getTasks().findByName("jacocoMergeReport")).isNotNull();
    }
}


