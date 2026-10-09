package com.alex.tools.pack

import com.alex.Cache
import com.alex.loaders.ComponentDefinition
import com.alex.util.crc32.CRC32HGenerator
import java.util.*
import kotlin.collections.ArrayList

class IfacePacker private constructor(val targetInterfaceId: Int) {

    private data class Task(
        val sourceId: Int,
        val targetId: Int,
        val modifier: (ComponentDefinition.() -> Unit)? = null
    )

    private val tasks: MutableList<Task> = ArrayList()
    private var src = 0
    private var startId = 0
    private var currentId = 0
    private var globalModifier: (ComponentDefinition.() -> Unit)? = null
    private val copiedComponents = mutableListOf<ComponentDefinition>()

    fun from(sourceInterface: Int): IfacePacker {
        this.src = sourceInterface
        return this
    }

    fun startAt(id: Int): IfacePacker {
        this.startId = id
        this.currentId = id
        return this
    }

    fun modify(modifier: ComponentDefinition.() -> Unit): IfacePacker {
        globalModifier = if (globalModifier == null) {
            modifier
        } else {
            val old = globalModifier!!
            {
                old()
                modifier()
            }
        }
        return this
    }

    fun copy(sourceId: Int, modifier: ComponentDefinition.() -> Unit = {}): IfacePacker {
        tasks.add(Task(sourceId, currentId++, modifier))
        return this
    }

    fun copy(vararg sourceIds: Int): IfacePacker {
        sourceIds.forEach { copy(it) }
        return this
    }

    fun copyRange(from: Int, to: Int): IfacePacker {
        for (i in from..to) copy(i)
        return this
    }

    fun addComponents(vararg modifiers: ComponentDefinition.() -> Unit): IfacePacker {
        modifiers.forEach { mod -> tasks.add(Task(-1, currentId++, mod)) }
        return this
    }

    fun addComponent(modifier: ComponentDefinition.() -> Unit): IfacePacker {
        tasks.add(Task(-1, currentId++, modifier))
        return this
    }

    private fun createNewComponent(targetId: Int): ComponentDefinition {
        var comps = ComponentDefinition.getInterface(targetInterfaceId, true)
        if (comps!!.size <= targetId) {
            val expanded = arrayOfNulls<ComponentDefinition>(targetId + 1)
            System.arraycopy(comps, 0, expanded, 0, comps.size)
            ComponentDefinition.componentDefinition?.set(targetInterfaceId, expanded)
            comps = expanded
        }
        if (comps[targetId] == null) comps[targetId] = ComponentDefinition()
        comps[targetId]!!.componentHash = (targetInterfaceId shl 16) or targetId
        return comps[targetId]!!
    }

    fun save(): List<ComponentDefinition> {
        copiedComponents.clear()
        val store = Cache.getStore() ?: return copiedComponents

        val log = StringBuilder()

        for (task in tasks) {
            val comp = if (task.sourceId >= 0)
                ComponentDefinition.getInterfaceComponent(src, task.sourceId)
            else
                createNewComponent(task.targetId)

            comp!!.componentHash = (targetInterfaceId shl 16) or task.targetId

            globalModifier?.let { it(comp) }
            task.modifier?.let { it(comp) }

            val status = if (task.sourceId >= 0) "Modified" else "Packed"
            log.append("$status component $targetInterfaceId:${task.targetId}:${comp.name ?: "unnamed"}\n")

            val fileNameHash: Int? = comp.name?.takeIf { it.isNotBlank() }?.let {
                CRC32HGenerator.getHash(it.uppercase(Locale.getDefault()).toByteArray(Charsets.UTF_8))
            }

            if (fileNameHash != null) {
                store.indexes[3].putFile(targetInterfaceId, task.targetId, 2, comp.encodeIf3(), null, true, true, -1, fileNameHash)
            } else {
                store.indexes[3].putFile(targetInterfaceId, task.targetId, comp.encodeIf3())
            }

            copiedComponents.add(comp)
        }

        init(targetInterfaceId)
        print(log.toString())
        return copiedComponents
    }

    private fun init(targetId: Int) {
        val compDefs = ComponentDefinition.componentDefinition
        if (compDefs == null || compDefs.size <= targetId) {
            val expanded = arrayOfNulls<Array<ComponentDefinition?>?>(targetId + 1)
            compDefs?.copyInto(expanded)
            ComponentDefinition.componentDefinition = expanded
        }

        val comps = ComponentDefinition.componentDefinition!![targetId]
        val maxTarget = tasks.maxOfOrNull { it.targetId } ?: -1
        if (comps == null || comps.size <= maxTarget) {
            val newComps = arrayOfNulls<ComponentDefinition>(maxTarget + 1)
            comps?.copyInto(newComps)
            ComponentDefinition.componentDefinition!![targetId] = newComps
        }
    }

    fun getCopiedComponents(): List<ComponentDefinition> = copiedComponents

    companion object {
        @JvmStatic
        fun to(targetInterface: Int): IfacePacker = IfacePacker(targetInterface)

        @JvmStatic
        fun newInterface(): IfacePacker {
            val id = createInterface(6, 36)
            return IfacePacker(id)
        }

        @JvmStatic
        fun newInterface(templateInterface: Int, templateComponent: Int): IfacePacker {
            val id = createInterface(templateInterface, templateComponent)
            return IfacePacker(id)
        }

        @JvmStatic
        fun createInterface(templateInterface: Int, templateComponent: Int): Int {
            val newId = ComponentDefinition.getInterfaceDefinitionsSize()
            val base = ComponentDefinition.getInterfaceComponent(templateInterface, templateComponent)
            if (base != null) {
                base.baseX = 0
                base.baseY = 0
                base.parentId = -1
                Cache.getStore()!!.indexes[3].putFile(newId, 0, base.encodeIf3())
            }
            ComponentDefinition.componentDefinition = arrayOfNulls(newId + 1)
            return newId
        }
    }
}