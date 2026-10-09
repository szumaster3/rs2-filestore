package com.alex.loaders

import com.alex.Cache
import com.alex.io.InputStream
import com.alex.io.OutputStream
import com.alex.utils.Utils
import content.data.ComponentType

class ComponentDefinition {
    internal var version: Byte = 0
    var type: Int = 0
    var contentType: Int = 0
    var componentHash: Int = 0
    var interfaceId: Int = 0
    var componentId: Int = 0

    var baseX: Int = 0
    var baseY: Int = 0
    var baseWidth: Int = 0
    var baseHeight: Int = 0

    var modelObjWidth: Int = 0
    var modelObjHeight: Int = 0
    var textLineHeight: Int = 0
    var scrollHeight: Int = 0
    var scrollWidth: Int = 0
    var mouseOverCursor: Int
    var targetOverCursor: Int = 0
    var targetLeaveCursor: Int = 0
    var parentId: Int = 0
    var xMode: Byte = 0
    var yMode: Byte = 0

    var spriteId: Int = 0
    var hidden: Boolean = false
    var filled: Boolean = false
    var shadow: Boolean = false
    var shadowColor: Int = 0
    var hasAlpha: Boolean = false
    var noClickThrough: Boolean = false
    var dragRenderBehavior: Boolean = false
    var spriteTiling: Boolean = false

    var vFlip: Boolean = false
    var hFlip: Boolean = false

    var text: String? = ""
    var textVerticalAli: Int = 0
    var textHorizontalAli: Int = 0
    var name: String? = null
    var fontId: Int = 0

    var modelId: Int = 0
    var modelOriginX: Int = 0
    var modelOriginY: Int = 0
    var modelXAngle: Int = 0
    var modelYAngle: Int = 0
    var modelZAngle: Int = 0
    var modelZoom: Int = 0
    var modelAnimId: Int = 0
    var modelOrtho: Boolean = false
    var unknownModelProp3: Int = 0
    var unknownModelProp4: Int = 0
    var unknownModelProp5: Boolean = false

    var color: Int = 0
    var transparency: Int = 0
    var borderThickness: Int = 0
    var angle2d: Int = 0
    var widthMode: Byte = 0
    var heightMode: Byte = 0
    var modelType: Int = 1

    var rightClickOptions: Array<String?>? = null
    var optionBase: String? = ""
    var optionCircumfix: String? = ""
    var optionMask: Int = 0
    private var mask = -1

    var dragDeadtime: Int = 0
    var dragDeadzone: Int = 0

    lateinit var settings: IComponentSettings
    var hasScripts: Boolean = false
    var onLoadScript: Array<Any?>? = null
    var onMouseHoverScript: Array<Any?>? = null
    var onMouseLeaveScript: Array<Any?>? = null
    var onUseWith: Array<Any?>? = null
    var onUse: Array<Any?>? = null
    var onVarpTransmit: Array<Any?>? = null
    var onInvTransmit: Array<Any?>? = null
    var onStatTransmit: Array<Any?>? = null
    var onTimer: Array<Any?>? = null
    var onOptionClick: Array<Any?>? = null
    var onMouseRepeat: Array<Any?>? = null
    var onClickRepeat: Array<Any?>? = null
    var onDrag: Array<Any?>? = null
    var onRelease: Array<Any?>? = null
    var onHold: Array<Any?>? = null
    var onDragStart: Array<Any?>? = null
    var onDragRelease: Array<Any?>? = null
    var onScroll: Array<Any?>? = null
    var onVarcTransmit: Array<Any?>? = null
    var onVarcStrTransmit: Array<Any?>? = null

    var varpTriggers: IntArray? = null
    var inventoryTriggers: IntArray? = null
    var statTriggers: IntArray? = null
    var varcTriggers: IntArray? = null
    var varcstrTriggers: IntArray? = null

    var opCursors: IntArray? = null

    var lineWidth: Int = 0
    var unknownProp_8: Boolean = false

    private var opId: IntArray? = null
    private var repeatCount: ByteArray? = null
    private var modifier: ByteArray? = null

