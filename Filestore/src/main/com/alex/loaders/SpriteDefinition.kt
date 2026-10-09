package com.alex.loaders

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException
import java.nio.ByteBuffer

/**
 * Represents a Sprite Archive.
 *
 * @author Graham
 * @author Discardedx2
 */
class SpriteDefinition @JvmOverloads constructor(
    width: Int,
    height: Int, size: Int = 1
) {

    private val width: Int
    private val height: Int
    private val frames: Array<BufferedImage?>

    init {
        require(size >= 1)
        this.width = width
        this.height = height
        this.frames = arrayOfNulls(size)
    }

    fun getSprite(id: Int): BufferedImage? = frames[id]

    fun setFrame(id: Int, frame: BufferedImage) {
        require(frame.width == width && frame.height == height) {
            "Frame size mismatch"
        }
        frames[id] = frame
    }

    fun size(): Int = frames.size

    @Throws(IOException::class)
    fun encode(): ByteBuffer {
        val bout = ByteArrayOutputStream()
        val os = DataOutputStream(bout)

        os.use {

            val palette = ArrayList<Int>()
            palette.add(0)

            val frameData = mutableListOf<Pair<Array<IntArray>, Boolean>>()

            for (image in frames) {
                requireNotNull(image)

                val indices = Array(width) { IntArray(height) }
                var hasAlpha = false

                for (x in 0 until width) {
                    for (y in 0 until height) {

                        val argb = image.getRGB(x, y)
                        val alpha = (argb ushr 24) and 0xFF

                        if (alpha == 0) {
                            indices[x][y] = 0
                            continue
                        }

                        val rgb = argb and 0xFFFFFF
                        hasAlpha = hasAlpha || (alpha != 255)

                        var index = palette.indexOf(rgb)
                        if (index == -1) {
                            if (palette.size >= 256) {
                                throw IOException("Too many colours in this sprite!")
                            }
                            palette.add(rgb)
                            index = palette.size - 1
                        }

                        indices[x][y] = index
                    }
                }

                frameData.add(indices to hasAlpha)
            }

            for ((indices, hasAlpha) in frameData) {

                var flags = FLAG_VERTICAL
                if (hasAlpha) flags = flags or FLAG_ALPHA

                os.write(flags)

                // indices
                for (x in 0 until width) {
                    for (y in 0 until height) {
                        os.write(indices[x][y])
                    }
                }

                // alpha
                if (hasAlpha) {
                    for (x in 0 until width) {
                        for (y in 0 until height) {
                            val rgb = frames[0]!!.getRGB(x, y)
                            val alpha = (rgb ushr 24) and 0xFF
                            os.write(alpha)
                        }
                    }
                }
            }

            // palette
            for (i in 1 until palette.size) {
                val rgb = palette[i]
                os.write((rgb shr 16) and 0xFF)
                os.write((rgb shr 8) and 0xFF)
                os.write(rgb and 0xFF)
            }

            os.writeShort(width)
            os.writeShort(height)
            os.write(palette.size - 1)

            for (i in frames.indices) {
                os.writeShort(0)
                os.writeShort(0)
                os.writeShort(width)
                os.writeShort(height)
            }

            os.writeShort(frames.size)

            return ByteBuffer.wrap(bout.toByteArray())
        }
    }

    companion object {

        const val FLAG_VERTICAL = 0x01
        const val FLAG_ALPHA = 0x02

        fun decode(buffer: ByteBuffer): SpriteDefinition? {
            return try {

                buffer.position(buffer.limit() - 2)
                val size = buffer.short.toInt() and 0xFFFF

                val offsetsX = IntArray(size)
                val offsetsY = IntArray(size)
                val subWidths = IntArray(size)
                val subHeights = IntArray(size)

                buffer.position(buffer.limit() - size * 8 - 7)

                val width = buffer.short.toInt() and 0xFFFF
                val height = buffer.short.toInt() and 0xFFFF
                val paletteSize = (buffer.get().toInt() and 0xFF) + 1

                val palette = IntArray(paletteSize)
                palette[0] = 0

                val sprite = SpriteDefinition(width, height, size)

                for (i in 0 until size) offsetsX[i] = buffer.short.toInt() and 0xFFFF
                for (i in 0 until size) offsetsY[i] = buffer.short.toInt() and 0xFFFF
                for (i in 0 until size) subWidths[i] = buffer.short.toInt() and 0xFFFF
                for (i in 0 until size) subHeights[i] = buffer.short.toInt() and 0xFFFF

                buffer.position(buffer.limit() - size * 8 - 7 - (paletteSize - 1) * 3)

                for (i in 1 until paletteSize) {
                    val r = buffer.get().toInt() and 0xFF
                    val g = buffer.get().toInt() and 0xFF
                    val b = buffer.get().toInt() and 0xFF
                    palette[i] = (r shl 16) or (g shl 8) or b
                }

                buffer.position(0)

                for (i in 0 until size) {

                    val img = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
                    val subW = subWidths[i]
                    val subH = subHeights[i]
                    val offX = offsetsX[i]
                    val offY = offsetsY[i]

                    val indices = Array(subW) { IntArray(subH) }

                    val flags = buffer.get().toInt() and 0xFF

                    for (x in 0 until subW) {
                        for (y in 0 until subH) {
                            indices[x][y] = buffer.get().toInt() and 0xFF
                        }
                    }

                    if ((flags and FLAG_ALPHA) != 0) {
                        for (x in 0 until subW) {
                            for (y in 0 until subH) {
                                val alpha = buffer.get().toInt() and 0xFF
                                val idx = indices[x][y]
                                val rgb = palette[idx]

                                val argb = (alpha shl 24) or rgb
                                img.setRGB(x + offX, y + offY, argb)
                            }
                        }
                    } else {
                        for (x in 0 until subW) {
                            for (y in 0 until subH) {
                                val idx = indices[x][y]

                                if (idx == 0) {
                                    img.setRGB(x + offX, y + offY, 0x00000000)
                                } else {
                                    img.setRGB(x + offX, y + offY, 0xFF000000.toInt() or palette[idx])
                                }
                            }
                        }
                    }

                    sprite.frames[i] = img
                }

                sprite

            } catch (e: Exception) {
                null
            }
        }
    }
}