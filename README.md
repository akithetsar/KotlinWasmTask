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

---

## Prerequisites

Install the following before opening the project:

### 1) Java / JDK
- **JDK 17+** recommended (JDK 21 is also fine).

### 2) IntelliJ IDEA
- IntelliJ IDEA Community or Ultimate.

### 3) Wasmtime (recommended runtime)
Install Wasmtime and ensure `wasmtime` is available on your `PATH`.

**Windows (common options):**
- `winget install BytecodeAlliance.Wasmtime`
- or `choco install wasmtime`

Verify:
```bash
wasmtime --version
```

### 4) (Optional) Node.js
If you want to run via Node instead of Wasmtime:
- Install Node.js (prefer a recent version).
Verify:
```bash
node --version
```

---

## Clone and Open in IntelliJ (from scratch)

### Step 1: Clone the repo
In IntelliJ:
1. **File → New → Project from Version Control…**
2. Paste the repository URL (or clone via Git externally)
3. Click **Clone**

### Step 2: Trust the project
When IntelliJ asks, click **Trust Project** (required for Gradle import / running tasks).

### Step 3: Configure the JDK in IntelliJ
1. **File → Project Structure…**
2. Under **Project SDK**, select **JDK 17+**
   - If you don’t have one listed: click **Add SDK → JDK** and point to your JDK install folder.

### Step 4: Import Gradle
IntelliJ should auto-import Gradle. If not:
1. Open the **Gradle** tool window (right side)
2. Click the **Refresh** icon to sync

**Recommended IntelliJ Gradle settings:**
- **Settings → Build, Execution, Deployment → Build Tools → Gradle**
  - *Use Gradle from*: **Gradle Wrapper**
  - *Gradle JVM*: **JDK 17+**

---

## Project Configuration (What matters in `build.gradle.kts`)

This project uses Kotlin Multiplatform with a WASI target:

- `kotlin("multiplatform") version "2.3.21"`
- `wasmWasi { nodejs(); binaries.executable() }`

Gradle tasks are included to compile and run the WASM output using either **Wasmtime** or **Node**.

---

## Running in IntelliJ

### Option A (Recommended): Run with Wasmtime (stdin works)
Run from IntelliJ Terminal (bottom tool window → **Terminal**):

```bash
./gradlew runWasm
```

Then type lines and press Enter. Example:

```text
hello
Wasm received: hello
```

To exit, send EOF:
- macOS/Linux: `Ctrl+D`
- Windows: `Ctrl+Z` then Enter

> Note: In the IntelliJ terminal you may see Gradle progress output while the program is running.  
> If you want a cleaner dedicated console window, use `runWasmTerminal` (Windows tested).

### Option B: Run with Node (experimental WASI flags)
```bash
./gradlew runNode
```

This runs the generated `.mjs` wrapper under:

- `node --experimental-wasm-exnref --experimental-wasi-unstable-preview1 ...`

---

## Running From IntelliJ Gradle Tool Window (GUI)

1. Open **Gradle** tool window
2. Navigate to: `Tasks → other` (or wherever IntelliJ lists custom tasks)
3. Double-click one of:
   - `runWasm`
   - `runNode`
   - `runWasmTerminal` (opens a separate terminal window; Windows tested)
   - `runNodeTerminal`

**Important:** For interactive stdin, the most reliable experience is usually:
- IntelliJ **Terminal** + `./gradlew runWasm`
- or the OS terminal (outside IntelliJ) + `./gradlew runWasm`

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

(These paths match the current Gradle script.)

---

## Troubleshooting

### `wasmtime: command not found` / not recognized
- Wasmtime is not installed or not on your `PATH`.
- Reinstall (see prerequisites) and restart IntelliJ/terminal.

### Stdin doesn’t work / no prompt appears
- Prefer running `./gradlew runWasm` inside IntelliJ’s **Terminal** (not the Run tool window).
- Or run from your system terminal (PowerShell/cmd/bash).

### Node run fails with WASI / experimental flag errors
- Node’s WASI support and flags can differ by version.
- Prefer **Wasmtime** for the cleanest WASI runtime behavior.

---

## Task Reference

**Objective:** Create a Kotlin project that compiles to a WebAssembly binary targeting WASI.

**This solution provides:**
- Kotlin Multiplatform `wasmWasi` target
- Interactive stdin loop + echoed stdout output
- Gradle task automation (`runWasm`, `runNode`, and terminal variants)