    fun encodeIf3(): ByteArray {
        val out = OutputStream()
        out.writeByte(255)

        var t = type and 0x7F
        if (!name.isNullOrEmpty()) {
            out.writeByte(t or 0x80)
            out.writeString(name!!)
        } else {
            out.writeByte(t)
        }

        out.writeShort(contentType)
        out.writeShort(baseX)
        out.writeShort(baseY)
        out.writeShort(baseWidth)
        out.writeShort(baseHeight)
        out.writeByte(widthMode.toInt())
        out.writeByte(heightMode.toInt())
        out.writeByte(yMode.toInt())
        out.writeByte(xMode.toInt())
        out.writeShort(if (parentId != -1) parentId and 0xFFFF else 65535)

        out.writeByte(if (hidden) 1 else 0)

        when (type) {
            ComponentType.LAYER -> {
                out.writeShort(scrollWidth)
                out.writeShort(scrollHeight)
                out.writeByte(if (noClickThrough) 1 else 0)
            }

            ComponentType.SPRITE -> {
                out.writeInt(spriteId)
                out.writeShort(angle2d)
                var spriteFlags = 0
                if (spriteTiling) spriteFlags = spriteFlags or 0x1
                if (hasAlpha) spriteFlags = spriteFlags or 0x2
                out.writeByte(spriteFlags)
                out.writeByte(transparency)
                out.writeByte(borderThickness)
                out.writeInt(shadowColor)
                out.writeByte(if (vFlip) 1 else 0)
                out.writeByte(if (hFlip) 1 else 0)
            }

            ComponentType.MODEL -> {
                out.writeShort(if (modelId == -1) 65535 else modelId)
                out.writeShort(modelOriginX)
                out.writeShort(modelOriginY)
                out.writeShort(modelXAngle)
                out.writeShort(modelYAngle)
                out.writeShort(modelZAngle)
                out.writeShort(modelZoom)
                out.writeShort(if (modelAnimId == -1) 65535 else modelAnimId)
                out.writeByte(if (modelOrtho) 1 else 0)
                out.writeShort(unknownModelProp3)
                out.writeShort(unknownModelProp4)
                out.writeByte(if (unknownModelProp5) 1 else 0)
                if (widthMode.toInt() != 0) out.writeShort(modelObjWidth)
                if (heightMode.toInt() != 0) out.writeShort(modelObjHeight)
            }

            ComponentType.TEXT -> {
                out.writeShort(if (fontId == -1) 65535 else fontId)
                out.writeString(text ?: "")
                out.writeByte(textLineHeight)
                out.writeByte(textHorizontalAli)
                out.writeByte(textVerticalAli)
                out.writeByte(if (shadow) 1 else 0)
                out.writeInt(color)
            }

            ComponentType.FIGURE -> {
                out.writeInt(color)
                out.writeByte(if (filled) 1 else 0)
                out.writeByte(transparency)
            }

            ComponentType.LINE -> {
                out.writeByte(lineWidth.toInt())
                out.writeInt(color)
                out.writeByte(if (unknownProp_8) 1 else 0)
            }
        }

        out.write24BitInt(optionMask)

        if (opId != null) {
            for (i in opId!!.indices) {
                val value = if (opId!![i] == -1) 4095 else opId!![i]
                out.writeByte(((i + 1) shl 4) or (value shr 8))
                out.writeByte(value and 0xFF)
                out.writeByte(repeatCount!![i].toInt())
                out.writeByte(modifier!![i].toInt())
            }
            out.writeByte(0)
        } else {
            out.writeByte(0)
        }

        out.writeString(optionBase ?: "")
        val optionCount = rightClickOptions?.size ?: 0
        val activeCursors = mutableListOf<Pair<Int, Int>>()

        if (opCursors != null) {
            for (i in opCursors!!.indices) {
                val value = opCursors!![i]
                if (value != -1) {
                    activeCursors.add(Pair(i, value))
                }
            }
        }

        val cursorFlag = when (activeCursors.size) {
            0 -> 0
            1 -> 1
            else -> 2
        }

        out.writeByte((cursorFlag shl 4) or optionCount)

        rightClickOptions?.forEach {
            out.writeString(it ?: "")
        }

        if (cursorFlag > 0) {
            val (index1, cursor1) = activeCursors[0]
            out.writeByte(index1)
            out.writeShort(cursor1)

            if (cursorFlag > 1) {
                val (index2, cursor2) = activeCursors[1]
                out.writeByte(index2)
                out.writeShort(cursor2)
            }
        }

        out.writeByte(dragDeadzone)
        out.writeByte(dragDeadtime)
        out.writeByte(if (dragRenderBehavior) 1 else 0)
        out.writeString(optionCircumfix ?: "")

        if ((optionMask shr 11 and 0x7F) != 0) {
            out.writeShort(if (mask == -1) 65535 else mask)
            out.writeShort(if (targetOverCursor == -1) 65535 else targetOverCursor)
            out.writeShort(if (targetLeaveCursor == -1) 65535 else targetLeaveCursor)
        }

        encodeScript(onLoadScript, out)
        encodeScript(onMouseHoverScript, out)
        encodeScript(onMouseLeaveScript, out)
        encodeScript(onUseWith, out)
        encodeScript(onUse, out)
        encodeScript(onVarpTransmit, out)
        encodeScript(onInvTransmit, out)
        encodeScript(onStatTransmit, out)
        encodeScript(onTimer, out)
        encodeScript(onOptionClick, out)
        encodeScript(onMouseRepeat, out)
        encodeScript(onClickRepeat, out)
        encodeScript(onDrag, out)
        encodeScript(onRelease, out)
        encodeScript(onHold, out)
        encodeScript(onDragStart, out)
        encodeScript(onDragRelease, out)
        encodeScript(onScroll, out)
        encodeScript(onVarcTransmit, out)
        encodeScript(onVarcStrTransmit, out)

        encodeTriggers(varpTriggers, out)
        encodeTriggers(inventoryTriggers, out)
        encodeTriggers(statTriggers, out)
        encodeTriggers(varcTriggers, out)
        encodeTriggers(varcstrTriggers, out)

        val bytes = ByteArray(out.offset)
        out.offset = 0
        out.getBytes(bytes, 0, bytes.size)
        return bytes
    }

