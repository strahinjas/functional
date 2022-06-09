package bloxorz.map.creator

import bloxorz.map.Field._
import bloxorz.map.{ Field, Map }
import bloxorz.map.Map.Position

import scala.collection.mutable.ArrayBuffer

object MapCreator {
    def removePlate(x: Int, y: Int)(map: Map): Map = {
        if (!map.isPositionValid(x, y)) throw new IllegalArgumentException()
        if (map.get(x, y) == Plate || map.get(x, y) == Trap) map.set(x, y, Empty)
        map
    }

    def addPlate(x: Int, y: Int)(map: Map): Map = {
        def getDifference(coordinate: Int, size: Int): Int = {
            if (coordinate < 0) coordinate
            else coordinate - size
        }

        def resizeMap(dx: Int, dy: Int): Unit = {
            val width = map.getWidth

            val xPadding = new ArrayBuffer[ArrayBuffer[Field]](dx.abs)
            for (_ <- 0 until dx.abs) xPadding.addOne(new ArrayBuffer[Field]())

            if (dx < 0) map.grid.prependAll(xPadding)
            if (dx > 0) map.grid.addAll(xPadding)

            if (dy < 0) map.grid.foreach(row =>
                if (row.isEmpty) row.padToInPlace(width - dy, Empty)
                else row.prependAll(new ArrayBuffer[Field]().padTo(-dy, Empty))
            )
            if (dy > 0) map.grid.foreach(row => row.padToInPlace(width + dy, Empty))
        }

        def getNewPosition(x: Int, y: Int): Position = {
            (if (x < 0) 0 else map.getHeight - 1, if (y < 0) 0 else map.getWidth - 1)
        }

        if (map.isPositionValid(x, y)) {
            if (map.get(x, y) == Empty) map.set(x, y, Plate)
        } else {
            resizeMap(getDifference(x, map.getHeight), getDifference(y, map.getWidth))
            map.set(getNewPosition(x, y), Plate)
        }
        map
    }

    def setTrap(x: Int, y: Int)(map: Map): Map = {
        if (!map.isPositionValid(x, y)) throw new IllegalArgumentException()
        if (map.get(x, y) == Plate) map.set(x, y, Trap)
        map
    }

    def removeTrap(x: Int, y: Int)(map: Map): Map = {
        if (!map.isPositionValid(x, y)) throw new IllegalArgumentException()
        if (map.get(x, y) == Trap) map.set(x, y, Plate)
        map
    }

    def setStart(x: Int, y: Int)(map: Map): Map = {
        if (!map.isPositionValid(x, y)) throw new IllegalArgumentException()

        map.set(map.getStartPosition, Plate)
        map.set(x, y, Start)
        map
    }

    def setFinish(x: Int, y: Int)(map: Map): Map = {
        if (!map.isPositionValid(x, y)) throw new IllegalArgumentException()

        map.set(map.getFinishPosition, Plate)
        map.set(x, y, Finish)
        map
    }

    def identity(map: Map): Map = map

    def invert(map: Map): Map = {
        val startPosition = map.getStartPosition
        val finishPosition = map.getFinishPosition

        map.set(startPosition, Finish)
        map.set(finishPosition, Start)
        map
    }

    def removeTraps(map: Map): Map = {
        map.getTrapPositions.foreach(position => map.set(position, Plate))
        map
    }

    def filter(x: Int, y: Int, n: Int)(map: Map): Map = {
        if (!map.isPositionValid(x, y)) throw new IllegalArgumentException()
        if (!Field.validFilter(map.get(x, y))) throw new IllegalArgumentException()

        map.getTrapPositions.foreach(position => {
            if ((position._1 == x && (y - position._2).abs <= n) ||
                (position._2 == y && (x - position._1).abs <= n)) {
                map.set(x, y, Plate)
                return map
            }
        })
        map
    }
}
