package com.alex.tools.pack

import com.alex.Cache
import com.alex.loaders.ItemDefinition

class ItemPacker private constructor(private val startId: Int = 0) {

    private var currentId = startId
    private val tasks = mutableListOf<Task>()
    private val copiedItems = mutableListOf<ItemDefinition>()

    private data class Task(
        val sourceId: Int,
        val modifier: (ItemDefinition.() -> Unit)?,
        val isEdit: Boolean = false
    )

    companion object {
        fun create(): ItemPacker = ItemPacker()
    }

    fun startAt(id: Int): ItemPacker {
        currentId = id
        return this
    }

    fun addItem(modifier: ItemDefinition.() -> Unit): ItemPacker {
        tasks.add(Task(-1, modifier))
        return this
    }

    fun addItems(vararg modifiers: ItemDefinition.() -> Unit): ItemPacker {
        modifiers.forEach { addItem(it) }
        return this
    }

    fun copyItem(sourceId: Int, modifier: ItemDefinition.() -> Unit = {}): ItemPacker {
        tasks.add(Task(sourceId, modifier, isEdit = false))
        return this
    }

    fun editItem(itemId: Int, modifier: ItemDefinition.() -> Unit = {}): ItemPacker {
        tasks.add(Task(itemId, modifier, isEdit = true))
        return this
    }

    fun copyRange(fromId: Int, toId: Int, modifier: ItemDefinition.() -> Unit = {}): ItemPacker {
        for (id in fromId..toId) copyItem(id, modifier)
        return this
    }

    fun save(): List<ItemDefinition> {
        val store = Cache.getStore() ?: throw IllegalStateException("Cache store not loaded")
        copiedItems.clear()

        for (task in tasks) {
            val item = when {
                task.sourceId >= 0 && !task.isEdit -> {
                    val srcItem = ItemDefinition(store, task.sourceId, true)
                    val copy = srcItem.clone() as ItemDefinition
                    copy.id = currentId
                    copy.apply { task.modifier?.invoke(this) }
                }
                task.sourceId >= 0 && task.isEdit -> {
                    val existing = ItemDefinition(store, task.sourceId, true)
                    existing.apply { task.modifier?.invoke(this) }
                }
                else -> {
                    val newItem = ItemDefinition(store, currentId, false)
                    newItem.apply { task.modifier?.invoke(this) }
                }
            }

            item.write(store)
            copiedItems.add(item)
            println("Packed item ${item.id}:${item.name ?: "unknown"}")
            if (!task.isEdit) currentId++
        }

        return copiedItems
    }

    /**
     * Adds a new item together with its noted version.
     */
    fun addItemWithNote(builder: ItemDefinition.() -> Unit): ItemPacker {
        val store = Cache.getStore() ?: throw IllegalStateException("Cache store not loaded!")

        val itemId = currentId
        val noteId = currentId + 1

        val item = ItemDefinition(store, itemId, false).apply {
            builder()
            this.certlink = noteId
        }

        item.write(store)

        val note = ItemDefinition(store, noteId, false).apply {
            name = item.name
            this.certlink = itemId
            certtemplate = 799
        }

        note.write(store)

        println("Packed item ${item.id}:${item.name ?: "unknown"} | Packed cert ${note.id}")

        currentId += 2
        return this
    }

    fun getCopiedItems(): List<ItemDefinition> = copiedItems
}