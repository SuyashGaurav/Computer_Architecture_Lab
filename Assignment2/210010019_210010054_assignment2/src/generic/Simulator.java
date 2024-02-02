package generic;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.regex.Pattern;

import generic.Operand.OperandType;


public class Simulator {
		
	static FileInputStream inputcodeStream = null;
	static HashMap<Instruction.OperationType, String> map = new HashMap<Instruction.OperationType, String>(){{
		put(Instruction.OperationType.add, "00000");
		put(Instruction.OperationType.addi, "00001");
		put(Instruction.OperationType.sub, "00010");
		put(Instruction.OperationType.subi, "00011");
		put(Instruction.OperationType.mul, "00100");
		put(Instruction.OperationType.muli, "00101");
		put(Instruction.OperationType.div, "00110");
		put(Instruction.OperationType.divi, "00111");
		put(Instruction.OperationType.and, "01000");
		put(Instruction.OperationType.andi, "01001");
		put(Instruction.OperationType.or, "01010");
		put(Instruction.OperationType.ori, "01011");
		put(Instruction.OperationType.xor, "01100");
		put(Instruction.OperationType.xori, "01101");
		put(Instruction.OperationType.slt, "01110");
		put(Instruction.OperationType.slti, "01111");
		put(Instruction.OperationType.sll, "10000");
		put(Instruction.OperationType.slli, "10001");
		put(Instruction.OperationType.srl, "10010");
		put(Instruction.OperationType.srli, "10011");
		put(Instruction.OperationType.sra, "10100");
		put(Instruction.OperationType.srai, "10101");
		put(Instruction.OperationType.load, "10110");
		put(Instruction.OperationType.store, "10111");
		put(Instruction.OperationType.end, "11101");
		put(Instruction.OperationType.jmp, "11000");
		put(Instruction.OperationType.beq, "11001");
		put(Instruction.OperationType.bne, "11010");
		put(Instruction.OperationType.blt, "11011");
		put(Instruction.OperationType.bgt, "11100");
	}};
	public static void setupSimulation(String assemblyProgramFile)
	{	
		int firstCodeAddress = ParsedProgram.parseDataSection(assemblyProgramFile);
		ParsedProgram.parseCodeSection(assemblyProgramFile, firstCodeAddress);
		ParsedProgram.printState();
	}
	static String toBinaryOfSpecificPrecisioString(int num, int digits){
		if(num < 0) return String.format("%"+digits+"s", Integer.toBinaryString(num)).replace(' ', '0').substring(32-digits);
		return String.format("%"+digits+"s", Integer.toBinaryString(num)).replace(' ', '0');
	}
	static String getString(Operand inst, int digits){
		if(inst == null) return toBinaryOfSpecificPrecisioString(0, digits);
		if(inst.getOperandType() == OperandType.Label) return toBinaryOfSpecificPrecisioString(ParsedProgram.symtab.get(inst.getLabelValue()), digits);
		return toBinaryOfSpecificPrecisioString(inst.getValue(), digits);
	}
	public static void assemble(String objectProgramFile) 
	{
		//TODO your assembler code
		//1. open the objectProgramFile in binary mode
		//2. write the firstCodeAddress to the file
		//3. write the data to the file
		//4. assemble one instruction at a time, and write to the file
		//5. close the file
		OutputStream file;
		try {
			file = new FileOutputStream(objectProgramFile);
			byte[] addressCode = ByteBuffer.allocate(4).putInt(ParsedProgram.firstCodeAddress).array();
			file.write(addressCode);
			for(int value: ParsedProgram.data){
				byte[] values = ByteBuffer.allocate(4).putInt(value).array();
				file.write(values);
			}
			for(Instruction inst: ParsedProgram.code){
				String ans = "";
				ans += map.get(inst.getOperationType());
				switch(inst.getOperationType()){
					//R3I type
					case add : 
					case sub : 
					case mul : 
					case div : 
					case and : 
					case or : 
					case xor : 
					case slt : 
					case sll : 
					case srl : 
					case sra :	{
									ans += getString(inst.getSourceOperand1(), 5);
									ans += getString(inst.getSourceOperand2(), 5);
									ans += getString(inst.getDestinationOperand(), 5);
									ans += toBinaryOfSpecificPrecisioString(0, 12);
									break;
								} 
					
					//R2I type
					case addi :
					case subi :
					case muli :
					case divi : 
					case andi : 
					case ori : 
					case xori : 
					case slti : 
					case slli : 
					case srli : 
					case srai :
					case load :
					case store :	{
										ans += getString(inst.getSourceOperand1(), 5);
										ans += getString(inst.getDestinationOperand(), 5);
										ans += getString(inst.getSourceOperand2(), 17);
										break;
									} 
					
					case beq : 
					case bne : 
					case blt : 
					case bgt : 	{
									ans += getString(inst.getSourceOperand1(), 5);
									ans += getString(inst.getSourceOperand2(), 5);
									int val = ParsedProgram.symtab.get(inst.getDestinationOperand().getLabelValue())-inst.getProgramCounter();
									ans += toBinaryOfSpecificPrecisioString(val, 17);
									break;
								}
					
					//RI type :
					case jmp :		{
										int val = ParsedProgram.symtab.get(inst.getDestinationOperand().getLabelValue())-inst.getProgramCounter();
										ans += toBinaryOfSpecificPrecisioString(0, 5);
										ans += toBinaryOfSpecificPrecisioString(val, 22);
										break;
									}
					
					case end :	{
						ans += toBinaryOfSpecificPrecisioString(0, 27);
						break;
					}
						
					default: Misc.printErrorAndExit("unknown instruction!!");
				}
				int intAns = (int) Long.parseLong(ans, 2);
				byte[] byteArr = ByteBuffer.allocate(4).putInt(intAns).array();
				file.write(byteArr);
			}
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
