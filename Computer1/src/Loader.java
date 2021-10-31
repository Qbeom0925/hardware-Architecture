import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Loader {
	
	private CPU cpu;
	private Memory memory;
	int sizeHeader;
	int sizeDataSegment;
	int sizeCodeSegment;
	int startAddress;
	int currentAddress;


	//associate
	public Loader(Memory memory, CPU cpu) {
		this.memory = memory;
		this.cpu = cpu;
	}


	private void loadHeader(Scanner scanner) {
		short lineDataSegmentSize = scanner.nextShort(16);
		short lineCodeSegmentSize = scanner.nextShort(16);
		this.sizeHeader = 2;
		this.sizeCodeSegment = 46;
		this.sizeDataSegment = 6;
		this.startAddress = this.memory.allocateMemory(sizeHeader+sizeDataSegment+sizeCodeSegment);
		memory.Loadstore((short)0, lineCodeSegmentSize);
		memory.Loadstore((short)1, (short)lineDataSegmentSize);
		this.cpu.setPC(startAddress+sizeHeader);
		this.cpu.setSP(startAddress+sizeHeader+sizeCodeSegment);
	}
	

	private void loadBody(Scanner scanner) {
		this.currentAddress=startAddress+sizeHeader;
		while(scanner.hasNext()) {
			short line = scanner.nextShort(16);
			memory.Loadstore((short)currentAddress, line);
			currentAddress++;
		}
	}
	
	public void load(String fileName){
		try {
		File file = new File("exe/"+fileName);
		Scanner scanner = new Scanner(file);
		loadHeader(scanner);
		loadBody(scanner);
		scanner.close();
		}catch (FileNotFoundException e) {
		}
	}
}
