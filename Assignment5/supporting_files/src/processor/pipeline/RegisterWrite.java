package processor.pipeline;

import generic.Simulator;
import processor.Processor;

public class RegisterWrite {
	Processor containingProcessor;
	MA_RW_LatchType MA_RW_Latch;
	IF_EnableLatchType IF_EnableLatch;
	
	public RegisterWrite(Processor containingProcessor, MA_RW_LatchType MA_RW_Latch, IF_EnableLatchType IF_EnableLatch)
	{
		this.containingProcessor = containingProcessor;
		this.MA_RW_Latch = MA_RW_Latch;
		this.IF_EnableLatch = IF_EnableLatch;
	}
	
	public void performRW() {
		 // Check if the RW stage is enabled in the pipeline
		if(MA_RW_Latch.isRW_enable()) {
			if(MA_RW_Latch.isNop == false) {
				int aluResult = MA_RW_Latch.aluResult; // Get the ALU result from the latch
				int rd = MA_RW_Latch.rd; // Get the destination register from the latch
				String opcode = MA_RW_Latch.opcode;  // Get the opcode from the latch
				int opcodeVal = Integer.parseInt(opcode, 2); // Parse the binary opcode as an integer

				// Check if the instruction is a load operation
				if(MA_RW_Latch.isLoad) {
					// Set the value of the destination register with the ALU result
					containingProcessor.getRegisterFile().setValue(rd, aluResult);
					MA_RW_Latch.isLoad = false;
				}
				else {
					String[] branchinst={"11000","11001","11010","11011","11100","11101"};
					Boolean isbranch=false;
					for (int i=0;i<6;i++){
						if (branchinst[i]==opcode){
							isbranch=true;
						}
					}
					if(opcodeVal<24){
						 // Set the value of the destination register with the ALU result
						containingProcessor.getRegisterFile().setValue(rd, aluResult);
					}
				}
				MA_RW_Latch.setRW_enable(false);
				if(opcodeVal==29) {
					// Set the simulation as complete, and disable the IF stage
					Simulator.setSimulationComplete(true);
					IF_EnableLatch.setIF_enable(false);
				}
			}
		}
	}
}
