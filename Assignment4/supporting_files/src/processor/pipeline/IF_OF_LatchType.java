package processor.pipeline;

public class IF_OF_LatchType {
	int inst;
	boolean OF_enable;
	public IF_OF_LatchType() {
		OF_enable = false;
	}
	public IF_OF_LatchType(boolean OF_enable) {	
		this.OF_enable = OF_enable;
	}
	public IF_OF_LatchType(boolean OF_enable, int inst) {
		this.OF_enable = OF_enable;
		this.inst = inst;
	}
	public void setOF_enable(boolean OF_enable) {
		this.OF_enable = OF_enable;
	}
	public void setInstruction(int inst) {
		this.inst = inst;
	}
	public boolean getOF_enable() {
		return OF_enable;
	}
	public int getInstruction() {
		return inst;
	}
}
