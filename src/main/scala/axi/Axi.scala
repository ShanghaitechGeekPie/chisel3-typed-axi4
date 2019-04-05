
package axi

import chisel3._
import chisel3.util._

import axi._branch.bibranch._
import axi._branch.natbranch._

object Axi {
  abstract class BoolOptField() extends BIBRANCH {
    type TRUE_T = Bool
    val instantiate_true_branch = () => Bool()
    type FALSE_T = Null
    val instantiate_false_branch = () => null
  }
  abstract class FixedWidthOptField(n: Int) extends BIBRANCH {
    type TRUE_T = UInt
    val instantiate_true_branch = () => UInt(n.W)
    type FALSE_T = Null
    val instantiate_false_branch = () => null
  }
  abstract class FreeWidthOptField extends NATBRANCH {
    type ZERO_T = Null
    val instantiate_zero_branch = () => null
    type SUCC_T = UInt
    val instantiate_succ_branch = (n: nat) => UInt(n.value.W)
  }
  class IdField    extends Axi.FreeWidthOptField
  class CacheField extends Axi.FixedWidthOptField(4)
  class LockField  extends Axi.BoolOptField
  class ProtField  extends Axi.FixedWidthOptField(3)
  class QosField   extends Axi.FixedWidthOptField(4)
  class RegionField extends Axi.FixedWidthOptField(4)
  class UserField  extends Axi.FreeWidthOptField
}

class AxiAddr[
  Id_CFG     <: BR_NAT[Axi.IdField],
  Cache_CFG  <: BR_BOOLEAN[Axi.CacheField],
  Lock_CFG   <: BR_BOOLEAN[Axi.LockField],
  Prot_CFG   <: BR_BOOLEAN[Axi.ProtField],
  Qos_CFG    <: BR_BOOLEAN[Axi.QosField],
  Region_CFG <: BR_BOOLEAN[Axi.RegionField],
  User_CFG   <: BR_NAT[Axi.UserField]
](
  val addrWidth:  Int,
  val id_cfg:     Id_CFG,
  val cache_cfg:  Cache_CFG,
  val lock_cfg:   Lock_CFG,
  val prot_cfg:   Prot_CFG,
  val qos_cfg:    Qos_CFG,
  val region_cfg: Region_CFG,
  val user_cfg:   User_CFG
) extends Bundle {
  val addr   = UInt(addrWidth.W)
  val id     = id_cfg.instantiate()
  val size   = UInt(3.W)
  val len    = UInt(8.W)
  val burst  = UInt(2.W)
  val cache  = cache_cfg.instantiate()
  val lock   = lock_cfg.instantiate()
  val prot   = prot_cfg.instantiate()
  val qos    = qos_cfg.instantiate()
  val region = region_cfg.instantiate()
  val user   = user_cfg.instantiate()
}

abstract class AxiData[
  User_CFG <: BR_NAT[Axi.UserField]
](val dataWidth: Int, val user_cfg: User_CFG) extends Bundle {
  val data = UInt(dataWidth.W)
  val last = Bool()
  val user = user_cfg.instantiate()
}

class AxiReadData[
  User_CFG <: BR_NAT[Axi.UserField],
  Id_CFG   <: BR_NAT[Axi.IdField]
](
  dataWidth:  Int,
  user_cfg:   User_CFG,
  val id_cfg: Id_CFG
) extends AxiData(dataWidth, user_cfg) {
  val id   = id_cfg.instantiate()
  val resp = UInt(2.W)
}

class AxiWriteData[
  User_CFG <: BR_NAT[Axi.UserField]
](
  dataWidth: Int, user_cfg: User_CFG
) extends AxiData(dataWidth, user_cfg) {
  val strb = UInt((dataWidth / 8).W)
}

class AxiWriteResp[
  Id_CFG   <: BR_NAT[Axi.IdField],
  User_CFG <: BR_NAT[Axi.UserField]
](
  val id_cfg:   Id_CFG,
  val user_cfg: User_CFG
) extends Bundle {
  val id   = id_cfg.instantiate()
  val resp = UInt(2.W)
  val user = user_cfg.instantiate()
}

