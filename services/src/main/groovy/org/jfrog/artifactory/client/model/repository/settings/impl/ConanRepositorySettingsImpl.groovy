package org.jfrog.artifactory.client.model.repository.settings.impl

import org.jfrog.artifactory.client.model.PackageType
import org.jfrog.artifactory.client.model.repository.settings.ConanRepositorySettings

class ConanRepositorySettingsImpl implements ConanRepositorySettings {

    @Override
    PackageType getPackageType() {
        return PackageType.conan
    }
}
