package content.items

import com.alex.tools.pack.ItemPacker

object `AgileTop(14647)` {

    fun add() {
        val copy = ItemPacker.create().startAt(14647)

        copy.addItems(
            {
                name = "Agile top"
                invModelId = 45475
                zoom2d = 1663
                xAngle2D = 593
                yAngle2D = 0
                zAngle2D = 0
                xOffset2D = 0
                yOffset2D = 1
                stackable = 0
                cost = 1
                members = true
                maleEquipModelId1 = 45476
                maleEquipModelId2 = 45478
                femaleEquipModelId1 = 45477
                femaleEquipModelId2 = 45479
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