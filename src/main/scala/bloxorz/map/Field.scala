package bloxorz.map

sealed class Field(val symbol: Char)

case class Plate() extends Field('o')
case class Empty() extends Field('-')
case class Start() extends Field('o')
case class Finish() extends Field('o')
case class Trap() extends Field('.')
