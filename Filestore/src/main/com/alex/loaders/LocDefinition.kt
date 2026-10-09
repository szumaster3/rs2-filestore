package com.alex.loaders

import com.alex.io.InputStream
import com.alex.io.OutputStream
import com.alex.store.Store
import com.alex.utils.Utils
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.lang.reflect.Modifier

class LocDefinition {
    var id = -1
    var name: String? = null
    var desc: String? = null
    var sizeX = 1
    var sizeY = 1
    var delayShading: Boolean = false
    @JvmField var interactable = false
    var blockProjectile = true
    var blocksProjectile = false
    var models: IntArray? = null
    var modelTypes: IntArray? = null

    var actions = arrayOfNulls<String>(5)
    var animations: IntArray? = null
    var alternateModelIds: IntArray? = null

    var recolourOriginal: IntArray? = null
    var recolourModified: IntArray? = null
    var textures: IntArray? = null
    var colourPalette: IntArray? = null

    var modelSizeX = 128
    var modelSizeY = 128
    var modelSizeZ = 128

    var offsetX = 0
    var offsetY = 0
    var offsetZ = 0
    var offsetMultiplier = 0

    var blocksLand = false
    var blocksSky = true
    var solid = 1
    var ignoreOnRoute = false
    var mirrored = false
    var castsShadow = true
    var animateImmediately = true
    var isSolidFlag = true
    var isMembers = false
    var adjustMapSceneRotation = false
    var hasAnimation = false

    var contouredGround: Byte = 0
    var cullingType = 1
    var brightness = 0
    var contrast = 0
    var mapSceneRotation = 0
    var mapSceneId = -1
    var configId = 0

    var ambientSoundId = -1
    var ambientSoundMinDelay = 0
    var ambientSoundMaxDelay = 0
    var animationId = 0

    var unknownAnimationField = 0
    var unknownField1 = 0
    var hideMinimap = false
    var mapFunction = -1
    var supportItems = 0
    var animationFrameCount = 0
    var animationDuration = 0
    var isInteractable = false
    var blockFlag = 0
    var parameters: Map<Int, Any>? = null
    var transforms: IntArray? = null

    var anInt4425: Int = -1
    var anInt4431: Int = -1

    var anIntArray380: IntArray? = null
    var loaded = false

    fun load(stream: InputStream) {
        while (true) {
            val opcode = stream.readUnsignedByte()
            if (opcode == 0) break
            decode(opcode, stream)
        }
        loaded = true
    }

