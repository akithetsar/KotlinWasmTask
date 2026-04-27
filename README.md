# Kotlin WASM (WASI) Echo Loop — IntelliJ Setup Guide

This repository is a solution for **Task #1**:

- Uses the **Kotlin/Wasm** compiler to produce a **WASI** `.wasm` binary.
- On execution, enters a loop reading lines from **stdin**.
- For every line received, prints it to stdout prefixed with:

```
Wasm received: <input>
```

It also includes Gradle automation tasks:
- `runWasm` (runs the produced `.wasm` via **Wasmtime**)
- `runNode` (runs the generated `.mjs` via **Node.js** + experimental WASI flags)
- Optional “open a new terminal window” variants for convenience (Windows tested).

Additionally, an **IntelliJ IDEA Run Configuration is provided** so the project can be run from the IDE without manually setting up a Gradle run target.

---

## Prerequisites

### 1) Java / JDK (IMPORTANT)
This project is intended to run on **JDK 17 only**.

- Use **JDK 17** (not 18/19/20/21).
- If you use another JDK version, the build and/or execution may fail.

### 2) IntelliJ IDEA
- IntelliJ IDEA Community or Ultimate.

### 3) Wasmtime (recommended runtime)
Install Wasmtime and ensure `wasmtime` is available on your `PATH`.

Verify:
```bash
wasmtime --version
```

### 4) (Optional) Node.js
If you want to run via Node instead of Wasmtime:
Verify:
```bash
node --version
```

---

## Clone and Open in IntelliJ (from scratch)

### Step 1: Clone the repo
In IntelliJ:
1. **File → New → Project from Version Control…**
2. Paste the repository URL
3. Click **Clone**

### Step 2: Trust the project
When IntelliJ asks, click **Trust Project** (required for Gradle import / running tasks).

### Step 3: Configure IntelliJ to use JDK 17
1. **File → Project Structure…**
2. Set **Project SDK** to **JDK 17**
   - If it’s not available: **Add SDK → JDK** and select your JDK 17 installation directory.
3. Also verify Gradle uses JDK 17:
   - **Settings → Build, Execution, Deployment → Build Tools → Gradle**
     - **Gradle JVM**: **JDK 17**

---

## Running in IntelliJ

### Option A: Use the included Run Configuration (recommended in IntelliJ)
This repository includes an IntelliJ IDEA **Run Configuration**. After importing Gradle and setting **JDK 17**, you can run it via:

- The Run/Debug dropdown at the top-right of IntelliJ
- Select the provided configuration (for example: `runWasm`)
- Click **Run**


### Option B: Run via IntelliJ Terminal (best for stdin)
```bash
./gradlew runWasm
```

Then type lines and press Enter. To exit, send EOF:
- macOS/Linux: `Ctrl+D`
- Windows: `Ctrl+Z` then Enter

---

## Output Artifacts / Paths

The tasks assume Kotlin/Wasm outputs are located here:

### WASM binary
```
build/compileSync/wasmWasi/main/developmentExecutable/kotlin/<projectName>.wasm
```

### Node launcher module
```
build/compileSync/wasmWasi/main/developmentExecutable/kotlin/KotlinWasmTask.mjs
```

---

## Task Reference

**Objective:** Create a Kotlin project that compiles to a WebAssembly binary targeting WASI.

**This solution provides:**
- Kotlin Multiplatform `wasmWasi` target
- Interactive stdin loop + echoed stdout output
- Gradle task automation (`runWasm`, `runNode`, and terminal variants)
- A provided IntelliJ Run Configuration
- **JDK 17-only** setup