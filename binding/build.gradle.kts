import org.gradle.internal.os.OperatingSystem
import java.io.BufferedReader
import java.io.InputStreamReader

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
    compilerArgs.addAll(getFlagsJNI())
    compilerArgs.addAll(getFlagsPCAP())
}

tasks.withType<LinkSharedLibrary>().configureEach {
    linkerArgs.addAll(getLibrariesPCAP())
}

private fun getFlagsJNI(): List<String> {
    val javaHome = System.getenv("JAVA_HOME") ?: System.getProperty("java.home")
    val os = OperatingSystem.current()
    val path = when {
        os.isMacOsX -> "darwin"
        os.isLinux -> "linux"
        os.isWindows -> "win32"
        else -> error("Unsupported OS: $os")
    }
    return listOf("-I$javaHome/include", "-I$javaHome/include/$path")
}

private fun getFlagsPCAP(): List<String> {
    return executePCAP("--cflags")
}

private fun getLibrariesPCAP(): List<String> {
    return executePCAP("--libs")
}

private fun executePCAP(vararg args: String): List<String> {
    val os = OperatingSystem.current()
    val cmd = when {
        os.isMacOsX -> "pcap-config"
        os.isLinux -> "pkg-config"
        os.isWindows -> error("Not Implemented Yet!")
        else -> error("Unsupported OS: $os")
    }
    val command = listOf(cmd) + args
    val process = ProcessBuilder(command).redirectErrorStream(true).start()
    val output = BufferedReader(InputStreamReader(process.inputStream)).use { it.readText().trim() }
    val code = process.waitFor()
    if (code != 0)
        error("finding (n)pcap failed (exit $code). Output:\n$output")
    if (output.isBlank())
        error("(n)pcap produced no output, please ensure you properly followed the installation guide.")
    return output.split(Regex("\\s+")).filter { it.isNotBlank() }
}