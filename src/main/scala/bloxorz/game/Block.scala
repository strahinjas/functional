package bloxorz.game

import bloxorz.game.Direction._
import bloxorz.game.Orientation._
import bloxorz.map.Map.Position

class Block(var position: Position, var orientation: Orientation = Vertical, var direction: Direction = Orthogonal) {
    def move(direction: Direction): Unit = {
        position = getNextPosition(direction)

        if (orientation != Horizontal || onSameAxis(direction, this.direction)) {
            orientation = Orientation.opposite(orientation)
            this.direction = direction
            if (orientation == Vertical) position = getNextPosition(direction)
        }
    }

    def immutableMove(direction: Direction): Block = {
        val block = new Block(position, orientation, direction)
        block.move(direction)
        block
    }

    def getOccupiedFields: Vector[Position] = {
        orientation match {
            case Horizontal => Vector(position, getNextPosition(direction))
            case Vertical => Vector(position)
        }
    }

    def getSecondPosition: Position = getNextPosition(direction)

    private def getNextPosition(direction: Direction): Position = direction match {
        case Up    => (position._1 - 1, position._2)
        case Down  => (position._1 + 1, position._2)
        case Left  => (position._1, position._2 - 1)
        case Right => (position._1, position._2 + 1)
    }
}
