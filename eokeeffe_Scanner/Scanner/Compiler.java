package src;

import java.io.IOException;

/**
 * The main class of the compiler you will build.
 * 
 * Be sure to modify the static member fields with your name, student ID, and
 * UCInet ID, so that the auto-grader can correctly attribute the submission to
 * you.
 */
public class Compiler
{

	public static String studentName = "Evan O'Keeffe";
	public static String studentID = "20645993";
	public static String uciNetID = "eokeeffe@uci.edu";

	private Scanner scanner;

	public Compiler(String sourceFilename) throws IOException
	{

		scanner = new Scanner(sourceFilename);
	}

	private String tokenToString(Token token, Scanner scanner)
	{

		if (token.defaultLexeme.length() == 0) return token.toString()
				.toLowerCase() + ":" + scanner.getLexeme();
		else return token.defaultLexeme;
	}

	/**
	 * Returns a string listing all the tokens in the program.
	 */
	private String getTokens() throws IOException
	{

		StringBuilder sb = new StringBuilder();

		Token token = scanner.next();
		while (token != Token.EOF && token != Token.ERROR)
		{
			sb.append(tokenToString(token, scanner)).append("\n");
			token = scanner.next();
		}
		sb.append(tokenToString(token, scanner)).append("\n"); // append the EOF
																// or ERROR

		return sb.toString();
	}

	/**
	 * Compiles the source file and returns the output of the compilation. This
	 * is the method called by Tester.java to test your implementation.
	 */
	public String compile() throws IOException
	{

		// Will be replaced with other methods in later labs.
		return getTokens();
	}

	// ---------- main() ----------
	public static void main(String[] args) throws IOException
	{

		if (args.length != 1)
		{
			System.err.println("Usage: java Compiler <sourceFile>");
			System.exit(1);
		}

		Compiler compiler = new Compiler(args[0]);
		String result = compiler.compile();
		System.out.println(result);
	}
}
