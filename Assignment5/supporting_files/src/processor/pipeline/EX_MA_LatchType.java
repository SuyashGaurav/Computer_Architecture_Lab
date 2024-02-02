package processor.pipeline;

public class EX_MA_LatchType {
	
	boolean MA_enable;
	int aluResult;
	int rs1,rs2,rd,imm;
	String opcode;
	int insPC;
	boolean isNop;
	boolean getPending;
	
	public EX_MA_LatchType()
	{
		MA_enable = false;
		opcode = "999";
		rs1 = 999;
		rs2 = 999;
		rd = 999;
		imm = 999;
		aluResult = 999;
		insPC = -1;
		isNop = false;
		getPending = false;
	}

	public boolean isMA_enable() {
		return MA_enable;
	}

	public void setMA_enable(boolean mA_enable) {
		MA_enable = mA_enable;
	}

}
