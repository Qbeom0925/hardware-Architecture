import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Memory {
	
	//association
	
	private short memory[];
	short currentAddress;
	private Process currentProcess;

	
	public Memory() {
		this.memory = new short[512];
		this.currentAddress = 0;
	}
	
	public class Process{
		static final short sizeHeader = 4;
		static final short indexPC = 0; //메모리주소
		static final short indexSP = 1;
		
		private short PC, SP;
		private short sizeData, sizeCode;
		
		public short getPC() {return this.PC;}
		public short getSP() {return this.SP;}
		
		public Process() {
		}
		
		private void loadHeader(Scanner scanner) {
			this.sizeData = scanner.nextShort(16); 
			this.sizeCode =  scanner.nextShort(16);
			//make process header
			memory[indexPC] = (short) (scanner.nextShort(16)+sizeHeader/2); 
			currentAddress++;
			memory[currentAddress] = (short) (scanner.nextShort(16)+sizeHeader/2 + this.sizeCode/2);
			currentAddress++;
		}
		
		private void loadBody(Scanner scanner) {
			//system genertared code
			//pc
			//sp
			
			//code segment
			PC = currentAddress;
			for(short i=0; i<sizeCode/2; i++) {
				memory[currentAddress] = scanner.nextShort(16); 
				currentAddress++;
			}
			//data segment
			SP = currentAddress;
			currentAddress = (short) (currentAddress + this.sizeData/2);
		}
			
		public void load(String fileName) { 
			try {
				Scanner scanner = new Scanner(new File("exe/"+fileName));
				this.loadHeader(scanner);
				this.loadBody(scanner);
				scanner.close();
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void loadProcess(String fileName) {
		this.currentProcess = new Process();
		this.currentProcess.load(fileName);
	}
	public short getPC() { return this.currentProcess.getPC();}
	public short getSP() {return this.currentAddress.getSP();}

	public short load(short mar) {
		short data = memory[mar];
		return data;
	}

	public short store(short mar, short mbr) {
		memory[mar] = mbr;
		
		return memory[mar];
	}
	
}
