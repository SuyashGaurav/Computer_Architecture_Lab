package processor.pipeline;

import generic.Simulator;
import processor.Processor;
import generic.Instruction;
import generic.Instruction.OperationType;

public class RegisterWrite {
	Processor containingProcessor;
	MA_RW_LatchType MA_RW_Latch;
	IF_EnableLatchType IF_EnableLatch;
	
	public RegisterWrite(Processor containingProcessor, MA_RW_LatchType mA_RW_Latch, IF_EnableLatchType iF_EnableLatch) {
	
		this.containingProcessor = containingProcessor;
		this.MA_RW_Latch = mA_RW_Latch;
		this.IF_EnableLatch = iF_EnableLatch;
	}
	
	public void performRW() {
		// This method executes the write-back stage of the pipeline.

		// Check if the stage is enabled (i.e., there is valid data in MA_RW_Latch)

		if(MA_RW_Latch.isRW_enable()) {
			// Get the instruction and its results from MA_RW_Latch
			Instruction instruction = MA_RW_Latch.getInstruction();
			int result_ALU = MA_RW_Latch.getALU_result();
			OperationType op_type = instruction.getOperationType();
				
			switch(op_type) {
	            // For certain instruction types, no write-back is needed, so we do nothing here.

				case store:
				case bne:
				case jmp:
				case beq:
				case blt:
				case bgt:
					break;
		        
					// For load instructions, write the result to the destination register.

				case load:
					int load_result = MA_RW_Latch.getLoad_result();
					int rd = instruction.getDestinationOperand().getValue();
					containingProcessor.getRegisterFile().setValue(rd, load_result);
					break;

                // For end instruction, set the simulation as complete.
				case end:
					Simulator.setSimulationComplete(true);
					break;
                // For other instructions, write the ALU result to the destination register.
				default:
					rd = instruction.getDestinationOperand().getValue();
					containingProcessor.getRegisterFile().setValue(rd, result_ALU);
					break;
			}
            // Disable the write-back stage and enable the instruction fetch stage.
			MA_RW_Latch.setRW_enable(false);
			IF_EnableLatch.setIF_enable(true);
		}
	}

}
