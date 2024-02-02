package processor.pipeline;

import processor.Processor;
import generic.Instruction;
import generic.Instruction.OperationType;

public class MemoryAccess {
	Processor containingProcessor;
	EX_MA_LatchType EX_MA_Latch;
	MA_RW_LatchType MA_RW_Latch;
	IF_EnableLatchType IF_EnableLatch;
	public MemoryAccess(Processor containingProcessor, EX_MA_LatchType EX_MA_Latch, MA_RW_LatchType MA_RW_Latch, IF_EnableLatchType IF_EnableLatch) {
		this.containingProcessor = containingProcessor;
		this.EX_MA_Latch = EX_MA_Latch;
		this.MA_RW_Latch = MA_RW_Latch;
		this.IF_EnableLatch = IF_EnableLatch;
	}
	public void performMA() {
		if (EX_MA_Latch.getIsNOP()) {
			EX_MA_Latch.setIsNOP(false);
			MA_RW_Latch.setIsNOP(true);
			MA_RW_Latch.setInstruction(null);
		} else if(EX_MA_Latch.isMA_enable()) {
			Instruction inst = EX_MA_Latch.getInstruction();
			int aluResult = EX_MA_Latch.getALU_result();
			OperationType op_type = inst.getOperationType();
			MA_RW_Latch.setALU_result(aluResult);
			if (op_type == OperationType.load) {
				int load_result = containingProcessor.getMainMemory().getWord(aluResult);
				MA_RW_Latch.setLoad_result(load_result);
			} else if (op_type == OperationType.store) {
				int store_result = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());
				containingProcessor.getMainMemory().setWord(aluResult, store_result);
			}
			if(op_type.ordinal() == 29) IF_EnableLatch.setIF_enable(false);
			MA_RW_Latch.setInstruction(inst);
			MA_RW_Latch.setRW_enable(true);
		}
	}

}
