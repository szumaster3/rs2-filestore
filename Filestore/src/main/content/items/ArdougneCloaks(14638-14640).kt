package content.items

import com.alex.tools.pack.ItemPacker

object `ArdougneCloaks(14638-14640)` {

    fun add() {
        val copy = ItemPacker.create().startAt(14638)

        copy.addItems(
            {
                name = "Ardougne cloak 1"
                invModelId = 45482
                cost = 0
                stackable = 0
                isMembers = true
                invModelZoom = 2140
                xAngle2D = 400
                yAngle2D = 948
                xOffset2D = 3
                yOffset2D = 6
                changeModelColor(4, 181)
                maleEquipModelId1 = 45480
                femaleEquipModelId1 = 45481
                team = 0
                iops = arrayOfNulls<String>(5).apply {
                    this[1] = "Wear"
                    this[3] = "Teleports"
                    this[4] = "Destroy"
                }
                ops = arrayOfNulls<String>(5).apply { this[2] = "Take" }
            },
            {
                name = "Ardougne cloak 2"
                invModelId = 45482
                cost = 0
                stackable = 0
                isMembers = true
                invModelZoom = 2140
                xAngle2D = 400
                yAngle2D = 948
                xOffset2D = 3
                yOffset2D = 6
                changeModelColor(4, 168)
                changeModelColor(46112, 46109)
                changeModelColor(46110, 46107)
                maleEquipModelId1 = 45480
                femaleEquipModelId1 = 45481
                team = 0
                iops = arrayOfNulls<String>(5).apply {
                    this[1] = "Wear"
                    this[3] = "Teleports"
                    this[4] = "Destroy"
                }
                ops = arrayOfNulls<String>(5).apply { this[2] = "Take" }
            },
            {
                name = "Ardougne cloak 3"
                invModelId = 45482
                cost = 0
                stackable = 0
                isMembers = true
                invModelZoom = 2140
                xAngle2D = 400
                yAngle2D = 948
                xOffset2D = 3
                yOffset2D = 6
                maleEquipModelId1 = 45480
                femaleEquipModelId1 = 45481
                team = 0
                iops = arrayOfNulls<String>(5).apply {
                    this[1] = "Wear"
                    this[3] = "Teleports"
                    this[4] = "Destroy"
                }
                ops = arrayOfNulls<String>(5).apply { this[2] = "Take" }
            }
        )

        copy.save()
    }
}