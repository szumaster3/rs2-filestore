package com.alex.tools.pack

import com.alex.Cache
import com.alex.loaders.StructDefinition

class StructPacker private constructor(private var startId: Int = 0) {

    private var currentId = startId
    private val tasks = mutableListOf<Task>()
    private val copied = mutableListOf<StructDefinition>()

    private data class Task(
        val sourceId: Int,
        val modifier: (StructDefinition.() -> Unit)?,
        val isEdit: Boolean = false
    )

    companion object {
        fun create(): StructPacker = StructPacker()
    }

    fun startAt(id: Int): StructPacker {
        currentId = id
        return this
    }

    fun addStruct(modifier: StructDefinition.() -> Unit): StructPacker {
        tasks.add(Task(-1, modifier))
        return this
    }

    fun addStructs(vararg modifiers: StructDefinition.() -> Unit): StructPacker {
        modifiers.forEach { addStruct(it) }
        return this
    }

    fun copyStruct(sourceId: Int, modifier: StructDefinition.() -> Unit = {}): StructPacker {
        tasks.add(Task(sourceId, modifier, isEdit = false))
        return this
    }

    fun editStruct(id: Int, modifier: StructDefinition.() -> Unit = {}): StructPacker {
        tasks.add(Task(id, modifier, isEdit = true))
        return this
    }

    fun copyRange(fromId: Int, toId: Int, modifier: StructDefinition.() -> Unit = {}): StructPacker {
        for (id in fromId..toId) {
            copyStruct(id, modifier)
        }
        return this
    }

    fun save(): List<StructDefinition> {
        val store = Cache.getStore()
            ?: throw IllegalStateException("Cache store not loaded!")

        val index = store.indexes[2]
        val archive = 26

        copied.clear()

        for (task in tasks) {

            val struct = when {
                task.sourceId >= 0 && !task.isEdit -> {
                    val src = StructDefinition(store, task.sourceId)

                    val copy = StructDefinition(currentId)
                    copyFrom(src, copy)

                    copy.apply { task.modifier?.invoke(this) }
                }
                task.sourceId >= 0 && task.isEdit -> {
                    val existing = StructDefinition(store, task.sourceId)
                    existing.apply { task.modifier?.invoke(this) }
                }
                else -> {
                    val newStruct = StructDefinition(currentId)
                    newStruct.apply { task.modifier?.invoke(this) }
                }
            }

            val data = struct.encode()
            index.putFile(archive, struct.id, data)

            copied.add(struct)

            println("Packed struct ${struct.id}")

            if (!task.isEdit) {
                currentId++
            }
        }

        return copied
    }

    private fun copyFrom(src: StructDefinition, dst: StructDefinition) {
        dst.params = src.params?.toMutableMap()
    }

    fun getCopied(): List<StructDefinition> = copied
}