package processor.pipeline;

import processor.Processor;

import generic.Instruction;
import generic.Instruction.OperationType;
import generic.Operand;
import generic.Operand.OperandType;

public class OperandFetch {
	Processor containingProcessor;
	IF_OF_LatchType IF_OF_Latch;
	OF_EX_LatchType OF_EX_Latch;
		
	public OperandFetch(Processor containingProcessor, IF_OF_LatchType iF_OF_Latch, OF_EX_LatchType oF_EX_Latch) {
		
		this.containingProcessor = containingProcessor;
		this.IF_OF_Latch = iF_OF_Latch;
		this.OF_EX_Latch = oF_EX_Latch;
	}

    static int onesComplement(int num) { 
          
        int number_of_bits =  (int)(Math.floor(Math.log(num) / Math.log(2))) + 1; 
        return ((1 << number_of_bits) - 1) ^ num; 
    } 

	public static String twosComplement(String bin) {
		
        String twos = "", ones = "";  // Initialize two strings to store the one's complement and two's complement
        for (int i = 0; i < bin.length(); i++) // Calculate the one's complement by flipping 0s to 1s and 1s to 0s
            ones += (bin.charAt(i) == '0') ? '1' : '0';

		// Create a StringBuilder to manipulate the one's complement
        StringBuilder builder = new StringBuilder(ones);
        boolean addExtra = false;
		int count = 0;

		// Calculate the two's complement by starting from the least significant bit
        for (int i = ones.length() - 1; i > 0; i--) {
			count = count + i;
			// If a '1' is encountered, flip it to '0'
            if (ones.charAt(i) == '1') 
                builder.setCharAt(i, '0');

			// If a '0' is encountered, flip it to '1' and set the flag to add an extra 1
            else {
                builder.setCharAt(i, '1');
                addExtra = true;
                break;
            }
        }

		    // If no extra 1 was added, add it at the beginning (most significant bit)
        if (!addExtra) 
            builder.append("1", 0, 7);

		    // Convert the StringBuilder back to a string, which now contains the two's complement
        twos = builder.toString();
        return twos;
    }
	
	public void performOF() {
		// This method performs the Operand Fetch (OF) stage of the pipeline.
    // Check if the stage is enabled (i.e., there is a valid instruction in IF_OF_Latch)

		if(IF_OF_Latch.isOF_enable()) {
		
			OperationType[] operationType = OperationType.values();
			// Get the binary representation of the instruction from IF_OF_Latch
			String instruction = Integer.toBinaryString(IF_OF_Latch.getInstruction());
			// Pad the binary instruction to ensure it's 32 bits long
			while(instruction.length() != 32) 
				instruction = "0" + instruction;
		
        // Extract the opcode from the instruction to determine the operation type
			String opcode = instruction.substring(0, 5);
			int type_operation = Integer.parseInt(opcode, 2);
			OperationType operation = operationType[type_operation];
        // Create an empty Instruction object
			Instruction inst = new Instruction();
			switch(operation) {
            // Handle ALU instructions (add, sub, mul, etc.)

				case add:
				case sub:
				case mul:
				case or:
				case xor:
				case sll:
				case div:
				case and:
				case slt:
				case srl:
				case sra:
				// Extract source registers and destination register from the instruction

					Operand rs1 = new Operand();
					rs1.setOperandType(OperandType.Register);
					int registerNo = Integer.parseInt(instruction.substring(5, 10), 2);
					rs1.setValue(registerNo);

					Operand rs2 = new Operand();
					rs2.setOperandType(OperandType.Register);
					registerNo = Integer.parseInt(instruction.substring(10, 15), 2);
					rs2.setValue(registerNo);

					Operand rd = new Operand();
					rd.setOperandType(OperandType.Register);
					registerNo = Integer.parseInt(instruction.substring(15, 20), 2);
					rd.setValue(registerNo);

					inst.setOperationType(operationType[type_operation]);
					inst.setSourceOperand1(rs1);
					inst.setSourceOperand2(rs2);
					inst.setDestinationOperand(rd);
					break;

				case end:
					inst.setOperationType(operationType[type_operation]);
					break;
				case jmp:
					Operand op = new Operand();
					String imm = instruction.substring(10, 32);
					int imm_val = Integer.parseInt(imm, 2);
					if (imm.charAt(0) == '1') {
						imm = twosComplement(imm);
						imm_val = Integer.parseInt(imm, 2) * -1;
					}
		
					if (imm_val != 0) {
		
						op.setOperandType(OperandType.Immediate);
						op.setValue(imm_val);
					}
					else {
						registerNo = Integer.parseInt(instruction.substring(5, 10), 2);
						op.setOperandType(OperandType.Register);
						op.setValue(registerNo);
					}

					inst.setOperationType(operationType[type_operation]);
					inst.setDestinationOperand(op);
					break;
			
				case blt:
				case beq:
				case bne:
				case bgt:
					rs1 = new Operand();
					rs1.setOperandType(OperandType.Register);
					registerNo = Integer.parseInt(instruction.substring(5, 10), 2);
					rs1.setValue(registerNo);

					// destination register
					rs2 = new Operand();
					rs2.setOperandType(OperandType.Register);
					registerNo = Integer.parseInt(instruction.substring(10, 15), 2);
					rs2.setValue(registerNo);

					// Immediate value
					rd = new Operand();
					rd.setOperandType(OperandType.Immediate);
					imm = instruction.substring(15, 32);
					imm_val = Integer.parseInt(imm, 2);
		
					if (imm.charAt(0) == '1') {
		
						imm = twosComplement(imm);
						imm_val = Integer.parseInt(imm, 2) * -1;
					}
		
					rd.setValue(imm_val);

					inst.setOperationType(operationType[type_operation]);
					inst.setSourceOperand1(rs1);
					inst.setSourceOperand2(rs2);
					inst.setDestinationOperand(rd);
					break;

				default:
					// Source register 1
					rs1 = new Operand();
					rs1.setOperandType(OperandType.Register);
					registerNo = Integer.parseInt(instruction.substring(5, 10), 2);
					System.out.println(registerNo);
					rs1.setValue(registerNo);

					// Destination register
					rd = new Operand();
					rd.setOperandType(OperandType.Register);
					registerNo = Integer.parseInt(instruction.substring(10, 15), 2);
					System.out.println(registerNo);
					rd.setValue(registerNo);

					// Immediate values
					rs2 = new Operand();
					rs2.setOperandType(OperandType.Immediate);
					imm = instruction.substring(15, 32);
					System.out.println(imm);
					imm_val = Integer.parseInt(imm, 2);
		
					if (imm.charAt(0) == '1') {
		
						imm = twosComplement(imm);
						imm_val = Integer.parseInt(imm, 2) * -1;
					}
					rs2.setValue(imm_val);

					inst.setOperationType(operationType[type_operation]);
					inst.setSourceOperand1(rs1);
					inst.setSourceOperand2(rs2);
					inst.setDestinationOperand(rd);
					break;
			}

			OF_EX_Latch.setInstruction(inst);
			IF_OF_Latch.setOF_enable(false);
			OF_EX_Latch.setEX_enable(true);
		}
	}

}
