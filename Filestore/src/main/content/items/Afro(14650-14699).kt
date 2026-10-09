package content.items

import com.alex.tools.pack.ItemPacker

object `Afro(14650-14699)` {
    fun add() {
        val packer = ItemPacker.create().startAt(14650)
        val afroData = listOf(
            Triple("Burgundy afro",     908,    899),
            Triple("Red afro",          571,    581),
            Triple("Vermilion afro",    2983,   2973),
            Triple("Pink afro",         468,    478),
            Triple("Orange afro",       5698,   5688),
            Triple("Yellow afro",       7752,   7742),
            Triple("Peach afro",        3650,   3640),
            Triple("Brown afro",        4634,   4624),
            Triple("Dark brown afro",   6540,   6535),
            Triple("Light brown afro",  5677,   5667),
            Triple("Mint green afro",   21830,  21820),
            Triple("Green afro",        22433,  22423),
            Triple("Dark green afro",   28946,  28936),
            Triple("Dark blue afro",    43691,  43681),
            Triple("Turquoise afro",    33697,  33687),
            Triple("Light blue afro",   38362,  38352),
            Triple("Purple afro",       51526,  51516),
            Triple("Violet afro",       54193,  54183),
            Triple("Indigo afro",       49835,  49825),
            Triple("Dark grey afro",    20,     15),
            Triple("Military grey afro",10295,  10285),
            Triple("White afro",        107,    98),
            Triple("Light grey afro",   10438,  10428),
            Triple("Taupe afro",        5281,   5271),
            Triple("Black afro",        8,      1)
        )

        afroData.forEach { (name, recol1d, recol2d) ->
            packer.addItemWithNote {
                this.name = name
                invModelId = 45467
                zoom2d = 728
                xAngle2D = 30
                yAngle2D = 180
                zAngle2D = 0
                xOffset2D = 0
                yOffset2D = 1
                stackable = 0
                cost = 50
                members = true
                maleEquipModelId1 = 45470
                femaleEquipModelId1 = 45471
                manhead = 45468
                womanhead = 45469
                stockMarket = false
                team = 0
                ops = arrayOfNulls<String>(5).apply { this[2] = "Take" }
                iops = arrayOfNulls<String>(5).apply { this[1] = "Wear"; this[4] = "Drop" }
                changeModelColor(6805, recol1d)
                changeModelColor(6794, recol2d)
            }
        }
        packer.save()
    }
}