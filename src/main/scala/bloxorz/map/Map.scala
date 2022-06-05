package bloxorz.map

import bloxorz.map.Field.{ Field, Finish, Start, Trap, Unknown }

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class Map(val grid: ArrayBuffer[ArrayBuffer[Field]]) {
    require(isValid)

    private def isValid: Boolean = {
        if (grid.isEmpty) return false
        val n = grid(0).size

        for (row <- grid) {
            if (row.size != n) return false
            if (row.contains(Unknown)) return false
        }

        if (grid.flatten.count(_ == Start) != 1) return false
        if (grid.flatten.count(_ == Finish) != 1) return false

        true
    }

    import bloxorz.map.Map.Position

    def get(x: Int, y: Int): Field = grid(x)(y)
    def get(position: Position): Field = get(position._1, position._2)

    def set(x: Int, y: Int, field: Field): Unit = grid(x)(y) = field
    def set(x: Int, y: Int, symbol: Char): Unit = set(x, y, Field.withName(symbol))
    def set(position: Position, field: Field): Unit = set(position._1, position._2, field)
    def set(position: Position, symbol: Char): Unit = set(position._1, position._2, symbol)

    def getStartPosition: Position = {
        (for (x <- grid.indices; y <- grid(0).indices
              if grid(x)(y) == Start) yield (x, y)).head
    }

    def getFinishPosition: Position = {
        (for (x <- grid.indices; y <- grid(0).indices
              if grid(x)(y) == Finish) yield (x, y)).head
    }

    def getTrapPositions: Vector[Position] = {
        (for (x <- grid.indices; y <- grid(0).indices
              if grid(x)(y) == Trap) yield (x, y)).toVector
    }

    def isPositionValid(position: Position): Boolean = {
        val x = position._1
        val y = position._2

        x >= 0 && x < grid.size && y >= 0 && y < grid(0).size
    }

    def getHeight: Int = grid.size
    def getWidth: Int = grid(0).size

    override def toString: String = {
        val stringBuilder = new mutable.StringBuilder()

        grid.foreach(row => {
            row.foreach(stringBuilder.append(_))
            stringBuilder.append(System.lineSeparator())
        })

        stringBuilder.toString()
    }
}

object Map {
    type Position = (Int, Int)
}
