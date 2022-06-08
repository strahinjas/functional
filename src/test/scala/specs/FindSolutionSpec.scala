package specs

import bloxorz.game.Game
import bloxorz.game.Outcome.Victory
import mocks.MockUserInterface

import java.nio.file.{ Files, Paths }

class FindSolutionSpec extends UnitSpec {
    def withGame(mapName: String)(test: Game => Any): Unit = {
        val game = new Game(new MockUserInterface)

        game.loadMap(mapName)
        game.start(mapName)

        test(game)
    }

    "Game.findSolution" should "create a move sequence that could be " +
        "successfully loaded" in withGame("maps/a.txt") { game =>
        val solutionFileName = "solutions/a.txt"
        game.findSolution(solutionFileName)

        assert(Files.exists(Paths.get(solutionFileName)))
        assert(game.loadMoveSequence(solutionFileName).isSuccess)
    }

    it should "create a move sequence that would lead to Victory outcome " +
        "if one such sequence exists" in withGame("maps/a.txt") { game =>
        val solutionFileName = "solutions/a.txt"

        val moves = game.findSolution(solutionFileName)
        assert(moves.nonEmpty)

        game.loadMoveSequence(solutionFileName)
        assert(game.executeMoveSequence() === Victory)
    }

    it should "return empty list of moves if there is no " +
        "possible solution" in withGame("maps/d.txt") { game =>
        val solutionFileName = "solutions/d.txt"

        val moves = game.findSolution(solutionFileName)
        assert(moves.isEmpty)
    }
}
