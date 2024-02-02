package processor.pipeline;

import generic.Instruction;

public class OF_EX_LatchType {
	boolean EX_enable, nop;
	Instruction inst;
	public OF_EX_LatchType() {
		EX_enable = false;
	}
	public OF_EX_LatchType(boolean eX_enable) {
		EX_enable = eX_enable;
	}
	public OF_EX_LatchType(boolean EX_enable, Instruction inst) {
		this.EX_enable = EX_enable;
		this.inst = inst;
	}
	public void setEX_enable(boolean eX_enable) {
		EX_enable = eX_enable;
	}
	public void setNop(boolean is_NOP) {
		nop = is_NOP;
	}
	public void setInstruction(Instruction inst) {
		this.inst = inst;
	}
	public boolean getEX_enable() {
		return EX_enable;
	}
	public boolean getNop() {
		return nop;
	}
	public Instruction getInstruction() {
		return this.inst;
	}
}
