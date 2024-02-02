package processor.pipeline;

public class OF_EX_LatchType {
	
	boolean EX_enable;
	String opcode;
	int rs1,rs2,rd,imm;
	int insPC;
	boolean isNop;
	boolean getPending;
	boolean NOP;
	
	public OF_EX_LatchType() {
		EX_enable = false;
		opcode = "999";
		insPC = -1;
		isNop = false;
		getPending = false;
		rs1 = 999;
		rs2 = 999;
		imm = 999;
		rd = 999;
	}
	public String toString() {
		return "OF_EX_LatchType";
	}

	public boolean comparePC (int pc) {
		return insPC == pc;
	}

	public boolean isEX_enable() {
		return EX_enable;
	}
	public void setIsNOP(boolean is_NOP) {
		NOP = is_NOP;
	}
	public void setEX_enable(boolean eX_enable) {
		EX_enable = eX_enable;
	}
	public boolean getIsNOP() {
		return NOP;
	}
}
