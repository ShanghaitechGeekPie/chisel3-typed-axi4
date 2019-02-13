
package nat

trait WEAK_FINBRANCH {
  type ZERO_T
  val instantiate_zero_branch: () => ZERO_T
  type SUCC_T
  val instantiate_succ_branch: (nat) => SUCC_T
}

sealed abstract class WEAK_NAT[WFB <: WEAK_FINBRANCH](wfb: WFB) {
  type WFB <: WEAK_FINBRANCH
  val _wfb = wfb
  val value: Int
  val as_nat: nat
  val iszero: Boolean
  type T
  val instantiate: () => T
}

case class WEAK_ZERO[WFB <: WEAK_FINBRANCH](wfb: WFB) extends WEAK_NAT[WFB](wfb) {
  val value = 0
  val as_nat = ZERO()
  val iszero = true
  type T = _wfb.ZERO_T
  val instantiate: () => T = () => _wfb.instantiate_zero_branch()
}

case class WEAK_SUCC[WFB <: WEAK_FINBRANCH, PRED <: WEAK_NAT[WFB]](pred: PRED) extends WEAK_NAT[WFB](pred._wfb) {
  val value = pred.value + 1
  val as_nat = SUCC(pred.as_nat)
  val iszero = false
  type T = _wfb.SUCC_T
  val instantiate: () => T = () => _wfb.instantiate_succ_branch(as_nat)
}

object WEAK_NAT {
  def apply[WFB <: WEAK_FINBRANCH](wfb: WFB, n: Int): WEAK_NAT[WFB] = {
    var _n: Int = n
    var ret: WEAK_NAT[WFB] = WEAK_ZERO(wfb)
    while(_n > 0) {
      ret = WEAK_SUCC(ret)
      _n = _n - 1
    }
    return ret
  }
}

