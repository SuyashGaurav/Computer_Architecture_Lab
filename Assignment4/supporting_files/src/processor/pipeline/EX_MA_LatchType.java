package processor.pipeline;

import generic.Instruction;

public class EX_MA_LatchType {
	boolean MA_enable, nop;
	int aluResult;
	Instruction inst;
	public EX_MA_LatchType() {
		MA_enable = false;
	}
	public EX_MA_LatchType(boolean MA_enable) {
		this.MA_enable = MA_enable;
	}
	public EX_MA_LatchType(boolean MA_enable, int aluResult) {
		this.MA_enable = MA_enable;
		this.aluResult = aluResult;
	}
	public EX_MA_LatchType(boolean MA_enable, int aluResult, Instruction inst) {
		this.MA_enable = MA_enable;
		this.aluResult = aluResult;
		this.inst = inst;
	}
	public void setIsNOP(boolean nop) {
		this.nop = nop;
	}
	public void setMA_enable(boolean MA_enable) {
		this.MA_enable = MA_enable;
	}
	public void setInstruction(Instruction inst) {
		this.inst = inst;
	}
	public void setALU_result(int result) {
		aluResult = result;
	}
	public boolean isMA_enable() {
		return MA_enable;
	}
	public Instruction getInstruction() {
		return inst;
	}
	public int getALU_result() {
		return aluResult;
	}
	public boolean getIsNOP() {
		return nop;
	}
}
