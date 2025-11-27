import org.gradle.internal.os.OperatingSystem

plugins {
	java
	application
}

java {
	toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

application {
	mainClass = "me.spencernold.jrs.Main"
}

tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
}

tasks.named<JavaExec>("run") {
	dependsOn(":binding:build")
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
	jvmArgs = listOf("-Djava.library.path=binding/build/lib/main/debug/$osPart/$archPart")
}