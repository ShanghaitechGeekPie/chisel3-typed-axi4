
package axi._branch.natbranch

trait NATBRANCH {
  type ZERO_T
  val instantiate_zero_branch: () => ZERO_T
  type SUCC_T
  val instantiate_succ_branch: (nat) => SUCC_T
}

sealed abstract class BR_NAT[NB <: NATBRANCH](nb: NB) {
  type NB <: NATBRANCH
  val _nb = nb
  val value: Int
  val as_nat: nat
  val iszero: Boolean
  type T
  val instantiate: () => T
}

case class BR_ZERO[NB <: NATBRANCH](nb: NB) extends BR_NAT[NB](nb) {
  val value = 0
  val as_nat = ZERO()
  val iszero = true
  type T = _nb.ZERO_T
  val instantiate: () => T = () => _nb.instantiate_zero_branch()
}

case class BR_SUCC[NB <: NATBRANCH, PRED <: BR_NAT[NB]](pred: PRED) extends BR_NAT[NB](pred._nb) {
  val value = pred.value + 1
  val as_nat = SUCC(pred.as_nat)
  val iszero = false
  type T = _nb.SUCC_T
  val instantiate: () => T = () => _nb.instantiate_succ_branch(as_nat)
}

object BR_NAT {
  def apply[NB <: NATBRANCH](nb: NB, n: Int): BR_NAT[NB] = {
    var _n: Int = n
    var ret: BR_NAT[NB] = BR_ZERO(nb)
    while(_n > 0) {
      ret = BR_SUCC(ret)
      _n = _n - 1
    }
    return ret
  }
}

