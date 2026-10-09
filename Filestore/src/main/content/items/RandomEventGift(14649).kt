package content.items

import com.alex.tools.pack.ItemPacker

object `RandomEventGift(14649)` {
    fun add() {
        val copy = ItemPacker.create().startAt(14649)

        copy.addItems(
            {
                name = "Random event gift"
                invModelId = 2426
                zoom2d = 1180
                xAngle2D = 97
                yAngle2D = 1895
                zAngle2D = 0
                xOffset2D = 0
                yOffset2D = -8
                stackable = 0
                cost = 100
                members = false
                stockMarket = false
                ambient = 5
                team = 0
                changeModelColor(22410,60325)
                iops = arrayOfNulls<String>(5).apply {
                    this[0] = "Open"
                    this[4] = "Drop"
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