
public class Memory {
	
	//association
	
	private short memory[];
	
	public Memory() {
		this.memory = new short[512];
	}
	

	public int allocateMemory(int i) {
		return i-i;
	}

	
	public short load(short mar) {
		short data = memory[mar];
		return data;
	}

	public short loadA(short mar) { 
		mar = this.DS_Search(mar);
		short data = memory[mar];
		return data;
	}
	
	private short DS_Search(short mar) {
		short search = (short) (mar/2);
		short output;
		short i = 2;
		while(true) {
			if(memory[i]==0){
				++i;
				output=(short) (i+search);
				break;
			}
			i++;
		}
		return output;
	}
	
	public short store(short mar, short mbr) {
		mar=this.DS_Search(mar);
		memory[mar] = mbr;
		return memory[mar];
	}





	public short Loadstore(short mar, short mbr) {
		memory[mar] = mbr;
		
		return memory[mar];		
	}
	
	
	
	
}
