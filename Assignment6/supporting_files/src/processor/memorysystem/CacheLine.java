package processor.memorysystem;

public class CacheLine{
    int[] tagArray = new int[2]; 
    int[] dataArray = new int[2];
    int LRU;

    public CacheLine() {
        this.tagArray[0] = -1;
        this.tagArray[1] = -1;
        this.LRU = 0;
    }

    public CacheLine(int newLRU) {
        this.LRU = newLRU;
        this.tagArray[0] = -1;
        this.tagArray[1] = -1;
    }

    public int setLRU(int newLRU) {
        this.LRU = newLRU;
        return this.LRU;
    }
    
    public void setValue(int tag, int value) {
        if(tag == this.tagArray[0]) {
            this.LRU = 1;
            this.dataArray[0] = value;
        } else if(tag == this.tagArray[1]) {
            this.LRU = 0;
            this.dataArray[1] = value;
        } else {
            this.tagArray[this.LRU] = tag;
            this.dataArray[this.LRU] = value;
            this.LRU = 1- this.LRU;
        }
	}
    public int getdataArray(int index) {
        return this.dataArray[index];
    }

    public int gettagArray(int index) {
        return this.tagArray[index];
    }

    public int getLRU() {
        return this.LRU;
    }


    public String toString() {
        return Integer.toString(this.LRU);
    }
}