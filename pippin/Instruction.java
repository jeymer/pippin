package pippin;
public interface Instruction {
	void execute(int arg, int flags);
	
	//Original code from lab10 
	/*
	public static int numOnes(int input) {
		String temp = Integer.toUnsignedString(input, 2);
		int count = 0;
		for(int i = 0; i < temp.length(); i++) {
			if(temp.charAt(i) == '1') {
				count++;
			}
		}
		return count;
	}
	*/
	
	//Copied code from assignment9
	public static int numOnes(int input) {
		input = input - ((input >>> 1) & 0x55555555);
		input = (input & 0x33333333) + ((input >>> 2) & 0x33333333);
		return (((input + (input >>> 4)) & 0x0F0F0F0F) * 0x01010101) >>> 24;
	}
	
	public static void checkParity(int input) {
		if(numOnes(input) % 2 == 1) {
			throw new ParityCheckException("The instruction is corrupted.");
		}
	}
}
