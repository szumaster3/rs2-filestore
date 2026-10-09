package content

import com.alex.Cache
import com.alex.loaders.*
import com.alex.store.Store
import com.alex.tools.dump.MapDumper
import com.alex.tools.dump.ModelDumper
import com.alex.tools.dump.SpriteDumper
import com.alex.tools.pack.ModelPacker
import com.alex.tools.pack.SpritePacker
import com.alex.util.DefinitionLink
import content.ContentPaths.DUMPS
import content.ContentPaths.XTEAS
import content.interfaces.`CarpetInfo(835)`
import content.interfaces.`GuildHallOverlay(834)`
import content.items.*
import content.npcs.`GuildHallOfficer(8591)`
import content.objects.`Obelisk(42004)`
import java.io.File
import java.nio.file.Paths

object ContentLoader {
    @JvmStatic
    fun main(args: Array<String>) {
        runCatching {
            Cache.init()
            load()
            // print()
            // dumpMaps()
        }.onFailure { e -> e.printStackTrace() }
    }

    private fun load() {
        maps()
        models()
        sprites()
        interfaces()
        objects()
        items()
        npcs()
        worldmap()
    }

    private fun interfaces() {
        `GuildHallOverlay(834)`.add()
        `CarpetInfo(835)`.add()
    }

    private fun items() {
        `FixSeersHeadband(14631)`.add()
        `ArdougneCloaks(14638-14640)`.add()
        `AntiqueLamps(14641-14643)`.add()
        `SummoningObelisk(14644)`.add()
        `SeersHeadbands(14645-14646)`.add()
        `AgileTop(14647)`.add()
        `AgileLegs(14648)`.add()
        `RandomEventGift(14649)`.add()
        `Afro(14650-14699)`.add()
    }

    private fun objects() {
        `Obelisk(42004)`.add()
    }

    private fun models() {
        ModelPacker.add()
    }

    private fun sprites() {
        SpritePacker.add()
        SpritePacker.packLogo()
    }

    private fun npcs() {
        `GuildHallOfficer(8591)`.add()
    }

    private fun print() {
        val store = Cache.getStore()

        val dumps = listOf<Pair<String, (Store, String) -> Unit>>(
            "object_dumps.txt"    to LocDefinition::print,
            "item_dumps.txt"      to ItemDefinition::print,
            "bas_dumps.txt"       to BasDefinition::print,
            "npc_dumps.txt"       to NpcDefinition::print,
            "enum_dumps.txt"      to EnumDefinition::print,
            "flo_dumps.txt"       to FloDefinition::print,
            "flu_dumps.txt"       to FluDefinition::print,
            "idk_dumps.txt"       to IdkDefinition::print,
            "param_dumps.txt"     to ParamDefinition::print,
            "seq_dumps.txt"       to SeqDefinition::print,
            "struct_dumps.txt"    to StructDefinition::print,
        )

        dumps.forEach { (def, printer) ->
            println("Print $def")
            printer(store, "../${DUMPS}/$def")
        }
    }

    private fun dump() {
        SpriteDumper.dump()
        ModelDumper.dump()
    }

    private fun link() {
        val dumper = DefinitionLink(Cache.getStore())
        dumper.run(File("../${DUMPS}/linked_definitions.txt"))
    }

    private fun maps() {
    }

    private fun worldmap() {}

    private fun dumpMaps() {
        MapDumper.init(Paths.get("../${XTEAS}"));
        MapDumper.verify(Cache.getStore())
    }
}
