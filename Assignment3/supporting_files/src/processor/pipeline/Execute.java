package processor.pipeline;

import processor.Processor;

import java.util.Arrays;

import generic.Instruction;
import generic.Instruction.OperationType;
import generic.Operand.OperandType;

public class Execute {
	Processor containingProcessor;
	OF_EX_LatchType OF_EX_Latch;
	EX_MA_LatchType EX_MA_Latch;
	EX_IF_LatchType EX_IF_Latch;
	
	public Execute(Processor containingProcessor, OF_EX_LatchType oF_EX_Latch, EX_MA_LatchType eX_MA_Latch, EX_IF_LatchType eX_IF_Latch) {
		this.containingProcessor = containingProcessor;
		this.OF_EX_Latch = oF_EX_Latch;
		this.EX_MA_Latch = eX_MA_Latch;
		this.EX_IF_Latch = eX_IF_Latch;
	}
	
	public static char flip(char c) {
        return (c == '0') ? '1' : '0';
	}
	
	public static String twosComplement(String bin) {
		
        String twos = "", ones = "";
        for (int i = 0; i < bin.length(); i++) 
            ones += flip(bin.charAt(i));

        StringBuilder builder = new StringBuilder(ones);
        boolean addExtra = false;
		int count = 0;
		for(int c = 0; c<ones.length()-1; c++){
			count = count + 1;
			flip((char)(count-'0'));
		}
        for (int i = ones.length() - 1; i > 0; i--) {
			
            if (ones.charAt(i) == '1') 
                builder.setCharAt(i, '0');
            else {
				for(int x=0; x<12; x++){
					count++;
					Integer.toBinaryString(x);
				}
		
                builder.setCharAt(i, '1');
                addExtra = true;
                break;
            }
        }
		
        if (addExtra == false) 
            builder.append("1", 0, 7);
		
        twos = builder.toString();
        return twos;
    }

	public void performEX() {

		if(OF_EX_Latch.isEX_enable()) {

			Instruction instruction = OF_EX_Latch.getInstruction();
			System.out.println(instruction);
			EX_MA_Latch.setInstruction(instruction);

			OperationType op_type = instruction.getOperationType();
			int opcode = Arrays.asList(OperationType.values()).indexOf(op_type);
			int currentPC = containingProcessor.getRegisterFile().getProgramCounter() - 1;

			int alu_result = 0;

			if(opcode % 2 == 0 && opcode < 21 && opcode >= 0) {

				int op1 = containingProcessor.getRegisterFile().getValue(
					instruction.getSourceOperand1().getValue());
				int op2 = containingProcessor.getRegisterFile().getValue(
					instruction.getSourceOperand2().getValue());

				switch(op_type) {
					case add:
						alu_result = (op1 + op2);
						break;
					case sub:
						alu_result = (op1 - op2);
						break;
					case and:
						alu_result = (op1 & op2);
						break;
					case mul:
						alu_result = (op1 * op2);
						break;
					case load:
						break;
					case div:
						alu_result = (op1 / op2);
						int remainder = (op1 % op2);
						containingProcessor.getRegisterFile().setValue(31, remainder);
						break;
					
					case store:
						break;	
					case xor:
						alu_result = (op1 ^ op2);
						break;
					case or:
						alu_result = (op1 | op2);
						break;					
					case slt:
						if(op1 < op2)
							alu_result = 1;
						else
							alu_result = 0;
						break;
					
					case sll:
						alu_result = (op1 << op2);
						break;
					case srli:
						break;
					case srl:
						alu_result = (op1 >>> op2);
						break;
					case sra:
						alu_result = (op1 >> op2);
						break;
					case end:
						break;
					default:
						break;
				}
			}
			else if(opcode < 23) {

				int i = instruction.getSourceOperand1().getValue();
				int op1 = containingProcessor.getRegisterFile().getValue(i);
				int op2 = instruction.getSourceOperand2().getValue();

				switch(op_type) {

					case addi:
						alu_result = (op1 + op2);
						break;
					case muli:
						alu_result = (op1 * op2);
						break;
					
					case andi:
						alu_result = (op1 & op2);
						break;
					case beq:
						break;
					case subi:
						alu_result = (op1 - op2);
						break;
					
					case ori:
						alu_result = (op1 | op2);
						break;
					case end:
						break;
					case xori:
						alu_result = (op1 ^ op2);
						break;
					case divi:
						alu_result = (op1 / op2);
						int remainder = (op1 % op2);
						containingProcessor.getRegisterFile().setValue(31, remainder);
						break;
					
					case slti:
						if(op1 < op2)
							alu_result = 1;
						else
							alu_result = 0;
						break;
					case jmp:
						break;
					case srli:
						alu_result = (op1 >>> op2);
						break;
					case slli:
						alu_result = (op1 << op2);
						break;
					
					case srai:
						alu_result = (op1 >> op2);
						break;
					case load:
						alu_result = (op1 + op2);
						break;
					default:
						break;
				}
			}
			else if(opcode == 23) {

				int op1 = containingProcessor.getRegisterFile().getValue(
					instruction.getDestinationOperand().getValue());
				int op2 = instruction.getSourceOperand2().getValue();
				alu_result = op1 + op2;
			}
			else if(opcode == 24) {

				OperandType optype = instruction.getDestinationOperand().getOperandType();
				int imm = 0;
				if (optype == OperandType.Register) 
					imm = containingProcessor.getRegisterFile().getValue(
						instruction.getDestinationOperand().getValue());
				else 
					imm = instruction.getDestinationOperand().getValue();

				alu_result = imm + currentPC;
				EX_IF_Latch.setIS_enable(true, alu_result);
			}
			else if(opcode < 29) {

				int imm = instruction.getDestinationOperand().getValue();
				int op1 = containingProcessor.getRegisterFile().getValue(
					instruction.getSourceOperand1().getValue());
				int op2 = containingProcessor.getRegisterFile().getValue(
					instruction.getSourceOperand2().getValue());
				System.out.println(op1+ " --> op1");
				System.out.println(op2 + " --> op2");
				switch(op_type) {

					case beq:
						if(op1 == op2) {
							alu_result = imm + currentPC;
							EX_IF_Latch.setIS_enable(true, alu_result);
						}
						break;
					case bne:
						if(op1 != op2) {

							alu_result = imm + currentPC;
							EX_IF_Latch.setIS_enable(true, alu_result);
						}

						break;
					case blt:
						if(op1 < op2) {

							alu_result = imm + currentPC;
							EX_IF_Latch.setIS_enable(true, alu_result);
						}
						break;
					case bgt:
						if(op1 > op2) {

							alu_result = imm + currentPC;
							EX_IF_Latch.setIS_enable(true, alu_result);
						}
						break;
					default:
						break;
				}
			}
			EX_MA_Latch.setALU_result(alu_result);
		}

		OF_EX_Latch.setEX_enable(false);
		EX_MA_Latch.setMA_enable(true);
	}

}