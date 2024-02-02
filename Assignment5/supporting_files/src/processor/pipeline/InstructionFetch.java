package processor.pipeline;

import configuration.Configuration;
import generic.*;
import processor.Clock;
import processor.Processor;

public class InstructionFetch implements Element{
	
	Processor containingProcessor;
	IF_EnableLatchType IF_EnableLatch;
	IF_OF_LatchType IF_OF_Latch;
	EX_IF_LatchType EX_IF_Latch;
	int currentPC;
	
	public InstructionFetch(Processor containingProcessor, IF_EnableLatchType IF_EnableLatch, IF_OF_LatchType IF_OF_Latch, EX_IF_LatchType EX_IF_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.IF_EnableLatch = IF_EnableLatch;
		this.IF_OF_Latch = IF_OF_Latch;
		this.EX_IF_Latch = EX_IF_Latch;
	}

	@Override
	public void handleEvent(Event e) {
		if(IF_OF_Latch.getPending == true) {
			e.setEventTime(Clock.getCurrentTime() + 1);
			Simulator.getEventQueue().addEvent(e);
		} else {
			MemoryResponseEvent event = (MemoryResponseEvent) e ; 
			if(EX_IF_Latch.isBranchTaken == false)	{
				IF_OF_Latch.setInstruction(event.getValue());
			}
			else IF_OF_Latch.setInstruction(0);
			IF_OF_Latch.insPC = this.currentPC;
			containingProcessor.getRegisterFile().setProgramCounter(this.currentPC + 1);
			System.out.println("event value " + event.getValue());
			IF_OF_Latch.setOF_enable(true);
			IF_EnableLatch.getPending = false;
		}
	}
	
	public void performIF(){
		// Check if the IF stage is enabled in the pipeline
		if(IF_EnableLatch.isIF_enable()) {
			// Check if there's a pending operation, and if so, return without further processing
			if(IF_EnableLatch.getPending) {
				return;
			}
			// Retrieve the current program counter (PC) from the Register File
			currentPC = containingProcessor.getRegisterFile().getProgramCounter();

			// Check if a branch was taken in the previous stage (EX/IF), adjust the PC accordingly
			if(EX_IF_Latch.isBranchTaken == true) {
				currentPC = currentPC + EX_IF_Latch.offset - 1;
				EX_IF_Latch.isBranchTaken = false; // Reset the branch taken flag
			}
			
			// Increment the instruction count to keep track of executed instructions
			Simulator.instruction_count++;

			// Schedule a memory read event to fetch the instruction from main memory
			Simulator.getEventQueue().addEvent(
				new MemoryReadEvent(
					Clock.getCurrentTime() + Configuration.mainMemoryLatency, 
					this, 
					containingProcessor.getMainMemory(), 
					currentPC)
			);
			// Set the pending flag in the IF_EnableLatch to prevent further instruction fetches
			IF_EnableLatch.getPending = true;
		}
	}

}
