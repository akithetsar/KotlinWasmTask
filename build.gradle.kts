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

//Note for task examiner: In IntelliJ terminal you will see gradle exectution percentage, if you wwant to change that you can use open terminal for your OS(The only tested one is Windows right now)
tasks.register<Exec>("runWasm") {
    dependsOn("compileDevelopmentExecutableKotlinWasmWasi")

    doFirst {
        val wasmFile = file(
            "build/compileSync/wasmWasi/main/developmentExecutable/kotlin/${project.name}.wasm"
        )

        commandLine(
            "wasmtime",
            "run",
            "--wasm", "gc=yes",
            "--wasm", "function-references=yes",
            "--wasm", "exceptions=yes",
            wasmFile.absolutePath
        )
    }

    standardInput = System.`in`
}

tasks.register("runNode") {
    dependsOn("compileDevelopmentExecutableKotlinWasmWasi")

    doLast {
        val mjsPath = layout.buildDirectory.file(
            "compileSync\\wasmWasi\\main\\developmentExecutable\\kotlin\\KotlinWasmTask.mjs"
        ).get().asFile.absolutePath

        exec {
            commandLine(
                "node",
                "--experimental-wasm-exnref",
                "--experimental-wasi-unstable-preview1",
                mjsPath
            )
            standardInput = System.`in`
        }

    }

}


//Note: Only tested on Windows, if it can't open the respective console for your OS, use the above that run embedded in intelliJ Terminal
fun currentOs(): String = System.getProperty("os.name").lowercase()

tasks.register("runWasmTerminal") {
    dependsOn("compileDevelopmentExecutableKotlinWasmWasi")

    doLast {
        val wasmPath = layout.buildDirectory.file(
            "compileSync/wasmWasi/main/developmentExecutable/kotlin/${project.name}.wasm"
        ).get().asFile.absolutePath

        val os = currentOs()

        exec {
            when {
                os.contains("win") -> {
                    commandLine(
                        "cmd", "/c", "start", "cmd", "/k",
                        "wasmtime run -W function-references,gc,exceptions $wasmPath"
                    )
                }

                os.contains("mac") -> {
                    commandLine(
                        "osascript",
                        "-e",
                        """tell application "Terminal" to do script "wasmtime run -W function-references,gc,exceptions $wasmPath""""
                    )
                }

                else -> { // Linux
                    commandLine(
                        "x-terminal-emulator",
                        "-e",
                        "wasmtime run -W function-references,gc,exceptions $wasmPath"
                    )
                }
            }
        }
    }
}

tasks.register("runNodeTerminal") {
    dependsOn("compileDevelopmentExecutableKotlinWasmWasi")

    doLast {
        val mjsPath = layout.buildDirectory.file(
            "compileSync/wasmWasi/main/developmentExecutable/kotlin/${project.name}.mjs"
        ).get().asFile.absolutePath

        val os = currentOs()

        exec {
            when {
                os.contains("win") -> {
                    commandLine(
                        "cmd", "/c", "start", "cmd", "/k",
                        "node --experimental-wasm-exnref --experimental-wasi-unstable-preview1 $mjsPath"
                    )
                }

                os.contains("mac") -> {
                    commandLine(
                        "osascript",
                        "-e",
                        """tell application "Terminal" to do script "node --experimental-wasm-exnref --experimental-wasi-unstable-preview1 $mjsPath""""
                    )
                }

                else -> { // Linux
                    commandLine(
                        "x-terminal-emulator",
                        "-e",
                        "node --experimental-wasm-exnref --experimental-wasi-unstable-preview1 $mjsPath"
                    )
                }
            }
        }
    }
}




