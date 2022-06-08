package bloxorz.game

import bloxorz.console.CommandLineUserInterface
import bloxorz.game.Direction._
import bloxorz.game.Orientation._
import bloxorz.game.Outcome._
import bloxorz.map.Field._
import bloxorz.map._

import java.io.PrintWriter
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.io.Source
import scala.util.{ Try, Using }

class Game(interface: UserInterface) {
    private val maps: mutable.HashMap[String, Map] = new mutable.HashMap()
    private var selectedMap: Map = _

    private var block: Block = _

    private var moveSequence: Vector[Direction] = _

    interface.game = this
    interface.run()

    def loadMap(fileName: String): Try[Boolean] = {
        Using(Source.fromFile(fileName)) { source =>
            val grid = ArrayBuffer.from(source.getLines().toArray.map(line => ArrayBuffer.from(line.toCharArray)))
            val fieldGrid = grid.map(row => row.map(Field.withName))

            maps(fileName) = new Map(fieldGrid)
            maps.contains(fileName)
        }
    }

    def getLoadedMaps: Vector[String] = maps.keys.toVector.sorted
    def getMap(mapName: String): Map = new Map(maps(mapName).grid)

    def start(mapName: String): Unit = {
        selectedMap = new Map(maps(mapName).grid)
        block = new Block(selectedMap.getStartPosition)
    }

    def move(direction: Direction): Outcome = {
        block.move(direction)
        printMap()
        evaluateOutcome(block)
    }

    private def evaluateOutcome(block: Block): Outcome = {
        val position = block.position
        val orientation = block.orientation

        if (!selectedMap.isPositionValid(position) ||
            (orientation == Horizontal && !selectedMap.isPositionValid(block.getSecondPosition))) {
            return Defeat
        }

        if (orientation == Vertical && position == selectedMap.getFinishPosition) {
            return Victory
        }

        if (orientation == Vertical && Field.validVertical(selectedMap.get(position))) {
            return InProgress
        }

        if (orientation == Horizontal &&
            selectedMap.get(position) != Empty &&
            selectedMap.get(block.getSecondPosition) != Empty) {
            return InProgress
        }

        Defeat
    }

    def loadMoveSequence(fileName: String): Try[Boolean] = {
        Using(Source.fromFile(fileName)) { source =>
            moveSequence = source.getLines().toVector.map(Direction.fromString)
            true
        }
    }

    def executeMoveSequence(): Outcome = {
        if (moveSequence != null) {
            moveSequence.foreach(move(_) match {
                case Victory => return Victory
                case Defeat => return Defeat
                case _ =>
            })
            return InProgress
        }
        throw new IllegalStateException()
    }

    def printMap(): Unit = {
        if (selectedMap != null && block != null) {
            val stringBuilder = new mutable.StringBuilder()
            val occupiedFields = block.getOccupiedFields

            for (x <- selectedMap.grid.indices) {
                for (y <- selectedMap.grid(0).indices) {
                    stringBuilder.append(
                        if (occupiedFields.contains((x, y))) Field.Block
                        else selectedMap.grid(x)(y)
                    )
                }
                stringBuilder.append(System.lineSeparator())
            }

            println(stringBuilder.toString())
        }
    }

    def findSolution(fileName: String): List[Direction] = {
        val solution = Nil

        // TODO: Find appropriate algorithm

        if (solution.nonEmpty) {
            new PrintWriter(fileName) {
                solution.foreach(move => write(Direction.toStringFile(move) + System.lineSeparator()))
                close()
            }
        }

        solution
    }
}

object Game {
    def main(args: Array[String]): Unit = {
        new Game(new CommandLineUserInterface)
    }
}
