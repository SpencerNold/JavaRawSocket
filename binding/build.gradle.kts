import org.gradle.internal.os.OperatingSystem

plugins {
    `cpp-library`
}

library {
    linkage.set(listOf(Linkage.SHARED))
    targetMachines.add(machines.linux.x86_64)
    targetMachines.add(machines.windows.x86_64)
    targetMachines.add(machines.macOS.architecture("arm64"))
    targetMachines.add(machines.macOS.architecture("x86_64"))
}

tasks.withType<CppCompile>().configureEach {
    val javaHome = System.getenv("JAVA_HOME") ?: System.getProperty("java.home")
    includes.from(file("$javaHome/include"))
    val os = OperatingSystem.current()
    val path = when {
        os.isMacOsX -> "darwin"
        os.isLinux -> "linux"
        os.isWindows -> "win32"
        else -> error("Unsupported OS: $os")
    }
    includes.from(file("$javaHome/include/$path"))
}