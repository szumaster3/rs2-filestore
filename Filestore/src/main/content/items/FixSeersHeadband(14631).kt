package content.items

import com.alex.tools.pack.ItemPacker

object `FixSeersHeadband(14631)` {

    fun add() {
        ItemPacker.create()
            .editItem(14631) {
                name                        = "Seer's headband 1"
                maleEquipModelId1           = 45485
                femaleEquipModelId1         = 45486
                manhead     = 45483
                womanhead   = 45484
                changeModelColor(8997,5425)
                changeModelColor(8994,5420)
                changeModelColor(8990,5415)
                changeModelColor(8988,5410)
                changeModelColor(8986,5405)
                changeModelColor(8982,5400)
            }
            .save()
    }
}