package processor.pipeline;

import processor.Processor;

public class InstructionFetch {
	
	Processor containingProcessor;
	IF_EnableLatchType IF_EnableLatch;
	IF_OF_LatchType IF_OF_Latch;
	EX_IF_LatchType EX_IF_Latch;
	
	public InstructionFetch(Processor containingProcessor, IF_EnableLatchType IF_EnableLatch, IF_OF_LatchType IF_OF_Latch, EX_IF_LatchType EX_IF_Latch) {
		this.containingProcessor = containingProcessor;
		this.IF_EnableLatch = IF_EnableLatch;
		this.IF_OF_Latch = IF_OF_Latch;
		this.EX_IF_Latch = EX_IF_Latch;
	}
	
	public void performIF() {
		if(IF_EnableLatch.isIF_enable()) {
			if(EX_IF_Latch.getIS_enable()) {
				int newPC = EX_IF_Latch.getPC();
				containingProcessor.getRegisterFile().setProgramCounter(newPC);
				EX_IF_Latch.setIS_enable(false);
			}
			int currPC = containingProcessor.getRegisterFile().getProgramCounter(), newInst = containingProcessor.getMainMemory().getWord(currPC);
			IF_OF_Latch.setOF_enable(true);
			IF_OF_Latch.setInstruction(newInst);
			containingProcessor.getRegisterFile().setProgramCounter(currPC + 1);
		}
	}

}
