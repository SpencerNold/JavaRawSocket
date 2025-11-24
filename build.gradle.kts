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
	jvmArgs = listOf("-Djava.library.path=binding/build/lib/main/debug")
}