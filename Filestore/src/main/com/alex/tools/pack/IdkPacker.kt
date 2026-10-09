package com.alex.tools.pack

import com.alex.Cache
import com.alex.loaders.IdkDefinition

class IdkPacker private constructor(private var startId: Int = 0) {

    private var currentId = startId
    private val tasks = mutableListOf<Task>()
    private val copied = mutableListOf<IdkDefinition>()

    private data class Task(
        val sourceId: Int,
        val modifier: (IdkDefinition.() -> Unit)?,
        val isEdit: Boolean = false
    )

    companion object {
        fun create(): IdkPacker = IdkPacker()
    }

    fun startAt(id: Int): IdkPacker {
        currentId = id
        return this
    }

    fun addIdk(modifier: IdkDefinition.() -> Unit): IdkPacker {
        tasks.add(Task(-1, modifier))
        return this
    }

    fun addIdks(vararg modifiers: IdkDefinition.() -> Unit): IdkPacker {
        modifiers.forEach { addIdk(it) }
        return this
    }

    fun copyIdk(sourceId: Int, modifier: IdkDefinition.() -> Unit = {}): IdkPacker {
        tasks.add(Task(sourceId, modifier, isEdit = false))
        return this
    }

    fun editIdk(id: Int, modifier: IdkDefinition.() -> Unit = {}): IdkPacker {
        tasks.add(Task(id, modifier, isEdit = true))
        return this
    }

    fun copyRange(fromId: Int, toId: Int, modifier: IdkDefinition.() -> Unit = {}): IdkPacker {
        for (id in fromId..toId) {
            copyIdk(id, modifier)
        }
        return this
    }

    fun save(): List<IdkDefinition> {
        val store = Cache.getStore()
            ?: throw IllegalStateException("Cache store not loaded!")

        val index = store.indexes[2]
        val archive = 3

        copied.clear()

        for (task in tasks) {
            val idk = when {
                task.sourceId >= 0 && !task.isEdit -> {
                    val src = IdkDefinition(store, task.sourceId)

                    val copy = IdkDefinition(currentId)
                    copyFrom(src, copy)

                    copy.apply { task.modifier?.invoke(this) }
                }
                task.sourceId >= 0 && task.isEdit -> {
                    val existing = IdkDefinition(store, task.sourceId)
                    existing.apply { task.modifier?.invoke(this) }
                }
                else -> {
                    val newIdk = IdkDefinition(currentId)
                    newIdk.apply { task.modifier?.invoke(this) }
                }
            }

            val data = idk.encode()
            index.putFile(archive, idk.id, data)

            copied.add(idk)

            println("Packed idk ${idk.id}")

            if (!task.isEdit) {
                currentId++
            }
        }

        return copied
    }

    private fun copyFrom(src: IdkDefinition, dst: IdkDefinition) {

        dst.feature = src.feature
        dst.disable = src.disable

        dst.model = src.model?.clone()

        dst.head = src.head.clone()

        dst.recol_s = src.recol_s?.clone()
        dst.recol_d = src.recol_d?.clone()

        dst.retex_s = src.retex_s?.clone()
        dst.retex_d = src.retex_d?.clone()
    }

    fun getCopied(): List<IdkDefinition> = copied
}