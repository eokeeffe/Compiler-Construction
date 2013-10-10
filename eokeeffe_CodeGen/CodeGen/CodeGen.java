
import java.io.*;
import java.util.Vector;

/* NOTE: PLEASE FEEL FREE TO USE ALL, PART, OR NONE OF THIS FILE
 IT IS SIMPLY GIVEN TO SERVE AS A STARTING POINT */

public class CodeGen {

	public static String fp = "$fp";
	public static String sp = "$sp";
	public static String gp = "$gp";
	public static String zero = "$0";
	Boolean tempRegs[]; // there are 8 temporary registers
	Boolean args[]; // there are 4 arguments
	Boolean savedTempRegs[];// there are 8 saved temporary registers
	Vector<String> codeSeg; // Storage for code/text segment
	Vector<String> dataSeg; // Storage for any data constants

	int mainInstruction; // Integer location for the first instruction of main
							// in the codeSeg
	int nextInstruction; // Storage for next available instruction location
	int nextLocalLocation;// Storage for counting the next available local data
							// cell
	int nextGlobalLocation;// Storage for counting the next available global
							// data cell
	int labelCounter; // Used for generating unique labels as needed throughout
						// the program

	CodeGen() {
		tempRegs = new Boolean[8];
		args = new Boolean[8];
		savedTempRegs = new Boolean[8];
		nextInstruction = 0;
		nextLocalLocation = 0;
		nextGlobalLocation = 0;
		mainInstruction = -1;
		codeSeg = new Vector<String>();
		dataSeg = new Vector<String>();
		labelCounter = 0;

		for (int i = 0; i < 8; i++) {
			args[i] = savedTempRegs[i] = tempRegs[i] = false;
		}
	}

	// Returns a unique label
	public String makeLabel() {
		return "label." + (labelCounter++);
	}

	// Insert an instruction into the code segment
	public void insertInstruction(String instr) {
		codeSeg.add(instr);
	}

	// Insert an allocation command into the data segment
	public void insertData(String data) {
		dataSeg.add(data);
	}

	// Resets the local allocation counter (i.e. when you exit a procedure)
	public void resetLocalLocation() {
		nextLocalLocation = 0;
	}

	// Resets the global allocation counter (if you should need it)
	public void resetGlobalLocation() {
		nextGlobalLocation = 0;
	}

	public void loadImmediate(int immediateRegister, String value) {
		insertInstruction("li $t" + immediateRegister + " , " + value);
	}

	public void loadAddress(String symbolName, int immediateRegister) {
		insertInstruction("la $t" + immediateRegister + " , userdata."
				+ symbolName);
	}

	public void storeAddress(String symbolName, int immediateRegister) {
		insertInstruction("sw	$t" + immediateRegister + ", userdata."
				+ symbolName);
	}

	public void loadWord(int src, int dest) {
		insertInstruction("lw $s" + dest + " ,  0($t" + src + ")");
	}

	public void storeConstValue(int src, int dest) {
		insertInstruction("sw  $s" + dest + " , 0($t" + src + ")");
	}

	public void storeValue(int src, int dest) {
		insertInstruction("sw  $t" + dest + " , 0($t" + src + ")");
	}

	public void storeWord(int register) {
		insertInstruction("sw    $t" + register + ", 0($sp)");
	}

	public void add(int register1, int register2) {
		/**
		 * Store the value of result = register1+register2
		 */
		insertInstruction("add $t" + register1 + ",$t" + register1 + ",$t" + register2);

		push(register1);
	}
	
	public void subtract(int register1, int register2)
	{
		/**
		 * Store the value of result = register2 - register1
		 */
		insertInstruction("sub $t" + register1 + ",$t" + register2 + ",$t" + register1);

		push(register1);
	}
	
	public void divide(int register1, int register2)
	{
		/**
		 * Store the value of result = register2 / register1
		 */
		insertInstruction("div $t" + register1 + ",$t" + register2 + ",$t" + register1);

		push(register1);
	}

	public void multiply(int register1, int register2)
	{
		/**
		 * Store the value of result = register1*register2
		 */
		insertInstruction("mul $t" + register1 + ",$t" + register2 + ",$t" + register1);

		push(register1);
	}
	
	public void deReference(int register) {
		insertInstruction("lw $t" + register + ", 0($sp)");
		insertInstruction("addi $sp , $sp , 4");
		insertInstruction("lw $t" + register + ", 0($t" + register + ")");
	}

