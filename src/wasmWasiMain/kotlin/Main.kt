@file:OptIn(
    kotlin.wasm.ExperimentalWasmInterop::class,
    kotlin.wasm.unsafe.UnsafeWasmMemoryApi::class
)

import kotlin.wasm.WasmImport
import kotlin.wasm.WasmExport
import kotlin.wasm.unsafe.Pointer
import kotlin.wasm.unsafe.withScopedMemoryAllocator

private const val STDOUT = 1

@WasmImport("wasi_snapshot_preview1", "fd_write")
private external fun wasiFdWrite(fd: Int, iovs: Int, iovsLen: Int, nwritten: Int): Int

fun wasiPrintLn(text : String) = withScopedMemoryAllocator { allocator ->

    val bytes = text.encodeToByteArray()

    val buffer = allocator.allocate(bytes.size)

    for (i in bytes.indices) {
        Pointer(buffer.address + i.toUInt()).storeByte(bytes[i])
    }

    val iov = allocator.allocate(8)

    val bufAddr = buffer.address.toUInt()

    Pointer(iov.address).storeInt(bufAddr.toInt())
    Pointer(iov.address + 4u).storeInt(bytes.size)

    val written = allocator.allocate(4)

    wasiFdWrite(
        1,
        iov.address.toInt(),
        1,
        written.address.toInt()
    )

}

fun main(){
    wasiPrintLn("Hello world!");
}

@WasmExport
fun dummy() {}