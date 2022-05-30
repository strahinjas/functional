package bloxorz.map

sealed class Field(val symbol: Char)

case class Plate() extends Field('o')
case class Empty() extends Field('-')
case class Start() extends Field('S')
case class Finish() extends Field('T')
case class Trap() extends Field('.')

object FieldFactory {
    def createField(symbol: Char): Field = symbol match {
        case 'o' => Plate()
        case '-' => Empty()
        case 'S' => Start()
        case 'T' => Finish()
        case '.' => Trap()
        case _ => new Field(symbol)
    }
}
