import org.springframework.boot.gradle.tasks.bundling.BootJar

val jar: Jar by tasks
val bootJar: BootJar by tasks

bootJar.enabled = false

jar.apply {
    enabled = true
    exclude("main.js")
}

apply {
    plugin("maven-publish")
}

dependencies {
    compileOnly(Dependencies.junitApi)
    compileOnly(Dependencies.junitPlatformLauncher)
    compileOnly(Dependencies.springBootStarter)
    compileOnly(Dependencies.springTest)

    testImplementation(Dependencies.springBootTest)
    testImplementation(Dependencies.junitPlatformLauncher)
    testImplementation(Dependencies.mockk)
}

val npmInstall = tasks.create("npmInstall", Exec::class) {
    inputs.files(
        "src/frontend/package-lock.json",
        "src/frontend/webpack.config.js"
    )

    outputs.dir("src/frontend/node_modules/")

    workingDir("src/frontend/")
    if (System.getProperty("os.name").toLowerCase().contains("windows")) {
        commandLine("cmd", "/c", "npm install")
    } else {
        commandLine("sh", "-c", "npm install")
    }
}

tasks.create("npmRunBuild", Exec::class) {
    dependsOn(npmInstall)

    inputs.dir("src/frontend/src")
    outputs.file("src/main/resources/index.html")
    workingDir("src/frontend")

    workingDir("src/frontend/")
    if (System.getProperty("os.name").toLowerCase().contains("windows")) {
        commandLine("cmd", "/c", "npm run build")
    } else {
        commandLine("sh", "-c", "npm run build")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()

    systemProperty("de.adesso.junitinsights.enabled", "false")
}