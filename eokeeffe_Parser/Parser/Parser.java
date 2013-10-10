

//Parser.java
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class Parser
{
	static String studentName = "Evan O'Keeffe";
	static String studentID = "";
	static String uciNetID = "";

	private int recursionDepth;
	private Scanner scanner;
	public Token currentToken;
	private static FileOutputStream parserOutFile;
	private static PrintStream parserPrintData;
	private static String currentIdent;

	/* USED FOR INDENTATION & THE PRINTING OF RULE LABELS */
	public void printNonTerminal(NonTerminal nonTerminal)
	{

		String lineData = new String();
		for (int i = 0; i < recursionDepth; i++)
		{
			lineData += "   ";
		}
		lineData += nonTerminal.lexeme;
		parserPrintData.println(lineData);
	}

	/* USED FOR PRINTING ERROR INFORMATION */
	private void printError(Token token) throws ParserException
	{

		// For Comparing Output: please use this format as it will be used
		// to compare output results
		parserPrintData.println("Error: " + token + "  " + scanner.getLexeme());
		System.err.println("Error Found on Line Number: "
				+ scanner.getLineNum());
		throw new ParserException();
	}

	public class ParserException extends Exception
	{
		private static final long serialVersionUID = 1L;
		String msg;

		ParserException()
		{

			super();
			msg = currentToken + "(" + currentToken.defaultLexeme + ")";
		}

		ParserException(String message)
		{

			super();
			msg = message;
		}
	}

	public Parser(String arg)
	{

		try
		{
			scanner = new Scanner(arg);
		}
		catch (IOException io)
		{
			System.out.println("Caught an error " + io);
		}

		System.out.println("outfile: " + arg + ".out");
		try
		{
			parserOutFile = new FileOutputStream(arg + ".out");
			parserPrintData = new PrintStream(parserOutFile);
		}

		catch (IOException e)
		{
			e.printStackTrace();
			System.err.println("init: Errors accessing output file: " + arg
					+ ".gdl");
			System.exit(-2);
		}

		try
		{
			currentToken = scanner.next();
		}
		catch (IOException e)
		{
			System.out.println("Caught an error " + e);
		}
	}

	public static void main(String[] args)
	{

		Parser p = new Parser(args[0]);
		try
		{
			p.program();
			parserOutFile.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(-2);
		}
		catch (ParserException e)
		{
			System.exit(-1);
		}
	}

	/**
	 * For further explanation of the accept/expect functions see
	 * Recursive_descent_parser in Wikipedia
	 * 
	 * @throws IOException
	 */
	private boolean accept(Token token)
	{

		try
		{
			if (currentToken == token)
			{
				if (currentToken == Token.IDENT)
				{
					currentIdent = scanner.getLexeme();
				}
				currentToken = scanner.next();
				return true;
			}
			return false;
		}
		catch (IOException e)
		{
			System.out.println("Caught an error " + e);
			return false;
		}
	}

	private boolean expect(Token token) throws ParserException
	{

		if (accept(token)) { return true; }
		// System.out.println("ERROR:"+token);
		// //printError(currentToken);
		return false;
	}

	private boolean acceptNT(NonTerminal nt)
	{

		if (have(nt))
		{
			try
			{
				currentToken = scanner.next();
			}
			catch (IOException e)
			{
				return false;
			}
			return true;
		}
		return false;
	}

	private boolean have(NonTerminal nt)
	{

		return nt.firstSet.contains(currentToken);
	}

	private boolean expectNT(NonTerminal nt) throws ParserException
	{

		if (acceptNT(nt)) { return true; }
		// //printError(currentToken);
		return false;
	}

	public void program() throws ParserException
	{
		/*
		 * main(){
		 * 
		 * }
		 */
		if (NonTerminal.PROGRAM.firstSet.contains(currentToken))
		{
			//System.err.println("PROGRAM:" + currentToken);

			recursionDepth++;
			printNonTerminal(NonTerminal.PROGRAM);

			declarations();
			procedureDeclarations();

			expect(Token.MAIN);
			expect(Token.L_PAREN);
			expect(Token.R_PAREN);
			expect(Token.L_BRACE);
			declarations();
			statementSequence();
			expect(Token.R_BRACE);
			expect(Token.EOF);
		}
		else if (currentToken == Token.EOF)
		{
			parserPrintData.println("Error: " + Token.EOF + "  ");
			System.err.println("Error Found on Line Number: "
					+ scanner.getLineNum());
			throw new ParserException();
		}
		else
		{
			// ERROR
			printError(currentToken);
		}
	}

	public void declarations() throws ParserException
	{

		/**
		 * const ident = number; or var ident {,ident} : type ;
		 */
		if (NonTerminal.DECLARATIONS.firstSet.contains(currentToken))
		{
			// System.err.println("DECLARATIONS:" + currentToken);
			recursionDepth++;
			printNonTerminal(NonTerminal.DECLARATIONS);

			while (currentToken == (Token.CONST) || currentToken == (Token.VAR))
			{
				if (accept(Token.CONST))
				{
					// expect(Token.CONST);
					expect(Token.IDENT);
					expect(Token.ASSIGN);
					expect(Token.NUMBER);
					expect(Token.SEMICOLON);
				}
				else if (accept(Token.VAR))
				{
					// expect(Token.VAR);
					expect(Token.IDENT);
					while (accept(Token.COMMA))
					{
						expect(Token.IDENT);
					}
					expect(Token.COLON);
					type();
					expect(Token.SEMICOLON);
				}
			}
			recursionDepth--;
		}
		else
		{
			// ERROR
			//printError(currentToken);
		}
	}

	public void procedureDeclarations() throws ParserException
	{

		/**
		 * rettype ident (parameters) { declarations procedureDeclarations
		 * statementSequence }
		 */
		if (NonTerminal.PROCEDURE_DECLARATIONS.firstSet.contains(currentToken))
		{
			// System.err.println("PROCEDURE DEC:" + currentToken);
			recursionDepth++;
			printNonTerminal(NonTerminal.PROCEDURE_DECLARATIONS);

			while (currentToken == Token.INT || currentToken == Token.CHAR
					|| currentToken == Token.STRING
					|| currentToken == Token.VOID)
			{

				retType();
				expect(Token.IDENT);
				expect(Token.L_PAREN);
				procedureFormalParams();
				expect(Token.R_PAREN);
				expect(Token.L_BRACE);
				declarations();
				procedureDeclarations();
				statementSequence();
				expect(Token.R_BRACE);
			}
			recursionDepth--;
		}
		else if (!NonTerminal.PROCEDURE_DECLARATIONS.firstSet
				.contains(currentToken))
		{
			
		}
		else
		{
			// ERROR
			// printError(currentToken);
		}

	}

	public void statement() throws ParserException
	{

		/**
		 * assignment,input,output,if,while, return,procedure statement
		 */
		if (NonTerminal.STATEMENT.firstSet.contains(currentToken))
		{
			// System.err.println("STATEMENT:" + currentToken);
			recursionDepth++;
			printNonTerminal(NonTerminal.STATEMENT);

			if (currentToken == Token.IDENT)
			{
				// System.out.println("1");
				assignment();
			}
			else if (currentToken == Token.INPUT)
			{
				// System.out.println("2");
				input();
			}
			else if (currentToken == Token.PRINT)
			{
				// System.out.println("3");
				output();
			}
			else if (currentToken == Token.IF)
			{
				// System.out.println("4");
				ifstatement();
			}
			else if (currentToken == Token.WHILE)
			{
				// System.out.println("5");
				whileStatement();
			}
			else if (currentToken == Token.RETURN)
			{
				// System.out.println("6");
				returnStatement();
			}
			else if (currentToken == Token.COLON)
			{
				// System.out.println("7");
				procedureStatement();
			}
			recursionDepth--;
		}
		else
		{
			// ERROR
			printError(currentToken);
		}
	}

	public void statementSequence() throws ParserException
	{

		/**
		 * statement {statement}
		 */
		if (NonTerminal.STATEMENT_SEQUENCE.firstSet.contains(currentToken))
		{
			// System.err.println("STATEMENT SEQ:" + currentToken);
			recursionDepth++;
			printNonTerminal(NonTerminal.STATEMENT_SEQUENCE);
			do
			{
				statement();
			}
			while (NonTerminal.STATEMENT.firstSet.contains(currentToken));
			recursionDepth--;
		}
		else
		{
			// ERROR
			printError(currentToken);
		}
	}

	public void procedureFormalParams() throws ParserException
	{

		/**
		 * type ident {,ident}
		 */
		if (NonTerminal.PROCEDURE_FORMAL_PARAMS.firstSet.contains(currentToken))
		{
			// System.err.println("ProcedureFormalParams:" + currentToken);
			recursionDepth++;
			printNonTerminal(NonTerminal.PROCEDURE_FORMAL_PARAMS);
			do
			{
				type();
				expect(Token.IDENT);
			}
			while (accept(Token.COMMA));
			recursionDepth--;
		}
		else
		{
			// ERROR
			//printError(currentToken);
		}
	}

	public void procedureStatement() throws ParserException
	{

		/**
		 * procedurecall.
		 */
		if (NonTerminal.PROCEDURE_STATEMENT.firstSet.contains(currentToken))
		{
			// System.err.println("PROCEDURE STATEMENT:" + currentToken);
			recursionDepth++;
			printNonTerminal(NonTerminal.PROCEDURE_STATEMENT);

			procedureCall();
			expect(Token.SEMICOLON);
			recursionDepth--;
		}
		else
		{
			// ERROR
			 printError(currentToken);
		}
	}

	public void retType() throws ParserException
	{

		if (NonTerminal.RET_TYPE.firstSet.contains(currentToken))
		{
			// System.err.println("RETTYPE:" + currentToken);
			recursionDepth++;
			printNonTerminal(NonTerminal.RET_TYPE);

			if (accept(Token.INT))
			{
			}
			else if (accept(Token.STRING))
			{
			}
			else if (accept(Token.VOID))
			{
			}
			else if (accept(Token.CHAR))
			{
			}

			recursionDepth--;
		}
		else
		{
			// ERROR
			printError(currentToken);
		}
	}

	public void type() throws ParserException
	{

		if (NonTerminal.TYPE.firstSet.contains(currentToken))
		{
			// System.err.println("TYPE:" + currentToken);
			recursionDepth++;
			printNonTerminal(NonTerminal.TYPE);

			if (accept(Token.INT))
			{
			}
			else if (accept(Token.CHAR))
			{
			}
			else if (accept(Token.STRING))
			{
			}
			else if (accept(Token.ARRAY))
			{
				expect(Token.NUMBER);
				expect(Token.OF);
				type();
			}
			recursionDepth--;
		}
		else
		{
			// ERROR
			printError(currentToken);
		}
	}

	public void procedureCall() throws ParserException
	{

		if (NonTerminal.PROCEDURE_CALL.firstSet.contains(currentToken))
		{
			// System.err.println("PROCEDURE CALL:" + currentToken);
			recursionDepth++;
			printNonTerminal(NonTerminal.PROCEDURE_CALL);

			if (accept(Token.COLON))
			{
				expect(Token.COLON);
				expect(Token.IDENT);
				expect(Token.L_PAREN);
				if (currentToken == Token.R_PAREN)
				{
					expect(Token.R_PAREN);
				}
				else
				{
					parameters();
					expect(Token.R_PAREN);
				}
			}
			recursionDepth--;
		}
		else
		{
			// ERROR
			printError(currentToken);
		}
	}

	public void returnStatement() throws ParserException
	{

		if (NonTerminal.RETURN_STATEMENT.firstSet.contains(currentToken))
		{
			// System.err.println("RETURN:" + currentToken);
			recursionDepth++;
			printNonTerminal(NonTerminal.RETURN_STATEMENT);

			expect(Token.RETURN);
			expression();
			expect(Token.SEMICOLON);

			recursionDepth--;
		}
		else
		{
			// ERROR
			printError(currentToken);
		}
	}

	public void whileStatement() throws ParserException
	{

		/**
		 * while(condition){statements}
		 */
		if (NonTerminal.WHILE_STATEMENT.firstSet.contains(currentToken))
		{
			// System.err.println("WHILE:" + currentToken);
			recursionDepth++;
			printNonTerminal(NonTerminal.WHILE_STATEMENT);

			expect(Token.WHILE);
			condition();
			expect(Token.L_BRACE);
			statementSequence();
			expect(Token.R_BRACE);

			recursionDepth--;
		}
		else
		{
			// ERROR
			printError(currentToken);
		}
	}

	public void ifstatement() throws ParserException
	{

		/*
		 * if(condition){statement}else{statement}
		 */
		if (NonTerminal.IF_STATEMENT.firstSet.contains(currentToken))
		{
			// System.err.println("IF:" + currentToken);
			recursionDepth++;
			printNonTerminal(NonTerminal.IF_STATEMENT);

			expect(Token.IF);
			condition();

			expect(Token.L_BRACE);
			statementSequence();
			expect(Token.R_BRACE);

			if (accept(Token.ELSE))
			{
				expect(Token.L_BRACE);
				statementSequence();
				expect(Token.R_BRACE);
			}

			recursionDepth--;
		}
		else
		{
			// ERROR
			printError(currentToken);
		}
	}

	public void condition() throws ParserException
	{

		if (NonTerminal.CONDITION.firstSet.contains(currentToken))
		{
			// System.err.println("CONDITION:" + currentToken);
			recursionDepth++;
			printNonTerminal(NonTerminal.CONDITION);

			expect(Token.L_PAREN);

			expression();
			expectNT(NonTerminal.RELOP);
			expression();

			expect(Token.R_PAREN);
			recursionDepth--;
		}
		else
		{
			// ERROR
			printError(currentToken);
		}
	}

	public void output() throws ParserException
	{

		if (NonTerminal.OUTPUT.firstSet.contains(currentToken))
		{
			// System.err.println("OUTPUT:" + currentToken);
			recursionDepth++;
			printNonTerminal(NonTerminal.OUTPUT);

			expect(Token.PRINT);
			expect(Token.L_PAREN);
			parameters();
			expect(Token.R_PAREN);
			expect(Token.SEMICOLON);

			recursionDepth--;
		}
		else
		{
			// ERROR
			printError(currentToken);
		}

	}

	public void input() throws ParserException
	{

		if (NonTerminal.INPUT.firstSet.contains(currentToken))
		{
			// System.err.println("INPUT:" + currentToken);
			recursionDepth++;
			printNonTerminal(NonTerminal.INPUT);

			expect(Token.INPUT);
			expect(Token.L_PAREN);
			parameters();
			expect(Token.R_PAREN);
			expect(Token.SEMICOLON);

			recursionDepth--;
		}
		else
		{
			// ERROR
			printError(currentToken);
		}
	}

	public void assignment() throws ParserException
	{

		/**
		 * selector = expr ;
		 */
		if (NonTerminal.ASSIGNMENT.firstSet.contains(currentToken))
		{
			// System.err.println("ASSIGNMENT:" + currentToken);
			recursionDepth++;
			printNonTerminal(NonTerminal.ASSIGNMENT);

			selector();
			expect(Token.ASSIGN);
			if (currentToken == Token.STRING_LITERAL)
			{
				expect(Token.STRING_LITERAL);
			}
			else
			{
				expression();
			}
			expect(Token.SEMICOLON);

			recursionDepth--;
		}
		else
		{
			// ERROR
			printError(currentToken);
		}

	}

	public void expression() throws ParserException
	{

		if (NonTerminal.EXPRESSION.firstSet.contains(currentToken))
		{
			// System.err.println("EXPR:" + currentToken);
			recursionDepth++;
			printNonTerminal(NonTerminal.EXPRESSION);
			do
			{
				term();
			}
			while (accept(Token.ADD) || accept(Token.SUB));
			recursionDepth--;
		}
		else
		{
			// ERROR
			printError(currentToken);
		}
	}

	public void term() throws ParserException
	{

		if (NonTerminal.TERM.firstSet.contains(currentToken))
		{
			// System.err.println("TERM:" + currentToken);
			recursionDepth++;
			printNonTerminal(NonTerminal.TERM);

			do
			{
				factor();
			}
			while (accept(Token.MULT) || accept(Token.DIV));
			recursionDepth--;
		}
		else
		{
			// ERROR
			printError(currentToken);
		}

	}

	public void factor() throws ParserException
	{

		/**
		 * factor -> ident|number|procedureCall|(expr)
		 */
		if (NonTerminal.FACTOR.firstSet.contains(currentToken))
		{
			// System.err.println("FACTOR:" + currentToken);
			recursionDepth++;
			printNonTerminal(NonTerminal.FACTOR);

			if (currentToken == Token.IDENT)
			{
				selector();
			}
			else if (currentToken == Token.COLON)
			{
				procedureCall();
			}
			else if (currentToken == Token.NUMBER)
			{
				expect(Token.NUMBER);
			}
			else if (currentToken == Token.L_PAREN)
			{
				expect(Token.L_PAREN);
				expression();
				expect(Token.R_PAREN);
			}
			recursionDepth--;
		}
		else
		{
			// ERROR
			printError(currentToken);
		}

	}

	public void selector() throws ParserException
	{

		/**
		 * selector -> ident { [ expr] }
		 */
		if (NonTerminal.SELECTOR.firstSet.contains(currentToken))
		{
			// System.err.println("SELECTOR:" + currentToken);
			recursionDepth++;
			printNonTerminal(NonTerminal.SELECTOR);

			expect(Token.IDENT);
			while (currentToken == Token.L_BRACKET)
			{
				expect(Token.L_BRACKET);
				expression();
				expect(Token.R_BRACKET);
			}
			recursionDepth--;
		}
		else
		{
			// ERROR
			printError(currentToken);
		}
	}

	private void parameters() throws ParserException
	{

		if (NonTerminal.PARAMETERS.firstSet.contains(currentToken))
		{
			// System.err.println("PARAMETERS:" + currentToken);
			recursionDepth++;
			printNonTerminal(NonTerminal.PARAMETERS);
			do
			{
				expect(Token.IDENT);
			}
			while (accept(Token.COMMA));
			recursionDepth--;
		}
		else
		{
			// ERROR
			printError(currentToken);
		}
	}

}