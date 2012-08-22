package org.artifactory.client;

import groovyx.net.http.HttpResponseException;
import org.apache.commons.lang.StringUtils;
import org.artifactory.client.model.File;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.testng.Assert.*;

/**
 * @author jbaruch
 * @since 03/08/12
 */
public class DownloadUploadTest extends ArtifactoryTestBase {

    @Test(groups = "uploadBasics", dependsOnGroups = "repositoryBasics")
    public void testUploadWithSingleProperty() throws IOException {
        InputStream inputStream = this.getClass().getResourceAsStream("/sample.txt");
        assertNotNull(inputStream);
        File deployed = artifactory.repository(NEW_LOCAL).upload(PATH, inputStream).withProperty("color", "red")
                .withProperty("color", "red").doUpload();
        assertNotNull(deployed);
        assertEquals(deployed.getRepo(), NEW_LOCAL);
        assertEquals(deployed.getPath(), "/" + PATH);
        assertEquals(deployed.getCreatedBy(), username);
        assertEquals(deployed.getDownloadUri(), url + "/" + NEW_LOCAL + "/" + PATH);
        assertEquals(deployed.getSize(), 3044);
        assertTrue(curlAndStrip("api/storage/" + NEW_LOCAL + "/" + PATH + "?properties").contains("\"color\":[\"red\"]"));
    }

    @Test(groups = "uploadBasics", dependsOnMethods = "testUploadWithSingleProperty")//to spare all the checks
    public void testUploadWithMultipleProperties() throws IOException {
        artifactory.repository(NEW_LOCAL).upload(PATH, this.getClass().getResourceAsStream("/sample.txt"))
                .withProperty("colors", "red")
                .withProperty("build", 28)
                .withProperty("released", false).doUpload();
        assertTrue(curlAndStrip("api/storage/" + NEW_LOCAL + "/" + PATH + "?properties").contains("{\"build\":[\"28\"],\"colors\":[\"red\"],\"released\":[\"false\"]}"));
    }

    //TODO (jb) enable once RTFACT-5126 is fixed
    @Test(enabled = false, dependsOnMethods = "testUploadWithSingleProperty")
    public void testUploadWithMultiplePropertyValues() throws IOException {
        artifactory.repository(NEW_LOCAL).upload(PATH, this.getClass().getResourceAsStream("/sample.txt"))
                .withProperty("colors", "red", "green", "blue")
                .withProperty("build", 28)
                .withProperty("released", false).doUpload();
        assertTrue(curl("api/storage/" + NEW_LOCAL + "/" + PATH + "?properties")
                .contains("{\"build\":[\"28\"],\"colors\":[\"red\",\"green\",\"blue\"],\"released\":[\"false\"]}"));
    }

    @Test(dependsOnMethods = "testUploadWithSingleProperty")
    public void testDownloadWithoutProperties() throws IOException {
        InputStream inputStream = artifactory.repository(NEW_LOCAL).download(PATH).doDownload();
        String actual = textFrom(inputStream);
        assertEquals(actual, textFrom(this.getClass().getResourceAsStream("/sample.txt")));
    }

    @Test(dependsOnMethods = "testUploadWithMultipleProperties")
    public void testDownloadWithMatchingNonMandatoryProperties() throws IOException {
        //property matches
        InputStream inputStream =
                artifactory.repository(NEW_LOCAL).download(PATH).withProperty("colors", "red")
                        .doDownload();
        assertEquals(textFrom(inputStream), textFrom(this.getClass().getResourceAsStream("/sample.txt")));
    }

    @Test(dependsOnMethods = "testUploadWithMultipleProperties")
    public void testDownloadWithNonExistingNonMandatoryProperties() throws IOException {
        //property doesn't exist
        InputStream inputStream =
                artifactory.repository(NEW_LOCAL).download(PATH).withProperty("foo", "bar").doDownload();
        assertEquals(textFrom(inputStream), textFrom(this.getClass().getResourceAsStream("/sample.txt")));
    }

    @Test(dependsOnMethods = "testUploadWithMultipleProperties", expectedExceptions = HttpResponseException.class)
    public void testDownloadWithWrongNonMandatoryProperties() throws IOException {
        //property doesn't match, will fail
        artifactory.repository(NEW_LOCAL).download(PATH).withProperty("colors", "black").doDownload();
    }

    @Test(dependsOnMethods = "testUploadWithMultipleProperties")
    public void testDownloadWithMatchingMandatoryProperties() throws IOException {
        //property matches
        InputStream inputStream =
                artifactory.repository(NEW_LOCAL).download(PATH).withMandatoryProperty("colors", "red").doDownload();
        assertEquals(textFrom(inputStream), textFrom(this.getClass().getResourceAsStream("/sample.txt")));
    }

    @Test(dependsOnMethods = "testUploadWithMultipleProperties", expectedExceptions = HttpResponseException.class)
    public void testDownloadWithNonExistingMandatoryProperties() throws IOException {
        //property doesn't exist, will fail
        artifactory.repository(NEW_LOCAL).download(PATH).withMandatoryProperty("foo", "bar").doDownload();
    }

    @Test(dependsOnMethods = "testUploadWithMultipleProperties", expectedExceptions = HttpResponseException.class)
    public void testDownloadWithWrongMandatoryProperties() throws IOException {
        //property doesn't match, will fail
        artifactory.repository(NEW_LOCAL).download(PATH).withMandatoryProperty("colors", "black").doDownload();
    }

    @Test(dependsOnMethods = "testUploadWithMultipleProperties")
    public void testDownloadWithMandatoryAndNonMandatoryProperties() throws IOException {
        InputStream inputStream =
                artifactory.repository(NEW_LOCAL).download(PATH).withProperty("released", false)
                        .withProperty("foo", "bar").withMandatoryProperty("colors", "red").doDownload();
        assertEquals(textFrom(inputStream), textFrom(this.getClass().getResourceAsStream("/sample.txt")));
    }
}
