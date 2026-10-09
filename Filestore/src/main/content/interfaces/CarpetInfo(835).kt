package content.interfaces

import com.alex.tools.pack.IfacePacker
import content.data.Color
import content.data.ComponentType
import content.data.InterfaceFont

object `CarpetInfo(835)` {
    fun add() {
        fun hash(id: Int) = (835 shl 16) or id
        val packer = IfacePacker.newInterface()
        packer.startAt(0)
            .addComponent {
                type = ComponentType.FIGURE
                baseY = 22
                baseWidth = 190
                baseHeight = 202
                filled = true
                color = Color.BROWN
                transparency = 150
            }
            // 1
            .addComponent {
                type = ComponentType.SPRITE
                baseX = 1
                baseY = 22
                baseWidth = 189
                baseHeight = 202
                spriteId = 1136
                spriteTiling = true
                transparency = 100
            }
            // 2
            .addComponent {
                type = ComponentType.FIGURE
                baseY = 22
                baseWidth = 190
                baseHeight = 202
                filled = true
                color = Color.BROWN
                transparency = 150
            }
            // 3
            .addComponent {
                type = ComponentType.SPRITE
                baseY = 9
                baseWidth = 190
                baseHeight = 32
                spriteId = 987
                spriteTiling = true
            }
            // 4
            .addComponent {
                type = ComponentType.SPRITE
                baseY = 210
                baseWidth = 190
                baseHeight = 32
                spriteId = 987
                spriteTiling = true
            }
            // 5
            .addComponent {
                type = ComponentType.TEXT
                baseX = 2
                baseY = 3
                baseWidth = 186
                baseHeight = 17
                text = "~ What is this? ~"
                textVerticalAli = 1
                textHorizontalAli = 1
                fontId = InterfaceFont.B12_FULL
                color = Color.YELLOW
            }
            // 6
            .addComponent {
                type = ComponentType.LAYER
                baseX = 2
                baseY = 24
                baseWidth = 170
                baseHeight = 199
                scrollWidth = 170
                scrollHeight = 442
                rightClickOptions = arrayOf("", "", "", "", "", "", "", "", "", "")
            }
            // 7
            .addComponent {
                type = ComponentType.LAYER
                baseX = 174
                baseY = 24
                baseWidth = 16
                baseHeight = 199
                onLoadScript = arrayOf(63, -2147483645, hash(6), 792, 789, 790, 791, 773, 788)
            }
            // 8
            .addComponent {
                type = ComponentType.LAYER
                baseX = 5
                baseY = 229
                baseWidth = 180
                baseHeight = 30
                onLoadScript = arrayOf(94,-2147483645)
                onMouseHoverScript = arrayOf(92,-2147483645)
                onMouseLeaveScript = arrayOf(94,-2147483645)
            }
            // 9
            .addComponent {
                type = ComponentType.TEXT
                baseX = 5
                baseY = 229
                baseWidth = 180
                baseHeight = 30
                textVerticalAli = 1
                textHorizontalAli = 1
                fontId = InterfaceFont.B12_FULL
                color = Color.YELLOW
                optionMask = 2
                rightClickOptions = arrayOf("Select")
            }
            // 10
            .addComponent {
                type = ComponentType.TEXT
                baseWidth = 170
                baseHeight = 442
                parentId = 6
                fontId = InterfaceFont.TUTORIAL_FONT
                color = Color.WHITE
            }
            // 11
            .addComponent {
                type = ComponentType.SPRITE
                baseX = 172
                baseY = 3
                baseWidth = 16
                baseHeight = 16
                spriteId = 831
                optionMask = 2
                rightClickOptions = arrayOf("Close")
                onMouseHoverScript = arrayOf(44, -2147483645, 832)
                onMouseLeaveScript = arrayOf(44, -2147483645, 831)
                onOptionClick = arrayOf(29)
            }
            .save()
    }
}