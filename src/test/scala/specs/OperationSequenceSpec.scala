package specs

import bloxorz.game.Game
import bloxorz.map.Field._
import bloxorz.map.Map
import bloxorz.map.creator.{ MapCreator, OperationSequence }
import mocks.MockUserInterface

class OperationSequenceSpec extends UnitSpec {
    def withMap(test: Map => Any): Unit = {
        val game = new Game(new MockUserInterface)
        val mapName = "maps/a.txt"

        game.loadMap(mapName)
        val loadedMap = game.getMap(mapName)

        test(loadedMap)
    }

    "Empty operation sequence" should "contain just identity function (y = x)" in withMap { map =>
        val sequence = new OperationSequence("TestSequence")
        val resultMap = sequence.execute(map)

        assert(map === resultMap)
    }

    "Operation sequence" should "execute its operations in the correct order" in withMap { map =>
        val sequence = new OperationSequence("TestSequence")

        sequence.attach(MapCreator.filter(5, 4, 5))
        sequence.attach(MapCreator.removeTraps)
        sequence.attach(MapCreator.setTrap(3, 9))
        sequence.attach(MapCreator.filter(3, 12, 5))

        val resultMap = sequence.execute(map)

        assert(resultMap.get(1, 4) === Plate)
        assert(resultMap.get(5, 4) === Plate)
        assert(resultMap.get(3, 9) === Trap)
        assert(resultMap.get(3, 12) === Plate)
    }

    "Operation sequence" should "return valid resulting map" in withMap { map =>
        val sequence = new OperationSequence("TestSequence")

        sequence.attach(MapCreator.setStart(3, 7))
        sequence.attach(MapCreator.setFinish(4, 3))
        sequence.attach(MapCreator.invert)

        val resultMap = sequence.execute(map)
        assert(resultMap.get(4, 3) === Start)
        assert(resultMap.get(3, 7) === Finish)
    }

    "Chaining operation sequences" should "work as well" in withMap { map =>
        val sequence1 = new OperationSequence("TestSequence1")
        val sequence2 = new OperationSequence("TestSequence2")

        sequence1.attach(MapCreator.addPlate(1, 5))
        sequence1.attach(MapCreator.addPlate(1, 6))

        sequence2.attach(OperationSequence.toOperation(sequence1))
        sequence2.attach(MapCreator.addPlate(0, 3))
        sequence2.attach(MapCreator.filter(0, 4, 1))
        sequence2.attach(MapCreator.addPlate(0, 5))

        val resultMap = sequence2.execute(map)
        for ((x, y) <- List((0, 3), (0, 4), (0, 5), (1, 5), (1, 6))) {
            assert(resultMap.get(x, y) === Plate)
        }
    }
}
