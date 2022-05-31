package specs

import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

abstract class UnitSpec extends AnyFlatSpec with should.Matchers with EitherValues
