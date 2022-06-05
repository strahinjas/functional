package bloxorz.console

import bloxorz.console.Phase._
import bloxorz.game.Outcome._
import bloxorz.game.{ Direction, UserInterface }
import bloxorz.map.Map

import scala.annotation.tailrec
import scala.util.{ Failure, Success }

class CommandLineUserInterface extends UserInterface {
    private var editedMap: Map = _

    private def mapSelector(): String = {
        println("Available maps:")

        val maps = game.getLoadedMaps

        for ((map, index) <- maps.zipWithIndex) {
            println(s"${index + 1}. $map")
        }

        maps(safeReadOption(maps.length))
    }

    private def mainMenu(option: Int = 0): Phase = option match {
        case 0 =>
            println("1. Load map from file")
            println("2. Start game")
            println("3. Create new map")
            println("4. Quit game")

            Phase.MainMenu
        case 1 =>
            print("Please enter map file name: ")
            val fileName = scala.io.StdIn.readLine()

            game.loadMap(fileName) match {
                case Success(_) =>
                    println(s"Map '$fileName' was successfully loaded.")
                case Failure(exception) if exception.isInstanceOf[IllegalArgumentException] =>
                    println("Unsupported map format provided.")
                case Failure(_) =>
                    println(s"Map '$fileName' could not be found.")
            }

            Phase.MainMenu
        case 2 =>
            val selectedMap = mapSelector()
            game.start(selectedMap)

            println(s"Map '$selectedMap' is selected.")
            println()
            println("Game has started.")
            println("Block is represented with 'X' on the grid.")
            println()
            game.printMap()

            Phase.InGame
        case 3 =>
            println("Please choose the map you want to edit.")

            val mapName = mapSelector()
            editedMap = game.getMap(mapName)

            println(s"Map '$mapName' is selected.")
            println("Welcome to Map Creator.")
            println()

            Phase.MapCreator
        case _ =>
            Phase.Quit
    }

    private def inGame(option: Int = 0): Phase = option match {
        case 0 =>
            println("1. Make a move")
            println("2. Load move sequence from file")
            println("3. Quit game")

            Phase.InGame
        case 1 =>
            print("Please type your move (u/up, d/down, l/left, r/right): ")

            try {
                val direction = Direction.fromString(scala.io.StdIn.readLine())
                println()

                val outcome = outcomeMessage(game.move(direction))
                println()

                outcome
            } catch {
                case _: IllegalArgumentException =>
                    println("You've entered invalid move.")
                    println("Please try again.")
                    Phase.InGame
            }
        case 2 =>
            print("Please enter moves file name: ")
            val fileName = scala.io.StdIn.readLine()

            game.loadMoveSequence(fileName) match {
                case Success(_) =>
                case Failure(exception) if exception.isInstanceOf[IllegalArgumentException] =>
                    println("Invalid move specifier provided.")
                case Failure(_) =>
                    println(s"Move sequence '$fileName' could not be found.")
            }

            outcomeMessage(game.executeMoveSequence())
        case _ =>
            Phase.Quit
    }

    private def mapCreator(option: Int = 0): Phase = option match {
        case 0 =>
            println("1. Remove plate from the field")
            println("2. Add plate to the field")
            println("3. Set trap")
            println("4. Remove trap")
            println("5. Set start position")
            println("6. Set finish position")
            println("7. Create operation sequence")
            println("8. Save current map to a file")
            println("9. Return to main menu (editing progress will be lost)")
            println("10. Quit game")

            Phase.MapCreator
        case _ =>
            Phase.Quit
    }

    @tailrec
    private def safeReadOption(): Int = {
        try {
            print("Please choose desired option: ")
            scala.io.StdIn.readInt()
        } catch {
            case _: Throwable => safeReadOption()
        }
    }

    private def safeReadOption(max: Int): Int = {
        try {
            print("Please choose desired option: ")
            val option = scala.io.StdIn.readInt() - 1

            if (option < 0 || option >= max) safeReadOption(max)
            else option
        } catch {
            case _: Throwable => safeReadOption(max)
        }
    }

    private def outcomeMessage(outcome: Outcome): Phase = outcome match {
        case Victory =>
            println("Congratulations!")
            println("You are victorious!")
            Phase.MainMenu
        case Defeat =>
            println("Unfortunately you lost...")
            println("Better luck next time!")
            Phase.MainMenu
        case InProgress =>
            Phase.InGame
    }

    @tailrec
    private def displayMenu(phase: Phase): Unit = {
        phase match {
            case Phase.MainMenu => mainMenu()
            case Phase.InGame => inGame()
            case Phase.MapCreator => mapCreator()
            case Phase.Quit => return
        }

        val option = safeReadOption()

        val nextPhase = phase match {
            case Phase.MainMenu => mainMenu(option)
            case Phase.InGame => inGame(option)
            case Phase.MapCreator => mapCreator(option)
        }

        displayMenu(nextPhase)
    }

    override def run(): Unit = {
        displayMenu(MainMenu)
    }
}