	public void cond(String op, String branch){
		/**
		 * Jump to a branch if the condition is true
		 * Branch is the label where we jump to
		 * after either an if statement or while loop
		 */
		labelComment("CONDITION");
		int register = getTemporaryRegister();
		int register2 = getTemporaryRegister();
		
		int t0=pop();
		int t1=pop();
		
		/*
		 * beq $t0,$t1,target # branch to target if $t0 = $t1 
		 * blt $t0,$t1,target # branch to target if $t0 < $t1 
		 * ble $t0,$t1,target # branch to target if $t0 <= $t1 
		 * bgt $t0,$t1,target # branch to target if $t0 > $t1 
		 * bge $t0,$t1,target # branch to target if $t0 >= $t1 
		 * bne $t0,$t1,target #branch to target if $t0 <> $t1
		 */
		
		if (op.equals("==")) {
			
			insertInstruction("seq   $t"+register2+", $t"+t1+", $t"+t0);
			insertInstruction("addi  $sp, $sp, -4");
			insertInstruction("sw    $t"+register2+", 0($sp)");
			insertInstruction("lw    $t"+t1+", 0($sp)");
			insertInstruction("addi  $sp, $sp, 4");
			insertInstruction("beqz  $t"+t1+","+branch);
			
			//insertInstruction("beq $t"+t0+" , $t"+t1+" , "+branch);
			debugComment("IF a "+op+ " b");
		}
		else if (op.equals("!=")) {
			
			insertInstruction("sne   $t"+register2+", $t"+t1+", $t"+t0);
			insertInstruction("addi  $sp, $sp, -4");
			insertInstruction("sw    $t"+register2+", 0($sp)");
			insertInstruction("lw    $t"+t1+", 0($sp)");
			insertInstruction("addi  $sp, $sp, 4");
			insertInstruction("beqz  $t"+t1+","+branch);
			
			//insertInstruction("bne $t"+t0+" , $t"+t1+" , "+branch);
			debugComment("IF a "+op+ " b");
		}
		else if (op.equals("<")) {
			
			insertInstruction("slt   $t"+register2+", $t"+t1+", $t"+t0);
			insertInstruction("addi  $sp, $sp, -4");
			insertInstruction("sw    $t"+register2+", 0($sp)");
			insertInstruction("lw    $t"+t1+", 0($sp)");
			insertInstruction("addi  $sp, $sp, 4");
			insertInstruction("beqz  $t"+t1+","+branch);
			
			//insertInstruction("blt $t"+t0+" , $t"+t1+" , "+branch);
			debugComment("IF a "+op+ " b");
		}
		else if (op.equals("<=")) {
			
			insertInstruction("sle   $t"+register2+", $t"+t1+", $t"+t0);
			insertInstruction("addi  $sp, $sp, -4");
			insertInstruction("sw    $t"+register2+", 0($sp)");
			insertInstruction("lw    $t"+t1+", 0($sp)");
			insertInstruction("addi  $sp, $sp, 4");
			insertInstruction("beqz  $t"+t1+","+branch);
			
			
			//insertInstruction("ble $t"+t0+" , $t"+t1+" , "+branch);
			debugComment("IF a "+op+ " b");
		}
		else if (op.equals(">")) {
			
			insertInstruction("sgt   $t"+register2+", $t"+t1+", $t"+t0);
			insertInstruction("addi  $sp, $sp, -4");
			insertInstruction("sw    $t"+register2+", 0($sp)");
			insertInstruction("lw    $t"+t1+", 0($sp)");
			insertInstruction("addi  $sp, $sp, 4");
			insertInstruction("beqz  $t"+t1+","+branch);
			
			//insertInstruction("bgt $t"+t0+" , $t"+t1+" , "+branch);
			debugComment("IF a "+op+ " b");
		}
		else if (op.equals(">=")) {
			
			insertInstruction("sge   $t"+register2+", $t"+t1+", $t"+t0);
			insertInstruction("addi  $sp, $sp, -4");
			insertInstruction("sw    $t"+register2+", 0($sp)");
			insertInstruction("lw    $t"+t1+", 0($sp)");
			insertInstruction("addi  $sp, $sp, 4");
			insertInstruction("beqz  $t"+t1+","+branch);
			
			//insertInstruction("bge $t"+t0+" , $t"+t1+" , "+branch);
			debugComment("IF a "+op+ " b");
		}
		releaseTemporaryRegister(register);
		releaseTemporaryRegister(register2);
	}

	// Allocates memory by incrementing the local location counter
	// Returns the first address of the memory block allocated
	// Allocates a memory block as specified in allocationSize
	// Allocates based on the current local data segment counter
	public int getNextLocalLocation(int allocationSize) {
		/* FILL THIS IN */

		return 0;
	}

	// Allocates memory by incrementing the global location counter
	// Returns the first address of the memory block allocated
	// Allocates a memory block as specified in allocationSize
	// Allocates based on the current global data segment counter
	public int getNextGlobalLocation(int allocationSize) {
		/* FILL THIS IN */

		return 0;
	}

	// Returns the text of the instruction at the position specified
	public String getInstructionAt(int position) {
		return codeSeg.elementAt(position);
	}

	// Substitutes the text of the instruction at the position specified
	// with the value stored in update string
	public void updateInstructionAt(int position, String update) {
		/* FILL THIS IN */
	}

