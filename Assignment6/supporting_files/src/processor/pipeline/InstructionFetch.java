package processor.pipeline;

import configuration.Configuration;
import generic.*;
import processor.Clock;
import processor.Processor;
import processor.memorysystem.*;	

public class InstructionFetch implements Element{
	
	Processor containingProcessor;
	IF_EnableLatchType IF_EnableLatch;
	IF_OF_LatchType IF_OF_Latch;
	EX_IF_LatchType EX_IF_Latch;
	int currentPC;
	Cache cache;

	public InstructionFetch(Processor containingProcessor, IF_EnableLatchType iF_EnableLatch, IF_OF_LatchType iF_OF_Latch, EX_IF_LatchType eX_IF_Latch, Cache cach)
	{
		this.containingProcessor = containingProcessor;
		this.IF_EnableLatch = iF_EnableLatch;
		this.IF_OF_Latch = iF_OF_Latch;
		this.EX_IF_Latch = eX_IF_Latch;
		this.cache = cach;
	}
	
	public void performIF()
	{
		if(IF_EnableLatch.isIF_enable()) {
			if(IF_EnableLatch.getPending) {
				return;
			}

			currentPC = containingProcessor.getRegisterFile().getProgramCounter();
			if(EX_IF_Latch.isBranchTaken == true) {
				currentPC = currentPC + EX_IF_Latch.offset - 1;
				EX_IF_Latch.isBranchTaken = false;
			}
			
			Simulator.instruction_count++;
			Simulator.getEventQueue().addEvent(
				new MemoryReadEvent(
					Clock.getCurrentTime()+this.cache.latency, 
					this, 
					this.cache, 
					currentPC)
			);

			IF_EnableLatch.getPending = true;
		}
	}

	@Override
	public void handleEvent(Event e) {
		if(IF_OF_Latch.getPending == true) {
			e.setEventTime(Clock.getCurrentTime() + 1);
			Simulator.getEventQueue().addEvent(e);
		}
		else {
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

}
