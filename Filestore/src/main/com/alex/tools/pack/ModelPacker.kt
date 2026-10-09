package com.alex.tools.pack

import com.alex.Cache
import com.alex.store.Index
import java.io.File

object ModelPacker {

    private val modelIndex: Index
        get() = Cache.getStore()!!.indexes[7]

    @JvmStatic
    fun add()
    {
        val baseFolder = File("../Assets/models/")
        if (!baseFolder.exists() || !baseFolder.isDirectory) return

        val dataList = baseFolder.walk()
            .filter { it.isFile && it.extension.lowercase() == "dat" }
            .mapNotNull {
                val id = it.nameWithoutExtension.toIntOrNull() ?: return@mapNotNull null
                id to it.readBytes()
            }
            .sortedBy { it.first }
            .toList()

        for ((id, data) in dataList)
        {
            try {
                modelIndex.putFile(id, 0, 2, data, null, false, false, -1, -1)
                println("Packed model $id")
            } catch (ex: Exception) {
                println("Failed model $id: ${ex.message}")
            }
        }

        modelIndex.rewriteTable()
        modelIndex.resetCachedFiles()
    }

}