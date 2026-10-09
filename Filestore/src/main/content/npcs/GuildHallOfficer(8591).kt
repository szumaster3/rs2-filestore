package content.npcs

import com.alex.loaders.BasDefinition
import com.alex.tools.pack.NpcPacker

object `GuildHallOfficer(8591)` {

    fun add() {

        val packer = NpcPacker.create().startAt(8591)

        packer.addNpc {
            name = "Guild Hall officer"
            size = 1
            modelIndices = intArrayOf(45498)
            headmodels = intArrayOf(39706,45497)
            recol_s = shortArrayOf(6798, 6165)
            recol_d = shortArrayOf(6252, 41279.toShort())
            options = arrayOf("Talk-to", null, null, null, null)
            resizeX = 128
            resizeY = 128
            hasshadow = true
            headicon = 0
            loginscreenproperties = 3
            minimapdisplay = true
            interactive = true
            rotationflag = true
            shadowcolor1 = 0
            shadowcolor2 = 0
            shadowcolormodifier1 = 0
            shadowcolormodifier2 = 0
            combatLevel = 0
            rotationspeed = 32
            spawndirection = 7
            hitBarId = 0
            attackCursor = 0
            iconHeight = 0
            minimapmarkerobjectentry = 0
            val basId = 1491
            bastypeid = basId
            val bas = BasDefinition(basId).apply {
                idleAnimationId = 808
                walkAnimation = 819
                runAnimationId = 824
                walkFullTurnAnimationId = 820
                walkCCWTurnAnimationId = 821
                walkCWTurnAnimationId = 822
                standingCCWTurn = 823
                standingCWTurn = 823
            }
            basDefinition[basId] = bas
        }

        packer.save()
    }
}