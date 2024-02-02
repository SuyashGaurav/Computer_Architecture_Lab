package processor.pipeline;

import processor.Processor;
import generic.Instruction;
import generic.Instruction.OperationType;
import generic.Operand;
import generic.Statistics;
import generic.Operand.OperandType;

public class OperandFetch {
	Processor containingProcessor;
	IF_OF_LatchType IF_OF_Latch;
	OF_EX_LatchType OF_EX_Latch;
	EX_MA_LatchType EX_MA_Latch;
	MA_RW_LatchType MA_RW_Latch;
	IF_EnableLatchType IF_EnableLatch;
	public OperandFetch(Processor containingProcessor, IF_OF_LatchType IF_OF_LatchType, OF_EX_LatchType OF_EX_LatchType, EX_MA_LatchType EX_MA_Latch, IF_EnableLatchType IF_EnableLatchType, MA_RW_LatchType MA_RW_LatchType) {
		this.containingProcessor = containingProcessor;
		this.IF_OF_Latch = IF_OF_LatchType;
		this.OF_EX_Latch = OF_EX_LatchType;
		this.EX_MA_Latch = EX_MA_Latch;
		this.IF_EnableLatch = IF_EnableLatchType;
		this.MA_RW_Latch = MA_RW_LatchType;
	}
	private int twoscompliment(String s) {
		StringBuilder num = new StringBuilder(s);
		for (int i = 0; i < num.length(); i++) {
			if (num.charAt(i) == '0') num.setCharAt(i, '1');
			else num.setCharAt(i, '0');
		}
		String finalNum = num.toString();
		return Integer.parseInt(finalNum, 2)+1;
	}
	private boolean check(Instruction inst, int regVal1, int regVal2) {
		if(inst == null || inst.getOperationType() == null) return false;
		int instno = inst.getOperationType().ordinal();
		if (instno <= 23) {
			int dest_reg = inst != null ? inst.getDestinationOperand().getValue() : -1;
			return (regVal2 == dest_reg || regVal1 == dest_reg);
		} else if ((instno == 6 || instno == 7) && (regVal1 == 31 || regVal2 == 31)) return true;
		return false;
	}
	private void datahazard () {
		IF_EnableLatch.setIF_enable(false);
		OF_EX_Latch.setNop(true);
	}
	public void performOF(){
		if(IF_OF_Latch.getOF_enable()) {
			//TODO
			Statistics.setNumberOfInstructions(Statistics.getNumberOfInstructions() + 1);
			OperationType[]optype= OperationType.values();
			String instruction = Integer.toBinaryString(IF_OF_Latch.getInstruction());
			while(instruction.length() != 32) instruction = "0" + instruction;
			String opcode = instruction.substring(0, 5);
			int type_operation = Integer.parseInt(opcode, 2);
			if (type_operation >= 24 && type_operation <= 28 ) IF_EnableLatch.setIF_enable(false);
			boolean conflict=false;
			Instruction ex_stage_inst = OF_EX_Latch.getInstruction();
			Instruction ma_stage_inst = EX_MA_Latch.getInstruction();
			Instruction rw_stage_inst = MA_RW_Latch.getInstruction();
			Instruction inst = new Instruction();
			Operand rs1 = new Operand();
			Operand rs2 = new Operand();
			Operand rd = new Operand();
			switch(optype[type_operation]){
				case add : 
				case sub : 
				case mul : 
				case div : 
				case and : 
				case or : 
				case xor : 
				case slt : 
				case sll : 
				case srl : 
				case sra : {
					rs1.setOperandType(OperandType.Register);
					rs2.setOperandType(OperandType.Register);
					int regVal1 = Integer.parseInt(instruction.substring(5, 10), 2);
					int regVal2 = Integer.parseInt(instruction.substring(10, 15), 2);
					rs1.setValue(regVal1);
					rs2.setValue(regVal2);
					if (check(ex_stage_inst,regVal1,regVal2)) conflict=true;
					if (check(ma_stage_inst,regVal1,regVal2)) conflict=true;
					if (check(rw_stage_inst,regVal1,regVal2)) conflict=true;
					if (conflict){
						datahazard();
						break;
					}

					rd.setOperandType(OperandType.Register);
					int regVal3 = Integer.parseInt(instruction.substring(15, 20), 2);
					rd.setValue(regVal3);

					inst.setOperationType(optype[type_operation]);
					inst.setSourceOperand1(rs1);
					inst.setSourceOperand2(rs2);
					inst.setDestinationOperand(rd);
					break;}
				case addi :
				case subi :
				case muli :
				case divi : 
				case andi : 
				case ori : 
				case xori : 
				case slti : 
				case slli : 
				case srli : 
				case srai :
				case load :
				case store: {
					rs1.setOperandType(OperandType.Register);
					rs2.setOperandType(OperandType.Immediate);
					int regVal1 = Integer.parseInt(instruction.substring(5, 10), 2);
					rs1.setValue(regVal1);					
					int regVal2 = Integer.parseInt(instruction.substring(15, 32), 2);
					String imm=instruction.substring(15, 32);
					if (imm.charAt(0) == '1'){
						regVal2 = twoscompliment(imm);
						regVal2 =  regVal2 * -1;
					}
					rs2.setValue(regVal2);
					if (check(ex_stage_inst,regVal1,regVal2)) conflict=true;
					if (check(ma_stage_inst,regVal1,regVal2)) conflict=true;
					if (check(rw_stage_inst,regVal1,regVal2)) conflict=true;
					if (conflict){
						datahazard();
						break;
					}
					rd.setOperandType(OperandType.Register);
					int regVal3 = Integer.parseInt(instruction.substring(10, 15), 2);
					rd.setValue(regVal3);

					inst.setOperationType(optype[type_operation]);
					inst.setSourceOperand1(rs1);
					inst.setSourceOperand2(rs2);
					inst.setDestinationOperand(rd);		
					break;		
				}
				case beq : 
				case bne: 
				case blt : 
				case bgt : {
					rs1.setOperandType(OperandType.Register);
					rs2.setOperandType(OperandType.Register);
					int regVal1 = Integer.parseInt(instruction.substring(5, 10), 2);
					int regVal2 = Integer.parseInt(instruction.substring(10, 15), 2);
					rs1.setValue(regVal1);
					rs2.setValue(regVal2);
					if (check(ex_stage_inst,regVal1,regVal2)) conflict=true;
					if (check(ma_stage_inst,regVal1,regVal2)) conflict=true;
					if (check(rw_stage_inst,regVal1,regVal2)) conflict=true;
					if (conflict){
						datahazard();
						break;
					}
					
					rd.setOperandType(OperandType.Immediate);
					int regVal3 = Integer.parseInt(instruction.substring(15, 32), 2);
					String immediate = instruction.substring(15, 32);
					if (immediate.charAt(0) == '1'){
						regVal3 = twoscompliment(immediate);
						regVal3 =  regVal3* -1;
					}
					rd.setValue(regVal3);
					inst.setOperationType(optype[type_operation]);
					inst.setSourceOperand1(rs1);
					inst.setSourceOperand2(rs2);
					inst.setDestinationOperand(rd);	
					break;
				} case jmp :{
					rs1.setOperandType(OperandType.Register);
					rd.setOperandType(OperandType.Immediate);
					int regVal1 = Integer.parseInt(instruction.substring(5, 10), 2);
					rs1.setValue(regVal1);	
					int regVal3 = Integer.parseInt(instruction.substring(10, 32), 2);
					String immediate = instruction.substring(10, 32);
					if (immediate.charAt(0) == '1'){
						regVal3 = twoscompliment(immediate);
						regVal3 =  regVal3* -1;
					}
					rd.setValue(regVal3);
					inst.setOperationType(optype[type_operation]);
					inst.setSourceOperand1(rs1);
					inst.setDestinationOperand(rd);		
					break;				
				}
				default:{
					inst.setOperationType(optype[type_operation]);
					IF_EnableLatch.setIF_enable(false);
					break;
				}
			}
			OF_EX_Latch.setInstruction(inst);
			OF_EX_Latch.setEX_enable(true);
		}
	}
}