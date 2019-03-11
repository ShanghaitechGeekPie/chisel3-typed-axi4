
package axi._branch.bibranch

trait BIBRANCH {
  type TRUE_T
  val instantiate_true_branch: () => TRUE_T
  type FALSE_T
  val instantiate_false_branch: () => FALSE_T
}

sealed abstract class BR_BOOLEAN[BB <: BIBRANCH](bb: BB) {
  type BB <: BIBRANCH
  val value: Boolean
  type T
  val instantiate: () => T
}

case class BR_TRUE[BB <: BIBRANCH](bb: BB) extends BR_BOOLEAN[BB](bb) {
  val value = true
  type T = bb.TRUE_T
  val instantiate: () => T = () => bb.instantiate_true_branch()
}

case class BR_FALSE[BB <: BIBRANCH](bb: BB) extends BR_BOOLEAN[BB](bb) {
  val value = true
  type T = bb.FALSE_T
  val instantiate: () => T = () => bb.instantiate_false_branch()
}

object BR_BOOLEAN {
  def apply[BB <: BIBRANCH](bb: BB, b: Boolean): BR_BOOLEAN[BB] = {
    if(b) {
      new BR_TRUE(bb)
    } else {
      new BR_FALSE(bb)
    }
  }
}
