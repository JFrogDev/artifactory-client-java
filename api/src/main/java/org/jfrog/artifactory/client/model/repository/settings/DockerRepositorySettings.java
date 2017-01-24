package org.jfrog.artifactory.client.model.repository.settings;

import org.jfrog.artifactory.client.model.repository.settings.docker.DockerApiVersion;

/**
 * @author Ivan Vasylivskyi (ivanvas@jfrog.com)
 */
public interface DockerRepositorySettings extends RepositorySettings {

    // ** local ** //

    DockerApiVersion getDockerApiVersion();
    Integer getMaxUniqueTags();

    // ** remote ** //

    Boolean getEnableTokenAuthentication();

    Boolean getListRemoteFolderItems();
}
