@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.detekt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.dataframe)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(libs.google.api)
            implementation(libs.google.auth)
            implementation(libs.google.youtube)
            implementation(libs.bundles.dataframe)

            implementation(libs.compose.navigation)
            implementation(libs.bundles.logback)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }

    jvmToolchain(jdkVersion = 21)

    jvm {
        dependencies {
            detektPlugins(rootProject.libs.detekt.formatting)
            detektPlugins(rootProject.libs.detekt.libraries)
        }

        detekt {
            autoCorrect = true
            config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
            source.setFrom("$rootDir/composeApp/")
        }
    }
}

compose.desktop {
    application {
        mainClass = "org.malv.descuentados.MainKt"

        buildTypes {
            release {
                proguard {
                    configurationFiles.from(project.file("proguard-rules.pro"))
                }
            }
        }

        nativeDistributions {
            modules("jdk.httpserver")
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "descuentados"
            packageVersion = "1.0.0"
        }
    }
}

tasks.withType<Detekt> {
    autoCorrect = true
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    baseline.set(file("$rootDir/config/detekt/detekt-baseline.xml"))
}

dependencies {
    add("kspJvm", "org.jetbrains.kotlinx.dataframe:symbol-processor-all:${libs.versions.dataframe.get()}")
}

tasks.withType<Jar>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    exclude(
        "META-INF/DEPENDENCIES",
        "META-INF/LICENSE",
        "META-INF/LICENSE.txt",
        "META-INF/NOTICE",
        "META-INF/NOTICE.txt",
        "META-INF/MANIFEST.MF",
        "META-INF/*.SF",
        "META-INF/*.DSA",
        "META-INF/*.RSA",
        "module-info.class",
        "arrow-git.properties"
    )
    filesMatching("META-INF/services/**") {
        filter {
            it.lineSequence().distinct().joinToString("\n")
        }
    }
}
