package generic;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import processor.Clock;
import processor.Processor;

public class Simulator {
		
	static Processor processor;
	static boolean simulationComplete;
	
	public static void setupSimulation(String assemblyProgramFile, Processor p) {

		Simulator.processor = p;
		try {

			loadProgram(assemblyProgramFile);
		} catch (IOException e) {

			e.printStackTrace();
		}
		
		simulationComplete = false;
	}
	
	static void loadProgram(String assemblyProgramFile) throws IOException {
		/*
		 * TODO
		 * 1. load the program into memory according to the program layout described
		 *    in the ISA specification
		 * 2. set PC to the address of the first instruction in the main
		 * 3. set the following registers:
		 *     x0 = 0
		 *     x1 = 65535
		 *     x2 = 65535
		 */
		InputStream inp = null;
		try {
			inp = new FileInputStream(assemblyProgramFile);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		DataInputStream inp_data = new DataInputStream(inp);

		int address = -1;
		while(inp_data.available() > 0) {

			int next = inp_data.readInt();
			if(address == -1){
				processor.getRegisterFile().setProgramCounter(next);
			}
			else{
				processor.getMainMemory().setWord(address, next);
			}
			address++;
		}		
        processor.getRegisterFile().setValue(0, 0);
        processor.getRegisterFile().setValue(1, 65535);
        processor.getRegisterFile().setValue(2, 65535);
	}

	public static void simulate() {

		while(simulationComplete == false) {

			processor.getIFUnit().performIF();
			Clock.incrementClock();
			processor.getOFUnit().performOF();
			Clock.incrementClock();
			processor.getEXUnit().performEX();
			Clock.incrementClock();
			processor.getMAUnit().performMA();
			Clock.incrementClock();
			processor.getRWUnit().performRW();
			Clock.incrementClock();

			Statistics.setNumberOfInstructions(Statistics.getNumberOfInstructions() + 1);
			Statistics.setNumberOfCycles(Statistics.getNumberOfCycles() + 1);
		}
	}
	
	public static void setSimulationComplete(boolean value) {
		simulationComplete = value;
	}
}
