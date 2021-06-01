dependencies {
    implementation(Dependencies.springBootWeb)

    testImplementation(Dependencies.springBootTest)
    testImplementation(Dependencies.junitPlatformLauncher)
    testImplementation(project(":library"))
}

tasks.withType<Test> {
    useJUnitPlatform()

    systemProperty("junit.jupiter.extensions.autodetection.enabled", "true")
    systemProperty("de.adesso.junitinsights.enabled", "true")
    systemProperty("de.adesso.junitinsights.reportpath", "reports/")
}