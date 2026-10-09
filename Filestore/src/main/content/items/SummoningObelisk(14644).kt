package content.items

import com.alex.tools.pack.ItemPacker

object `SummoningObelisk(14644)` {

    fun add() {
        val copy = ItemPacker.create().startAt(14644)
        copy.addItems(
            {
                name = "Summoning obelisk"
                invModelId = 31686
                zoom2d = 5456
                xAngle2D = 210
                yAngle2D = 1880
                zAngle2D = 0
                xOffset2D = 0
                yOffset2D = -100
                stackable = 0
                cost = 1
                members = false
                dummyItem = 1
                stockMarket = false
                team = 0
                iops = arrayOfNulls<String>(5).apply {
                    this[4] = "Drop"
                }
                ops = arrayOfNulls<String>(5).apply {
                    this[2] = "Take"
                }
                params = HashMap<Any, Any>().apply {
                    put(211, "${8786}")
                    put(212, "1")    // Amount
                    put(213, "${12183}")
                    put(214, "1000") // Amount
                    put(215, "${12160}")
                    put(216, "10")   // Amount
                    put(217, "${12163}")
                    put(218, "10")   // Amount

                }
            },
        )

        copy.save()
    }
}