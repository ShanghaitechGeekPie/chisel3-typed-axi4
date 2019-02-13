
package bibranch

trait BIBRANCH {
  type TRUE_T
  val instantiate_true_branch: () => TRUE_T
  type FALSE_T
  val instantiate_false_branch: () => FALSE_T
}

sealed abstract class BOOLEAN[BB <: BIBRANCH](bb: BB) {
  type BB <: BIBRANCH
  val value: Boolean
  type T
  val instantiate: () => T
}

case class TRUE[BB <: BIBRANCH](bb: BB) extends BOOLEAN[BB](bb) {
  val value = true
  type T = bb.TRUE_T
  val instantiate: () => T = () => bb.instantiate_true_branch()
}

case class FALSE[BB <: BIBRANCH](bb: BB) extends BOOLEAN[BB](bb) {
  val value = true
  type T = bb.FALSE_T
  val instantiate: () => T = () => bb.instantiate_false_branch()
}

object BOOLEAN {
  def apply[BB <: BIBRANCH](bb: BB, b: Boolean): BOOLEAN[BB] = {
    if(b) {
      new TRUE(bb)
    } else {
      new FALSE(bb)
    }
  }
}
