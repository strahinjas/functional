package bloxorz.console

import bloxorz.console.Phase._
import bloxorz.game.Interface

import scala.annotation.tailrec

class CommandLineInterface extends Interface {
    private def mapChooser(): String = {
        println("Available maps:")

        val maps = game.getLoadedMaps

        for ((index, map) <- maps.zipWithIndex) {
            println(s"${index + 1}. $map")
        }

        maps(safeReadOption(maps.length))
    }

    private def mainMenu(option: Int = 0): Unit = option match {
        case 0 =>
            println("1. Load map from file")
            println("2. Start game")
            println("3. Create new map")
            println("4. Exit game")
        case 1 =>
            print("Please enter map file name: ")
            val fileName = scala.io.StdIn.readLine()
            game.loadMap(fileName)
            displayMenu(MainMenu)
        case 2 =>
            val selectedMap = mapChooser()
            game.selectMap(selectedMap)

            println(s"Map '$selectedMap' is selected")
            println("Game is starting...")
            println()

            displayMenu(InGame)
        case 3 =>
            displayMenu(MapCreator)
        case _ =>
    }

    private def inGame(option: Int = 0): Unit = option match {
        case 0 =>
    }

    private def mapCreator(option: Int = 0): Unit = option match {
        case 0 =>
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
            case _: Throwable => safeReadOption()
        }
    }

    private def displayMenu(phase: Phase): Unit = {
        phase match {
            case MainMenu => mainMenu()
            case InGame => inGame()
            case MapCreator => mapCreator()
        }

        val option = safeReadOption()

        phase match {
            case MainMenu => mainMenu(option)
            case InGame => inGame(option)
            case MapCreator => mapCreator(option)
        }
    }

    override def run(): Unit = {
        displayMenu(MainMenu)
    }
}
