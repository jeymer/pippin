package pippin;
import java.util.Observable;

public class MachineModel extends Observable {

	private class CPU {
		private int accum;
		private int pc;
	}

	public final Instruction[] INSTRUCTIONS = new Instruction[0x10];
	private CPU cpu = new CPU();
	private Memory memory = new Memory();
	private boolean withGUI = false;
	private Code code = new Code();
	private boolean running = false;

	void halt() {
		if(!withGUI) {
			System.exit(0);
		}
		else {
			running = false;
		}
	}

	public MachineModel(boolean withGUI) {
		this.withGUI = withGUI;

		//INSTRUCTION_MAP entry for "NOP"
		INSTRUCTIONS[0x0] = (arg, flags) -> {
			flags = flags & 0x6; // remove parity bit that will have been verified
			if(flags != 0) {
				String fString = "(" + (flags%8 > 3?"1":"0") + 
						(flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException(
						"Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;			
		};

		//INSTRUCTION_MAP entry for "LOD"
		INSTRUCTIONS[0x1] = (arg, flags) -> {
			flags = flags & 0x6; // remove parity bit that will have been verified
			if(flags == 0) { // direct addressing
				cpu.accum = memory.getData(arg);
			} else if(flags == 2) { // immediate addressing
				cpu.accum = arg;
			} else if(flags == 4) { // indirect addressing
				cpu.accum = memory.getData(memory.getData(arg));
			} else { 
				String fString = "(" + (flags%8 > 3?"1":"0") 
						+ (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException(
						"Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;
		};

		//INSTRUCTION entry for "STO"
		INSTRUCTIONS[0x2] = (arg, flags) -> {
			flags = flags & 0x6;
			if(flags == 0) { // direct addressing
				memory.setData(arg, cpu.accum);
			} else if(flags == 4) { // indirect addressing
				memory.setData(memory.getData(arg), cpu.accum);
			} else { 
				String fString = "(" + (flags%8 > 3?"1":"0") 
						+ (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException(
						"Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;
		};

		//INSTRUCTION_MAP entry for "JUMP"
		INSTRUCTIONS[0x3] = (arg, flags) -> {
			flags = flags & 0x6; // remove parity bit that will have been verified
			if(flags == 0) { // direct addressing
				cpu.pc += arg;
			} else if(flags == 2) { // immediate addressing
				cpu.pc = arg;
			} else if(flags == 4) { // indirect addressing
				cpu.pc += memory.getData(arg);
			} else { 
				cpu.pc = memory.getData(arg);
			}
		};

		//INSTRUCTION_MAP entry for "JMPZ"
		INSTRUCTIONS[0x4] = (arg, flags) -> {
			if(cpu.accum == 0) {
				flags = flags & 0x6; // remove parity bit that will have been verified
				if(flags == 0) { // direct addressing
					cpu.pc += arg;
				} else if(flags == 2) { // immediate addressing
					cpu.pc = arg;
				} else if(flags == 4) { // indirect addressing
					cpu.pc += memory.getData(arg);
				} else { 
					cpu.pc = memory.getData(arg);
				}
			} else cpu.pc++;
		};

		//INSTRUCTION entry for ADD (add)
		INSTRUCTIONS[0x5] = (arg, flags) -> {
			flags = flags & 0x6; // remove parity bit that will have been verified
			if(flags == 0) { // direct addressing
				cpu.accum += memory.getData(arg);
			} else if(flags == 2) { // immediate addressing
				cpu.accum += arg;
			} else if(flags == 4) { // indirect addressing
				cpu.accum += memory.getData(memory.getData(arg));				
			} else { // here the illegal case is "11"
				String fString = "(" + (flags%8 > 3?"1":"0") 
						+ (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException(
						"Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;			
		};

		//INSTRUCTION entry for SUB (subtract)
		INSTRUCTIONS[0x6] = (arg, flags) -> {
			flags = flags & 0x6; // remove parity bit that will have been verified
			if(flags == 0) { // direct addressing
				cpu.accum -= memory.getData(arg);
			} else if(flags == 2) { // immediate addressing
				cpu.accum -= arg;
			} else if(flags == 4) { // indirect addressing
				cpu.accum -= memory.getData(memory.getData(arg));				
			} else { // here the illegal case is "11"
				String fString = "(" + (flags%8 > 3?"1":"0") 
						+ (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException(
						"Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;			
		};

		//INSTRUCTION entry for MUL (multiply)
		INSTRUCTIONS[0x7] = (arg, flags) -> {
			flags = flags & 0x6; // remove parity bit that will have been verified
			if(flags == 0) { // direct addressing
				cpu.accum *= memory.getData(arg);
			} else if(flags == 2) { // immediate addressing
				cpu.accum *= arg;
			} else if(flags == 4) { // indirect addressing
				cpu.accum *= memory.getData(memory.getData(arg));				
			} else { // here the illegal case is "11"
				String fString = "(" + (flags%8 > 3?"1":"0") 
						+ (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException(
						"Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;			
		};

		//INSTRUCTION entry for DIV (divide)
		INSTRUCTIONS[0x8] = (arg, flags) -> {
			flags = flags & 0x6; // remove parity bit that will have been verified
			if(flags == 0) { // direct addressing
				if(memory.getData(arg) != 0) {
					cpu.accum /= memory.getData(arg);
				}
				else throw new DivideByZeroException("Cannot divide by zero");
			} else if(flags == 2) { // immediate addressing
				if(arg != 0) {
					cpu.accum /= arg;
				}
				else throw new DivideByZeroException("Cannot divide by zero");
			} else if(flags == 4) { // indirect addressing
				if(memory.getData(memory.getData(arg)) != 0) {
					cpu.accum /= memory.getData(memory.getData(arg));
				}	
				else throw new DivideByZeroException("Cannot divide by zero");
			} else { // here the illegal case is "11"
				String fString = "(" + (flags%8 > 3?"1":"0") 
						+ (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException(
						"Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;			
		};

		//INSTRUCTION entry for "AND"
		INSTRUCTIONS[0x9] = (arg, flags) -> {
			flags = flags & 0x6; // remove parity bit that will have been verified
			if(flags == 0) { // direct addressing
				if(cpu.accum != 0 && memory.getData(arg) != 0) {
					cpu.accum = 1;
				}
				else cpu.accum = 0;
			} else if(flags == 2) { // immediate addressing
				if(cpu.accum != 0 && arg != 0) {
					cpu.accum = 1;
				}
				else cpu.accum = 0;
			} else { // here the illegal case is "11"
				String fString = "(" + (flags%8 > 3?"1":"0") 
						+ (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException(
						"Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;			
		};

		//INSTRUCTION entry for "NOT"
		INSTRUCTIONS[0xA] = (arg, flags) -> {
			flags = flags & 0x6; // remove parity bit that will have been verified
			if(flags == 0) {
				if(cpu.accum == 0) {
					cpu.accum = 1;
				}
				else cpu.accum = 0;
			} else {
				String fString = "(" + (flags%8 > 3?"1":"0") + 
						(flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException(
						"Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;			
		};

		//INSTRUCTION entry for "CMPL"
		INSTRUCTIONS[0xB] = (arg, flags) -> {
			flags = flags & 0x6; // remove parity bit that will have been verified
			if(flags == 0) {
				if(memory.getData(arg) < 0) {
					cpu.accum = 1;
				}
				else cpu.accum = 0;
			} else {
				String fString = "(" + (flags%8 > 3?"1":"0") + 
						(flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException(
						"Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;			
		};

		//INSTRUCTION entry for "CMPZ"
		INSTRUCTIONS[0xC] = (arg, flags) -> {
			flags = flags & 0x6; // remove parity bit that will have been verified
			if(flags == 0) {
				if(memory.getData(arg) == 0) {
					cpu.accum = 1;
				}
				else cpu.accum = 0;
			} else {
				String fString = "(" + (flags%8 > 3?"1":"0") + 
						(flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException(
						"Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;			
		};
		
		//INSTRUCTION entry for "FOR"
		//Doesn't increment cpu.pc at the end because the second for loop will leave the PC 
		//at the position after the last instruction in the for loop
		//Not the best implementation, but it works
		INSTRUCTIONS[0xD] = (arg, flags) -> {
			flags = flags & 0x6; // remove parity bit that will have been verified
			if(flags == 0) { // direct addressing
				int tempPC = cpu.pc + 1;
				if(getData(arg)/0x1000 > 0 && getData(arg)%0x1000 > 0) {
					for(int a = 0; a < getData(arg)%0x1000; a++) {
						cpu.pc = tempPC;
						for(int b = 0; b < getData(arg)/0x1000; b++) {
							//cpu.pc++;
							step();
						}
					}
				}
			} else if(flags == 2) { // immediate addressing
				int tempPC = cpu.pc + 1;
				if(arg/0x1000 > 0 && arg%0x1000 > 0) {
					for(int a = 0; a < arg%0x1000; a++) {
						cpu.pc = tempPC;
						for(int b = 0; b < arg/0x1000; b++) {
							//cpu.pc++;
							step();
						}
					}
				}
			} else { 
				String fString = "(" + (flags%8 > 3?"1":"0") 
						+ (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException(
						"Illegal flags for this instruction: " + fString);
			}
			//cpu.pc++
		};

		//INSTRUCTION entry for "HALT"
		INSTRUCTIONS[0xF] = (arg, flags) -> {
			flags = flags & 0x6; // remove parity bit that will have been verified
			if(flags == 0) {
				halt();
			} else {
				String fString = "(" + (flags%8 > 3?"1":"0") + 
						(flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException(
						"Illegal flags for this instruction: " + fString);
			}		
		};
	}
	
	public MachineModel() {
		this(false);
	}
	
	public void setData(int i, int j) {
		memory.setData(i, j);		
	}
	public Instruction get(int i) {
		return INSTRUCTIONS[i];
	}
	int[] getData() {
		return memory.getData();
	}
	int getData(int i) {
		return memory.getData(i);
	}
	public int getPC() {
		return cpu.pc;
	}
	public void setPC(int pc) {
		cpu.pc = pc;
	}
	public int getAccum() {
		return cpu.accum;
	}
	public void setAccum(int i) {
		cpu.accum = i;
	}
	public void setRunning(boolean running) {
		this.running = running;
	}
	public boolean isRunning() {
		return this.running;
	}
	public void setCode(int op, int arg) {
		code.setCode(op, arg);
	}
	void clear() {
		memory.clear();
		code.clear();
		cpu.accum = 0;
		cpu.pc = 0;
	}
	void step() {
		try{
			int opPart = code.getOpPart(cpu.pc);
			int arg = code.getArg(cpu.pc);
			Instruction.checkParity(opPart);
			INSTRUCTIONS[opPart/8].execute(arg, opPart%8);
		}catch(Exception e) {
			halt();
			throw e;
		}
	}
	Code getCode() {
		return this.code;
	}
	int getChangedIndex() {
		return memory.getChangedIndex();
	}
}
