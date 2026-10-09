package com.alex.tools.pack

import com.alex.Cache
import com.alex.loaders.SeqDefinition

class SeqPacker private constructor(private var startId: Int = 0) {
    private var currentId = startId
    private val tasks = mutableListOf<Task>()
    private val copied = mutableListOf<SeqDefinition>()

    private data class Task(
        val sourceId: Int,
        val modifier: (SeqDefinition.() -> Unit)?,
        val isEdit: Boolean = false
    )

    companion object {
        fun create(): SeqPacker = SeqPacker()
    }

    fun startAt(id: Int): SeqPacker {
        currentId = id
        return this
    }

    fun addSeq(modifier: SeqDefinition.() -> Unit): SeqPacker {
        tasks.add(Task(-1, modifier))
        return this
    }

    fun addSeqs(vararg modifiers: SeqDefinition.() -> Unit): SeqPacker {
        modifiers.forEach { addSeq(it) }
        return this
    }

    fun copySeq(sourceId: Int, modifier: SeqDefinition.() -> Unit = {}): SeqPacker {
        tasks.add(Task(sourceId, modifier, isEdit = false))
        return this
    }

    fun editSeq(id: Int, modifier: SeqDefinition.() -> Unit = {}): SeqPacker {
        tasks.add(Task(id, modifier, isEdit = true))
        return this
    }

    fun copyRange(fromId: Int, toId: Int, modifier: SeqDefinition.() -> Unit = {}): SeqPacker {
        for (id in fromId..toId) {
            copySeq(id, modifier)
        }
        return this
    }

    fun save(): List<SeqDefinition> {
        val store = Cache.getStore()
            ?: throw IllegalStateException("Cache store not loaded!")

        val index = store.indexes[20]

        copied.clear()

        for (task in tasks) {

            val seq = when {
                task.sourceId >= 0 && !task.isEdit -> {
                    val src = SeqDefinition(task.sourceId)
                    src.load()

                    val copy = SeqDefinition(currentId)
                    copyFrom(src, copy)

                    copy.apply { task.modifier?.invoke(this) }
                }
                task.sourceId >= 0 && task.isEdit -> {
                    val existing = SeqDefinition(task.sourceId)
                    existing.load()

                    existing.apply { task.modifier?.invoke(this) }
                }
                else -> {
                    val newSeq = SeqDefinition(currentId)
                    newSeq.apply { task.modifier?.invoke(this) }
                }
            }

            val data = seq.encode()

            val archive = seq.id shr 7
            val file = seq.id and 0x7F

            index.putFile(archive, file, data)

            copied.add(seq)

            println("Packed seq ${seq.id}")

            if (!task.isEdit) {
                currentId++
            }
        }

        return copied
    }

    private fun copyFrom(src: SeqDefinition, dst: SeqDefinition) {

        dst.frames = src.frames?.clone()
        dst.frameDelay = src.frameDelay?.clone()
        dst.frameGroup = src.frameGroup?.clone()
        dst.frameSet = src.frameSet?.clone()

        dst.soundEffect = src.soundEffect?.map { it?.clone() }?.toTypedArray()

        dst.replayOff = src.replayOff
        dst.replayCount = src.replayCount
        dst.loopType = src.loopType
        dst.moveType = src.moveType
        dst.exactMove = src.exactMove
        dst.priority = src.priority
        dst.mainHand = src.mainHand
        dst.offHand = src.offHand

        dst.stretches = src.stretches
        dst.tween = src.tween
        dst.alpha = src.alpha

        dst.loaded = false
    }

    fun getCopied(): List<SeqDefinition> = copied
}