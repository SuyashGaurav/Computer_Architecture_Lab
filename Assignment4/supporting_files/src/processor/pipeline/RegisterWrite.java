package processor.pipeline;

import generic.Simulator;
import generic.Statistics;
import processor.Processor;
import generic.Instruction;
import generic.Instruction.OperationType;

public class RegisterWrite {
	Processor containingProcessor;
	MA_RW_LatchType MA_RW_Latch;
	IF_EnableLatchType IF_EnableLatch;
	public RegisterWrite(Processor containingProcessor, MA_RW_LatchType MA_RW_Latch, IF_EnableLatchType IF_EnableLatch) {
		this.containingProcessor = containingProcessor;
		this.MA_RW_Latch = MA_RW_Latch;
		this.IF_EnableLatch = IF_EnableLatch;
	}
	public void performRW() {
		if (MA_RW_Latch.getIsNOP()) {
			MA_RW_Latch.setIsNOP(false);
		} else if(MA_RW_Latch.getRW_enable()) {
			int numberOfRWInstructions = Statistics.getNumberOfRegisterWriteInstructions();
			Statistics.setnumberOfRegisterWriteInstructions(numberOfRWInstructions+1);
			int aluResult = MA_RW_Latch.getALU_result();
			Instruction instruction = MA_RW_Latch.getInstruction();
			OperationType opType = instruction.getOperationType();		
			switch(opType) {
				case store:
				case bne:
				case jmp:
				case beq:
				case blt:
				case bgt:
					break;
				case load:
					int loadResult = MA_RW_Latch.getLoad_result(), rd = instruction.getDestinationOperand().getValue();
					containingProcessor.getRegisterFile().setValue(rd, loadResult);
					break;
				case end:
					Simulator.setSimulationComplete(true);
					break;
				default:
					rd = instruction.getDestinationOperand().getValue();
					containingProcessor.getRegisterFile().setValue(rd, aluResult);
					break;
			}
			if (opType.ordinal() != 29) {
				IF_EnableLatch.setIF_enable(true);
			}
		}
	}
}
