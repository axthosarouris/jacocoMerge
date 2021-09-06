package com.github.axthosarouris.jacocomerge;

import static com.github.axthosarouris.jacocomerge.Constants.AGGREGATE_REPORT_FOLDER_NAME;
import static com.github.axthosarouris.jacocomerge.Constants.BUILD_FOLDER_NAME;
import static com.github.axthosarouris.jacocomerge.Constants.JACOCO_AGGREGATE_REPORT_FILENAME;
import static com.github.axthosarouris.jacocomerge.Constants.JACOCO_RESULTS_FOLDER_NAME;
import static com.github.axthosarouris.jacocomerge.Constants.REPORT_NAME;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.gradle.api.Project;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.tools.ExecFileLoader;
import org.jacoco.report.FileMultiReportOutput;
import org.jacoco.report.IReportVisitor;
import org.jacoco.report.ISourceFileLocator;
import org.jacoco.report.MultiReportVisitor;
import org.jacoco.report.csv.CSVFormatter;
import org.jacoco.report.html.HTMLFormatter;
import org.jacoco.report.xml.XMLFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportBuilder {


    private final Collection<File> sourceFiles;
    private final Collection<File> classFiles;

    public static final Logger logger = LoggerFactory.getLogger(ReportBuilder.class);
    private File xmlFile;
    private File csvFile;
    private File reportFolder;

    public ReportBuilder(
                        Project project,
                        Collection<File> sourceFiles,
                         Collection<File> classFiles
    ) {
        setupFolders(project);
        this.sourceFiles = sourceFiles;
        this.classFiles = classFiles;
    }

    private void setupFolders(Project project) {
        File projectBuildJacocoFolder = createBuilldJacocoFolder(project);
        reportFolder = new File(projectBuildJacocoFolder, AGGREGATE_REPORT_FOLDER_NAME);
        this.xmlFile = new File(reportFolder,JACOCO_AGGREGATE_REPORT_FILENAME+".xml");
        this.csvFile = new File(reportFolder,JACOCO_AGGREGATE_REPORT_FILENAME+".csv");


    }

    private File createBuilldJacocoFolder(Project project) {
        File projectFolder = project.getProjectDir().getAbsoluteFile();
        File projectBuildFolder = new File(projectFolder, BUILD_FOLDER_NAME);
        return new File(projectBuildFolder, JACOCO_RESULTS_FOLDER_NAME);
    }

    public void writeReports(File execFile) throws IOException {
        final ExecFileLoader loader = loadExecutionData(Set.of(execFile));

        final IBundleCoverage bundle = analyze(loader.getExecutionDataStore());
        writeReports(bundle, loader);
    }

    private  void createBuildJacocoFolder() {
        if (!reportFolder.exists()) {
            reportFolder.mkdirs();
        }
    }

    private void writeReports(final IBundleCoverage bundle,
                              final ExecFileLoader loader)
        throws IOException {

        final IReportVisitor visitor = createReportVisitor();
        visitor.visitInfo(loader.getSessionInfoStore().getInfos(),
                          loader.getExecutionDataStore().getContents());
        visitor.visitBundle(bundle, getSourceLocator());
        visitor.visitEnd();
    }

    private ISourceFileLocator getSourceLocator() {
        List<SourceFile> sourceFileSet = sourceFiles
            .stream()
            .map(SourceFile::fromPhysicalLocation)
            .collect(Collectors.toList());
        return new SourceFileLocatorImpl(sourceFileSet);
    }

    private IReportVisitor createReportVisitor() throws IOException {
        final List<IReportVisitor> visitors = new ArrayList<>();
        createBuildJacocoFolder();
        final XMLFormatter xmlFormatter = new XMLFormatter();
        visitors.add(xmlFormatter.createVisitor(new FileOutputStream(xmlFile)));
        final CSVFormatter csVFormatter = new CSVFormatter();
        visitors.add(csVFormatter.createVisitor(new FileOutputStream(csvFile)));
        final HTMLFormatter htmlFormatter = new HTMLFormatter();
        visitors.add(
            htmlFormatter.createVisitor(new FileMultiReportOutput(reportFolder)));
        return new MultiReportVisitor(visitors);
    }

    private IBundleCoverage analyze(final ExecutionDataStore data) throws IOException {
        final CoverageBuilder builder = new CoverageBuilder();
        final Analyzer analyzer = new Analyzer(data, builder);
        for (final File f : classFiles) {
            analyzer.analyzeAll(f);
        }
        logNoMatchWarning(builder.getNoMatchClasses());
        return builder.getBundle(REPORT_NAME);
    }

    //This method is as appears in Jacoco source code.
    private void logNoMatchWarning(final Collection<IClassCoverage> nomatch) {
        if (!nomatch.isEmpty()) {
            logger.warn("Some classes do not match with execution data.");
            logger.warn("For report generation the same class files must be used as at runtime.");
            for (final IClassCoverage c : nomatch) {
                logger.warn("Execution data for class {} does not match.", c.getName());
            }
        }
    }

    private ExecFileLoader loadExecutionData(Set<File> execfiles)
        throws IOException {
        final ExecFileLoader loader = new ExecFileLoader();
        for (File file : execfiles) {
            loader.load(file);
        }
        return loader;
    }
}
