package processor.pipeline;

import processor.Processor;

import java.util.Arrays;

import generic.Instruction;
import generic.Instruction.OperationType;
import generic.Operand.OperandType;
import generic.Statistics;

public class Execute {
	Processor containingProcessor;
	OF_EX_LatchType OF_EX_Latch;
	EX_MA_LatchType EX_MA_Latch;
	EX_IF_LatchType EX_IF_Latch;
	IF_OF_LatchType IF_OF_Latch;
	IF_EnableLatchType IF_EnableLatch;
	public Execute(Processor containingProcessor, OF_EX_LatchType OF_EX_Latch, EX_MA_LatchType EX_MA_Latch, EX_IF_LatchType EX_IF_Latch, IF_EnableLatchType IF_OF_Latch, IF_OF_LatchType IF_EnableLatch){
		this.containingProcessor = containingProcessor;
		this.OF_EX_Latch = OF_EX_Latch;
		this.EX_MA_Latch = EX_MA_Latch;
		this.EX_IF_Latch = EX_IF_Latch;
		this.IF_EnableLatch = IF_OF_Latch;
		this.IF_OF_Latch = IF_EnableLatch;
	}
	public static int twoscompliment(String s) {
		StringBuilder num = new StringBuilder(s);
		for (int i = 0; i < num.length(); i++) {
			if (num.charAt(i) == '0') num.setCharAt(i, '1');
			else num.setCharAt(i, '0');
		}
		String finalNum = num.toString();
		return Integer.parseInt(finalNum, 2)+1;
	}
	public void performEX() {
		if (OF_EX_Latch.getNop()) {
			EX_MA_Latch.setIsNOP(true);
			OF_EX_Latch.setNop(false);
			EX_MA_Latch.setInstruction(null);
		} else if(OF_EX_Latch.getEX_enable()) {
			Instruction inst = OF_EX_Latch.getInstruction();
			EX_MA_Latch.setInstruction(inst);

			OperationType opType = inst.getOperationType();
			int opcode = Arrays.asList(OperationType.values()).indexOf(opType),currPC = containingProcessor.getRegisterFile().getProgramCounter() - 1;
			if (opcode >= 24 && opcode <= 29) {
				Statistics.setNumberOfBranchTaken(Statistics.getNumberOfBranchTaken() + 2);
				IF_EnableLatch.setIF_enable(false);
				IF_OF_Latch.setOF_enable(false);
				OF_EX_Latch.setEX_enable(false);
			}
			int aluResult = 0;
			if(opcode % 2 == 0 && opcode < 21) {
				int op1 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());
				int op2 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand2().getValue());
				switch(opType) {
					case add:
						aluResult = op1 + op2;
						break;
					case sub:
						aluResult = op1 - op2;
						break;
					case and:
						aluResult = op1 & op2;
						break;
					case mul:
						aluResult = op1 * op2;
						break;
					case div:
						aluResult = op1 / op2;
						containingProcessor.getRegisterFile().setValue(31, op1 % op2);
						break;
					case xor:
						aluResult = op1 ^ op2;
						break;
					case or:
						aluResult = op1 | op2;
						break;					
					case slt:
						if(op1 < op2) aluResult = 1;
						else aluResult = 0;
						break;
					case sll:
						aluResult = op1 << op2;
						break;
					case srli:
						break;
					case srl:
						aluResult = (op1 >>> op2);
						break;
					case sra:
						aluResult = (op1 >> op2);
						break;
					case end:
						break;
					default:
						break;
				}
			}
			else if(opcode < 23) {
				int op1 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());
				int op2 = inst.getSourceOperand2().getValue();
				switch(opType) {
					case addi:
						aluResult = op1 + op2;
						break;
					case muli:
						aluResult = op1 * op2;
						break;
					case andi:
						aluResult = op1 & op2;
						break;
					case subi:
						aluResult = op1 - op2;
						break;
					case ori:
						aluResult = op1 | op2;
						break;
					case end:
						break;
					case xori:
						aluResult = op1 ^ op2;
						break;
					case divi:
						aluResult = (op1 / op2);
						containingProcessor.getRegisterFile().setValue(31, op1 % op2);
						break;
					case slti:
						if(op1 < op2) aluResult = 1;
						else aluResult = 0;
						break;
					case srli:
						aluResult = (op1 >>> op2);
						break;
					case slli:
						aluResult = (op1 << op2);
						break;
					case srai:
						aluResult = (op1 >> op2);
						break;
					case load:
						aluResult = (op1 + op2);
						break;
					default:
						break;
				}
			} else if(opcode == 23) {
				int op1 = containingProcessor.getRegisterFile().getValue(inst.getDestinationOperand().getValue());
				int op2 = inst.getSourceOperand2().getValue();
				aluResult = op1 + op2;
			} else if(opcode == 24) {
				OperandType optype = inst.getDestinationOperand().getOperandType();
				int imm = 0;
				if (optype == OperandType.Register) imm = containingProcessor.getRegisterFile().getValue(inst.getDestinationOperand().getValue());
				else imm = inst.getDestinationOperand().getValue();
				aluResult = imm + currPC;
				EX_IF_Latch.setIS_enable(true, aluResult);
			} else if(opcode < 29) {
				int imm = inst.getDestinationOperand().getValue();
				int op1 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());
				int op2 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand2().getValue());
				switch(opType) {
					case beq:
						if(op1 == op2) {
							aluResult = imm + currPC;
							EX_IF_Latch.setIS_enable(true, aluResult);
						}
						break;
					case bne:
						if(op1 != op2) {
							aluResult = imm + currPC;
							EX_IF_Latch.setIS_enable(true, aluResult);
						}

						break;
					case blt:
						if(op1 < op2) {
							aluResult = imm + currPC;
							EX_IF_Latch.setIS_enable(true, aluResult);
						}
						break;
					case bgt:
						if(op1 > op2) {
							aluResult = imm + currPC;
							EX_IF_Latch.setIS_enable(true, aluResult);
						}
						break;
					default:
						break;
				}
			}
			EX_MA_Latch.setALU_result(aluResult);
			EX_MA_Latch.setMA_enable(true);
		}
	}
}
