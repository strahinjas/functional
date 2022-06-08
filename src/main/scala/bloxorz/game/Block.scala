package bloxorz.game

import bloxorz.game.Block.BlockPosition
import bloxorz.game.Direction._
import bloxorz.game.Orientation._
import bloxorz.map.Map.Position

class Block(var position: Position, var orientation: Orientation = Vertical, var direction: Direction = Orthogonal) {
    def move(direction: Direction): Unit = {
        position = getNextPosition(direction)

        if (orientation != Horizontal || Direction.onSameAxis(direction, this.direction)) {
            orientation = Orientation.opposite(orientation)
            if (orientation == Vertical && direction == this.direction) position = getNextPosition(direction)
            this.direction = direction
        }
    }

    def immutableMove(direction: Direction): Block = {
        val block = new Block(this.position, this.orientation, this.direction)
        block.move(direction)
        println(s"Block moved ${Direction.toString(direction)}")
        block
    }

    def getOccupiedFields: Vector[Position] = {
        orientation match {
            case Horizontal => Vector(position, getNextPosition(direction))
            case Vertical => Vector(position)
        }
    }

    def getSecondPosition: Position = getNextPosition(direction)

    def getCurrentPositionVector: BlockPosition = (position, orientation, direction)

    private def getNextPosition(direction: Direction): Position = direction match {
        case Up    => (position._1 - 1, position._2)
        case Down  => (position._1 + 1, position._2)
        case Left  => (position._1, position._2 - 1)
        case Right => (position._1, position._2 + 1)
    }
}

object Block {
    type BlockPosition = (Position, Orientation, Direction)
}
