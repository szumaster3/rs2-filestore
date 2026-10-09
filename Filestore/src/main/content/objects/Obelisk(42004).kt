package content.objects

import com.alex.tools.pack.LocPacker

object `Obelisk(42004)` {

    fun add() {
        LocPacker.create()
            .startAt(42004)
            .addLoc {
                name = "Obelisk"
                sizeX = 2
                sizeY = 2
                blockProjectile = true
                models = intArrayOf(31686)
                actions = arrayOfNulls<String>(5).apply {
                    this[0] = "Renew-points"
                    this[4] = "Remove"
                }
                animations = intArrayOf(8510)
                modelSizeX = 128
                modelSizeY = 128
                modelSizeZ = 128
                blocksSky = true
                castsShadow = true
                cullingType = 1
                brightness = 10
                contrast = 420
                animateImmediately = true
            }
            .save()
    }
}