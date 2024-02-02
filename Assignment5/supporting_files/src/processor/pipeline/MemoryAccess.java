package processor.pipeline;

import configuration.Configuration;
import generic.*;
import generic.Event.EventType;
import processor.Clock;
import processor.Processor;

public class MemoryAccess implements Element{
	Processor containingProcessor;
	EX_MA_LatchType EX_MA_Latch;
	MA_RW_LatchType MA_RW_Latch;
	
	public MemoryAccess(Processor containingProcessor, EX_MA_LatchType eX_MA_Latch, MA_RW_LatchType mA_RW_Latch) {
		this.containingProcessor = containingProcessor;
		this.EX_MA_Latch = eX_MA_Latch;
		this.MA_RW_Latch = mA_RW_Latch;
	}
	@Override
	public void handleEvent(Event e) {
		if(e.getEventType() == EventType.MemoryResponse) {
			MemoryResponseEvent event = (MemoryResponseEvent) e ; 
			MA_RW_Latch.aluResult = event.getValue();
			MA_RW_Latch.insPC = EX_MA_Latch.insPC;
			MA_RW_Latch.setRW_enable(true);
			EX_MA_Latch.getPending = false;
		}
		else {
			EX_MA_Latch.getPending = false;
		}
	}
	
	public void performMA() {
		// Check if the MA stage is enabled in the pipeline and no pending operations
		if(EX_MA_Latch.isMA_enable() && EX_MA_Latch.getPending == false) {
			if(EX_MA_Latch.isNop == true) {
				// Set the MA/RW latch as NOP and assign a dummy destination register (rd = 999)
				MA_RW_Latch.isNop = true;
				MA_RW_Latch.rd = 999;
			}
			else {
				// Set the MA/RW latch as not NOP and retrieve various information from the EX/MA latch
				MA_RW_Latch.isNop = false;
				int aluResult = EX_MA_Latch.aluResult;
				String opcode = EX_MA_Latch.opcode;
				int rs1 = EX_MA_Latch.rs1;
				int rs2 = EX_MA_Latch.rs2;
				int rd = EX_MA_Latch.rd;
				int imm = EX_MA_Latch.imm;

				// Copy information from EX/MA latch to MA/RW latch
				MA_RW_Latch.insPC = EX_MA_Latch.insPC;
				MA_RW_Latch.aluResult = aluResult;
				MA_RW_Latch.rs1 = rs1;
				MA_RW_Latch.rs2 = rs2;
				MA_RW_Latch.rd = rd;
				MA_RW_Latch.imm = imm;
				MA_RW_Latch.opcode = opcode;

				// Check if the opcode represents a load instruction (opcode "10110")
				if(opcode.equals("10110") == true) {
					MA_RW_Latch.isLoad = true;
					// Set the EX/MA stage to pending and schedule a memory read event
					EX_MA_Latch.getPending = true;
					Simulator.getEventQueue().addEvent(
						new MemoryReadEvent(Clock.getCurrentTime() + Configuration.mainMemoryLatency, this, containingProcessor.getMainMemory(), aluResult)
					);
					// Disable the MA stage and return
					EX_MA_Latch.setMA_enable(false);
					return;
				}

				// Check if the opcode represents a store instruction (opcode "10111")
				if(opcode.equals("10111") == true) { 
					// Set the EX/MA stage to pending and schedule a memory write event
					EX_MA_Latch.getPending = true;
					Simulator.storeresp = Clock.getCurrentTime();
					Simulator.getEventQueue().addEvent(new MemoryWriteEvent(Clock.getCurrentTime() + Configuration.mainMemoryLatency, this, containingProcessor.getMainMemory(), aluResult, rs1));
					 // Disable the MA stage and return
					EX_MA_Latch.setMA_enable(false);
					return;
				}
			}
			EX_MA_Latch.setMA_enable(false);
			// Disable the MA stage if the opcode corresponds to a specific instruction (opcode "11101")
			if(EX_MA_Latch.opcode.equals("11101")) {
				EX_MA_Latch.setMA_enable(false);
			}
			// Enable the RW stage and proceed
			MA_RW_Latch.setRW_enable(true);
		}
	}

}
