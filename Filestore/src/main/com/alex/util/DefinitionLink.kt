package com.alex.util

import com.alex.io.InputStream
import com.alex.loaders.LocDefinition
import com.alex.loaders.NpcDefinition
import com.alex.loaders.SeqDefinition
import com.alex.store.Store
import java.io.File

class DefinitionLink(private val store: Store)
{

    private val animToNpc = mutableMapOf<Int, MutableList<Int>>()
    private val animToLoc = mutableMapOf<Int, MutableList<Int>>()
    private val animToSeq = mutableMapOf<Int, MutableList<Int>>()

    private val npcNames = mutableMapOf<Int, String>()
    private val locNames = mutableMapOf<Int, String>()

    fun run(output: File)
    {
        loadSeq()
        loadLoc()
        loadNpc()
        print(output)
    }

    private fun loadSeq()
    {
        val index = 20
        val archives = store.indexes[index].lastArchiveId

        for (archive in 0..archives)
        {
            val files = store.indexes[index].getLastFileId(archive)

            for (file in 0..files)
            {
                val id = (archive shl 7) or file

                val seq = SeqDefinition(id)
                if (!seq.load()) continue

                seq.frames?.forEach { frame ->
                    val animId = frame shr 16
                    animToSeq.getOrPut(animId) { mutableListOf() }.add(id)
                }
            }
        }
    }

    private fun loadLoc()
    {
        val index = 16
        val locIndex = store.indexes[index]
        val archiveCount = locIndex.lastArchiveId + 1

        for (archive in 0 until archiveCount)
        {
            val fileCount = locIndex.getLastFileId(archive) + 1

            for (file in 0 until fileCount)
            {
                val data = locIndex.getFile(archive, file, null) ?: continue

                val loc = LocDefinition()
                loc.id = (archive shl 8) or file
                loc.load(InputStream(data))

                locNames[loc.id] = loc.name ?: ""

                loc.animations?.forEach { animToLoc.getOrPut(it) { mutableListOf() }.add(loc.id) }
            }
        }
    }

    private fun loadNpc()
    {
        val index = 18
        val max = store.indexes[index].getLastFileId(0)

        for (id in 0..max)
        {
            val npc = NpcDefinition(store, id)
            try {
                npc.loadNPCDefinition(store)
            } catch (e: Exception) {
                continue
            }

            npcNames[id] = npc.name ?: ""

            npc.modelIndices?.forEach { animToNpc.getOrPut(it) { mutableListOf() }.add(id) }
        }
    }

    private fun print(output: File) {
        output.parentFile?.mkdirs();output.bufferedWriter().use { out ->

            val keys = (animToNpc.keys + animToLoc.keys + animToSeq.keys)
                .distinct()
                .sorted()

            for (key in keys) {

                out.appendLine("Base: $key")

                animToNpc[key]?.let {
                    out.appendLine(
                        "Linked NPCs: " +
                                it.distinct().joinToString(", ") { id ->
                                    "${npcNames[id] ?: ""} [$id]"
                                }
                    )
                }

                animToLoc[key]?.let {
                    out.appendLine(
                        "Linked Objects: " +
                                it.distinct().joinToString(", ") { id ->
                                    "${locNames[id] ?: ""} [$id]"
                                }
                    )
                }

                animToSeq[key]?.let {
                    out.appendLine(
                        "Linked Sequences: " +
                                it.distinct().joinToString(", ")
                    )
                }

                out.appendLine()
            }
        }
    }
}
