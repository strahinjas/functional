package bloxorz.map.creator

import bloxorz.map.Map

@SerialVersionUID(100L)
class OperationSequence(val name: String) extends Serializable {
    import OperationSequence.Operation
    var sequence: Operation = MapCreator.identity

    def attach(operation: Operation): Unit = {
        def attachInternal(operation: Operation)(map: Map): Map = {
            operation(sequence(map))
        }

        sequence = attachInternal(operation)
    }

    def execute(map: Map): Map = sequence(map)
}

object OperationSequence {
    type Operation = Map => Map
}