class AxiMaster[
  Id_CFG     <: BR_NAT[Axi.IdField],
  Cache_CFG  <: BR_BOOLEAN[Axi.CacheField],
  Lock_CFG   <: BR_BOOLEAN[Axi.LockField],
  Prot_CFG   <: BR_BOOLEAN[Axi.ProtField],
  Qos_CFG    <: BR_BOOLEAN[Axi.QosField],
  Region_CFG <: BR_BOOLEAN[Axi.RegionField],
  arUser_CFG <: BR_NAT[Axi.UserField],
  rUser_CFG  <: BR_NAT[Axi.UserField],
  awUser_CFG <: BR_NAT[Axi.UserField],
  wUser_CFG  <: BR_NAT[Axi.UserField],
  bUser_CFG  <: BR_NAT[Axi.UserField]
](
  val addrWidth:  Int,
  val dataWidth:  Int,
  val cache_cfg:  Cache_CFG,
  val lock_cfg:   Lock_CFG,
  val prot_cfg:   Prot_CFG,
  val qos_cfg:    Qos_CFG,
  val region_cfg: Region_CFG,
  val id_cfg:     Id_CFG,
  val aruser_cfg: arUser_CFG,
  val ruser_cfg:  rUser_CFG,
  val awuser_cfg: awUser_CFG,
  val wuser_cfg:  wUser_CFG,
  val buser_cfg:  bUser_CFG
) extends Bundle {
  val readAddr = Decoupled(
    new AxiAddr(
      addrWidth,
      id_cfg,
      cache_cfg,
      lock_cfg,
      prot_cfg,
      qos_cfg,
      region_cfg,
      aruser_cfg
    )
  )
  val writeAddr = Decoupled(
    new AxiAddr(
      addrWidth,
      id_cfg,
      cache_cfg,
      lock_cfg,
      prot_cfg,
      qos_cfg,
      region_cfg,
      awuser_cfg
    )
  )
  val readData = Flipped(Decoupled(
    new AxiReadData(
      dataWidth,
      ruser_cfg,
      id_cfg
    )
  ))
  val writeData = Flipped(Decoupled(
    new AxiWriteData(
      dataWidth,
      wuser_cfg
    )
  ))
  val writeResp = Flipped(Decoupled(
    new AxiWriteResp(
      id_cfg,
      buser_cfg
    )
  ))
}

object AxiMaster {
  def apply[
    Id_CFG     <: BR_NAT[Axi.IdField],
    Cache_CFG  <: BR_BOOLEAN[Axi.CacheField],
    Lock_CFG   <: BR_BOOLEAN[Axi.LockField],
    Prot_CFG   <: BR_BOOLEAN[Axi.ProtField],
    Qos_CFG    <: BR_BOOLEAN[Axi.QosField],
    Region_CFG <: BR_BOOLEAN[Axi.RegionField],
    arUser_CFG <: BR_NAT[Axi.UserField],
    rUser_CFG  <: BR_NAT[Axi.UserField],
    awUser_CFG <: BR_NAT[Axi.UserField],
    wUser_CFG  <: BR_NAT[Axi.UserField],
    bUser_CFG  <: BR_NAT[Axi.UserField]
  ](
    addrWidth:   Int,
    dataWidth:   Int,
    hasCache:    Boolean = false,
    hasLock:     Boolean = false,
    hasProt:     Boolean = false,
    hasQos:      Boolean = false,
    hasRegion:   Boolean = false,
    idWidth:     Int = 0,
    arUserWidth: Int = 0,
    rUserWidth:  Int = 0,
    awUserWidth: Int = 0,
    wUserWidth:  Int = 0,
    bUserWidth:  Int = 0
  ) = {
    new AxiMaster(
      addrWidth,
      dataWidth,
      BR_BOOLEAN(new Axi.CacheField,  hasCache),
      BR_BOOLEAN(new Axi.LockField,   hasLock),
      BR_BOOLEAN(new Axi.ProtField,   hasProt),
      BR_BOOLEAN(new Axi.QosField,    hasQos),
      BR_BOOLEAN(new Axi.RegionField, hasRegion),
      BR_NAT(new Axi.IdField,    idWidth),
      BR_NAT(new Axi.UserField,  arUserWidth),
      BR_NAT(new Axi.UserField,  rUserWidth),
      BR_NAT(new Axi.UserField,  awUserWidth),
      BR_NAT(new Axi.UserField,  wUserWidth),
      BR_NAT(new Axi.UserField,  bUserWidth)
    )
  }
}

