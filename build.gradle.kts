import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.7.10"

    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.gitlab.arturbosch.detekt") version "1.22.0"
}

group = "jp.hisano"
version = "1.0-SNAPSHOT"
val shadedPackage = "jp.hisano.cozy.jdbc.mysql.shaded"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.jasync-sql:jasync-mysql:2.1.16")

    testImplementation(kotlin("test"))

    testImplementation(platform("org.testcontainers:testcontainers-bom:1.17.6"))
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mysql")

    testImplementation("mysql:mysql-connector-java:8.0.32")
}

detekt {
    buildUponDefaultConfig = true
    config = files("detekt.yml")
}

tasks.test {
    useJUnitPlatform()

    systemProperty("TARGET_VERSION", System.getProperty("TARGET_VERSION"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.create<ConfigureShadowRelocation>("relocateShadowJar") {
    target = tasks["shadowJar"] as ShadowJar
    prefix = shadedPackage
}

tasks.named<ShadowJar>("shadowJar").configure {
    dependsOn(tasks["relocateShadowJar"])
}
