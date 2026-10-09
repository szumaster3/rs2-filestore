package com.alex.tools.pack

import com.alex.Cache
import com.alex.loaders.EnumDefinition

class EnumPacker private constructor(private var startId: Int = 0) {

    private var currentId = startId
    private val tasks = mutableListOf<Task>()
    private val copied = mutableListOf<EnumDefinition>()

    private data class Task(
        val sourceId: Int,
        val modifier: (EnumDefinition.() -> Unit)?,
        val isEdit: Boolean = false
    )

    companion object {
        fun create(): EnumPacker = EnumPacker()
    }

    fun startAt(id: Int): EnumPacker {
        currentId = id
        return this
    }

    fun addEnum(modifier: EnumDefinition.() -> Unit): EnumPacker {
        tasks.add(Task(-1, modifier))
        return this
    }

    fun addEnums(vararg modifiers: EnumDefinition.() -> Unit): EnumPacker {
        modifiers.forEach { addEnum(it) }
        return this
    }

    fun copyEnum(sourceId: Int, modifier: EnumDefinition.() -> Unit = {}): EnumPacker {
        tasks.add(Task(sourceId, modifier, isEdit = false))
        return this
    }

    fun editEnum(id: Int, modifier: EnumDefinition.() -> Unit = {}): EnumPacker {
        tasks.add(Task(id, modifier, isEdit = true))
        return this
    }

    fun copyRange(fromId: Int, toId: Int, modifier: EnumDefinition.() -> Unit = {}): EnumPacker {
        for (id in fromId..toId) {
            copyEnum(id, modifier)
        }
        return this
    }

    fun save(): List<EnumDefinition> {
        val store = Cache.getStore()
            ?: throw IllegalStateException("Cache store not loaded!")

        val index = store.indexes[2]
        val archive = 2

        copied.clear()

        for (task in tasks) {
            val def = when {
                task.sourceId >= 0 && !task.isEdit -> {
                    val src = EnumDefinition(store, task.sourceId)

                    val copy = EnumDefinition(currentId)
                    copyFrom(src, copy)

                    copy.apply { task.modifier?.invoke(this) }
                }
                task.sourceId >= 0 && task.isEdit -> {
                    val existing = EnumDefinition(store, task.sourceId)
                    existing.apply { task.modifier?.invoke(this) }
                }
                else -> {
                    val newDef = EnumDefinition(currentId)
                    newDef.apply { task.modifier?.invoke(this) }
                }
            }

            val data = def.encode()
            index.putFile(archive, def.id, data)

            copied.add(def)

            println("Packed enum ${def.id}")

            if (!task.isEdit) {
                currentId++
            }
        }

        return copied
    }

    private fun copyFrom(src: EnumDefinition, dst: EnumDefinition) {

        dst.keyType = src.keyType
        dst.valueType = src.valueType
        dst.defaultInt = src.defaultInt
        dst.defaultString = src.defaultString

        dst.map.putAll(src.map)
    }

    fun getCopied(): List<EnumDefinition> = copied
}