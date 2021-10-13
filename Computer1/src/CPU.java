
public class CPU {
	
	private enum EOpcode{
		eHALT,
		eLDA,
		eLDC,
		eSTA,
		eADD,
		eSUB,
		eSUBC,
		eNUL,
		eDIV,
		eAND,
		eJMP,
		eJMPBZ,
		eJMPEQ
	}

	private enum ERegister{
		eIR,
		eSP,
		ePC,
		eAC,
		eMBR,
		eMAR,   
		eStatus
	}
	

	private class ALU{

		public void add() {
			
			
		}

		public void subtract() {
			// TODO Auto-generated method stub
			
		}

		public void greaterThan() {
			// TODO Auto-generated method stub
			
		}

		public void equal() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private class CU {
		
	}
	
	private class Register{
		protected short value;
		public short getValue() {return this.value;}
		public void setValue(short value) {this.value = value;}
	}
	
	private class IR extends Register{
		
		public short getOperator() {
			short shr = (short)(this.value & 0xff00);
			int i = (int)shr;
			String str = Integer.toHexString(i);
			int i2 = Integer.parseInt(str);
			i2 = i2/100;
			return (short)(i2);
		}
		public short getOperand() {
			return (short)(this.value & 0x00ff);
		} 
	}
	

	
	
	//components
	private ALU alu;
	private CU cu;
	Register registers[];
	
	
	//association
	private Memory memory;
	
	//states
	private boolean bPowerOn;
	private boolean isPowerOn() {return this.bPowerOn;}
	public void setPowerOn() {
		this.cu = new CU();
		this.alu= new ALU();
		this.bPowerOn = true;
		this.run();
	}
	
	public void shutDown() {this.bPowerOn = false;}
	
	//associate
	public void associate(Memory memory) {this.memory = memory;}

	
	//constructor
	public CPU() {
		this.alu = new ALU();
		this.cu = new CU();
		this.registers = new Register[ERegister.values().length];
		for(ERegister eRegister: ERegister.values()) {
			this.registers[eRegister.ordinal()] = new Register();
			this.registers[eRegister.ordinal()] = new IR();
		}
		for(short i=0; i<=6; i++) {
			this.registers[i].setValue(i);
		}
	}
	
	//method
	private void fetch() {
		//인스트럭션을 가져오는 것
		//load next instruction from memory to IR
		// PC -> MAR
		this.registers[ERegister.eMAR.ordinal()].setValue(this.registers[ERegister.ePC.ordinal()].getValue());
		//MBR로 들어옴
		this.registers[ERegister.eMBR.ordinal()].setValue(this.memory.load(this.registers[ERegister.eMAR.ordinal()].getValue()));
		this.registers[ERegister.eIR.ordinal()].setValue(this.registers[ERegister.eMBR.ordinal()].getValue());
	}
	
	private void load() {
		//데이터를 가져오는 것 MAR
		this.registers[ERegister.eMAR.ordinal()].setValue(
				(short) 
				(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand()));
//				+ this.registers[ERegister.eSP.ordinal()].getValue()));
	}
	
	private void store() {
		this.memory.store(this.registers[ERegister.eMAR.ordinal()].getValue(),this.registers[ERegister.eMBR.ordinal()].getValue());
	}
	
	//EOpcode.values()[((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperator()]
	
	private void execute() {
		switch (EOpcode.values()[((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperator()]) {
		case eHALT:
			break;
		case eLDA:
			this.load();
			this.memory.load((short)ERegister.eMAR.ordinal());
			break;
		case eLDC:
			this.load();
			this.registers[ERegister.eAC.ordinal()].setValue(this.registers[ERegister.eMAR.ordinal()].getValue());
			System.out.println("현재 AC 값 : "+this.registers[ERegister.eAC.ordinal()].getValue());
			this.PCplus();
			break;	
		case eSTA:
			this.registers[ERegister.eMAR.ordinal()].setValue(this.registers[ERegister.eSP.ordinal()].getValue());
			this.memory.store((short)this.registers[ERegister.eMAR.ordinal()].getValue(),(short)this.registers[ERegister.eMBR.ordinal()].getValue());
			this.PCplus();
			break;
		case eADD:
			this.registers[ERegister.eAC.ordinal()].setValue(this.registers[ERegister.eMBR.ordinal()].getValue());
			this.load();
			this.alu.add();
			break;
		case eSUB:
			break;
		case eSUBC:
			break;
		case eNUL:
			break;
		case eDIV:
			break;
		case eAND:
			break;
		case eJMP:
			break;
		case eJMPBZ:
			break;
		case eJMPEQ:
			break;
		default:
			break;
		} 
	}
	
	private void PCplus() {
		short i = 1;
		i += this.registers[ERegister.ePC.ordinal()].getValue();
		
		this.registers[ERegister.ePC.ordinal()].setValue(i);
	}
	
	public void run() {
		this.exe();
		while(isPowerOn()) {
			this.fetch();
			this.execute();
		}
	}
	
	private void exe() {
		this.getPC();
//		this.getSP();
	}
	
//	private void getSP() {
//		this.registers[ERegister.eMBR.ordinal()].setValue(this.memory.load((short)20));
//		this.registers[ERegister.eIR.ordinal()].setValue(this.registers[ERegister.eMBR.ordinal()].getValue());
//		this.registers[ERegister.eSP.ordinal()].setValue(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperator());
//	}
	
	private void getPC() {
		this.registers[ERegister.eMBR.ordinal()].setValue(this.memory.load((short)2));
		this.registers[ERegister.eIR.ordinal()].setValue(this.registers[ERegister.eMBR.ordinal()].getValue());
		this.registers[ERegister.ePC.ordinal()].setValue(((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperator());
	}
	
	public static void main(String[] args) {
		CPU cpu = new CPU();
		Memory memory = new Memory();
		cpu.associate(memory);
		
		memory.load("sum");
		cpu.setPowerOn();
	}
}
