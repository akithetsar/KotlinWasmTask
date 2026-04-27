@file:OptIn(
    kotlin.wasm.ExperimentalWasmInterop::class,
    kotlin.wasm.unsafe.UnsafeWasmMemoryApi::class
)

import kotlin.wasm.WasmImport
import kotlin.wasm.WasmExport
import kotlin.wasm.unsafe.Pointer
import kotlin.wasm.unsafe.withScopedMemoryAllocator

private const val STDIN = 0
private const val STDOUT = 1

@WasmImport("wasi_snapshot_preview1", "fd_write")
private external fun wasiFdWrite(fd: Int, iovs: Int, iovsLen: Int, nwritten: Int): Int

@WasmImport("wasi_snapshot_preview1", "fd_read")
private external fun wasiFdRead(fd: Int, iovs: Int, iovsLen: Int, nread: Int): Int


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
        STDOUT,
        iov.address.toInt(),
        1,
        written.address.toInt()
    )

}

fun wasiReadLN() : String = withScopedMemoryAllocator { allocator ->
    val bufferSize = 1024

    val buffer = allocator.allocate(bufferSize)

    val iov = allocator.allocate(8)
    Pointer(iov.address).storeInt(buffer.address.toInt())
    Pointer(iov.address + 4u).storeInt(bufferSize)

    val nread = allocator.allocate(4)

    val result = wasiFdRead(
        STDIN,
        iov.address.toInt(),
        1,
        nread.address.toInt()
    )

    val size = Pointer(nread.address).loadInt()


    val bytes = ByteArray(size)
    for (i in 0 until size) {
        bytes[i] = Pointer(buffer.address + i.toUInt()).loadByte()
    }

    val input = bytes.decodeToString()

    return input
}
fun main() {

    while(true) {
        val input = wasiReadLN()
        val prefixed = "Wasm received: $input"
        wasiPrintLn(prefixed);
    }
}

@WasmExport
fun dummy(){}
