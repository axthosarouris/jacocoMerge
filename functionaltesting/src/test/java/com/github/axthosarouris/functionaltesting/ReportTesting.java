package com.github.axthosarouris.functionaltesting;

import static org.assertj.core.api.Assertions.assertThat;
import com.github.axthosarouris.testingutils.filelisting.BuildFilesListing;
import com.github.axthosarouris.xmlreport.XmlReport;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Set;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReportTesting {

    public static final String JACOCO_EXECUTION_FILES_DEFAULT_NAME = "test.exec";
    public static final String JACOCO_AGGREGATE_REPORT_XML_FILENAME = "jacocoAggregateReport.xml";
    public static final String CURRENT_FOLDER = "";
    public static final String DEFAULT_BUILD_FOLDER = "build";
    public static final String DEFAULT_JACOCO_FOLDER = "jacoco";
    public static final String PROJECT_FOLDER = "testProject01";
    private static final String REPORT_FOLDER = "aggregatedReport";
    private File projectFolder;
    private File jacocoMergedReportFolder;
    private File buildJacocoFolder;

    @BeforeEach
    public void init() {
        File rootFolder = new File(new File(CURRENT_FOLDER).getAbsolutePath()).getParentFile().getAbsoluteFile();
        File projectFolder = new File(rootFolder, PROJECT_FOLDER);
        setupFolders(projectFolder);
    }

    @Test
    public void reportFolderExists() {
        assertThat(jacocoMergedReportFolder).exists();
    }

    @Test
    public void jacocoMergeReportFileExists() {
        File reportFile = new File(buildJacocoFolder, JACOCO_EXECUTION_FILES_DEFAULT_NAME);
        assertThat(reportFile).exists();
    }



    @Test
    public void jacocoMergeReportXmlFileExists() throws JAXBException, FileNotFoundException, XMLStreamException {
        XmlReport report = readXmlReport();
        assertThat(report).isNotNull();
    }

    @Test
    public void jacocoMergeReportContainsAllClassesOfProjectModules()
        throws XMLStreamException, JAXBException, FileNotFoundException {
        XmlReport report = readXmlReport();
        Set<String> reportClassNames = report.getClassNames();
        List<String> allClassFiles = new BuildFilesListing(projectFolder).listClassNames();
        assertThat(allClassFiles).isNotEmpty();
        assertThat(reportClassNames).containsAll(allClassFiles);
    }

    @Test
    public void jacocoMergeReportContainsAllMethodsOfProjectModules()
        throws XMLStreamException, JAXBException, FileNotFoundException {
        List<String> allMethodNames = new BuildFilesListing(projectFolder).listAllMethodNames();
        XmlReport report = readXmlReport();
        List<String> reportMethodNames = report.listAllReportedMethodNames();
        assertThat(allMethodNames).hasSameElementsAs(reportMethodNames);
    }

    private void setupFolders(File projectFolder) {
        this.projectFolder = projectFolder;
        File buildFolder = new File(projectFolder, DEFAULT_BUILD_FOLDER).getAbsoluteFile();
        this.buildJacocoFolder = new File(buildFolder, DEFAULT_JACOCO_FOLDER);
        this.jacocoMergedReportFolder = new File(buildJacocoFolder, REPORT_FOLDER);
    }

    private XmlReport readXmlReport() throws XMLStreamException, FileNotFoundException, JAXBException {
        File reportFile = new File(jacocoMergedReportFolder, JACOCO_AGGREGATE_REPORT_XML_FILENAME);
        assertThat(reportFile).exists();
        return parseReport(reportFile);
    }

    private XmlReport parseReport(File reportFile) throws XMLStreamException, FileNotFoundException, JAXBException {
        XMLInputFactory xif = XMLInputFactory.newFactory();
        xif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        JAXBContext jaxbContext = JAXBContext.newInstance(XmlReport.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        XMLStreamReader reader = xif.createXMLStreamReader(new FileReader(reportFile));
        return (XmlReport) unmarshaller.unmarshal(reader);
    }
}
