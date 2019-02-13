
package axi

import chisel3._
import chisel3.util._

import nat._
import bibranch._

object AxiStream {
  class StrbField extends Axi.FreeWidthOptField
  class KeepField extends Axi.FreeWidthOptField
  class LastField extends Axi.BoolOptField
  class UserField extends Axi.FreeWidthOptField
  class DestField extends Axi.FreeWidthOptField
  class IdField   extends Axi.FreeWidthOptField
}

class AxiStreamData[
  Strb_CFG <: WEAK_NAT[AxiStream.StrbField],
  Keep_CFG <: WEAK_NAT[AxiStream.KeepField],
  Last_CFG <: BOOLEAN[AxiStream.LastField],
  User_CFG <: WEAK_NAT[AxiStream.UserField],
  Dest_CFG <: WEAK_NAT[AxiStream.DestField],
  Id_CFG   <: WEAK_NAT[AxiStream.IdField]
](
  val dataWidth: Int,
  val strb_cfg: Strb_CFG,
  val keep_cfg: Keep_CFG,
  val last_cfg: Last_CFG,
  val user_cfg: User_CFG,
  val dest_cfg: Dest_CFG,
  val id_cfg:   Id_CFG
) extends Bundle {
  val data = UInt(dataWidth.W)
  val strb = strb_cfg.instantiate()
  val keep = keep_cfg.instantiate()
  val last = last_cfg.instantiate()
  val user = user_cfg.instantiate()
  val dest = dest_cfg.instantiate()
  val id   = id_cfg.instantiate()
}

class AxiStreamMaster[
  Strb_CFG <: WEAK_NAT[AxiStream.StrbField],
  Keep_CFG <: WEAK_NAT[AxiStream.KeepField],
  Last_CFG <: BOOLEAN[AxiStream.LastField],
  User_CFG <: WEAK_NAT[AxiStream.UserField],
  Dest_CFG <: WEAK_NAT[AxiStream.DestField],
  Id_CFG   <: WEAK_NAT[AxiStream.IdField]
](
  val dataWidth: Int,
  val strb_cfg: Strb_CFG,
  val keep_cfg: Keep_CFG,
  val last_cfg: Last_CFG,
  val user_cfg: User_CFG,
  val dest_cfg: Dest_CFG,
  val id_cfg:   Id_CFG
) extends Bundle {
  val data = Decoupled(
    new AxiStreamData(
      dataWidth,
      strb_cfg,
      keep_cfg,
      last_cfg,
      user_cfg,
      dest_cfg,
      id_cfg
    )
  )
}

object AxiStreamMaster {
  def apply(
    dataWidth: Int,
    hasStrb:   Boolean = false,
    hasKeep:   Boolean = false,
    hasLast:   Boolean = false,
    userWidth: Int     = 0,
    destWidth: Int     = 0,
    idWidth:   Int     = 0
  ) = {
    new AxiStreamMaster(
      dataWidth,
      WEAK_NAT(new AxiStream.StrbField, if(hasStrb){dataWidth / 8}else{0}),
      WEAK_NAT(new AxiStream.KeepField, if(hasKeep){dataWidth / 8}else{0}),
      BOOLEAN(new AxiStream.LastField,  hasLast),
      WEAK_NAT(new AxiStream.UserField, userWidth),
      WEAK_NAT(new AxiStream.DestField, destWidth),
      WEAK_NAT(new AxiStream.IdField,   idWidth)
    )
  }
}

class AxiStreamSlave[
  Strb_CFG <: WEAK_NAT[AxiStream.StrbField],
  Keep_CFG <: WEAK_NAT[AxiStream.KeepField],
  Last_CFG <: BOOLEAN[AxiStream.LastField],
  User_CFG <: WEAK_NAT[AxiStream.UserField],
  Dest_CFG <: WEAK_NAT[AxiStream.DestField],
  Id_CFG   <: WEAK_NAT[AxiStream.IdField]
](
  val dataWidth: Int,
  val strb_cfg: Strb_CFG,
  val keep_cfg: Keep_CFG,
  val last_cfg: Last_CFG,
  val user_cfg: User_CFG,
  val dest_cfg: Dest_CFG,
  val id_cfg:   Id_CFG
) extends Bundle {
  val data = Flipped(Decoupled(
    new AxiStreamData(
      dataWidth,
      strb_cfg,
      keep_cfg,
      last_cfg,
      user_cfg,
      dest_cfg,
      id_cfg
    )
  ))
}

object AxiStreamSlave {
  def apply(
    dataWidth: Int,
    hasStrb:   Boolean = false,
    hasKeep:   Boolean = false,
    hasLast:   Boolean = false,
    userWidth: Int     = 0,
    destWidth: Int     = 0,
    idWidth:   Int     = 0
  ) = {
    new AxiStreamSlave(
      dataWidth,
      WEAK_NAT(new AxiStream.StrbField, if(hasStrb){dataWidth / 8}else{0}),
      WEAK_NAT(new AxiStream.KeepField, if(hasKeep){dataWidth / 8}else{0}),
      BOOLEAN(new AxiStream.LastField,  hasLast),
      WEAK_NAT(new AxiStream.UserField, userWidth),
      WEAK_NAT(new AxiStream.DestField, destWidth),
      WEAK_NAT(new AxiStream.IdField,   idWidth)
    )
  }
}
