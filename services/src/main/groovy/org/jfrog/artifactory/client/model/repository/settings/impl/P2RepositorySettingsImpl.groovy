package org.jfrog.artifactory.client.model.repository.settings.impl

import groovy.transform.EqualsAndHashCode
import org.jfrog.artifactory.client.model.PackageType
import org.jfrog.artifactory.client.model.impl.PackageTypeImpl
import org.jfrog.artifactory.client.model.repository.settings.P2RepositorySettings

/**
 * GroovyBean implementation of the {@link P2RepositorySettings}
 * 
 * @author Ivan Vasylivskyi (ivanvas@jfrog.com)
 */
@EqualsAndHashCode
class P2RepositorySettingsImpl extends MavenRepositorySettingsImpl implements P2RepositorySettings {

    @Override
    PackageType getPackageType() {
        return PackageTypeImpl.p2
    }
}
