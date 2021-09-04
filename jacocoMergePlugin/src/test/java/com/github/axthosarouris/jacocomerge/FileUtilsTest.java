package com.github.axthosarouris.jacocomerge;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class FileUtilsTest {

    public static final int NUMBER_OF_REGULAR_FILES_IN_RESOURCE_FOLDER = 5;
    public static final String RESOURCE_FOLDER = "listFilesTestResource";

    @Test
    void listFilesListsAllFilesUnderAFolder() throws URISyntaxException {
        URI resource =
            this.getClass().getClassLoader().getResource(RESOURCE_FOLDER).toURI();
        File resourceFolder = new File(resource);
        List<File> actualFiles = new ArrayList<>(FileUtils.listAllFiles(resourceFolder));
        assertThat(actualFiles).hasSize(NUMBER_OF_REGULAR_FILES_IN_RESOURCE_FOLDER);
    }
}