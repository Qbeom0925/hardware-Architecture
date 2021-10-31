
public class CPU {
	
	private enum EOpcode{
		eHALT,
		eLDA,
		eLDC,
		eSTA,
		eADDA,
		eADDC,
		eSUBA,
		eSUBC,
		eDIVA,
		eDIVC,
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
		eSR
	}
	

	private class ALU{
		short st_value;
		short b;

		public void store(short value) {
			this.st_value = value;
		}

		public short add(short value) {
			this.b = (short) (st_value+value);
			return b;
		}
		
		public short div(short value) {
			this.b = (short) (st_value/value);
			return b;
		}
		public short sub(short value) {
			this.b = (short) (st_value-value);
			return b;
		}
		public void IsBZ_PushSR() {
			if(registers[ERegister.eAC.ordinal()].getValue()<=0) {
				registers[ERegister.eSR.ordinal()].setValue((short) 0x0000);
			}else {
				registers[ERegister.eSR.ordinal()].setValue((short) 0x1000);
			}
		}
		public void IsEQ_PushSR() {
			if(registers[ERegister.eAC.ordinal()].getValue()==0) {
				registers[ERegister.eSR.ordinal()].setValue((short) 0x0000);
			}else {
				registers[ERegister.eSR.ordinal()].setValue((short) 0x0100);
			}
		}
	}
	
	
	private class Register{
		protected short value;
		public short getValue() {return this.value;}
		public void setValue(short value) {this.value = value;}
	}
	
	private class CU {
		public boolean isBZ(Register sr) {
			if ((sr.getValue() & 0x1000) != 0) {
				return false;
			}else {
				return true;
			}
		}
		
