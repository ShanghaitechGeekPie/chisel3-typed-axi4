
package axi

import chisel3._
import chisel3.util._

import axi._branch.bibranch._
import axi._branch.natbranch._

object AxiLite {
  class ProtField extends Axi.FixedWidthOptField(3)
  class StrbField extends Axi.FreeWidthOptField
}

class AxiLiteAddr[
  Prot_CFG <: BR_BOOLEAN[AxiLite.ProtField]
](
  val addrWidth: Int,
  val prot_cfg:  Prot_CFG
) extends Bundle {
  val addr = UInt(addrWidth.W)
  val prot = prot_cfg.instantiate()
}

abstract class AxiLiteData(val dataWidth: Int) extends Bundle {
  //require((dataWidth == 32) || (dataWidth == 64), "Data width in AxiLite must be 32 or 64.")
  val data = UInt(dataWidth.W)
}

class AxiLiteReadData(dataWidth: Int) extends AxiLiteData(dataWidth) {
  val resp = UInt(2.W)
}

class AxiLiteWriteData[
  Strb_CFG <: BR_NAT[AxiLite.StrbField]
](
  dataWidth:    Int,
  val strb_cfg: Strb_CFG
) extends AxiLiteData(dataWidth) {
  val strb = strb_cfg.instantiate()
}

class AxiLiteWriteResp extends Bundle {
  val resp = UInt(2.W)
}

class AxiLiteMaster[
  Prot_CFG <: BR_BOOLEAN[AxiLite.ProtField],
  Strb_CFG <: BR_NAT[AxiLite.StrbField]
](
  val addrWidth: Int,
  val dataWidth: Int,
  val prot_cfg:  Prot_CFG,
  val strb_cfg:  Strb_CFG
) extends Bundle{
  val readAddr = Decoupled(
    new AxiLiteAddr(
      addrWidth,
      prot_cfg
    )
  )
  val writeAddr = Decoupled(
    new AxiLiteAddr(
      addrWidth,
      prot_cfg
    )
  )
  val readData = Flipped(Decoupled(
    new AxiLiteReadData(
      dataWidth
    )
  ))
  val writeData = Decoupled(
    new AxiLiteWriteData(
      dataWidth,
      strb_cfg
    )
  )
  val writeResp = Flipped(Decoupled(
    new AxiLiteWriteResp
  ))
}

object AxiLiteMaster {
  def apply[
    Prot_CFG <: BR_BOOLEAN[AxiLite.ProtField],
    Strb_CFG <: BR_NAT[AxiLite.StrbField]
  ](
    addrWidth: Int,
    dataWidth: Int,
    hasProt:   Boolean,
    hasStrb:   Boolean
  ) = {
    new AxiLiteMaster(
      addrWidth,
      dataWidth,
      BR_BOOLEAN(new AxiLite.ProtField,  hasProt),
      BR_NAT(new AxiLite.StrbField, if(hasStrb){dataWidth / 8}else{0})
    )
  }
}

class AxiLiteSlave[
  Prot_CFG <: BR_BOOLEAN[AxiLite.ProtField],
  Strb_CFG <: BR_NAT[AxiLite.StrbField]
](
  val addrWidth: Int,
  val dataWidth: Int,
  val prot_cfg:  Prot_CFG,
  val strb_cfg:  Strb_CFG
) extends Bundle{
  val readAddr = Flipped(Decoupled(
    new AxiLiteAddr(
      addrWidth,
      prot_cfg
    )
  ))
  val writeAddr = Flipped(Decoupled(
    new AxiLiteAddr(
      addrWidth,
      prot_cfg
    )
  ))
  val readData = Decoupled(
    new AxiLiteReadData(
      dataWidth
    )
  )
  val writeData = Flipped(Decoupled(
    new AxiLiteWriteData(
      dataWidth,
      strb_cfg
    )
  ))
  val writeResp = Decoupled(
    new AxiLiteWriteResp()
  )
}

object AxiLiteSlave {
  def apply[
    Prot_CFG <: BR_BOOLEAN[AxiLite.ProtField],
    Strb_CFG <: BR_NAT[AxiLite.StrbField]
  ](
    addrWidth: Int,
    dataWidth: Int,
    hasProt:   Boolean,
    hasStrb:   Boolean
  ) = {
    new AxiLiteSlave(
      addrWidth,
      dataWidth,
      BR_BOOLEAN(new AxiLite.ProtField,  hasProt),
      BR_NAT(new AxiLite.StrbField, if(hasStrb){dataWidth / 8}else{0})
    )
  }
}
