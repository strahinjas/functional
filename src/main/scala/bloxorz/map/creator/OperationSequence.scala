package bloxorz.map.creator

import bloxorz.map.Map

import scala.collection.mutable.ListBuffer

@SerialVersionUID(115L)
class OperationSequence(val name: String) extends Serializable {
    import OperationSequence.Operation
    var sequence: ListBuffer[Operation] = new ListBuffer()

    def attach(operation: Operation): Unit = {
        sequence.append(operation)
    }

    def execute(map: Map): Map = {
        sequence.foldLeft(map)((map, operation) => operation(map))
    }
}

object OperationSequence {
    type Operation = Map => Map

    def toOperation(operationSequence: OperationSequence)(map: Map): Map = {
        operationSequence.execute(map)
    }
}
