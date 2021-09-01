package com.github.axthosarouris.functionaltesting;

import static com.github.axthosarouris.testingutils.filelisting.BuildFilesListing.PROJECT_FOLDER;
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
import org.junit.jupiter.api.Test;

public class ReportTesting {

    public static final File BUILD_FOLDER = new File(PROJECT_FOLDER, "build");
    public static final File JACOCO_MERGED_REPORT_FOLDER = new File(BUILD_FOLDER, "jacoco");

    @Test
    public void reportFolderExists() {
        assertThat(JACOCO_MERGED_REPORT_FOLDER).exists();
    }

    @Test
    public void jacocoMergeReportFileExists() {
        File reportFile = new File(JACOCO_MERGED_REPORT_FOLDER, "test.exec");
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
        List<String> allClassFiles = new BuildFilesListing().listClassNames(PROJECT_FOLDER);
        assertThat(allClassFiles).isNotEmpty();
        assertThat(reportClassNames).containsAll(allClassFiles);
    }

    @Test
    public void jacocoMergeReportContainsAllMethodsOfProjectModules()
        throws XMLStreamException, JAXBException, FileNotFoundException {
        List<String> allMethodNames = new BuildFilesListing().listAllMethodNames(PROJECT_FOLDER);
        XmlReport report = readXmlReport();
        List<String> reportMethodNames = report.listAllReportedMethodNames();
        assertThat(allMethodNames).hasSameElementsAs(reportMethodNames);
    }


    private XmlReport readXmlReport() throws XMLStreamException, FileNotFoundException, JAXBException {
        File reportFile = new File(JACOCO_MERGED_REPORT_FOLDER, "jacocoAggregateReport.xml");
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
