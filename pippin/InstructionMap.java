package pippin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class InstructionMap {
	public static Map<String, Integer> opcode = new TreeMap<>();
	static {
		opcode.put("NOP", 0x0);
		opcode.put("LOD", 0x1);
		opcode.put("STO", 0x2);
		opcode.put("JUMP", 0x3);
		opcode.put("JMPZ", 0x4);
		opcode.put("ADD", 0x5);
		opcode.put("SUB", 0x6);
		opcode.put("MUL", 0x7);
		opcode.put("DIV", 0x8);
		opcode.put("AND", 0x9);
		opcode.put("NOT", 0xA);
		opcode.put("CMPL", 0xB);
		opcode.put("CMPZ", 0xC);
		opcode.put("FOR", 0xD);
		opcode.put("HALT", 0xF);
	}
	
	public static Map<Integer, String> mnemonics = new TreeMap<>();
	static {
		for(String key : opcode.keySet()) {
			mnemonics.put(opcode.get(key), key);
		}
	}
}

