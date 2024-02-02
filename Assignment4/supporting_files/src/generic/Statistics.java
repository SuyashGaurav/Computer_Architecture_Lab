package generic;

import java.io.PrintWriter;

public class Statistics {
	// TODO add your statistics here
	private static int numberOfInstructions = 0;
    private static int numberOfCycles = 0;
	private static int numberOfOFStageInstructions;
	private static int numberOfBranchTaken;
	private static int numberOfRegisterWriteInstructions;
	public static void printStatistics(String statFile) {
		try {
			// TODO add code here to print statistics in the output file
			PrintWriter writer = new PrintWriter(statFile);
			// Print statistics in the statFile
			writer.print("Number of instructions executed = " + numberOfInstructions + "\n");
			writer.print("Number of cycles taken = " + numberOfCycles + "\n");
			writer.close();
		}
		catch(Exception e){
			Misc.printErrorAndExit(e.getMessage());
		}
	}
	// TODO write functions to update statistics
	public static void setNumberOfInstructions(int numberOfInstructions) {
		Statistics.numberOfInstructions = numberOfInstructions;
	}
	public static void setNumberOfBranchTaken(int numberOfBranchTaken) {
		Statistics.numberOfBranchTaken = numberOfBranchTaken;
	}
	public static void setnumberOfRegisterWriteInstructions(int numberOfRegisterWriteInstructions) {
		Statistics.numberOfRegisterWriteInstructions = numberOfRegisterWriteInstructions;
	}
	public static void setNumberOfOFInstructions(int numberOfOFStageInstructions) {
		Statistics.numberOfOFStageInstructions = numberOfOFStageInstructions;
	}
	public static void setNumberOfCycles(int numberOfCycles) {
		Statistics.numberOfCycles = numberOfCycles;
	}
	public static int getNumberOfInstructions() {
		return numberOfInstructions;
	}
	public static int getNumberOfBranchTaken() {
		return numberOfBranchTaken;
	}
	public static int getNumberOfOFInstructions() {
		return numberOfOFStageInstructions;
	}
	public static int getNumberOfCycles() {
		return numberOfCycles;
	}
	public static int getNumberOfRegisterWriteInstructions() {
		return numberOfRegisterWriteInstructions;
	}
}