    fun decodeIf3(stream: InputStream, componentId: Int, interfaceId: Int) {
        stream.readUnsignedByte()
        version = 3

        this.componentId = componentId
        this.interfaceId = interfaceId

        var rawType = stream.readUnsignedByte()
        if ((rawType and 0x80) != 0) {
            rawType = rawType and 0x7F
            name = Utils.getJagString(stream)
        }
        type = rawType

        contentType = stream.readUnsignedShort()
        baseX = stream.readShort()
        baseY = stream.readShort()
        baseWidth = stream.readUnsignedShort()
        baseHeight = stream.readUnsignedShort()
        widthMode = stream.readByte().toByte()
        heightMode = stream.readByte().toByte()
        yMode = stream.readByte().toByte()
        xMode = stream.readByte().toByte()

        val rawParent = stream.readUnsignedShort()
        parentId = if (rawParent == 65535) -1 else (interfaceId shl 16) or rawParent

        hidden = stream.readUnsignedByte() == 1

        when (type) {
            ComponentType.LAYER -> {
                scrollWidth = stream.readUnsignedShort()
                scrollHeight = stream.readUnsignedShort()
                noClickThrough = stream.readUnsignedByte() == 1
            }

            ComponentType.SPRITE -> {
                spriteId = stream.readInt()
                angle2d = stream.readUnsignedShort()
                val flags = stream.readUnsignedByte()
                spriteTiling = (flags and 0x1) != 0
                hasAlpha = (flags and 0x2) != 0
                transparency = stream.readUnsignedByte()
                borderThickness = stream.readUnsignedByte()
                shadowColor = stream.readInt()
                vFlip = stream.readUnsignedByte() == 1
                hFlip = stream.readUnsignedByte() == 1
            }

            ComponentType.MODEL -> {
                modelType = 1
                modelId = stream.readUnsignedShort().let { if (it == 65535) -1 else it }
                modelOriginX = stream.readShort()
                modelOriginY = stream.readShort()
                modelXAngle = stream.readUnsignedShort()
                modelYAngle = stream.readUnsignedShort()
                modelZAngle = stream.readUnsignedShort()
                modelZoom = stream.readUnsignedShort()
                modelAnimId = stream.readUnsignedShort().let { if (it == 65535) -1 else it }
                modelOrtho = stream.readUnsignedByte() == 1
                unknownModelProp3 = stream.readUnsignedShort()
                unknownModelProp4 = stream.readUnsignedShort()
                unknownModelProp5 = stream.readUnsignedByte() == 1
                if (widthMode.toInt() != 0) modelObjWidth = stream.readUnsignedShort()
                if (heightMode.toInt() != 0) modelObjHeight = stream.readUnsignedShort()
            }

            ComponentType.TEXT -> {
                fontId = stream.readUnsignedShort().let { if (it == 65535) -1 else it }
                text = Utils.getJagString(stream)
                textLineHeight = stream.readUnsignedByte()
                textHorizontalAli = stream.readUnsignedByte()
                textVerticalAli = stream.readUnsignedByte()
                shadow = stream.readUnsignedByte() == 1
                color = stream.readInt()
            }

            ComponentType.FIGURE -> {
                color = stream.readInt()
                filled = stream.readUnsignedByte() == 1
                transparency = stream.readUnsignedByte()
            }

            ComponentType.LINE -> {
                lineWidth = stream.readUnsignedByte()
                color = stream.readInt()
                unknownProp_8 = stream.readUnsignedByte() == 1
            }
        }

        optionMask = stream.read24BitInt()
        var key = stream.readUnsignedByte()

        if (key != 0) {
            opId = IntArray(10)
            repeatCount = ByteArray(10)
            modifier = ByteArray(10)

            while (key != 0) {
                val index = (key shr 4) - 1
                var value = ((key and 0xF) shl 8) or stream.readUnsignedByte()
                if (value == 4095) value = -1
                repeatCount!![index] = stream.readByte().toByte()
                modifier!![index] = stream.readByte().toByte()
                opId!![index] = value
                key = stream.readUnsignedByte()
            }
        }

        optionBase = Utils.getJagString(stream)
        val flags = stream.readUnsignedByte()
        val optionCount = flags and 0xF
        val cursorFlag = flags shr 4

        if (optionCount > 0) {
            rightClickOptions = arrayOfNulls(optionCount)
            repeat(optionCount) { rightClickOptions!![it] = Utils.getJagString(stream) }
        }

        if (cursorFlag > 0) {
            val index = stream.readUnsignedByte()
            opCursors = IntArray(index + 1) { -1 }
            opCursors!![index] = stream.readUnsignedShort()
            if (cursorFlag > 1) {
                val index2 = stream.readUnsignedByte()
                opCursors!![index2] = stream.readUnsignedShort()
            }
        } else {
            opCursors = null
        }

        dragDeadzone = stream.readUnsignedByte()
        dragDeadtime = stream.readUnsignedByte()
        dragRenderBehavior = stream.readUnsignedByte() == 1
        optionCircumfix = Utils.getJagString(stream)

        if ((optionMask shr 11 and 0x7F) != 0) {
            mask = stream.readUnsignedShort().let { if (it == 65535) -1 else it }
            targetOverCursor = stream.readUnsignedShort().let { if (it == 65535) -1 else it }
            targetLeaveCursor = stream.readUnsignedShort().let { if (it == 65535) -1 else it }
        }

        settings = IComponentSettings(optionMask, mask)

        onLoadScript = decodeScript(stream)
        onMouseHoverScript = decodeScript(stream)
        onMouseLeaveScript = decodeScript(stream)
        onUseWith = decodeScript(stream)
        onUse = decodeScript(stream)
        onVarpTransmit = decodeScript(stream)
        onInvTransmit = decodeScript(stream)
        onStatTransmit = decodeScript(stream)
        onTimer = decodeScript(stream)
        onOptionClick = decodeScript(stream)
        onMouseRepeat = decodeScript(stream)
        onClickRepeat = decodeScript(stream)
        onDrag = decodeScript(stream)
        onRelease = decodeScript(stream)
        onHold = decodeScript(stream)
        onDragStart = decodeScript(stream)
        onDragRelease = decodeScript(stream)
        onScroll = decodeScript(stream)
        onVarcTransmit = decodeScript(stream)
        onVarcStrTransmit = decodeScript(stream)

        varpTriggers = decodeTriggers(stream)
        inventoryTriggers = decodeTriggers(stream)
        statTriggers = decodeTriggers(stream)
        varcTriggers = decodeTriggers(stream)
        varcstrTriggers = decodeTriggers(stream)
    }

