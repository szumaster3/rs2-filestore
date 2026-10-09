package com.alex.tools.pack

import com.alex.Cache
import com.alex.loaders.SpriteDefinition
import com.alex.loaders.SpriteDefinition.Companion.decode
import com.alex.store.Index
import java.awt.image.BufferedImage
import java.io.File
import java.nio.ByteBuffer
import javax.imageio.ImageIO

object SpritePacker {
    private val spriteCache: HashMap<Int, SpriteDefinition> = HashMap()

    private val numSprites: Int
        get() = spriteIndex.lastArchiveId + 1

    private fun getArchive(archiveId: Int): SpriteDefinition? {
        spriteCache[archiveId]?.let { return it }

        val data = spriteIndex.getFile(archiveId, 0) ?: return null
        val archive = decode(ByteBuffer.wrap(data)) ?: return null

        spriteCache[archiveId] = archive
        return archive
    }

    @JvmStatic
    fun getSprite(archiveId: Int): BufferedImage? = getSprite(archiveId, 0)

    private fun getSprite(archiveId: Int, frame: Int): BufferedImage? = getArchive(archiveId)?.getSprite(frame)

    fun getRaw(archiveId: Int): ByteBuffer? {
        val data = spriteIndex.getFile(archiveId, 0) ?: return null
        return ByteBuffer.wrap(data)
    }

    fun put(archiveId: Int, data: ByteArray): Boolean {
        val success = spriteIndex.putFile(archiveId, 0, data)
        return success
    }

    fun add() {
        val folder = File("../Assets/sprites/")
        if (!folder.exists() || !folder.isDirectory) return

        val files = folder.walkTopDown()
            .filter { it.isFile && it.extension.lowercase() == "png" }
            .toList()
            .sortedBy { it.name }

        if (files.isEmpty()) return

        val addedArchives = mutableListOf<Pair<String, Int>>()
        var archiveId = numSprites

        files.forEach { file ->
            try {
                val image: BufferedImage = ImageIO.read(file) ?: return@forEach
                val archive = SpriteDefinition(image.width, image.height)
                archive.setFrame(0, image)
                val encoded = archive.encode()
                val success = put(archiveId, encoded.array())
                if (success) addedArchives.add(file.name to archiveId)
                archiveId++
            } catch (ex: Exception) {
                println("Failed to add sprite: ${file.name} -> ${ex.message}")
            }
        }

        if (addedArchives.isNotEmpty()) {
            addedArchives.forEach { (name, id) ->
                println("Packed sprite $id")
            }
        }
    }

    fun packLogo() {
        val folder = File("../Assets/logo/")
        if (!folder.exists()) return
        val image = folder.listFiles()
            ?.firstOrNull { it.extension.equals("png", true) }
            ?.let { ImageIO.read(it) }
            ?: return
        val archiveId = spriteIndex.getArchiveId("logo")
        val sprite = SpriteDefinition(image.width, image.height)
        sprite.setFrame(0, image)
        val data = sprite.encode()
        spriteIndex.putFile(archiveId, 0, data.array())
        println("Packed logo $archiveId")
    }

    private val spriteIndex: Index
        get() = Cache.getStore()!!.indexes[8]
}
