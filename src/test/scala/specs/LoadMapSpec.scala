package specs

import bloxorz.game.Game
import mocks.MockUserInterface

import java.io.FileNotFoundException

class LoadMapSpec extends UnitSpec {
    "Game.loadMap" should "return Success(true) if the map is loaded correctly" in {
        val game = new Game(new MockUserInterface)
        val result = game.loadMap("maps/a.txt")

        assert(result.isSuccess)
        assert(result.toEither.right.value === true)
    }

    it should "return Failure with IllegalArgumentException if the map is not in the correct format" in {
        val game = new Game(new MockUserInterface)
        val result = game.loadMap("maps/b.txt")

        assert(result.isFailure)
        assert(result.toEither.left.value.isInstanceOf[IllegalArgumentException])
    }

    it should "return Failure with FileNotFoundException if the file does not exist" in {
        val game = new Game(new MockUserInterface)
        val result = game.loadMap("a.txt")

        assert(result.isFailure)
        assert(result.toEither.left.value.isInstanceOf[FileNotFoundException])
    }
}
