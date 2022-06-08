package specs

import bloxorz.game.Direction._
import bloxorz.game.Game
import mocks.MockUserInterface

class MoveBlockSpec extends UnitSpec {
    def withGame(test: Game => Any): Unit = {
        val game = new Game(new MockUserInterface)
        val mapName = "maps/a.txt"

        game.loadMap(mapName)
        game.start(mapName)

        test(game)
    }

    "Game.start" should "create a copy of the selected map and " +
        "move block to its starting position" in withGame { game =>
        assert(game.getBlock.position == game.getSelectedMapName.getStartPosition)
    }

    "Game.move" should "move the block in the given direction" in withGame { game =>
        assert(game.getBlock.position === (2, 2))

        game.move(Down)
        assert(game.getBlock.position === (3, 2))

        game.move(Right)
        assert(game.getBlock.position === (3, 3))
    }

    "Game.executeMoveSequence" should "move the block in the given direction sequence" in withGame { game =>
        game.loadMoveSequence("moves/test.txt")
        game.executeMoveSequence()

        assert(game.getBlock.position === (3, 3))
    }
}
