package com.github.axthosarouris.jacocomerge;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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

public class ReportBuilder {

    public static final File BASE_PATH = new File("build", "jacoco");
    public static final String JACOCO_AGGREGATE_REPORT = "jacocoAggregateReport";
    public static final File XML_FILE = new File(BASE_PATH, JACOCO_AGGREGATE_REPORT + ".xml");
    public static final File CSV_FILE = new File(BASE_PATH, JACOCO_AGGREGATE_REPORT + ".csv");
    private static final String REPORT_NAME = "JaCoCo Coverage Report";
    private final PrintWriter out = new PrintWriter(System.out, true);
    private final Collection<File> sourceFiles;
    private final Collection<File> classFiles;
    public ReportBuilder(Collection<File> sourceFiles,
                         Collection<File> classFiles
    ) {
        this.sourceFiles = sourceFiles;
        this.classFiles = classFiles;
    }

    public void writeReports(File execFile) throws IOException {
        final ExecFileLoader loader = loadExecutionData(Set.of(execFile));
        final IBundleCoverage bundle = analyze(loader.getExecutionDataStore(), out);
        writeReports(bundle, loader);
    }

    private static File createBasePath() {
        if (!BASE_PATH.exists()) {
            BASE_PATH.mkdirs();
        }

        return BASE_PATH;
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
        createBasePath();
        final XMLFormatter xmlFormatter = new XMLFormatter();
        visitors.add(xmlFormatter.createVisitor(new FileOutputStream(XML_FILE)));
        final CSVFormatter csVFormatter = new CSVFormatter();
        System.out.println(CSV_FILE.getAbsolutePath());
        visitors.add(csVFormatter.createVisitor(new FileOutputStream(CSV_FILE)));
        final HTMLFormatter htmlFormatter = new HTMLFormatter();
        visitors.add(
            htmlFormatter.createVisitor(new FileMultiReportOutput(BASE_PATH)));
        return new MultiReportVisitor(visitors);
    }

    private IBundleCoverage analyze(final ExecutionDataStore data,
                                    final PrintWriter out) throws IOException {
        final CoverageBuilder builder = new CoverageBuilder();
        final Analyzer analyzer = new Analyzer(data, builder);
        for (final File f : classFiles) {
            analyzer.analyzeAll(f);
        }
        printNoMatchWarning(builder.getNoMatchClasses(), out);
        return builder.getBundle(REPORT_NAME);
    }

    private void printNoMatchWarning(final Collection<IClassCoverage> nomatch,
                                     final PrintWriter out) {
        if (!nomatch.isEmpty()) {
            out.println(
                "[WARN] Some classes do not match with execution data.");
            out.println(
                "[WARN] For report generation the same class files must be used as at runtime.");
            for (final IClassCoverage c : nomatch) {
                out.printf(
                    "[WARN] Execution data for class %s does not match.%n",
                    c.getName());
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