    val isModelOrtho: Boolean
        get() = modelOrtho

    private fun encodeScript(
        script: Array<Any?>?,
        out: OutputStream,
    ) {
        if (script.isNullOrEmpty()) {
            out.writeByte(0)
            return
        }

        out.writeByte(script.size)

        for (obj in script) {
            when (obj) {
                is Int -> {
                    out.writeByte(0)
                    out.writeInt(obj)
                }

                is String -> {
                    out.writeByte(1)
                    out.writeString(obj)
                }

                null -> {
                    out.writeByte(0)
                    out.writeInt(0)
                }

                else -> {
                    throw IllegalArgumentException("Unsupported script argument type: ${obj::class}")
                }
            }
        }
    }

    private fun decodeScript(buffer: InputStream): Array<Any?>? {
        val length = buffer.readUnsignedByte()
        if (length == 0) return null

        val objects = arrayOfNulls<Any>(length)

        for (i in 0 until length) {
            val type = buffer.readUnsignedByte()
            objects[i] =
                when (type) {
                    0 -> buffer.readInt()
                    1 -> Utils.getJagString(buffer)
                    else -> null
                }
        }

        hasScripts = true
        return objects
    }

    private fun decodeTriggers(buffer: InputStream): IntArray? {
        val length = buffer.readUnsignedByte()
        if (length == 0) {
            return null
        }
        val `is` = IntArray(length)
        for (index in 0 until length) {
            `is`[index] = buffer.readInt()
        }
        return `is`
    }

