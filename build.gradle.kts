plugins {
    kotlin("multiplatform") version "2.3.21"
}

repositories {
    mavenCentral()
}
kotlin {
    wasmWasi {
        nodejs()
        binaries.executable()
    }
}

tasks.register("runWasmTerminal") {
    dependsOn("compileDevelopmentExecutableKotlinWasmWasi")

    doLast {
        val wasmPath = layout.buildDirectory.file(
            "compileSync\\wasmWasi\\main\\developmentExecutable\\kotlin\\KotlinWasmTask.wasm"
        ).get().asFile.absolutePath

        exec {
            commandLine(
                "cmd",
                "/c",
                "start",
                "wasmtime",
                "run",
                "-W", "function-references,gc,exceptions",
                wasmPath
            )
        }
    }
}

