package specs

import bloxorz.game.Game
import bloxorz.map.Field._
import bloxorz.map.Map
import bloxorz.map.creator.MapCreator
import mocks.MockUserInterface

class PredefinedOperationsSpec extends UnitSpec {
    def withMap(test: Map => Any): Unit = {
        val game = new Game(new MockUserInterface)
        val mapName = "maps/a.txt"

        game.loadMap(mapName)
        val loadedMap = game.getMap(mapName)

        test(loadedMap)
    }

    "MapCreator.invert" should "swap Start and Finish positions" in withMap { map =>
        val (x1, y1) = (2, 2)
        val (x2, y2) = (5, 9)
        assert(map.get(x1, y1) === Start)
        assert(map.get(x2, y2) === Finish)

        val editedMap = MapCreator.invert(map)
        assert(editedMap.get(x1, y1) === Finish)
        assert(editedMap.get(x2, y2) === Start)
    }

    "MapCreator.removeTraps" should "replace all Traps on the map with regular Plates" in withMap { map =>
        val trapPositions = map.getTrapPositions
        assert(trapPositions.size === 1)
        assert(trapPositions.head === (1, 4))

        val editedMap = MapCreator.removeTraps(map)

        val newTrapPositions = editedMap.getTrapPositions
        assert(newTrapPositions.isEmpty)
        assert(map.get(1, 4) === Plate)
    }

    "MapCreator.filter" should "replace all fields in horizontal and vertical range of N " +
        "from a Trap with regular Plates" in withMap { map =>
        val (x, y) = (5, 4)
        val n = 5
        assert(map.get(x, y) === Empty)

        val editedMap = MapCreator.filter(x, y, n)(map)
        assert(editedMap.get(x, y) === Plate)
    }
}
