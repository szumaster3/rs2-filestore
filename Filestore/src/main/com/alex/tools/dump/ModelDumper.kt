package com.alex.tools.dump

import com.alex.Cache
import com.alex.store.Index
import java.io.File

object ModelDumper {
    private val modelIndex: Index
        get() = Cache.getStore()!!.indexes[7]
    @JvmStatic
    fun dump() {
        val outDir = File("dumps/models/")
        if (!outDir.exists()) outDir.mkdirs()

        val modelIndex = modelIndex
        val totalArchives = modelIndex.lastArchiveId + 1

        for (archiveId in 0 until totalArchives) {
            val data = modelIndex.getFile(archiveId, 0) ?: continue
            val fileName = "$archiveId.dat"
            File(outDir, fileName).writeBytes(data)
        }

        println("Models exported.")
    }
}