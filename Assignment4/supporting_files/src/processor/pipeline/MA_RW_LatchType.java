package processor.pipeline;

import generic.Instruction;

public class MA_RW_LatchType {
	boolean RW_enable;
	Instruction inst;
	int loadResult;
	int aluResult;
	boolean nop;
	public MA_RW_LatchType() {
		this.RW_enable = false;
	}
	public MA_RW_LatchType(boolean RW_enable) {
		this.RW_enable = RW_enable;
	}
	public MA_RW_LatchType(boolean RW_enable, Instruction inst) {
		this.RW_enable = RW_enable;
		this.inst = inst;
	}
	public MA_RW_LatchType(boolean RW_enable, Instruction inst, int loadResult) {
		this.RW_enable = RW_enable;
		this.inst = inst;
		this.loadResult = loadResult;
	}
	public MA_RW_LatchType(boolean RW_enable, Instruction instruction, int loadResult, int aluResult) {
		this.RW_enable = RW_enable;
		this.inst = instruction;
		this.loadResult = loadResult;
		this.aluResult = aluResult;
	}
	public void setALU_result(int result) {
		aluResult = result;
	}
	public void setRW_enable(boolean RW_enable) {
		this.RW_enable = RW_enable;
	}
	public void setIsNOP(boolean nop) {
		this.nop = nop;
	}
	public void setInstruction(Instruction inst) {
		this.inst = inst;
	}
	public void setLoad_result(int result) {
		this.loadResult = result;
	}
	public boolean getRW_enable() {
		return RW_enable;
	}
	public boolean getIsNOP() {
		return nop;
	}
	public Instruction getInstruction() {
		return inst;
	}
	public int getLoad_result() {
		return loadResult;
	}
	public int getALU_result() {
		return aluResult;
	}
}
