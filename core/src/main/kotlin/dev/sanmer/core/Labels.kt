package dev.sanmer.core

object Labels {
    const val COMPOSE_CONFIG_HASH = "com.docker.compose.config-hash"
    const val COMPOSE_CONTAINER_NUMBER = "com.docker.compose.container-number"
    const val COMPOSE_DEPENDS_ON = "com.docker.compose.depends_on"
    const val COMPOSE_IMAGE = "com.docker.compose.image"
    const val COMPOSE_PROJECT = "com.docker.compose.project"
    const val COMPOSE_PROJECT_CONFIG_FILES = "com.docker.compose.project.config_files"
    const val COMPOSE_PROJECT_WORKING_DIR = "com.docker.compose.project.working_dir"
    const val COMPOSE_SERVICE = "com.docker.compose.service"
    const val COMPOSE_VERSION = "com.docker.compose.version"

    const val OCI_CREATED = "org.opencontainers.image.created"
    const val OCI_AUTHORS = "org.opencontainers.image.authors"
    const val OCI_URL = "org.opencontainers.image.url"
    const val OCI_DOCUMENTATION = "org.opencontainers.image.documentation"
    const val OCI_SOURCE = "org.opencontainers.image.source"
    const val OCI_VERSION = "org.opencontainers.image.version"
    const val OCI_REVISION = "org.opencontainers.image.revision"
    const val OCI_VENDOR = "org.opencontainers.image.vendor"
    const val OCI_LICENSES = "org.opencontainers.image.licenses"
    const val OCI_NO_LICENSES = "NOASSERTION"
    const val OCI_REF_NAME = "org.opencontainers.image.ref.name"
    const val OCI_TITLE = "org.opencontainers.image.title"
    const val OCI_DESCRIPTION = "org.opencontainers.image.description"
    const val OCI_BASE_DIGEST = "org.opencontainers.image.base.digest"
    const val OCI_BASE_NAME = "org.opencontainers.image.base.name"

    const val LS_BUILD_DATE = "org.label-schema.build-date"
    const val LS_URL = "org.label-schema.url"
    const val LS_VCS_URL = "org.label-schema.vcs-url"
    const val LS_VERSIOIN = "org.label-schema.version"
    const val LS_VCS_REF = "org.label-schema.vcs-ref"
    const val LS_VENDOR = "org.label-schema.vendor"
    const val LS_NAME = "org.label-schema.name"
    const val LS_DESCRIPTION = "org.label-schema.description"
    const val LS_USAGE = "org.label-schema.usage"

    const val LEGACY_MAINTAINER = "maintainer"
    const val LEGACY_VERSION = "version"
    const val LEGACY_DESCRIPTION = "description"
    const val LEGACY_BUILD_DATE = "build-date"

    private val labels = hashMapOf(
        OCI_CREATED to R.string.org_opencontainers_image_created,
        OCI_AUTHORS to R.string.org_opencontainers_image_authors,
        OCI_URL to R.string.org_opencontainers_image_url,
        OCI_DOCUMENTATION to R.string.org_opencontainers_image_documentation,
        OCI_SOURCE to R.string.org_opencontainers_image_source,
        OCI_VERSION to R.string.org_opencontainers_image_version,
        OCI_REVISION to R.string.org_opencontainers_image_revision,
        OCI_VENDOR to R.string.org_opencontainers_image_vendor,
        OCI_LICENSES to R.string.org_opencontainers_image_licenses,
        OCI_REF_NAME to R.string.org_opencontainers_image_ref_name,
        OCI_TITLE to R.string.org_opencontainers_image_title,
        OCI_DESCRIPTION to R.string.org_opencontainers_image_description,
        OCI_BASE_DIGEST to R.string.org_opencontainers_image_base_digest,
        OCI_BASE_NAME to R.string.org_opencontainers_image_base_name,

        LS_BUILD_DATE to R.string.org_label_schema_build_date,
        LS_URL to R.string.org_label_schema_url,
        LS_VCS_URL to R.string.org_label_schema_vcs_url,
        LS_VERSIOIN to R.string.org_label_schema_version,
        LS_VCS_REF to R.string.org_label_schema_vcs_ref,
        LS_VENDOR to R.string.org_label_schema_vendor,
        LS_NAME to R.string.org_label_schema_name,
        LS_DESCRIPTION to R.string.org_label_schema_description,
        LS_USAGE to R.string.org_label_schema_usage,

        LEGACY_MAINTAINER to R.string.maintainer,
        LEGACY_VERSION to R.string.version,
        LEGACY_DESCRIPTION to R.string.description,
        LEGACY_BUILD_DATE to R.string.build_date
    )

    operator fun invoke(name: String) = labels[name]
}