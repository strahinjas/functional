package bloxorz.game

import bloxorz.console.CommandLineInterface
import bloxorz.map._

import scala.collection.mutable
import scala.io.Source
import scala.util.{ Failure, Success, Using }

class Game(interface: Interface) {
    private val maps: mutable.HashMap[String, Map] = new mutable.HashMap()

    interface.game = this
    interface.run()

    def loadMap(fileName: String): Unit = {
        val result = Using(Source.fromFile(fileName)) { source =>
            val grid = source.getLines().toVector.map(line => line.toCharArray.toVector)
            val fieldGrid = grid.map(row => row.map(symbol => FieldFactory.createField(symbol)))

            maps += fileName -> new Map(fieldGrid)
        }

        result match {
            case Success(_) =>
                println(s"Map '$fileName' was successfully loaded.")
            case Failure(exception) if exception.isInstanceOf[IllegalArgumentException] =>
                println("Unsupported map format provided.")
            case Failure(_) =>
                println(s"Map '$fileName' could not be found.")
        }
    }
}

object Game {
    def main(args: Array[String]): Unit = {
        new Game(new CommandLineInterface)
    }
}
