package com.alex.tools.pack

import com.alex.Cache
import com.alex.io.OutputStream
import com.alex.loaders.LocDefinition

class LocPacker private constructor(private val startId: Int = 0) {

    private var currentId = startId
    private val tasks = mutableListOf<Task>()
    private var lastTask: Task? = null

    private data class Task(
        val sourceId: Int,
        var modifier: (LocDefinition.() -> Unit)?
    )

    companion object {
        fun create(): LocPacker = LocPacker()
        fun create(startId: Int): LocPacker = LocPacker(startId)
    }

    fun startAt(id: Int): LocPacker {
        currentId = id
        return this
    }

    fun addLoc(modifier: LocDefinition.() -> Unit): LocPacker {
        val task = Task(-1, modifier)
        tasks.add(task)
        lastTask = task
        return this
    }

    fun copyLoc(sourceId: Int, modifier: LocDefinition.() -> Unit = {}): LocPacker {
        val task = Task(sourceId, modifier)
        tasks.add(task)
        lastTask = task
        return this
    }

    fun copyRange(fromId: Int, toId: Int, modifier: LocDefinition.() -> Unit = {}): LocPacker {
        for (id in fromId..toId) copyLoc(id, modifier)
        return this
    }

    fun modify(modifier: LocDefinition.() -> Unit): LocPacker {
        lastTask?.modifier = lastTask?.modifier?.let { original ->
            {
                original()
                modifier()
            }
        } ?: modifier
        return this
    }

    fun save(): List<LocDefinition> {
        val store = Cache.getStore() ?: throw IllegalStateException("Cache store not loaded!")
        val savedLocs = mutableListOf<LocDefinition>()

        for (task in tasks) {
            val loc = if (task.sourceId >= 0) {
                val srcLoc = LocDefinition.load(store, task.sourceId)
                    ?: throw IllegalStateException("Loc ${task.sourceId} not found!")
                srcLoc.id = currentId
                srcLoc.apply { task.modifier?.invoke(this) }
            } else {
                LocDefinition().apply {
                    id = currentId
                    task.modifier?.invoke(this)
                }
            }

            val stream = OutputStream()
            loc.encode(stream)
            val data = stream.buffer.copyOf(stream.offset)
            store.indexes[16].putFile(loc.id shr 8, loc.id and 0xFF, data)

            println("Packed loc ${loc.id}:${loc.name ?: "unknown"}")
            savedLocs.add(loc)
            currentId++
        }

        return savedLocs
    }
}