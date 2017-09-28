package pippin;

public class Test {
	public static void main(String[] args) {
		//Works
		try{
			Instruction.checkParity(12);
		}catch(ParityCheckException e) {
			System.out.println(e);
		}
		//Doesn't work
		try{
			Instruction.checkParity(11);
		}catch(ParityCheckException e) {
			System.out.println(e);
		}
		//Works
		try{
			Instruction.checkParity(15);
		}catch(ParityCheckException e) {
			System.out.println(e);
		}
		//Doesn't work
		try{
			Instruction.checkParity(13);
		}catch(ParityCheckException e) {
			System.out.println(e);
		}
	}
}
