package bloxorz.game

import bloxorz.console.CommandLineInterface
import bloxorz.map.Map
import bloxorz.map.Field

import scala.collection.mutable
import scala.io.Source
import scala.util.Using

class Game(interface: Interface) {
    interface.game = this
    interface.run()

    private val maps: mutable.HashMap[String, Map] = new mutable.HashMap()

    def loadMap(fileName: String): Unit = {
        Using(Source.fromFile(fileName)) { source =>
            val grid = source.getLines().toVector.map(line => line.toCharArray.toVector)
            val fieldGrid = grid.map(row => row.map(symbol => new Field(symbol)))
            maps += fileName -> new Map(fieldGrid)
            maps(fileName).print()
        }
    }
}

object Game {
    def main(args: Array[String]): Unit = {
        new Game(new CommandLineInterface)
    }
}
