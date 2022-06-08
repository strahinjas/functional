package bloxorz.console

import bloxorz.console.Phase._
import bloxorz.game.Outcome._
import bloxorz.game._
import bloxorz.map.Map
import bloxorz.map.Map.Position
import bloxorz.map.creator.{ MapCreator, OperationSequence }

import java.io._
import java.nio.file.{ Files, Paths }
import scala.annotation.tailrec
import scala.collection.mutable
import scala.util.{ Failure, Success }

class CommandLineUserInterface extends UserInterface {
    private var editedMap: Map = _
    private var activeMapName: String = _

    private val sequences: mutable.HashMap[String, OperationSequence] = new mutable.HashMap()
    private var activeSequence: OperationSequence = _
    private val sequencesFileName: String = "sequences/bloxorz.file"

    if (Files.exists(Paths.get(sequencesFileName))) {
        new ObjectInputStream(new FileInputStream(sequencesFileName)) {
            sequences.addAll(readObject.asInstanceOf[mutable.HashMap[String, OperationSequence]])
            close()
        }
    }

    private def selector(text: String, values: Vector[String]): String = {
        if (values.isEmpty) throw new IllegalArgumentException()

        println(text)

        for ((value, index) <- values.zipWithIndex) {
            println(s"${index + 1}. $value")
        }

        values(safeReadOption(values.length))
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
            val loadedMaps = game.getLoadedMaps

            if (loadedMaps.isEmpty) {
                println("Please load a map before starting the game.")
                return Phase.MainMenu
            }

            activeMapName = selector("Available maps:", loadedMaps)
            game.start(activeMapName)

            println(s"Map '$activeMapName' is selected.")
            println()
            println("Game has started.")
            println("Block is represented with 'X' on the grid.")
            println()
            game.printMap()

            Phase.InGame
        case 3 =>
            println("Please choose the map you want to edit.")
            val loadedMaps = game.getLoadedMaps

            if (loadedMaps.isEmpty) {
                println("Please load a map before starting map creator.")
                return Phase.MainMenu
            }

            activeMapName = selector("Available maps:", loadedMaps)
            editedMap = game.getMap(activeMapName)

            println(s"Map '$activeMapName' is selected.")
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
            println("3. Write one possible solution to file")
            println("4. Quit game")

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
        case 3 =>
            print("Please enter output file name: ")
            val fileName = scala.io.StdIn.readLine() match {
                case "" =>
                    val index = activeMapName.lastIndexOf("/")

                    if (index >= 0) "solutions" + activeMapName.substring(index)
                    else "solutions/" + activeMapName
                case name: String => name
            }

            val moves = game.findSolution(fileName)

            if (moves.isEmpty) {
                println("There's no possible solution for the provided map.")
            } else {
                println("Here are the winning moves:")
                moves.foreach(move => println(Direction.toString(move)))
                println()
            }

            Phase.InGame
        case _ =>
            Phase.Quit
    }

    private def mapCreator(option: Int = 0): Phase = {
        try {
            option match {
                case 0 =>
                    println("1. Remove plate from the field")
                    println("2. Add plate to the field")
                    println("3. Set trap")
                    println("4. Remove trap")
                    println("5. Set start position")
                    println("6. Set finish position")
                    println("7. Swap start and finish positions")
                    println("8. Remove all traps")
                    println("9. Filter")
                    println("10. Create operation sequence")
                    println("11. Execute operation sequence")
                    println("12. Print current map state")
                    println("13. Save current map to a file")
                    println("14. Return to main menu (editing progress will be lost)")
                    println("15. Quit game")

                    Phase.MapCreator
                case 1 =>
                    val position = readCoordinates()
                    editedMap = MapCreator.removePlate(position._1, position._2)(editedMap)

                    Phase.MapCreator
                case 2 =>
                    val position = readCoordinates()
                    editedMap = MapCreator.addPlate(position._1, position._2)(editedMap)

                    Phase.MapCreator
                case 3 =>
                    val position = readCoordinates()
                    editedMap = MapCreator.setTrap(position._1, position._2)(editedMap)

                    Phase.MapCreator
                case 4 =>
                    val position = readCoordinates()
                    editedMap = MapCreator.removeTrap(position._1, position._2)(editedMap)

                    Phase.MapCreator
                case 5 =>
                    val position = readCoordinates()
                    editedMap = MapCreator.setStart(position._1, position._2)(editedMap)

                    Phase.MapCreator
                case 6 =>
                    val position = readCoordinates()
                    editedMap = MapCreator.setFinish(position._1, position._2)(editedMap)

                    Phase.MapCreator
                case 7 =>
                    editedMap = MapCreator.invert(editedMap)

                    Phase.MapCreator
                case 8 =>
                    editedMap = MapCreator.removeTraps(editedMap)

                    Phase.MapCreator
                case 9 =>
                    val position = readCoordinates()

                    println("Please enter filter range: ")
                    val n = scala.io.StdIn.readInt()

                    editedMap = MapCreator.filter(position._1, position._2, n)(editedMap)

                    Phase.MapCreator
                case 10 =>
                    print("Please enter sequence name: ")
                    val sequenceName = scala.io.StdIn.readLine()

                    sequences(sequenceName) = new OperationSequence(sequenceName)
                    activeSequence = sequences(sequenceName)

                    Phase.MapSequenceCreator
                case 11 =>
                    val operationSequences = sequences.keys.toVector.sorted

                    if (operationSequences.isEmpty) {
                        println("There isn't any operation sequence available.")
                        println("Try creating one before the execution.")
                        return Phase.MapCreator
                    }

                    val sequenceName = selector("Available operation sequences:", operationSequences)
                    editedMap = sequences(sequenceName).execute(editedMap)

                    Phase.MapCreator
                case 12 =>
                    println()
                    print(editedMap)
                    println()

                    Phase.MapCreator
                case 13 =>
                    print("Please enter output file name: ")
                    val fileName = scala.io.StdIn.readLine() match {
                        case "" => activeMapName.replace(".txt", "_edited.txt")
                        case name: String => name
                    }

                    new PrintWriter(fileName) {
                        write(editedMap.toString)
                        close()
                    }

                    println(s"Edited map was written to the file '$fileName'.")

                    Phase.MapCreator
                case 14 =>
                    Phase.MainMenu
                case _ =>
                    Phase.Quit
            }
        } catch {
            case _: IllegalArgumentException =>
                println("Illegal argument value provided.")
                Phase.MapCreator
        }
    }