    private fun encodeTriggers(
        arr: IntArray?,
        out: OutputStream,
    ) {
        val length = arr?.size ?: 0
        out.writeByte(length)
        for (index in 0 until length) {
            out.writeInt(arr!![index])
        }
    }

    init {
        componentHash = -1
        contentType = 0
        parentId = -1
        hidden = false
        noClickThrough = false

        baseX = 0
        baseY = 0
        baseWidth = 0
        baseHeight = 0
        modelObjWidth = 0
        modelObjHeight = 0
        xMode = 0
        yMode = 0
        widthMode = 0.toByte()
        heightMode = 0.toByte()

        color = 0

        borderThickness = 0
        spriteId = -1
        spriteTiling = false
        hasAlpha = false
        hFlip = false
        vFlip = false
        shadow = false
        filled = false

        modelId = -1
        modelAnimId = -1
        modelXAngle = 0
        modelYAngle = 0
        modelZAngle = 0
        modelZoom = 100
        modelOrtho = false
        modelOriginX = 0
        modelOriginY = 0
        unknownModelProp3 = 0
        unknownModelProp4 = 0
        unknownModelProp5 = false

        text = ""
        fontId = -1
        textHorizontalAli = 0
        textVerticalAli = 0
        textLineHeight = 0

        scrollWidth = 0
        scrollHeight = 0
        targetOverCursor = -1
        targetLeaveCursor = -1
        mouseOverCursor = -1

        dragDeadzone = 0
        dragDeadtime = 0
        dragRenderBehavior = false

        optionMask = 0
        optionBase = ""
        optionCircumfix = ""

        onLoadScript = null
        onMouseHoverScript = null
        onMouseLeaveScript = null
        onUseWith = null
        onUse = null
        onVarpTransmit = null
        onInvTransmit = null
        onStatTransmit = null
        onTimer = null
        onOptionClick = null
        onMouseRepeat = null
        onClickRepeat = null
        onDrag = null
        onRelease = null
        onHold = null
        onDragStart = null
        onDragRelease = null
        onScroll = null
        onVarcTransmit = null
        onVarcStrTransmit = null

        varpTriggers = null
        inventoryTriggers = null
        statTriggers = null
        varcTriggers = null
        varcstrTriggers = null
    }

