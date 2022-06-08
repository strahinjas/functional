package specs

import bloxorz.game.Game
import bloxorz.game.Outcome.Victory
import mocks.MockUserInterface

import java.nio.file.{ Files, Paths }

class FindSolutionSpec extends UnitSpec {
    def withGame(test: Game => Any): Unit = {
        val game = new Game(new MockUserInterface)
        val mapName = "maps/a.txt"

        game.loadMap(mapName)
        game.start(mapName)

        test(game)
    }

    "Game.findSolution" should "create a solution file with the name of the map " +
        "if a file name is not provided" in withGame { game =>
        val solutionFileName = "solutions/a.txt"

        game.findSolution("")
        assert(Files.exists(Paths.get(solutionFileName)))
    }

    it should "create a move sequence that could be successfully loaded" in withGame { game =>
        val solutionFileName = "solutions/a.txt"
        game.findSolution(solutionFileName)

        assert(game.loadMoveSequence(solutionFileName).isSuccess)
    }

    it should "create a move sequence that would lead to Victory outcome " +
        "if one such sequence exists" in withGame { game =>
        val solutionFileName = "solutions/a.txt"

        val moves = game.findSolution(solutionFileName)
        assert(moves.nonEmpty)

        game.loadMoveSequence(solutionFileName)
        assert(game.executeMoveSequence() === Victory)
    }

    it should "return empty list of moves if there is no possible solution" in withGame { game =>
        val solutionFileName = "solutions/d.txt"

        val moves = game.findSolution(solutionFileName)
        assert(moves.isEmpty)
    }
}
