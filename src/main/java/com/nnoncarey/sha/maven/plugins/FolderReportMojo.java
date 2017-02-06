package com.nnoncarey.sha.maven.plugins;

import org.apache.maven.doxia.siterenderer.RenderingContext;
import org.apache.maven.doxia.siterenderer.sink.SiteRendererSink;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.reporting.MavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.doxia.sink.Sink;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.util.Locale;

/**
 * Report goal which copies a given folder into the site, and links to it via a report.
 */
@Mojo(name = "folder-report")
public class FolderReportMojo extends AbstractMojo implements MavenReport {
    /**
     * The directory to copy into the site folder.
     */
    @Parameter(required = true)
    private File sourceDirectory;

    /**
     * Get the category name for this report.
     *
     * Should be {@link MavenReport#CATEGORY_PROJECT_INFORMATION} or {@link MavenReport#CATEGORY_PROJECT_REPORTS}
     */
    @Parameter(defaultValue = "Project Reports" )
    private String categoryName;

    /**
     * The name of the report. Must be unique in the site, as it is used as the name of the folder in the site.
     */
    @Parameter(required = true)
    private String name;

    /**
     * The display name of the report.
     */
    @Parameter(required = true)
    private String displayName;

    /**
     * A description of the report.
     */
    @Parameter(property = "folder-report.description")
    private String description;

    /**
     * The output directory for the report. Note that this parameter is only evaluated if the goal is run directly from
     * the command line. If the goal is run indirectly as part of a site generation, the output directory configured in
     * the Maven Site Plugin is used instead.
     */
    @Parameter(property = "project.reporting.outputDirectory", required = true)
    private File outputDirectory;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (this.canGenerateReport()) {
            try {
                RenderingContext context = new RenderingContext(outputDirectory, getOutputName() + ".html");

                SiteRendererSink sink = new SiteRendererSink(context);

                generate(sink, Locale.getDefault());
            } catch ( MavenReportException e ) {
                throw new MojoExecutionException(
                        "An error has occurred in " + getName(Locale.ENGLISH) + " report generation.", e);
            }
        }
    }

    public void generate(Sink sink, Locale locale) throws MavenReportException {
        getLog().info("generate() was called");

        if (sink == null) {
            throw new MavenReportException("You must specify a sink.");
        }

        if (!canGenerateReport()) {
            getLog().info( "This report " + this.name + " cannot be generated as part of the current build.");
            return;
        }

        try {
            File reportOutputDirectory = new File(this.getReportOutputDirectory(), this.name);
            if (!reportOutputDirectory.exists()) {
                getLog().info("Creating report output directory: " + reportOutputDirectory);

                boolean isDirCreated = reportOutputDirectory.mkdirs();
                if (!isDirCreated) {
                    throw new RuntimeException("Could not create directory: " + reportOutputDirectory);
                }
            } else {
                getLog().info("Report output directory already exists: " + reportOutputDirectory);
            }

            getLog().info("Copying directory " + this.sourceDirectory + " to " + reportOutputDirectory);
            FileUtils.copyDirectory(this.sourceDirectory, reportOutputDirectory);

            FolderReportRenderer renderer = new FolderReportRenderer(sink, this.displayName, this.getOutputName());
            renderer.render();

        } catch (Exception e) {
            throw new MavenReportException("An error has occurred in " + this.getName(Locale.ENGLISH) + " " +
                    "report generation.", e);
        }
    }

    public String getOutputName() {
        return name;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getName(Locale locale) {
        return displayName;
    }

    public String getDescription(Locale locale) {
        return description;
    }

    public void setReportOutputDirectory(File file) {
        this.outputDirectory = file;
    }

    public File getReportOutputDirectory() {
        return this.outputDirectory;
    }

    public boolean isExternalReport() {
        return false;
    }

    public boolean canGenerateReport() {
        return true;
    }
}
