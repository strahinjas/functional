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

    def fromString(input: String): Direction = {
        input.toLowerCase() match {
            case "u" | "up" => Up
            case "d" | "down" => Down
            case "l" | "left" => Left
            case "r" | "right" => Right
            case _ => throw new IllegalArgumentException()
        }
    }
}