		public boolean isEQ(Register sr) {
			if ((sr.getValue() & 0x0100) != 0) {
				return false;
			}else {
				return true;
			}
		}
	}
	

	
	private class IR extends Register{
		public short getOperator() {
			short shr = (short)(this.value & 0xff00);
			if(shr<0x0a00) {
				int i = (int)shr;
				String str = Integer.toHexString(i);
				int output = Integer.parseInt(str);
				output = output/100;
				return (short) output;
			}else {
				int i = (int)shr;
				String str = Integer.toHexString(i);
				char alp = str.charAt(0);
				short ascii = 97;
				short j=10;
				while(true) {
					if(alp==ascii) {
						return (short)j;
					}
					ascii++;
					j++;
				}
			}
		}
		public short getOperand() {
			short shr = (short)(this.value & 0x00ff);
			int i = (int)shr;
			String str = Integer.toHexString(i);
			int output = Integer.parseInt(str);
			return (short) output;
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
	
	//INSTRUCTIONS
	private void HALT() {		
		this.shutDown();
	}
	
	private void LDA() {
		//ir.operand -> MAR
		this.registers[ERegister.eMAR.ordinal()].setValue(
				((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand());
		//memory[MAR] -> MBR
		this.registers[ERegister.eMBR.ordinal()].setValue(
				this.memory.loadA((short)this.registers[ERegister.eMAR.ordinal()].getValue()));
		//MBR -> AC
		this.registers[ERegister.eAC.ordinal()].setValue(this.registers[ERegister.eMBR.ordinal()].getValue());
		this.PCplus();
	}
	private void LDC() {	
		//ir.operand -> MBR
		this.registers[ERegister.eMBR.ordinal()].setValue(
				((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand());
		//MBR -> AC
		this.registers[ERegister.eAC.ordinal()].setValue(this.registers[ERegister.eMBR.ordinal()].getValue());
		this.PCplus();
	}
	private void STA() {	
		//AC -> MBR
		this.registers[ERegister.eMBR.ordinal()].setValue(this.registers[ERegister.eAC.ordinal()].getValue());
		//ir.operand -> MAR
		this.registers[ERegister.eMAR.ordinal()].setValue(
				((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand());
		//MBR -> memory[MAR]
		this.memory.store(
			this.registers[ERegister.eMAR.ordinal()].getValue(),
			this.registers[ERegister.eMBR.ordinal()].getValue());
		this.PCplus();
	}
	
	private void ADDA() {		
		// AC -> ALU
		this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());
		this.PCplus();
		this.LDA();
		this.registers[ERegister.eAC.ordinal()].setValue(
				this.alu.add(this.registers[ERegister.eAC.ordinal()].getValue()));
		this.JMP(this.registers[ERegister.ePC.ordinal()].getValue(), (short)-1);
	}
	
	private void ADDC() {
		// AC -> ALU
		this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());
		this.PCplus();
		this.LDC();
		this.alu.add(this.registers[ERegister.eAC.ordinal()].getValue());
		this.JMP(this.registers[ERegister.ePC.ordinal()].getValue(), (short)-1);
	}
	private void SUBA() {		
		this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());
		this.PCplus();
		this.LDA();
		this.registers[ERegister.eAC.ordinal()].setValue(
		this.alu.sub(this.registers[ERegister.eAC.ordinal()].getValue()));
		this.JMP(this.registers[ERegister.ePC.ordinal()].getValue(), (short)-1);	
	}
	private void SUBC() {		
		this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());
		this.PCplus();
		this.LDC();
		this.registers[ERegister.eAC.ordinal()].setValue(
		this.alu.sub(this.registers[ERegister.eAC.ordinal()].getValue()));
		this.JMP(this.registers[ERegister.ePC.ordinal()].getValue(), (short)-1);	
	}
	private void DIVA() {	
		this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());
		this.PCplus();
		this.LDA();
		this.registers[ERegister.eAC.ordinal()].setValue(
		this.alu.div(this.registers[ERegister.eAC.ordinal()].getValue()));
		this.JMP(this.registers[ERegister.ePC.ordinal()].getValue(), (short)-1);	
	}
	private void DIVC() {	
		this.alu.store(this.registers[ERegister.eAC.ordinal()].getValue());
		this.PCplus();
		this.LDC();
		this.registers[ERegister.eAC.ordinal()].setValue(
		this.alu.div(this.registers[ERegister.eAC.ordinal()].getValue()));
		this.JMP(this.registers[ERegister.ePC.ordinal()].getValue(), (short)-1);	
	}
	
	private void JMP(short point, short i) {
		this.registers[ERegister.ePC.ordinal()].setValue((short) (point+i));
	}
	
	private void JMPBZ() {	
		this.alu.IsBZ_PushSR();
		if(this.cu.isBZ(this.registers[ERegister.eSR.ordinal()])) {
			this.registers[ERegister.ePC.ordinal()].setValue(
					((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand());
		}else {
			this.PCplus();
		}
	}
	private void JMPEQ() {		
		this.alu.IsEQ_PushSR();
		if(this.cu.isEQ(this.registers[ERegister.eSR.ordinal()])) {
			this.registers[ERegister.ePC.ordinal()].setValue(
					((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand());
		}else {
			this.PCplus();
		}
	}
	
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
	}
	

	
	
	private void execute() {
		switch (EOpcode.values()[((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperator()]) {
		case eHALT:
			this.HALT();
			break;
		case eLDA:
			this.LDA();
			break;
		case eLDC:
			this.LDC();
			break;	
		case eSTA:
			this.STA();
			break;
		case eADDA:
			this.ADDA();
			break;
		case eADDC:
			this.ADDC();
			break;
		case eSUBA:
			this.SUBA();
			break;
		case eSUBC:
			this.SUBC();
			break;
		case eDIVA:
			this.DIVA();
			break;
		case eDIVC:
			this.DIVC();
			break;
		case eJMP:
			this.JMP((short)((CPU.IR) this.registers[ERegister.eIR.ordinal()]).getOperand(), (short)0);
			break;
		case eJMPBZ:
			this.JMPBZ();
			break;
		case eJMPEQ:
			this.JMPEQ();
			break;
		default:
			break;
		} 
	}
	
	public void PCplus() {
		this.registers[ERegister.ePC.ordinal()].setValue(
				(short) (this.registers[ERegister.ePC.ordinal()].getValue()+1));
	}
	
	public void run() {
		while(isPowerOn()) {
			this.fetch();
			this.execute();
		}
	}
	
	public void setPC(int i) {
		this.registers[ERegister.eMBR.ordinal()].setValue((short)i);
		this.registers[ERegister.eIR.ordinal()].setValue(this.registers[ERegister.eMBR.ordinal()].getValue());
		this.registers[ERegister.ePC.ordinal()].setValue(this.registers[ERegister.eIR.ordinal()].getValue());
	}
	public void setSP(int i) {
		this.registers[ERegister.eMBR.ordinal()].setValue((short)i);
		this.registers[ERegister.eIR.ordinal()].setValue(this.registers[ERegister.eMBR.ordinal()].getValue());
		this.registers[ERegister.eSP.ordinal()].setValue(this.registers[ERegister.eIR.ordinal()].getValue());
	}

	
	public static void main(String[] args) {
		CPU cpu = new CPU();
		Memory memory = new Memory();
		cpu.associate(memory);
		Loader loader = new Loader(memory, cpu);
		loader.load("sum");
		cpu.setPowerOn();
		System.out.println("최종 rank = " + memory.loadA((short)10));
	}


}
