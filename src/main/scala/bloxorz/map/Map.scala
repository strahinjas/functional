package bloxorz.map

class Map(val grid: Vector[Vector[Field]]) {
    require(isValid)

    private def isValid: Boolean = {
        if (grid.isEmpty) return false
        val n = grid(0).size

        for (row <- grid) {
            if (row.size != n) return false

            for (field <- row) {
                field match {
                    case _: Plate | _: Empty | _: Start | _: Finish | _: Trap =>
                    case _ => return false
                }
            }
        }

        grid.size == n
    }

    def print(): Unit = {
        grid.foreach(row => {
            row.foreach(field => Console.print(field.symbol))
            println()
        })
    }
}
