package com.alex.tools.pack

import com.alex.Cache
import com.alex.loaders.NpcDefinition

class NpcPacker private constructor(private val startId: Int = 0) {

    private var currentId = startId
    private val tasks = mutableListOf<Task>()
    private val copiedNpcs = mutableListOf<NpcDefinition>()

    private data class Task(
        val sourceId: Int,
        val modifier: (NpcDefinition.() -> Unit)?,
        val isEdit: Boolean = false
    )

    companion object {
        fun create(): NpcPacker = NpcPacker()
    }

    fun startAt(id: Int): NpcPacker {
        currentId = id
        return this
    }

    fun addNpc(modifier: NpcDefinition.() -> Unit): NpcPacker {
        tasks.add(Task(-1, modifier))
        return this
    }

    fun addNpcs(vararg modifiers: NpcDefinition.() -> Unit): NpcPacker {
        modifiers.forEach { addNpc(it) }
        return this
    }

    fun copyNpc(sourceId: Int, modifier: NpcDefinition.() -> Unit = {}): NpcPacker {
        tasks.add(Task(sourceId, modifier, isEdit = false))
        return this
    }

    fun editNpc(npcId: Int, modifier: NpcDefinition.() -> Unit = {}): NpcPacker {
        tasks.add(Task(npcId, modifier, isEdit = true))
        return this
    }

    fun copyRange(fromId: Int, toId: Int, modifier: NpcDefinition.() -> Unit = {}): NpcPacker {
        for (id in fromId..toId) {
            copyNpc(id, modifier)
        }
        return this
    }

    fun save(): List<NpcDefinition> {
        val store = Cache.getStore()
            ?: throw IllegalStateException("Cache store not loaded!")

        copiedNpcs.clear()

        for (task in tasks) {
            val npc = when {
                task.sourceId >= 0 && !task.isEdit -> {
                    val src = NpcDefinition(store, task.sourceId, true)
                    val copy = src.clone() as NpcDefinition

                    copy.id = currentId
                    copy.apply { task.modifier?.invoke(this) }
                }

                task.sourceId >= 0 && task.isEdit -> {
                    val existing = NpcDefinition(store, task.sourceId, true)
                    existing.apply { task.modifier?.invoke(this) }
                }

                else -> {
                    val newNpc = NpcDefinition(store, currentId, false)
                    newNpc.apply { task.modifier?.invoke(this) }
                }
            }

            npc.write(store)
            writeBasConfig(npc, store)
            copiedNpcs.add(npc)
            println("Packed npc ${npc.id}:${npc.name ?: "unknown"}")
            if (!task.isEdit) currentId++
        }

        return copiedNpcs
    }

    private fun writeBasConfig(npc: NpcDefinition, store: com.alex.store.Store) {
        val bas = npc.basDefinition[npc.bastypeid] ?: return
        val data: ByteArray = bas.encode()
        store.indexes[2].putFile(32, bas.id, data)
    }

    fun getCopiedNpcs(): List<NpcDefinition> = copiedNpcs
}