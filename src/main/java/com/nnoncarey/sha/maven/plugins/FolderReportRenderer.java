package com.nnoncarey.sha.maven.plugins;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.reporting.MavenReportRenderer;

/**
 *
 */
public class FolderReportRenderer implements MavenReportRenderer {

    private final Sink sink;
    private final String displayName;
    private final String folder;

    public FolderReportRenderer(Sink sink, String displayName, String folder) {
        this.sink = sink;
        this.displayName = displayName;
        this.folder = folder;
    }

    @Override
    public String getTitle() {
        return this.displayName;
    }

    @Override
    public void render() {
        sink.section1();
        sink.sectionTitle1();
        sink.text(displayName);
        sink.sectionTitle1_();

        sink.link(folder + "/index.html");
        sink.text("View the Report");
        sink.link_();
    }
}
