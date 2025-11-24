plugins {
    `cpp-library`
}

library {
    linkage.set(listOf(Linkage.SHARED))
    //targetMachines.add(machines.linux.x86_64)
    //targetMachines.add(machines.windows.x86_64)
    targetMachines.add(machines.macOS.architecture("arm64"))
}

tasks.withType<CppCompile>().configureEach {
    includes.from(getIncludePaths().map(::File))
}

private fun getIncludePaths(): List<String> {
    val javaHome = System.getenv("JAVA_HOME") ?: System.getProperty("java.home")
    val list = mutableListOf(file("$javaHome/include").absolutePath)
    addOperatingSystemSpecific(list,
        "$javaHome/include/win32",
        "$javaHome/include/darwin",
        "$javaHome/include/linux"
    )
    return list
}

private fun <T> addOperatingSystemSpecific(list: MutableList<T>, win: T, mac: T, nux: T) {
    val os = System.getProperty("os.name").lowercase()
    if (os.contains("win")) {
        list.add(win)
    } else if (os.contains("mac")) {
        list.add(mac)
    } else if (os.contains("nux")) {
        list.add(nux)
    }
}