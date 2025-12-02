import org.gradle.internal.os.OperatingSystem

plugins {
	java
	application
}

java {
	toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

application {
	mainClass = "me.spencernold.jrs.Main"
}

tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
}

tasks.named<JavaExec>("run") {
	dependsOn(":binding:build")
	jvmArgs = listOf("-Djava.library.path=${getNativeBuildDirectory()}")
}

tasks.register<Copy>("copyJarBuildOutput") {
	dependsOn(":build")
	from("${file(layout.buildDirectory).absolutePath}/libs")
	into("prepared")
}

tasks.register<Copy>("copyNativeBuildOutput") {
	dependsOn(":binding:build")
	from(getNativeBuildDirectory())
	into("prepared/natives")
}

tasks.register("prepare") {
	dependsOn(":copyJarBuildOutput", ":copyNativeBuildOutput")
}

private fun getNativeBuildDirectory(): String {
	val os = OperatingSystem.current()
	val arch = System.getProperty("os.arch")
	val osPart = when {
		os.isMacOsX -> "macos"
		os.isLinux -> "linux"
		os.isWindows -> "windows"
		else -> error("Unsupported OS: $os")
	}
	val archPart = when (arch.lowercase()) {
		"aarch64", "arm64" -> "arm64"
		"x86_64", "amd64" -> "x86_64"
		else -> error("Unsupported architecture: $arch")
	}
	return "binding/build/lib/main/debug/$osPart/$archPart"
}