package org.jfrog.artifactory.client.aql;

import org.testng.annotations.Test;

import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import static org.jfrog.artifactory.client.aql.AqlItem.aqlItem;
import static org.jfrog.artifactory.client.aql.AqlItem.or;


public class AqlQueryBuilderTest {

    @Test
    public void include() {
        String result = new AqlQueryBuilder().include("size", "name", "repo").build();

        assertThat(result, notNullValue());
        assertThat(result, is("items.find().include(\"size\",\"name\",\"repo\")"));
    }

    @Test
    public void asc() {
        String result = new AqlQueryBuilder().asc("repo", "name").build();

        assertThat(result, notNullValue());
        assertThat(result, is("items.find().sort({\"$asc\":[\"repo\",\"name\"]})"));
    }

    @Test
    public void desc() {
        String result = new AqlQueryBuilder().desc("repo", "name").build();

        assertThat(result, notNullValue());
        assertThat(result, is("items.find().sort({\"$desc\":[\"repo\",\"name\"]})"));
    }

    @Test
    public void addElement() {
        String result = new AqlQueryBuilder()
            .item(aqlItem("type", "file"))
            .build();

        assertThat(result, notNullValue());
        assertThat(result, is("items.find({\"type\":\"file\"})"));
    }

    @Test
    public void addSeveralElements() {
        String result = new AqlQueryBuilder()
            .item(aqlItem("type", "file"))
            .item(aqlItem("name", "file.xml"))
            .item(aqlItem("repo", "my-repo"))
            .build();

        assertThat(result, notNullValue());
        assertThat(result, is("items.find({\"type\":\"file\",\"name\":\"file.xml\",\"repo\":\"my-repo\"})"));
    }

    @Test
    public void addElements() {
        String result = new AqlQueryBuilder()
            .elements(
                aqlItem("type", "file"),
                aqlItem("name", "file.xml"),
                aqlItem("repo", "my-repo"))
            .build();

        assertThat(result, notNullValue());
        assertTrue(result.contains("\"type\":\"file\""));
        assertTrue(result.contains("\"name\":\"file.xml\""));
        assertTrue(result.contains("\"repo\":\"my-repo\""));
    }

    @Test
    public void addArray() {
        String result = new AqlQueryBuilder()
            .array("list", aqlItem("repo", "myrepo1"), aqlItem("repo", "myrepo2"))
            .build();

        assertThat(result, notNullValue());
        assertThat(result, is("items.find({\"list\":[{\"repo\":\"myrepo1\"},{\"repo\":\"myrepo2\"}]})"));
    }

    @Test
    public void and() {
        String result = new AqlQueryBuilder()
            .and(aqlItem("repo", "myrepo1"), aqlItem("repo", "myrepo2"))
            .build();

        assertThat(result, notNullValue());
        assertEquals(result, "items.find("
                                   + "{\"$and\":["
                                   +  "{\"repo\":\"myrepo1\"},"
                                   +  "{\"repo\":\"myrepo2\"}"
                                   + "]})");
    }

    @Test
    public void andCollection() {
        String result = new AqlQueryBuilder()
            .and(Arrays.asList(aqlItem("repo", "myrepo1"), aqlItem("repo", "myrepo2")))
            .build();

        assertThat(result, notNullValue());
        assertEquals(result, "items.find("
                                   + "{\"$and\":["
                                   +  "{\"repo\":\"myrepo1\"},"
                                   +  "{\"repo\":\"myrepo2\"}"
                                   + "]})");
    }

    @Test
    public void andAddArray() {
        AqlItem[] items = new AqlItem[]{aqlItem("repo", "myrepo1"), aqlItem("repo", "myrepo2")};
        String result = new AqlQueryBuilder()
            .and(items)
            .build();

        assertThat(result, notNullValue());
        assertEquals(result, "items.find("
                                   + "{\"$and\":["
                                   +  "{\"repo\":\"myrepo1\"},"
                                   +  "{\"repo\":\"myrepo2\"}"
                                   + "]})");
    }

    @Test
    public void orTest() {
        String result = new AqlQueryBuilder()
            .or(aqlItem("repo", "myrepo1"), aqlItem("repo", "myrepo2"))
            .build();

        assertThat(result, notNullValue());
        assertThat(result, is("items.find("
                                   + "{\"$or\":["
                                   +  "{\"repo\":\"myrepo1\"},"
                                   +  "{\"repo\":\"myrepo2\"}"
                                   + "]})"));
    }

    @Test
    public void addNestedFilters() {
        final String result = new AqlQueryBuilder()
            .and(
                or(
                    aqlItem("repo", "myrepo1"),
                    aqlItem("repo", "myrepo2"),
                    aqlItem("repo", "myrepo3")
                ),
                or(
                    aqlItem("name", "maven-metadata1.xml"),
                    aqlItem("name", "maven-metadata2.xml")
                )
            )
            .build();

        assertThat(result, notNullValue());
        assertThat(result, is("items.find("
                                   + "{\"$and\":["
                                   +  "{\"$or\":["
                                   +    "{\"repo\":\"myrepo1\"},"
                                   +    "{\"repo\":\"myrepo2\"},"
                                   +    "{\"repo\":\"myrepo3\"}"
                                   +  "]},"
                                   +  "{\"$or\":["
                                   +    "{\"name\":\"maven-metadata1.xml\"},"
                                   +    "{\"name\":\"maven-metadata2.xml\"}"
                                   +  "]}"
                                   + "]}"
                                   + ")"));
    }

    @Test
    public void variousElements() {
        final String result = new AqlQueryBuilder()
            .and(
                or(
                    aqlItem("repo", "myrepo")
                ),
                or(
                    aqlItem("name", "metadata.xml")
                ),
                aqlItem("type", "file")
            )
            .item(aqlItem("property", "value"))
            .include("name", "repo")
            .asc("name", "repo")
            .build();

        assertThat(result, notNullValue());
        assertThat(result, is( "items.find("
                                   + "{\"$and\":["
                                   +  "{\"$or\":["
                                   +    "{\"repo\":\"myrepo\"}"
                                   +  "]},"
                                   +  "{\"$or\":["
                                   +    "{\"name\":\"metadata.xml\"}"
                                   +  "]},"
                                   +  "{\"type\":\"file\"}"
                                   + "],"
                                   + "\"property\":\"value\""
                                   + "})"
                                   + ".include(\"name\",\"repo\")"
                                   + ".sort({\"$asc\":[\"name\",\"repo\"]})"));
    }

    @Test
    public void emptyBuilder() {
        String result = new AqlQueryBuilder().build();
        assertThat(result, notNullValue());
        assertThat(result, is("items.find()"));
    }

    @Test
    public void emptyInclude() {
        String result = new AqlQueryBuilder().include().build();
        assertThat(result, notNullValue());
        assertThat(result, is("items.find()"));
    }

    @Test
    public void emptySort() {
        String result = new AqlQueryBuilder().asc().build();
        assertThat(result, notNullValue());
        assertThat(result, is("items.find()"));
    }

    @Test
    public void emptyElements() {
        String result = new AqlQueryBuilder().elements().build();
        assertThat(result, notNullValue());
        assertThat(result, is("items.find()"));
    }

    @Test
    public void transitive() {
        String result = new AqlQueryBuilder().include("size", "name", "repo").transitive().build();

        assertThat(result, notNullValue());
        assertThat(result, is("items.find().include(\"size\",\"name\",\"repo\").transitive()"));
    }
}