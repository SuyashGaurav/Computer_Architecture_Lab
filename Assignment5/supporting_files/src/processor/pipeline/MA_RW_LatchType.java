package processor.pipeline;

public class MA_RW_LatchType {
	
	boolean RW_enable;
	int aluResult;
	int rs1,rs2,rd,imm;
	String opcode;
	int alu_result;
	boolean NOP;
	int insPC;
	boolean isNop;
	int load_result;
	boolean isLoad;

	public MA_RW_LatchType() {
		RW_enable = false;
		opcode = "999";
		aluResult = 999;
		insPC = -1;
		isLoad =  false;
		isNop = false;
		rs1 = 999;
		rs2 = 999;
		rd = 999;
		imm = 999;
	}

	public String toString() {
		return "MA_RW_LatchType";
	}

	public boolean isRW_enable() {
		return RW_enable;
	}
		
	public void setIsNOP(boolean is_NOP) {
		NOP = is_NOP;
	}
	public void setRW_enable(boolean rW_enable) {
		RW_enable = rW_enable;
	}

	public void setALU_result(int result) {
		alu_result = result;
	}
	public void setLoad_result(int result) {
		load_result = result;
	}

	public int getLoad_result() {
		return load_result;
	}

	public int getALU_result() {
		return alu_result;
	}
	
	public boolean getIsNOP() {
		return NOP;
	}
}
