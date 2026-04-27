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





