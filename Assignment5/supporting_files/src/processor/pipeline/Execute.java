package processor.pipeline;

import processor.Processor;

public class Execute {
	Processor containingProcessor;
	IF_EnableLatchType IF_EnableLatch;
	OF_EX_LatchType OF_EX_Latch;
	EX_MA_LatchType EX_MA_Latch;
	EX_IF_LatchType EX_IF_Latch;
	IF_OF_LatchType IF_OF_Latch;
	
	public Execute(Processor containingProcessor, IF_OF_LatchType iF_OF_Latch, OF_EX_LatchType oF_EX_Latch, EX_MA_LatchType eX_MA_Latch, EX_IF_LatchType eX_IF_Latch, IF_EnableLatchType iF_EnableLatch) {
		this.containingProcessor = containingProcessor;
		this.OF_EX_Latch = oF_EX_Latch;
		this.EX_MA_Latch = eX_MA_Latch;
		this.EX_IF_Latch = eX_IF_Latch;
		this.IF_OF_Latch = iF_OF_Latch;
		this.IF_EnableLatch = iF_EnableLatch;
	}
	public void testFunction(int arr[]){
		for(int i = 0; i < arr.length; i++){
			// System.out.println(arr[i]);
		}
	}
	public void performEX() {
		// Check if there's a pending operation in the EX/MA stage and update the pending flag in OF/EX latch
		if(EX_MA_Latch.getPending == true) OF_EX_Latch.getPending = true;
		else OF_EX_Latch.getPending = false;
		// int arr[] = {1,2,3,4};
		// Check if the EX stage is enabled and there are no pending operations in the EX/MA stage
		if(OF_EX_Latch.isEX_enable() && EX_MA_Latch.getPending == false) {
			int offset = 999;
			// testFunction(arr);
			// Check if the instruction in the OF/EX latch is a No-Operation (NOP)
			if(OF_EX_Latch.isNop == true) {
				EX_MA_Latch.isNop = true;
				EX_MA_Latch.rd = 75000;
			}
			else {
				// Set the EX/MA latch as not NOP and perform ALU operations based on the opcode
				EX_MA_Latch.isNop = false;
				int aluResult = 999;
				int rs1 = OF_EX_Latch.rs1;
				int rs2 = OF_EX_Latch.rs2;
				int rd = OF_EX_Latch.rd;
				int immediate = OF_EX_Latch.imm;
				int opcodeVal = Integer.parseInt(OF_EX_Latch.opcode, 2);

				// Perform different ALU operations based on the opcode value
				switch(opcodeVal) {
					case 0: 
						aluResult = rs1 + rs2;
						break;
					case 1: 
						aluResult = rs1 + immediate;
						break;
					case 3: 
						aluResult = rs1 - immediate;
						break;
					case 4: 
						aluResult = rs1 * rs2;
						break;
					case 2: 
						aluResult = rs1 - rs2;
						break;
					case 5: 
						aluResult = rs1 * immediate;
						break;
					case 6: 
						aluResult = rs1 / rs2;
						containingProcessor.getRegisterFile().setValue(31, rs1 % rs2);
						break;
					case 7: 
						aluResult = rs1 / immediate;
						containingProcessor.getRegisterFile().setValue(31, rs1 % immediate);
						break;
					case 8: 
						aluResult = rs1 & rs2;
						break;
					case 9: 
						aluResult = rs1 & immediate;
						break;
					case 11: 
						aluResult = rs1 | immediate;
						break;
					case 10: 
						aluResult = rs1 | rs2;
						break;
					case 12: 
						aluResult = rs1 ^ rs2;
						break;
					case 13: 
						aluResult = rs1 ^ immediate;
						break;
					case 15: 
						if(rs1 < immediate) aluResult = 1;
						else aluResult = 0;
					case 14: 
						if(rs1 < rs2) aluResult = 1;
						else aluResult = 0;
						break;
					case 16: 
						aluResult = rs1 << rs2;
						String str = Integer.toBinaryString(rs1);
						while(str.length() != 5) {
							str = "0" + str;
						}
						containingProcessor.getRegisterFile().setValue(31, Integer.parseInt(str.substring(5-rs2, 5),2));
						break;
					case 17 : 
						str = Integer.toBinaryString(immediate);
						aluResult = rs1 << immediate;
						while(str.length() != 5) {
							str = "0" + str;
						}
						containingProcessor.getRegisterFile().setValue(31, Integer.parseInt(str.substring(5-immediate, 5),2));
						break;
					
					case 18: 
						str = Integer.toBinaryString(rs1);
						aluResult = rs1 >>> rs2;

						while(str.length() != 5) {
							str = "0" + str;
						}
						containingProcessor.getRegisterFile().setValue(31, Integer.parseInt(str.substring(0, rs2),2));
						break;
					

					case 20 : 
						str = Integer.toBinaryString(rs1);
						aluResult = rs1 >> rs2;

						while(str.length() != 5) {
							str = "0" + str;
						}
						containingProcessor.getRegisterFile().setValue(31, Integer.parseInt(str.substring(0, rs2),2));
						break;
					
					case 21 : 
						aluResult = rs1 >> immediate;
						str = Integer.toBinaryString(immediate);
						
						while(str.length() != 5) {
							str = "0" + str;
						}
						containingProcessor.getRegisterFile().setValue(31, Integer.parseInt(str.substring(0, immediate),2));
						break;
					
					case 19 : 
						aluResult = rs1 >>> immediate;
						str = Integer.toBinaryString(immediate);
						while(str.length() != 5) {
							str = "0" + str;
						}
						containingProcessor.getRegisterFile().setValue(31, Integer.parseInt(str.substring(0, immediate),2));
						break;
					case 22  : 
						aluResult = rs1 + immediate;
						break;
					case 23 : 
						aluResult = containingProcessor.getRegisterFile().getValue(rd) + immediate;
						break;
					case 24 : 
						offset = containingProcessor.getRegisterFile().getValue(rd) + immediate;
						break;
					case 25 : 
						if(rs1 == containingProcessor.getRegisterFile().getValue(rd)) offset = immediate;
						break;
					case 26 : 
						if(rs1 != containingProcessor.getRegisterFile().getValue(rd)) offset = immediate;
						break;
					case 27 : 
						if(rs1 < containingProcessor.getRegisterFile().getValue(rd)) offset = immediate;
						break;
					case 28 : 
						if(rs1 > containingProcessor.getRegisterFile().getValue(rd)) offset = immediate;
						break;
					default : break;
				}
				// testFunction(arr);
				// If there's a branch or jump instruction, set the offset and flags accordingly
				if(offset != 999) {
					EX_IF_Latch.offset = offset - 1;
					EX_IF_Latch.isBranchTaken = true;
					IF_EnableLatch.setIF_enable(true);
					OF_EX_Latch.setEX_enable(false);
					IF_OF_Latch.setOF_enable(false);
					OF_EX_Latch.rs1 = 0; OF_EX_Latch.rs2 = 0;
					OF_EX_Latch.rd = 0; OF_EX_Latch.imm = 0;
				}
				// testFunction(arr);
				// Update the EX/MA latch with the computed values
				EX_MA_Latch.rd = rd;
				EX_MA_Latch.rs1 = rs1;
				EX_MA_Latch.rs2 = rs2;
				EX_MA_Latch.imm = immediate;
				EX_MA_Latch.aluResult = aluResult;
				EX_MA_Latch.opcode = OF_EX_Latch.opcode;
				EX_MA_Latch.insPC = OF_EX_Latch.insPC;
				// Check if the opcode is for a specific instruction and disable the EX stage
				if(OF_EX_Latch.opcode.equals("11101") == true ) OF_EX_Latch.setEX_enable(false);
			}
			OF_EX_Latch.setEX_enable(false);// Disable the OF/EX latch
			EX_MA_Latch.setMA_enable(true);// Enable the EX/MA latch
		}
	}
}