    private def mapSequenceCreator(option: Int = 0): Phase = option match {
        case 0 =>
            println("1. Remove plate")
            println("2. Add plate")
            println("3. Set trap")
            println("4. Remove trap")
            println("5. Set start position")
            println("6. Set finish position")
            println("7. Inversion")
            println("8. Remove all traps")
            println("9. Filter")

            for ((name, index) <- sequences.keys.toVector.sorted.zipWithIndex) {
                println(s"${index + 10}. $name (sequence)")
            }

            println(s"${sequences.size + 10}. Return to map creator")

            Phase.MapSequenceCreator
        case 1 =>
            val position = readCoordinates()
            activeSequence.attach(MapCreator.removePlate(position._1, position._2))

            Phase.MapSequenceCreator
        case 2 =>
            val position = readCoordinates()
            activeSequence.attach(MapCreator.addPlate(position._1, position._2))

            Phase.MapSequenceCreator
        case 3 =>
            val position = readCoordinates()
            activeSequence.attach(MapCreator.setTrap(position._1, position._2))

            Phase.MapSequenceCreator
        case 4 =>
            val position = readCoordinates()
            activeSequence.attach(MapCreator.removeTrap(position._1, position._2))

            Phase.MapSequenceCreator
        case 5 =>
            val position = readCoordinates()
            activeSequence.attach(MapCreator.setStart(position._1, position._2))

            Phase.MapSequenceCreator
        case 6 =>
            val position = readCoordinates()
            activeSequence.attach(MapCreator.setFinish(position._1, position._2))

            Phase.MapSequenceCreator
        case 7 =>
            activeSequence.attach(MapCreator.invert)

            Phase.MapSequenceCreator
        case 8 =>
            activeSequence.attach(MapCreator.removeTraps)

            Phase.MapSequenceCreator
        case 9 =>
            val position = readCoordinates()

            println("Please enter filter range: ")
            val n = scala.io.StdIn.readInt()

            activeSequence.attach(MapCreator.filter(position._1, position._2, n))

            Phase.MapSequenceCreator
        case choice: Int if choice == sequences.size + 10 =>
            Phase.MapCreator
        case choice: Int =>
            val index = choice - 10
            val keys = sequences.keys.toVector.sorted

            activeSequence.attach(OperationSequence.toOperation(sequences(keys(index))))

            Phase.MapSequenceCreator
    }

    private def quit(): Unit = {
        new ObjectOutputStream(new FileOutputStream(sequencesFileName)) {
            writeObject(sequences)
            close()
        }
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

    private def readCoordinates(): Position = {
        print("Please enter target coordinates (x y - in the same line): ")
        val input = scala.io.StdIn.readLine().split(" ").map(_.toInt)

        (input(0), input(1))
    }

    @tailrec
    private def displayMenu(phase: Phase): Unit = {
        phase match {
            case Phase.MainMenu => mainMenu()
            case Phase.InGame => inGame()
            case Phase.MapCreator => mapCreator()
            case Phase.MapSequenceCreator => mapSequenceCreator()
            case Phase.Quit => quit(); return
        }

        val option = safeReadOption()

        val nextPhase = phase match {
            case Phase.MainMenu => mainMenu(option)
            case Phase.InGame => inGame(option)
            case Phase.MapCreator => mapCreator(option)
            case Phase.MapSequenceCreator => mapSequenceCreator(option)
        }

        displayMenu(nextPhase)
    }

    override def run(): Unit = {
        displayMenu(MainMenu)
    }
}