    public fun decode(opcode: Int, buffer: InputStream) {
        when (opcode) {

            1, 5 -> {
                val count = buffer.readUnsignedByte()
                if (count > 0) {
                    models = IntArray(count)
                    modelTypes = if (opcode == 1) IntArray(count) else null

                    repeat(count) { i ->
                        models!![i] = buffer.readUnsignedShort()
                        if (opcode == 1) {
                            modelTypes!![i] = buffer.readUnsignedByte()
                        }
                    }
                }
            }

            2 -> name = buffer.readString()

            14 -> sizeX = buffer.readUnsignedByte()
            15 -> sizeY = buffer.readUnsignedByte()

            17 -> {
                blocksSky = false
                solid = 0
            }

            18 -> blockProjectile = false

            19 -> interactable = buffer.readUnsignedByte() == 1

            21 -> contouredGround = 1
            22 -> delayShading = true
            23 -> cullingType = 1

            24 -> {
                val id = buffer.readUnsignedShort()
                animations = if (id == 65535) null else intArrayOf(id)
            }

            27 -> solid = 1
            28 -> offsetMultiplier = buffer.readUnsignedByte()
            29 -> brightness = buffer.readByte()

            in 30..34 -> {
                val str = buffer.readString()
                actions[opcode - 30] = str.takeIf { it.lowercase() != "hidden" }
            }

            39 -> contrast = buffer.readUnsignedByte() * 5

            40 -> readColours(buffer)
            41 -> readTextures(buffer)
            42 -> readColourPalette(buffer)

            60 -> mapFunction = buffer.readUnsignedShort()

            62 -> mirrored = true
            64 -> castsShadow = false

            65 -> modelSizeX = buffer.readUnsignedShort()
            66 -> modelSizeZ = buffer.readUnsignedShort()
            67 -> modelSizeY = buffer.readUnsignedShort()

            69 -> blockFlag = buffer.readUnsignedByte()

            70 -> offsetX = buffer.readUnsignedShort() shl 2
            71 -> offsetZ = buffer.readUnsignedShort() shl 2
            72 -> offsetY = buffer.readUnsignedShort() shl 2

            73 -> blocksLand = true
            74 -> ignoreOnRoute = true

            75 -> supportItems = buffer.readUnsignedByte()

            // Transforms
            77, 92 -> {

                anInt4425 = buffer.readUnsignedShort().let { if (it == 65535) -1 else it }
                anInt4431 = buffer.readUnsignedShort().let { if (it == 65535) -1 else it }

                var fallback = -1

                if (opcode == 92) {
                    fallback = buffer.readUnsignedShort().let { if (it == 65535) -1 else it }
                }

                val count = buffer.readUnsignedByte()

                val arr = IntArray(count + 2)

                for (i in 0..count) {
                    val v = buffer.readUnsignedShort().let { if (it == 65535) -1 else it }
                    arr[i] = v
                }

                arr[count + 1] = fallback

                anIntArray380 = arr
            }

            78 -> {
                ambientSoundId = buffer.readUnsignedShort()
                ambientSoundMinDelay = buffer.readUnsignedByte()
            }

            79 -> {
                ambientSoundMaxDelay = buffer.readUnsignedShort().let { if (it == 65535) -1 else it }
                animationId = buffer.readUnsignedShort().let { if (it == 65535) -1 else it }
                ambientSoundMinDelay = buffer.readUnsignedByte()

                val length = buffer.readUnsignedByte()
                alternateModelIds = IntArray(length) {
                    buffer.readUnsignedShort().let { if (it == 65535) -1 else it }
                }
            }

            81 -> {
                contouredGround = 2
                configId = buffer.readUnsignedByte() shl 8
            }

            82 -> hideMinimap = true

            88 -> isSolidFlag = false
            89 -> animateImmediately = false
            90 -> isInteractable = true
            91 -> isMembers = true

            93 -> {
                contouredGround = 3
                configId = buffer.readUnsignedShort()
            }

            94 -> contouredGround = 4
            95 -> contouredGround = 5

            96 -> blocksProjectile = true
            97 -> adjustMapSceneRotation = true
            98 -> hasAnimation = true

            99 -> {
                animationFrameCount = buffer.readUnsignedByte()
                animationDuration = buffer.readUnsignedShort()
            }

            100 -> {
                unknownAnimationField = buffer.readUnsignedByte()
                unknownField1 = buffer.readUnsignedShort()
            }

            101 -> mapSceneRotation = buffer.readUnsignedByte()
            102 -> mapSceneId = buffer.readUnsignedShort()

            249 -> readParameters(buffer)

            else -> Unit
        }
    }

