package com.nnoncarey.sha.maven.plugins;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertNotNull;

/**
 *
 */
public class FolderReportMojoTest {
    @Rule
    public MojoRule rule = new MojoRule() {
        @Override
        protected void before() throws Throwable {
        }

        @Override
        protected void after() {
        }
    };

    @Test
    public void testSomething() throws Exception {
        File pom = new File(getBasedir(), "src/test/resources/test-projects/test1.pom.xml");

        Mojo mojo = this.rule.lookupMojo("folder-report", pom);
        mojo.execute();

        URL reportURL = getGeneratedReport("test1", "my-report", "yep.html").toURI().toURL();
        assertNotNull(reportURL);
    }

    protected File getGeneratedReport(String projectName, String reportName, String fileName) throws IOException {
        String outputDirectory = getBasedir() + "/target/test-harness/" + projectName + "/" + reportName;

        File report = new File(outputDirectory, fileName);
        if (!report.exists()) {
            throw new IOException("File not found. Attempted: " + report);
        }

        return report;
    }

    private File getBasedir() {
        String basedirPath = System.getProperty( "basedir" );

        if (basedirPath == null) {
            basedirPath = new File( "" ).getAbsolutePath();
        }

        return new File(basedirPath);
    }
}