class AxiSlave[
  Id_CFG     <: BR_NAT[Axi.IdField],
  Cache_CFG  <: BR_BOOLEAN[Axi.CacheField],
  Lock_CFG   <: BR_BOOLEAN[Axi.LockField],
  Prot_CFG   <: BR_BOOLEAN[Axi.ProtField],
  Qos_CFG    <: BR_BOOLEAN[Axi.QosField],
  Region_CFG <: BR_BOOLEAN[Axi.RegionField],
  arUser_CFG <: BR_NAT[Axi.UserField],
  rUser_CFG  <: BR_NAT[Axi.UserField],
  awUser_CFG <: BR_NAT[Axi.UserField],
  wUser_CFG  <: BR_NAT[Axi.UserField],
  bUser_CFG  <: BR_NAT[Axi.UserField]
](
  val addrWidth:  Int,
  val dataWidth:  Int,
  val cache_cfg:  Cache_CFG,
  val lock_cfg:   Lock_CFG,
  val prot_cfg:   Prot_CFG,
  val qos_cfg:    Qos_CFG,
  val region_cfg: Region_CFG,
  val id_cfg:     Id_CFG,
  val aruser_cfg: arUser_CFG,
  val ruser_cfg:  rUser_CFG,
  val awuser_cfg: awUser_CFG,
  val wuser_cfg:  wUser_CFG,
  val buser_cfg:  bUser_CFG
) extends Bundle {
  val readAddr = Flipped(Decoupled(
    new AxiAddr(
      addrWidth,
      id_cfg,
      cache_cfg,
      lock_cfg,
      prot_cfg,
      qos_cfg,
      region_cfg,
      aruser_cfg
    )
  ))
  val writeAddr = Flipped(Decoupled(
    new AxiAddr(
      addrWidth,
      id_cfg,
      cache_cfg,
      lock_cfg,
      prot_cfg,
      qos_cfg,
      region_cfg,
      awuser_cfg
    )
  ))
  val readData = Decoupled(
    new AxiReadData(
      dataWidth,
      ruser_cfg,
      id_cfg
    )
  )
  val writeData = Flipped(Decoupled(
    new AxiWriteData(
      dataWidth,
      wuser_cfg
    )
  ))
  val writeResp = Decoupled(
    new AxiWriteResp(
      id_cfg,
      buser_cfg
    )
  )
}

object AxiSlave {
  def apply[
    Id_CFG     <: BR_NAT[Axi.IdField],
    Cache_CFG  <: BR_BOOLEAN[Axi.CacheField],
    Lock_CFG   <: BR_BOOLEAN[Axi.LockField],
    Prot_CFG   <: BR_BOOLEAN[Axi.ProtField],
    Qos_CFG    <: BR_BOOLEAN[Axi.QosField],
    Region_CFG <: BR_BOOLEAN[Axi.RegionField],
    arUser_CFG <: BR_NAT[Axi.UserField],
    rUser_CFG  <: BR_NAT[Axi.UserField],
    awUser_CFG <: BR_NAT[Axi.UserField],
    wUser_CFG  <: BR_NAT[Axi.UserField],
    bUser_CFG  <: BR_NAT[Axi.UserField]
  ](
    addrWidth:   Int,
    dataWidth:   Int,
    hasCache:    Boolean = false,
    hasLock:     Boolean = false,
    hasProt:     Boolean = false,
    hasQos:      Boolean = false,
    hasRegion:   Boolean = false,
    idWidth:     Int = 0,
    arUserWidth: Int = 0,
    rUserWidth:  Int = 0,
    awUserWidth: Int = 0,
    wUserWidth:  Int = 0,
    bUserWidth:  Int = 0
  ) = {
    new AxiSlave(
      addrWidth,
      dataWidth,
      BR_BOOLEAN(new Axi.CacheField,  hasCache),
      BR_BOOLEAN(new Axi.LockField,   hasLock),
      BR_BOOLEAN(new Axi.ProtField,   hasProt),
      BR_BOOLEAN(new Axi.QosField,    hasQos),
      BR_BOOLEAN(new Axi.RegionField, hasRegion),
      BR_NAT(new Axi.IdField,    idWidth),
      BR_NAT(new Axi.UserField,  arUserWidth),
      BR_NAT(new Axi.UserField,  rUserWidth),
      BR_NAT(new Axi.UserField,  awUserWidth),
      BR_NAT(new Axi.UserField,  wUserWidth),
      BR_NAT(new Axi.UserField,  bUserWidth)
    )
  }
}
