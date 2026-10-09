package content.interfaces

import com.alex.tools.pack.IfacePacker
import content.data.Color
import content.data.ComponentType

object `GuildHallOverlay(834)` {

    fun add() {

        val packer = IfacePacker.newInterface()

        packer
            .startAt(0)
            .addComponent {
                type = ComponentType.MODEL
                baseX = 122
                baseY = 18
                baseWidth = 264
                baseHeight = 76
                modelId = 10119
                modelOriginY = -8
                modelXAngle = 512
                modelZoom = 576
                unknownModelProp3 = 64938
                unknownModelProp4 = 1171
                modelType = 1
            }
            .addComponent {
                type = ComponentType.TEXT
                baseX = 122
                baseY = 30
                baseWidth = 263
                baseHeight = 48
                color = Color.ORANGE
                text = "Guild Hall"
                textHorizontalAli = 1
                textVerticalAli = 0
                fontId = 645
            }
            .save()
    }
}