	// Return the next available instruction location in the code segment
	public int getNextInstructionNumber() {
		return codeSeg.size();
	}

	// Return the next available instruction location in the data segment
	public int getNextDataNumber() {
		return dataSeg.size();
	}

	// allocate a temporary register
	public int getTemporaryRegister() {
		for (int i = 0; i < 8; i++)
			if (!tempRegs[i]) {
				tempRegs[i] = true;
				return i;
			}
		return -1;
	}

	// allocate a temporary register that is saved across a call
	public int getTemporarySavedRegister() {
		for (int i = 0; i < 8; i++)
			if (!savedTempRegs[i]) {
				savedTempRegs[i] = true;
				return i;
			}
		return -1;
	}

	// release a temporary register indicated by regNum
	public void releaseTemporaryRegister(int regNum) {
		tempRegs[regNum] = false;
	}

	// release a saved temporary register indicated by regNum
	public void releaseTemporarySavedRegister(int regNum) {
		savedTempRegs[regNum] = false;
	}

	// deallocate all temporary registers
	public void releaseAllTemporaryRegisters() {
		/* FILL THIS IN */
	}

	// deallocate all saved temporary registers
	public void releaseAllTemporarySavedRegisters() {
		/* FILL THIS IN */
	}

	// indicate which instruction in the code segment is the first instruction
	// in main
	// used to determine where/when to print out the label for main
	public void setMainInstruction(int instr) {
		mainInstruction = instr;
	}

	// generate code to print an integer; the value is stored in the register
	// number
	// indicated by parameter reg
	public void insertPrintIntSequence(int reg) {
		// System.err.println(reg);
		int valReg = getTemporarySavedRegister();
		insertInstruction("lw $s" + valReg + " ,  0($t" + reg + ")");
		codeSeg.add("move      $a0, $s" + reg + "       # load the integer");
		codeSeg.add("li      $v0,1                  # Prepare to print an integer");
		codeSeg.add("syscall                        # print it!!");
		codeSeg.add("la $a0, data.newline                # and then print out a newline.");
		codeSeg.add("li $v0, 4 ");
		codeSeg.add("syscall        ");

		releaseTemporarySavedRegister(reg);
	}

	// generate code to read an integer; the value is stored into a register and
	// the
	// register number is the return result of the function
	public int insertInputIntSequence(String name) {
		/* FILL THIS IN */

		int register = getTemporaryRegister();
		int sregister = getTemporarySavedRegister();

		insertInstruction("la    $a0, data.intquery");
		insertInstruction("li    $v0, 4");
		insertInstruction("syscall");
		insertInstruction("li    $v0, 5");
		insertInstruction("syscall");
		insertInstruction("move  $s" + sregister + ", $v0");
		insertInstruction("la    $t" + register + ", userdata." + name);
		insertInstruction("sw    $s" + sregister + ", 0($t" + register + ")");

		releaseTemporarySavedRegister(sregister);
		return register;
	}

	public int pop() {
		int register = getTemporaryRegister();
		insertInstruction("lw $t" + register + ", 0($sp)");
		insertInstruction("addi $sp , $sp , 4");
		return register;
	}

	public void push(int reg) {
		/**
		 * Push command onto the code stack
		 */
		insertInstruction("addi $sp, $sp, -4");

		insertInstruction("sw $t" + reg + ", 0($sp)");
		debugComment("Pushing the register (" + reg + ") to stack.");
	}

	// generate code to exit the program
	public void insertExitSequence() {
		/* FILL THIS IN */
		insertInstruction("## :: EXIT SEQUENCE ::");
		insertInstruction("li    $v0, 10");
		insertInstruction("syscall");
	}

	// Print the program to the provided stream
	public void dumpProgram(PrintWriter printWriter) {
		printWriter
				.println(".data                         # BEGIN Data Segment");
		for (int i = 0; i < dataSeg.size(); i++) {
			printWriter.println(dataSeg.elementAt(i));
		}
		printWriter.println("data.newline:      .asciiz       \"\\n\"");
		printWriter.println("data.intquery:      .asciiz       \"\"");

		printWriter.println("                              # END Data Segment");

		printWriter
				.println(".text                         # BEGIN Code Segment");
		printWriter.println("main:");
		for (int i = 0; i < codeSeg.size(); i++) {
			if (i == mainInstruction)
				printWriter.print("main:     ");
			printWriter.println(codeSeg.elementAt(i));
		}
		printWriter.println("                              # END Code Segment");
	}

	public void labelComment(String comment) {
		/**
		 * Ease of seeing whats happening
		 */
		insertInstruction("##::" + comment + "::");
	}

	public void debugComment(String comment) {
		String cmd = codeSeg.get(codeSeg.size() - 1);
		codeSeg.set(codeSeg.size() - 1, cmd + "\t#" + comment);
	}

	public void declareConstOrVar(Symbol sym) {
		insertData("userdata." + sym.getName() + ":\t.space\t 4");
	}
}