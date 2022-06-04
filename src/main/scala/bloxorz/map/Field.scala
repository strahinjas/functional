package bloxorz.map

import bloxorz.game.Direction.Direction

object Field extends Enumeration {
    type Field = Value

    val Block: Field = Value("X")
    val Plate: Field = Value("o")
    val Empty: Field = Value("-")
    val Start: Field = Value("S")
    val Finish: Field = Value("T")
    val Trap: Field = Value(".")
    val Unknown: Field = Value

    def withName(symbol: Char): Field = {
        values.find(_.toString.head == symbol).getOrElse(Unknown)
    }

    def validHorizontal(field: Field): Boolean = {
        field != Empty
    }
}
