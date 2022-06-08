package specs

import bloxorz.game.Game
import bloxorz.map.Field._
import bloxorz.map.Map
import bloxorz.map.creator.MapCreator
import mocks.MockUserInterface

class BasicOperationsSpec extends UnitSpec {
    def withMap(test: Map => Any): Unit = {
        val game = new Game(new MockUserInterface)
        val mapName = "maps/a.txt"

        game.loadMap(mapName)
        val loadedMap = game.getMap(mapName)

        test(loadedMap)
    }

    "MapCreator.removePlate" should "replace regular Plate with an Empty field" in withMap { map =>
        val (x, y) = (4, 4)
        assert(map.get(x, y) === Plate)

        val editedMap = MapCreator.removePlate(x, y)(map)
        assert(editedMap.get(x, y) === Empty)
    }

    "MapCreator.addPlate" should "replace Empty field with a regular Plate" in withMap { map =>
        val (x, y) = (0, 3)
        assert(map.get(x, y) === Empty)

        val editedMap = MapCreator.addPlate(x, y)(map)
        assert(editedMap.get(x, y) === Plate)
    }

    "MapCreator.setTrap" should "replace regular Plate with a Trap" in withMap { map =>
        val (x, y) = (4, 4)
        assert(map.get(x, y) === Plate)

        val editedMap = MapCreator.setTrap(x, y)(map)
        assert(editedMap.get(x, y) === Trap)
    }

    "MapCreator.removeTrap" should "replace Trap with a regular Plate" in withMap { map =>
        val (x, y) = (1, 4)
        assert(map.get(x, y) === Trap)

        val editedMap = MapCreator.removeTrap(x, y)(map)
        assert(editedMap.get(x, y) === Plate)
    }

    "MapCreator.setStart" should "move Start field to the given position" in withMap { map =>
        val (x1, y1) = (2, 2)
        val (x2, y2) = (4, 4)
        assert(map.get(x1, y1) === Start)
        assert(map.get(x2, y2) === Plate)

        val editedMap = MapCreator.setStart(x2, y2)(map)
        assert(editedMap.get(x1, y1) === Plate)
        assert(editedMap.get(x2, y2) === Start)
    }

    "MapCreator.setFinish" should "move Finish field to the given position" in withMap { map =>
        val (x1, y1) = (5, 9)
        val (x2, y2) = (4, 4)
        assert(map.get(x1, y1) === Finish)
        assert(map.get(x2, y2) === Plate)

        val editedMap = MapCreator.setFinish(x2, y2)(map)
        assert(editedMap.get(x1, y1) === Plate)
        assert(editedMap.get(x2, y2) === Finish)
    }
}