    fun encode(stream: OutputStream) {

        fun write(op: Int, block: OutputStream.() -> Unit) {
            stream.writeByte(op)
            stream.block()
        }

        models?.let {
            if (modelTypes != null) {
                write(1) {
                    writeByte(it.size)
                    for (i in it.indices) {
                        writeUnsignedShort(it[i])
                        writeByte(modelTypes!![i])
                    }
                }
            } else {
                write(5) {
                    writeByte(it.size)
                    it.forEach { id -> writeUnsignedShort(id) }
                }
            }
        }

        name?.let { write(2) { writeString(it) } }

        if (sizeX != 1) write(14) { writeByte(sizeX) }
        if (sizeY != 1) write(15) { writeByte(sizeY) }

        if (!blocksSky) {
            write(17) {}
        }

        if (!blockProjectile) write(18) {}

        if (interactable) write(19) { writeByte(1) }

        if (contouredGround.toInt() == 1) write(21) {}
        if (delayShading) write(22) {}
        if (cullingType == 1) write(23) {}

        animations?.let {
            val valToWrite = if (it[0] == -1) 65535 else it[0]
            write(24) { writeUnsignedShort(valToWrite) }
        }

        if (solid == 1) write(27) {}

        if (offsetMultiplier != 0) write(28) { writeByte(offsetMultiplier) }
        if (brightness != 0) write(29) { writeByte(brightness) }

        actions.forEachIndexed { i, a ->
            a?.let {
                write(30 + i) { writeString(it) }
            }
        }

        if (contrast != 0) write(39) { writeByte(contrast / 5) }

        recolourOriginal?.let {
            write(40) {
                writeByte(it.size)
                for (i in it.indices) {
                    writeUnsignedShort(it[i])
                    writeUnsignedShort(recolourModified!![i])
                }
            }
        }

        textures?.let {
            write(41) {
                writeByte(it.size / 2)
                for (i in it.indices step 2) {
                    writeUnsignedShort(it[i])
                    writeUnsignedShort(it[i + 1])
                }
            }
        }

        if (mapFunction != -1) write(60) { writeUnsignedShort(mapFunction) }

        if (mirrored) write(62) {}
        if (!castsShadow) write(64) {}

        if (modelSizeX != 128) write(65) { writeUnsignedShort(modelSizeX) }
        if (modelSizeZ != 128) write(66) { writeUnsignedShort(modelSizeZ) }
        if (modelSizeY != 128) write(67) { writeUnsignedShort(modelSizeY) }

        if (blockFlag != 0) write(69) { writeByte(blockFlag) }

        if (offsetX != 0) write(70) { writeUnsignedShort(offsetX shr 2) }
        if (offsetZ != 0) write(71) { writeUnsignedShort(offsetZ shr 2) }
        if (offsetY != 0) write(72) { writeUnsignedShort(offsetY shr 2) }

        if (blocksLand) write(73) {}
        if (ignoreOnRoute) write(74) {}

        if (supportItems != 0) write(75) { writeByte(supportItems) }

        transforms?.let {

            write(77) {
                writeUnsignedShort(anInt4425.takeIf { it != -1 } ?: 65535)
                writeUnsignedShort(anInt4431.takeIf { it != -1 } ?: 65535)

                writeByte(it.size)

                it.forEach { id ->
                    writeUnsignedShort(id.takeIf { it != -1 } ?: 65535)
                }
            }
        }

        if (ambientSoundId != -1) {
            write(78) {
                writeUnsignedShort(ambientSoundId)
                writeByte(ambientSoundMinDelay)
            }
        }

        alternateModelIds?.let {
            write(79) {
                writeUnsignedShort(ambientSoundMaxDelay)
                writeUnsignedShort(animationId)
                writeByte(ambientSoundMinDelay)

                writeByte(it.size)
                it.forEach { id -> writeUnsignedShort(id) }
            }
        }

        if (contouredGround.toInt() == 2) {
            write(81) {
                writeByte(configId / 256)
            }
        }

        if (hideMinimap) write(82) {}

        if (!castsShadow) write(64) {}
        if (!animateImmediately) write(89) {}
        if (isInteractable) write(90) {}
        if (isMembers) write(91) {}

        if (contouredGround.toInt() == 3) {
            write(93) { writeUnsignedShort(configId) }
        }

        if (contouredGround.toInt() == 4) write(94) {}
        if (contouredGround.toInt() == 5) write(95) {}

        if (blocksProjectile) write(96) {}

        if (adjustMapSceneRotation) write(97) {}
        if (hasAnimation) write(98) {}

        if (animationFrameCount != 0) {
            write(99) {
                writeByte(animationFrameCount)
                writeUnsignedShort(animationDuration)
            }
        }

        val hasAnimation = unknownAnimationField != 255
        val hasField = unknownField1 != 65535

        if (hasAnimation || hasField) {
            write(100) {
                writeByte(if (hasAnimation) unknownAnimationField else 0)
                writeUnsignedShort(if (hasField) unknownField1 else 65535)
            }
        }

        if (mapSceneRotation != 0) write(101) { writeByte(mapSceneRotation) }
        if (mapSceneId != -1) write(102) { writeUnsignedShort(mapSceneId) }

        parameters?.let {
            write(249) {
                writeByte(it.size)
                for ((key, value) in it) {
                    if (value is String) {
                        writeByte(1)
                        writeMedium(key)
                        writeString(value)
                    } else {
                        writeByte(0)
                        writeMedium(key)
                        writeInt(value as Int)
                    }
                }
            }
        }

        stream.writeByte(0)
    }

