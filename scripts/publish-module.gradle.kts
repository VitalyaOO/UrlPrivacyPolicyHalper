apply(plugin = "maven-publish")
apply(plugin = "signing")

val PUBLISH_GROUP_ID = "io.github.vitalyaoo"
val PUBLISH_VERSION = "1.0.0"
val PUBLISH_ARTIFACT_ID = "UrlPrivacyPolicyHalper"
val PUBLISH_DESCRIPTION = ""
val PUBLISH_URL = "https://github.com/VitalyaOO/UrlPrivacyPolicyHalper"
val PUBLISH_LICENSE_NAME = "Apache License"
val PUBLISH_LICENSE_URL = "https://github.com/VitalyaOO/UrlPrivacyPolicyHalper/blob/main/LICENSE"
val PUBLISH_DEVELOPER_ID = "Vitalya"
val PUBLISH_DEVELOPER_NAME = "Vitalii Zeus"
val PUBLISH_DEVELOPER_EMAIL = "vitalii.devop.wwl@gmail.com"
val PUBLISH_SCM_CONNECTION = "scm:git:github.com/VitaliiTilner/PrivacyPolicyHelper.git"
val PUBLISH_SCM_DEVELOPER_CONNECTION = "scm:git:ssh://github.com/VitaliiTilner/PrivacyPolicyHelper.git"
val PUBLISH_SCM_URL = "https://github.com/VitaliiTilner/PrivacyPolicyHelper/tree/main"

group = PUBLISH_GROUP_ID
version = PUBLISH_VERSION

afterEvaluate {
    configure<PublishingExtension> {
        publications.create<MavenPublication>("privacy-policy-halper") {
            groupId = PUBLISH_GROUP_ID
            artifactId = PUBLISH_ARTIFACT_ID
            version = PUBLISH_VERSION

            afterEvaluate {
                println(components)
                from(components.getByName("release"))
            }

            pom {
                name.set(PUBLISH_ARTIFACT_ID)
                description.set(PUBLISH_DESCRIPTION)
                url.set(PUBLISH_URL)
                licenses {
                    license {
                        name.set(PUBLISH_LICENSE_NAME)
                        url.set(PUBLISH_LICENSE_URL)
                    }
                }
                developers {
                    developer {
                        id.set(PUBLISH_DEVELOPER_ID)
                        name.set(PUBLISH_DEVELOPER_NAME)
                        email.set(PUBLISH_DEVELOPER_EMAIL)
                    }
                }
                scm {
                    connection.set(PUBLISH_SCM_CONNECTION)
                    developerConnection.set(PUBLISH_SCM_DEVELOPER_CONNECTION)
                    url.set(PUBLISH_SCM_URL)
                }
            }
        }
        repositories {
            mavenLocal()
            mavenCentral()
        }

        configure<SigningExtension> {
            sign(publications["privacy-policy-halper"])
        }
    }
}
