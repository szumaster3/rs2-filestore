package content.items

import com.alex.tools.pack.ItemPacker

object `AgileLegs(14648)` {
    fun add() {
        val copy = ItemPacker.create().startAt(14648)

        copy.addItems(
            {
                name = "Agile legs"
                invModelId = 45472
                zoom2d = 1979
                xAngle2D = 458
                yAngle2D = 0
                zAngle2D = 0
                xOffset2D = 0
                yOffset2D = 4
                stackable = 0
                cost = 1
                members = true
                maleEquipModelId1 = 455473
                femaleEquipModelId1 = 45474
                stockMarket = false
                team = 0
                ops = arrayOfNulls<String>(5).apply { this[2] = "Take" }
                iops = arrayOfNulls<String>(5).apply {
                    this[1] = "Wear"
                    this[4] = "Destroy"
                }
            }
        )
        copy.save()
    }
}