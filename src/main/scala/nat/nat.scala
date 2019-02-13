
package nat

sealed trait nat {
  val value: Int
  val iszero: Boolean
}

case class ZERO() extends nat {
  val value = 0
  val iszero = true
}

case class SUCC[PRED <: nat](pred: PRED) extends nat {
  val value = pred.value + 1
  val iszero = false
}

object nat {
  def apply(n: Int): nat = {
    var _n: Int = n
    var ret: nat = ZERO()
    while(_n > 0) {
      ret = SUCC(ret)
      _n = _n - 1
    }
    return ret
  }
}

