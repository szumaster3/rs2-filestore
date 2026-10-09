package com.alex.tools.dump

import com.alex.Cache
import com.alex.loaders.SpriteDefinition
import com.alex.loaders.SpriteDefinition.Companion.decode
import com.alex.store.Index
import java.io.File
import java.nio.ByteBuffer
import javax.imageio.ImageIO

object SpriteDumper {

    private val spriteCache: HashMap<Int, SpriteDefinition> = HashMap()
    private val spriteIndex: Index
        get() = Cache.getStore()!!.indexes[8]
    private fun getArchive(archiveId: Int): SpriteDefinition? {
        spriteCache[archiveId]?.let { return it }

        val data = spriteIndex.getFile(archiveId, 0) ?: return null
        val archive = decode(ByteBuffer.wrap(data)) ?: return null

        spriteCache[archiveId] = archive
        return archive
    }

    @JvmStatic
    fun dump() {
        val outDir = File("dumps/sprites/")
        if (!outDir.exists()) outDir.mkdirs()

        val spriteIndex = spriteIndex
        val totalArchives = spriteIndex.lastArchiveId + 1

        for (archiveId in 0 until totalArchives) {
            val archive = getArchive(archiveId) ?: continue
            val frames = archive.size()

            for (frame in 0 until frames) {
                val sprite = archive.getSprite(frame) ?: continue
                val fileName = "sprite_${archiveId}_$frame.png"
                ImageIO.write(sprite, "PNG", File(outDir, fileName))
            }
        }
        println("Sprites exported.")
    }

}