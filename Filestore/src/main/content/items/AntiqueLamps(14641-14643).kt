package content.items

import com.alex.tools.pack.ItemPacker

object `AntiqueLamps(14641-14643)` {

    fun add() {
        val copy = ItemPacker.create().startAt(14641)

        copy.addItems(
            {
                name = "Antique lamp"
                invModelId = 3348
                cost = 1
                stackable = 0
                isMembers = true
                invModelZoom = 840
                xAngle2D = 28
                yAngle2D = 228
                xOffset2D = 2
                yOffset2D = -2
                changeModelColor(11191, 840)
                changeModelColor(11183, 563)
                team = 0
                iops = arrayOfNulls<String>(5).apply {
                    this[0] = "Rub"
                    this[4] = "Destroy"
                }
                ops = arrayOfNulls<String>(5).apply {
                    this[2] = "Take"
                }
                params = HashMap<Any, Any>().apply { put(59, "1") }
            },
            {
                name = "Antique lamp"
                invModelId = 3348
                cost = 1
                stackable = 0
                isMembers = true
                invModelZoom = 840
                xAngle2D = 28
                yAngle2D = 228
                xOffset2D = 2
                yOffset2D = -2
                changeModelColor(11191, 840)
                changeModelColor(11183, 563)
                team = 0
                iops = arrayOfNulls<String>(5).apply {
                    this[0] = "Rub"
                    this[4] = "Destroy"
                }
                ops = arrayOfNulls<String>(5).apply {
                    this[2] = "Take"
                }
                params = HashMap<Any, Any>().apply { put(59, "1") }
            },
            {
                name = "Antique lamp"
                invModelId = 3348
                cost = 1
                stackable = 0
                isMembers = true
                invModelZoom = 840
                xAngle2D = 28
                yAngle2D = 228
                xOffset2D = 2
                yOffset2D = -2
                changeModelColor(11191, 840)
                changeModelColor(11183, 563)
                team = 0
                iops = arrayOfNulls<String>(5).apply {
                    this[0] = "Rub"
                    this[4] = "Destroy"
                }
                ops = arrayOfNulls<String>(5).apply {
                    this[2] = "Take"
                }
                params = HashMap<Any, Any>().apply { put(59, "1") }
            }
        )

        copy.save()
    }
}