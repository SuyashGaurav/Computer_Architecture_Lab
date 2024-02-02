package processor.pipeline;
import processor.Processor;
public class OperandFetch {
	Processor containingProcessor;
	IF_EnableLatchType IF_EnableLatch;
	IF_OF_LatchType IF_OF_Latch;
	OF_EX_LatchType OF_EX_Latch;
	EX_MA_LatchType EX_MA_Latch;
	MA_RW_LatchType MA_RW_Latch;

	private int twoscompliment(String s) {
		StringBuilder num = new StringBuilder(s);
		for (int i = 0; i < num.length(); i++) {
			if (num.charAt(i) == '0') num.setCharAt(i, '1');
			else num.setCharAt(i, '0');
		}
		String finalNum = num.toString();
		return Integer.parseInt(finalNum, 2)+1;
	}
	public OperandFetch(Processor containingProcessor, IF_OF_LatchType IF_OF_Latch, OF_EX_LatchType OF_EX_Latch, EX_MA_LatchType EX_MA_Latch, MA_RW_LatchType MA_RW_Latch,IF_EnableLatchType IF_EnableLatch){
		this.containingProcessor = containingProcessor;
		this.IF_OF_Latch = IF_OF_Latch;
		this.OF_EX_Latch = OF_EX_Latch;
		this.EX_MA_Latch = EX_MA_Latch;
		this.MA_RW_Latch = MA_RW_Latch;
		this.IF_EnableLatch = IF_EnableLatch;
	}
	public void performOF(){	
		 // Check if OF (Operand Fetch) stage is enabled and there is no pending instruction in the previous stage (EX - Execute)
		if(IF_OF_Latch.isOF_enable() && OF_EX_Latch.getPending == false){
			String instruction = Integer.toBinaryString(IF_OF_Latch.getInstruction());
			// Convert the instruction to a 32-bit binary string
			while(instruction.length() != 32) instruction = "0" + instruction;
			int opcode,rs1,rs2,rd,imm;
			String op = instruction.substring(0, 5);
			opcode = Integer.parseInt(op,2);
			int dummyVar = 999;
			rs1 = dummyVar;
			rs2 = dummyVar;
			rd = dummyVar;
			imm = dummyVar;
			if(opcode == 0) {
				rs1 = containingProcessor.getRegisterFile().getValue(Integer.parseInt(instruction.substring(5, 10),2));
				rs2 = containingProcessor.getRegisterFile().getValue(Integer.parseInt(instruction.substring(10, 15),2));
				rd = Integer.parseInt(instruction.substring(15, 20),2);
				imm = dummyVar;
			}else if(opcode > 0 && opcode < 22) {
				if(opcode % 2 == 1) {
					rs1 = containingProcessor.getRegisterFile().getValue(Integer.parseInt(instruction.substring(5, 10),2));
					rs2 = dummyVar;
					rd = Integer.parseInt(instruction.substring(10, 15),2);
					imm = Integer.parseInt(instruction.substring(15, 32),2);
				}else {
					rs1 = containingProcessor.getRegisterFile().getValue(Integer.parseInt(instruction.substring(5, 10),2));
					rs2 = containingProcessor.getRegisterFile().getValue(Integer.parseInt(instruction.substring(10, 15),2));
					rd = Integer.parseInt(instruction.substring(15, 20),2);
					imm = dummyVar;
				}
			}else {
				if(opcode == 24) {
					rs1 = dummyVar;
					rs2 = dummyVar;
					rd = Integer.parseInt(instruction.substring(5, 10),2);
					imm = Integer.parseInt(instruction.substring(10, 32),2);
					if(instruction.substring(10, 32).charAt(0) == '1') {
						imm = twoscompliment(instruction.substring(10, 32));
						imm =  imm * -1;
					}
				}else if(opcode != 29) {
					rs1 = containingProcessor.getRegisterFile().getValue(Integer.parseInt(instruction.substring(5, 10),2));
					rs2 = dummyVar;
					rd = Integer.parseInt(instruction.substring(10, 15),2);
					imm = Integer.parseInt(instruction.substring(15, 32),2);
					if(instruction.substring(15, 32).charAt(0) == '1') {
						imm = twoscompliment(instruction.substring(15, 32));
						imm =  imm * -1;
					}
				}else {
					rs1 = dummyVar;
					rs2 = dummyVar;
					imm = dummyVar;
					rd = dummyVar;
				}
			}
			IF_EnableLatch.setIF_enable(true);
			OF_EX_Latch.setEX_enable(true);
			OF_EX_Latch.insPC = IF_OF_Latch.insPC;
			if(opcode == 29) {
				IF_EnableLatch.setIF_enable(false);
				IF_OF_Latch.setOF_enable(false);
			}
			// Update the OF_EX_Latch with the extracted information
			OF_EX_Latch.isNop = false;
			OF_EX_Latch.opcode = op;
			OF_EX_Latch.rs1 = rs1;
			OF_EX_Latch.rs2 = rs2;
			OF_EX_Latch.imm = imm;
			OF_EX_Latch.rd = rd;
			//TODO
			OF_EX_Latch.setEX_enable(true);
			IF_OF_Latch.setOF_enable(false);
		}
	}
}
