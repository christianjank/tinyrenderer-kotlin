import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.3.21"
    id("org.openjfx.javafxplugin").version("0.0.7")
}

group = "io.github.christianjank"
version = "1.0.0-SNAPSHOT"

application {
    mainClassName = "io.github.christianjank.Renderer"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

val javafxModules = arrayOf("controls", "fxml", "graphics")

javafx {
    modules = javafxModules.map { "javafx.$it" }
}


defaultTasks("run")

repositories {
    mavenCentral()
    jcenter()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}

tasks.wrapper {
    gradleVersion = "5.2.1"
    distributionType = Wrapper.DistributionType.ALL
}
