package bloxorz.game

object Direction extends Enumeration {
    type Direction = Value

    val Up, Down, Left, Right, Orthogonal = Value

    def xAxis(direction: Direction): Boolean = {
        direction == Left || direction == Right
    }

    def yAxis(direction: Direction): Boolean = {
        direction == Up || direction == Down
    }

    def onSameAxis(first: Direction, second: Direction): Boolean = {
        (xAxis(first) && xAxis(second)) || (yAxis(first) && yAxis(second))
    }

    def opposite(direction: Direction): Direction = direction match {
        case Up => Down
        case Down => Up
        case Left => Right
        case Right => Left
        case _ => throw new IllegalArgumentException()
    }

    def fromString(input: String): Direction = {
        input.toLowerCase() match {
            case "u" | "up" => Up
            case "d" | "down" => Down
            case "l" | "left" => Left
            case "r" | "right" => Right
            case _ => throw new IllegalArgumentException()
        }
    }

    def toString(direction: Direction): String = direction match {
        case Up => "UP"
        case Down => "DOWN"
        case Left => "LEFT"
        case Right => "RIGHT"
        case _ => throw new IllegalArgumentException()
    }

    def toStringFile(direction: Direction): String = direction match {
        case Up => "u"
        case Down => "d"
        case Left => "l"
        case Right => "r"
        case _ => throw new IllegalArgumentException()
    }
}