    companion object {
        var c: ComponentDefinition? = null
        var componentDefinition: Array<Array<ComponentDefinition?>?>? =
            arrayOfNulls(getInterfaceDefinitionsSize())

        @JvmStatic
        fun getInterfaceComponent(interfaceId: Int, componentId: Int): ComponentDefinition? {
            val inter = getInterface(interfaceId) ?: return null
            if (componentId < 0 || componentId >= inter.size) return null
            return inter[componentId]
        }

        @JvmStatic
        fun getInterface(id: Int): Array<ComponentDefinition?>? = getInterface(id, false)

        @JvmStatic
        fun getInterface(id: Int, reload: Boolean): Array<ComponentDefinition?>? {
            if (componentDefinition == null || id >= componentDefinition!!.size) return null
            if (componentDefinition!![id] == null || reload) {
                val size = getInterfaceDefinitionsComponentsSize(id)
                componentDefinition!![id] = arrayOfNulls(size)
                for (i in 0 until size) {
                    val data: ByteArray? = Cache.getStore().indexes[3].getFile(id, i)
                    if (data == null) { continue }
                    val version = data[0].toInt() and 0xFF
                    if (version != 255) continue
                    val defs = ComponentDefinition()
                    defs.componentHash = i + (id shl 16)
                    defs.decodeIf3(InputStream(data), i, id)
                    componentDefinition!![id]!![i] = defs
                }
            }

            return componentDefinition!![id]
        }

        @JvmStatic
        fun getInterfaceDefinitionsSize(): Int = Cache.getStore().indexes[3].lastArchiveId + 1

        @JvmStatic
        fun getInterfaceDefinitionsComponentsSize(interfaceId: Int): Int = Cache.getStore().indexes[3].getValidFilesCount(interfaceId)
    }

    class IComponentSettings(var settings: Int, var mask: Int) {
        fun rightClickAllowed(): Boolean = (settings and 0x1) != 0
        fun isRightClickOptionAllowed(optionID: Int): Boolean {
            require(optionID in 1..10) { "optionID must be 1-10" }
            return ((settings shr optionID) and 0x1) != 0
        }

        fun hasUnlockedSlots(): Boolean = ((settings and 0x2EAA42) shr 13) != 0
        val useOnMask: Int get() = (settings shr 11) and 0x7F
        fun canUseOnGroundItems(): Boolean = (useOnMask and 0x1) != 0
        fun canUseOnNpcs(): Boolean = (useOnMask and 0x2) != 0
        fun canUseOnObjects(): Boolean = (useOnMask and 0x4) != 0
        fun canUseOnOtherPlayers(): Boolean = (useOnMask and 0x8) != 0
        fun canUseOnSelf(): Boolean = (useOnMask and 0x10) != 0
        fun canUseOnInterfaceComponent(): Boolean = (useOnMask and 0x20) != 0
        fun interfaceEventDepth(): Int = (settings shr 18) and 0x7
        fun unknownBit21(): Boolean = ((settings shr 21) and 0x1) != 0
        fun canBeUsedOn(): Boolean = ((settings shr 22) and 0x1) != 0
        fun unknownBits28to30(): Boolean = ((settings shr 28) and 0x7) != 0
        fun unknownBit31(): Boolean = ((settings shr 31) and 0x1) != 0
    }
}