    private fun readColours(buffer: InputStream) {
        val length = buffer.readUnsignedByte()
        recolourOriginal = IntArray(length)
        recolourModified = IntArray(length)
        repeat(length) { i ->
            recolourOriginal!![i] = buffer.readUnsignedShort()
            recolourModified!![i] = buffer.readUnsignedShort()
        }
    }

    private fun readTextures(buffer: InputStream) {
        val length = buffer.readUnsignedByte()
        textures = IntArray(length * 2)
        repeat(length) { i ->
            textures!![i * 2] = buffer.readUnsignedShort()
            textures!![i * 2 + 1] = buffer.readUnsignedShort()
        }
    }

    private fun readColourPalette(buffer: InputStream) {
        val length = buffer.readUnsignedByte()
        colourPalette = IntArray(length * 2)
        repeat(length) { i ->
            colourPalette!![i * 2] = buffer.readUnsignedShort()
            colourPalette!![i * 2 + 1] = buffer.readUnsignedShort()
        }
    }

    private fun readTransforms(buffer: InputStream, var1: Boolean) {
        configId = buffer.readUnsignedShort()
        val len = if (var1) buffer.readUnsignedShort() else buffer.readUnsignedByte()
        alternateModelIds = IntArray(len)
        repeat(len) { i -> alternateModelIds!![i] = buffer.readUnsignedShort() }
    }

    private fun readParameters(buffer: InputStream) {
        val length = buffer.readUnsignedByte()
        val params = mutableMapOf<Int, Any>()
        repeat(length) {
            val isString = buffer.readUnsignedByte() == 1
            val key = buffer.readMedium()
            val value = if (isString) buffer.readJagString() else buffer.readInt()
            params[key] = value
        }
    }


    companion object {
        fun load(cache: Store, id: Int): LocDefinition? {
            val loc = LocDefinition()
            loc.id = id
            val data: ByteArray? = cache.indexes[16].getFile(loc.getArchiveId(), loc.getFileId())
            if (data != null) {
                try {
                    loc.load(InputStream(data))
                    loc.loaded = true
                    return loc
                } catch (e: RuntimeException) {
                    e.printStackTrace()
                }
            }
            return null
        }

        fun print(cache: Store, outputFile: String) {
            try {
                val file = File(outputFile)
                file.parentFile?.takeIf { !it.exists() }?.mkdirs()
                if (!file.exists()) file.createNewFile()

                PrintWriter(BufferedWriter(FileWriter(file))).use { writer ->
                    val size = Utils.getObjectDefinitionsSize(cache)

                    for (id in 0 until size) {
                        val loc = load(cache, id) ?: continue
                        if (!loc.loaded) continue

                        val fields = loc::class.java.declaredFields
                        var wroteSomething = false

                        val buffer = StringBuilder()
                        buffer.appendLine("========== OBJECT ${loc.id} ==========")

                        for (field in fields) {
                            if (Modifier.isStatic(field.modifiers)) continue

                            field.isAccessible = true
                            val value = try { field.get(loc) } catch (e: Exception) { null }

                            if (value == null) continue
                            if (value is Int && value == 0) continue
                            if (value is Int && value == -1) continue
                            if (value is Boolean && !value) continue
                            if (value is String && value.isEmpty()) continue
                            if (value is IntArray && value.isEmpty()) continue
                            if (value is ByteArray && value.isEmpty()) continue
                            if (value is Array<*> && value.isEmpty()) continue
                            if (value is Map<*, *> && value.isEmpty()) continue

                            val valueString = when (value) {
                                is IntArray -> value.joinToString(prefix = "[", postfix = "]")
                                is Array<*> -> value.joinToString(prefix = "[", postfix = "]")
                                is ByteArray -> value.joinToString(prefix = "[", postfix = "]")
                                is Map<*, *> -> value.entries.joinToString(prefix = "{", postfix = "}") { "${it.key}=${it.value}" }
                                else -> value.toString()
                            }

                            buffer.appendLine("${field.name} = $valueString")
                            wroteSomething = true
                        }

                        if (wroteSomething) {
                            writer.println(buffer.toString())
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getArchiveId(): Int = this.id shr 8
    private fun getFileId(): Int = this.id and 0xFF
}