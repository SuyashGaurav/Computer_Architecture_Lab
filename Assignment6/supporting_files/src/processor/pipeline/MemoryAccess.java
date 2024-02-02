package processor.pipeline;

import configuration.Configuration;
import generic.*;
import generic.Event.EventType;
import processor.Clock;
import processor.Processor;
import processor.memorysystem.Cache;

public class MemoryAccess implements Element{
	Processor containingProcessor;
	EX_MA_LatchType EX_MA_Latch;
	MA_RW_LatchType MA_RW_Latch;
	Cache cache;

	public MemoryAccess(Processor containingProcessor, EX_MA_LatchType eX_MA_Latch, MA_RW_LatchType mA_RW_Latch, Cache cach){
		this.containingProcessor = containingProcessor;
		this.EX_MA_Latch = eX_MA_Latch;
		this.MA_RW_Latch = mA_RW_Latch;
		this.cache = cach;
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
		if(EX_MA_Latch.isMA_enable() && EX_MA_Latch.getPending == false) {
			int arr[] = {1,2,3,4};
			if(EX_MA_Latch.isNop) {
				MA_RW_Latch.isNop = true;
				MA_RW_Latch.rd = 75000;
			}
			else {
				MA_RW_Latch.isNop = false;
				int aluResult = EX_MA_Latch.aluResult;
				int rs1 = EX_MA_Latch.rs1;
				int rs2 = EX_MA_Latch.rs2;
				int imm = EX_MA_Latch.imm;
				int rd = EX_MA_Latch.rd;
				String opcode = EX_MA_Latch.opcode;
				MA_RW_Latch.insPC = EX_MA_Latch.insPC;

				System.out.println("MA\t" + EX_MA_Latch.insPC + "\trs1:" + rs1 + "\trs2:" + rs2 + "\trd:" + rd + "\timm:" + imm + "\talu:" + aluResult);
				testFunction(arr);
				MA_RW_Latch.aluResult = aluResult;
				MA_RW_Latch.rd = rd;
				MA_RW_Latch.imm = imm;
				MA_RW_Latch.rs1 = rs1;
				MA_RW_Latch.rs2 = rs2;
				MA_RW_Latch.opcode = opcode;

				if(opcode.equals("10111")) {  //store
					Simulator.storeresp = Clock.getCurrentTime();
					EX_MA_Latch.getPending = true;
					Simulator.getEventQueue().addEvent(
						new MemoryWriteEvent(Clock.getCurrentTime() + Configuration.mainMemoryLatency, 
						this, this.cache, aluResult, rs1));
					EX_MA_Latch.setMA_enable(false);
					return;
				} else if(opcode.equals("10110")) { //load
					EX_MA_Latch.getPending = true;
					MA_RW_Latch.isLoad = true;
					Simulator.getEventQueue().addEvent(new MemoryReadEvent(Clock.getCurrentTime() + 
					Configuration.mainMemoryLatency, this, this.cache, aluResult));
					EX_MA_Latch.setMA_enable(false);
					return;
				}
			}
			EX_MA_Latch.setMA_enable(false);
			if(EX_MA_Latch.opcode.equals("11101") == true ) EX_MA_Latch.setMA_enable(false);
			MA_RW_Latch.setRW_enable(true);
		}
		//TODO
	}
	public void testFunction(int arr[]){
		for(int i = 0; i < arr.length; i++){
			// System.out.println(arr[i]);
		}
	}
}
