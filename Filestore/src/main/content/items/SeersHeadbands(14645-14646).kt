package content.items

import com.alex.tools.pack.ItemPacker

object `SeersHeadbands(14645-14646)` {

    fun add() {
        val copy = ItemPacker.create().startAt(14645)

        copy.addItems(
            {
                name = "Seer's headband 2"
                invModelId = 45487
                zoom2d = 724
                xAngle2D = 111
                yAngle2D = 1841
                zAngle2D = 0
                xOffset2D = -3
                yOffset2D = -8
                stackable = 0
                cost = 1
                members = true
                maleEquipModelId1 = 45490
                femaleEquipModelId1 = 45491
                manhead = 45488
                womanhead = 45489
                stockMarket = false
                team = 0
                ops = arrayOfNulls<String>(5).apply { this[2] = "Take" }
                iops = arrayOfNulls<String>(5).apply {
                    this[1] = "Wear"
                    this[4] = "Destroy"
                }
                val originalModelColors = arrayOf(8997,8994,8990,8988,8986,8982)
                val modifiedModelColors = arrayOf(30803,30798,30793,30788,30783,30778)
                for (i in originalModelColors.indices) {
                    changeModelColor(originalModelColors[i], modifiedModelColors[i])
                }
            },
            {
                name = "Seer's headband 3"
                invModelId = 45492
                zoom2d = 852
                xAngle2D = 111
                yAngle2D = 1841
                zAngle2D = 0
                xOffset2D = -3
                yOffset2D = -3
                stackable = 0
                cost = 1
                members = true
                maleEquipModelId1 = 45495
                femaleEquipModelId1 = 45496
                manhead = 45493
                womanhead = 45494
                stockMarket = false
                team = 0
                ops = arrayOfNulls<String>(5).apply { this[2] = "Take" }
                iops = arrayOfNulls<String>(5).apply {
                    this[1] = "Wear"
                    this[4] = "Destroy"
                }
                val originalModelColors = arrayOf(8997,8994,8990,8988,8986,8982)
                val modifiedModelColors = arrayOf(9043,9038,9033,9023,9018,9013)
                for (i in originalModelColors.indices) {
                    changeModelColor(originalModelColors[i], modifiedModelColors[i])
                }
            }
        )
        copy.save()
    }
}