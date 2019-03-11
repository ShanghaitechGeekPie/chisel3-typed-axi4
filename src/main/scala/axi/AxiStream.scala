
package axi

import chisel3._
import chisel3.util._

import axi._branch.bibranch._
import axi._branch.natbranch._

object AxiStream {
  class StrbField extends Axi.FreeWidthOptField
  class KeepField extends Axi.FreeWidthOptField
  class LastField extends Axi.BoolOptField
  class UserField extends Axi.FreeWidthOptField
  class DestField extends Axi.FreeWidthOptField
  class IdField   extends Axi.FreeWidthOptField
}

class AxiStreamData[
  Strb_CFG <: BR_NAT[AxiStream.StrbField],
  Keep_CFG <: BR_NAT[AxiStream.KeepField],
  Last_CFG <: BR_BOOLEAN[AxiStream.LastField],
  User_CFG <: BR_NAT[AxiStream.UserField],
  Dest_CFG <: BR_NAT[AxiStream.DestField],
  Id_CFG   <: BR_NAT[AxiStream.IdField]
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
  Strb_CFG <: BR_NAT[AxiStream.StrbField],
  Keep_CFG <: BR_NAT[AxiStream.KeepField],
  Last_CFG <: BR_BOOLEAN[AxiStream.LastField],
  User_CFG <: BR_NAT[AxiStream.UserField],
  Dest_CFG <: BR_NAT[AxiStream.DestField],
  Id_CFG   <: BR_NAT[AxiStream.IdField]
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
      BR_NAT(new AxiStream.StrbField, if(hasStrb){dataWidth / 8}else{0}),
      BR_NAT(new AxiStream.KeepField, if(hasKeep){dataWidth / 8}else{0}),
      BR_BOOLEAN(new AxiStream.LastField,  hasLast),
      BR_NAT(new AxiStream.UserField, userWidth),
      BR_NAT(new AxiStream.DestField, destWidth),
      BR_NAT(new AxiStream.IdField,   idWidth)
    )
  }
}

class AxiStreamSlave[
  Strb_CFG <: BR_NAT[AxiStream.StrbField],
  Keep_CFG <: BR_NAT[AxiStream.KeepField],
  Last_CFG <: BR_BOOLEAN[AxiStream.LastField],
  User_CFG <: BR_NAT[AxiStream.UserField],
  Dest_CFG <: BR_NAT[AxiStream.DestField],
  Id_CFG   <: BR_NAT[AxiStream.IdField]
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
      BR_NAT(new AxiStream.StrbField, if(hasStrb){dataWidth / 8}else{0}),
      BR_NAT(new AxiStream.KeepField, if(hasKeep){dataWidth / 8}else{0}),
      BR_BOOLEAN(new AxiStream.LastField,  hasLast),
      BR_NAT(new AxiStream.UserField, userWidth),
      BR_NAT(new AxiStream.DestField, destWidth),
      BR_NAT(new AxiStream.IdField,   idWidth)
    )
  }
}
