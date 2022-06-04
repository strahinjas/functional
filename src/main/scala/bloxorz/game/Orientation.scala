package bloxorz.game

object Orientation extends Enumeration {
    type Orientation = Value

    val Horizontal, Vertical = Value

    def opposite(orientation: Orientation): Orientation = orientation match {
        case Horizontal => Vertical
        case Vertical => Horizontal
    }
}
