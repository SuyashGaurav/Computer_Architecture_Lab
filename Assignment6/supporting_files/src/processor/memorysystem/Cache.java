package processor.memorysystem;

import generic.*;
import processor.*;
import configuration.Configuration;

public class Cache implements Element {
	private int cacheSize;
    private int missAddr;
	private Processor containingProcessor;
    private int[] index;
    private boolean isPresent = true;
    private CacheLine[] cach;
    public int temp;
	public int latency;
	
	public Cache(Processor containingProcessor, int latency, int cacheSize) {
		this.containingProcessor = containingProcessor;
        this.latency = latency;
        this.cacheSize = cacheSize;
        this.temp = (int)(Math.log(this.cacheSize/8)/Math.log(2));
        this.cach = new CacheLine[cacheSize/8];
		for(int i = 0; i < cacheSize/8; i++) 
			this.cach[i] = new CacheLine();
    }

    public boolean checkPresence() {
        return this.isPresent;
    }
    
    public void setProcessor(Processor processor) {
        this.containingProcessor = processor;
    }
    public CacheLine[] getCaches() {
        return this.cach;
    }

    public int[] getIndexes() {
        return this.index;
    }

    public Processor getProcessor() {
        return this.containingProcessor;
    }

    public String toString() {
        return Integer.toString(this.latency) + " : latency";
    }
    
    public void handleCacheMiss(int addr) {
		Simulator.getEventQueue().addEvent(
            new MemoryReadEvent(
                Clock.getCurrentTime() + Configuration.mainMemoryLatency,
                this,
                containingProcessor.getMainMemory(),
                addr
            )
        );    
	}

    
    public int cacheRead(int address){
        int currInd;
        StringBuilder addr = new StringBuilder(Integer.toBinaryString(address));
        StringBuilder ind = new StringBuilder();
        for(int i = 0; i < 32-addr.length(); i++) addr.insert(0, "0");
        for(int i = 0; i < temp; i++) ind.append("1");
        currInd = temp == 0 ? 0 : address & Integer.parseInt(ind.toString(), 2);

        int tag = Integer.parseInt(addr.substring(0, addr.length()-temp),2); //Extracts the tag part from the memory address.

        if(tag == cach[currInd].tagArray[0]){
            isPresent = true;
            cach[currInd].LRU = 1;
            return cach[currInd].dataArray[0];
        } else if(tag == cach[currInd].tagArray[1]){
            isPresent = true;
            cach[currInd].LRU = 0;
            return cach[currInd].dataArray[1];
        } else {
            isPresent = false;
            return -1;
        }
    }

    public void cacheWrite(int address, int value){
        int currInd;
        StringBuilder addr = new StringBuilder(Integer.toBinaryString(address));
        StringBuilder ind = new StringBuilder();
        for(int i = 0; i < 32-addr.length(); i++) addr.insert(0, "0");
        for(int i = 0; i < temp; i++) ind.append("1");
        currInd = temp == 0 ? 0 : address & Integer.parseInt(ind.toString(), 2);
        int tag = Integer.parseInt(addr.substring(0, addr.length()-temp),2);
        cach[currInd].setValue(tag, value);
    }


    @Override
	public void handleEvent(Event event) {
        if(Event.EventType.MemoryRead == event.getEventType()){
            MemoryReadEvent readEvent = (MemoryReadEvent) event;
            int data = cacheRead(readEvent.getAddressToReadFrom());
            if(!isPresent){
                this.missAddr = readEvent.getAddressToReadFrom();
                readEvent.setEventTime(Clock.getCurrentTime() + Configuration.mainMemoryLatency+1);
                handleCacheMiss(readEvent.getAddressToReadFrom());
                Simulator.getEventQueue().addEvent(readEvent);
            } else{
                Simulator.getEventQueue().addEvent(
                    new MemoryResponseEvent(
                        Clock.getCurrentTime() + this.latency, this, 
                        readEvent.getRequestingElement(), 
                        data
                    )
                );
            }
        } else if(event.getEventType() == Event.EventType.MemoryWrite){
            MemoryWriteEvent readEvent = (MemoryWriteEvent) event;
            containingProcessor.getMainMemory().setWord(readEvent.getAddressToWriteTo(), readEvent.getValue());
            cacheWrite(readEvent.getAddressToWriteTo(), readEvent.getValue());
            Simulator.getEventQueue().addEvent(
				new ExecutionCompleteEvent(
					Clock.getCurrentTime()+Configuration.mainMemoryLatency, 
					containingProcessor.getMainMemory(), 
					readEvent.getRequestingElement()
                )
			);
		} else if(Event.EventType.MemoryResponse == event.getEventType()){
            MemoryResponseEvent readEvent = (MemoryResponseEvent) event;
            cacheWrite(this.missAddr, readEvent.getValue());
        } 
	}
}