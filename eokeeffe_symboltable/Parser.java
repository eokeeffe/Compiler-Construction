package src;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Parser
{

	public enum ErrorType
	{
		UNDEFINED_ERROR, REDEFINED_ERROR;
	}

	private Scanner scanner;

	private SymbolTable symbolTable;

	private StringBuilder parseTreeBuffer;
	private int recursionDepth;
	private List<String> errors;

	private boolean doSemanticChecks;

	public Parser(boolean doSemanticChecks)
	{
		this.doSemanticChecks = doSemanticChecks;
	}

	public Parser()
	{
		this(true);
	}

	public void parse(String sourceFilename) throws ParserException
	{

		// (Re-)initialize fields
		parseTreeBuffer = new StringBuilder();
		recursionDepth = 1; // to match test cases, should really be 0
		errors = new ArrayList<String>();

		scanner = new Scanner(sourceFilename);
		scanner.next();
		initSymbolTable();

		// Start the parsing
		program();
	}

	// ---------- Methods for managing parse tree ----------
	private void enterRule(NonTerminal nt) throws ParserException
	{

		if(!have(nt))
		{
			printError(nt);
		}

		StringBuilder lineData = new StringBuilder();
		for (int i = 0; i < recursionDepth; i++)
			lineData.append("   ");
		lineData.append(nt.name);
		parseTreeBuffer.append(lineData).append("\n");
		recursionDepth++;
	}

	private void exitRule(NonTerminal nt)
	{

		recursionDepth--;
	}

	public String getParseTree()
	{

		return parseTreeBuffer.toString();
	}

	// ---------- Methods for error reporting ----------
	private void addTestCaseErrorMessage()
	{

		// For comparing output: please use this format as it will be used to
		// compare output results.
		if(doSemanticChecks)
		{
			StringBuilder errorMessage = new StringBuilder();
			if(scanner.getTokenKind() == TokenKind.ERROR)
			{
				errorMessage
						.append("Syntax Error (Scanner): SCAN_ERROR on symbol "
								+ scanner.getLexeme());
			}
			else
			{
				errorMessage.append("Syntax Error (Parser):  on symbol "
						+ scanner.getLexeme());
			}
			errorMessage.append("\n");
			errorMessage.append("Error Found on Line Number: ")
					.append(scanner.getLineNum()).append("\n");
			errorMessage.append(symbolTable);
			errors.add(errorMessage.toString());
		}
		else
		{
			errors.add("Error: " + scanner.getTokenKind() + "  "
					+ scanner.getLexeme());
		}
	}

	private void printError(TokenKind expectedToken) throws ParserException
	{
		// We are terminating the parse here, but better compilers will do error
		// recovery and keep going to find more syntax errors.
		addTestCaseErrorMessage();
		String errorMessage = "Error: found " + scanner.getTokenKind() + " ("
				+ scanner.getLexeme() + ")" + " but expected: " + expectedToken;
		throw new ParserException(scanner.getLineNum(), scanner.getCharPos(),
				errorMessage);
	}

	private void printError(NonTerminal nt) throws ParserException
	{

		addTestCaseErrorMessage();
		String errorMessage = "Error: found " + scanner.getTokenKind() + " ("
				+ scanner.getLexeme() + ")" + " but expected: " + nt + " with "
				+ nt.firstSet;
		throw new ParserException(scanner.getLineNum(), scanner.getCharPos(),
				errorMessage);
	}

	private void printError(ErrorType errorType, Token nameToken)
			throws ParserException
	{
		String errorMessage = "SEMANTIC ERROR: " + errorType + " on symbol "
				+ nameToken.getLexeme();
		StringBuilder outputError = new StringBuilder();
		outputError.append(errorMessage).append("\n");
		outputError.append(
				"Error Found on Line Number: " + nameToken.getLineNum())
				.append("\n");
		outputError.append(symbolTable);
		errors.add(outputError.toString());
		throw new ParserException(nameToken.getLineNum(),
				nameToken.getCharPos(), errorMessage);
	}

	public List<String> getErrors()
	{

		return errors;
	}

	// ---------- Symbol Table management methods ----------
	private void initSymbolTable()
	{
		// TODO: initialize your symbol table here
		symbolTable = new SymbolTable();
		// add the following symbols to the init table
		symbolTable.addSymbol(Symbol.newTypeSymbol("int"));
		symbolTable.addSymbol(Symbol.newTypeSymbol("char"));
		symbolTable.addSymbol(Symbol.newTypeSymbol("string"));
		symbolTable.addSymbol(Symbol.newTypeSymbol("void"));
		// System.out.println(symbolTable);

	}

	private void tryDeclareSymbol(Token nameToken, Symbol symbol)
			throws ParserException
	{
		if(!(symbolTable.addSymbol(symbol)))
		{
			printError(ErrorType.REDEFINED_ERROR, nameToken);
		}
		else
		{

		}
	}

	private Symbol tryResolveSymbol(Token nameToken, Symbol.Kind kind)
			throws ParserException
	{
		// TODO: implement

		//System.out.println("T:"+nameToken.getLexeme()+"--K:"+kind);
		Symbol temp = symbolTable.getSymbol(nameToken.getLexeme(), kind);
		if(temp != null)
		{
			return temp;
		}
		else
		{
			printError(ErrorType.UNDEFINED_ERROR, nameToken);
			return null;
		}
	}

	public SymbolTable getSymbolTable()
	{
		return symbolTable;
	}

	// ---------- Scope methods -----------
	private void enterScope()
	{
		/**
		 * Enters a new scobe for symbols.
		 */
		// System.out.println(symbolTable);
		// System.out.println("-------------------------------------");
		symbolTable = symbolTable.newScope();
		// System.out.println("-------------------------------------");
		// System.out.println(symbolTable);
	}

	private void exitScope()
	{
		/**
		 * Exit current symbol scope.
		 */
		symbolTable = symbolTable.getParent();
	}

	// ---------- Helper methods ----------
	private boolean have(TokenKind tokenKind)
	{
		return scanner.getTokenKind() == tokenKind;
	}

	private boolean have(NonTerminal nt)
	{

		return nt.firstSet.contains(scanner.getTokenKind());
	}

	private boolean accept(TokenKind tokenKind)
	{
		if(have(tokenKind))
		{
			scanner.next();
			return true;
		}
		return false;
	}

	private boolean accept(NonTerminal nt)
	{
		if(have(nt))
		{
			scanner.next();
			return true;
		}
		return false;
	}

	private boolean expect(TokenKind tokenKind) throws ParserException
	{
		if(accept(tokenKind)) return true;
		printError(tokenKind);
		return false;
	}

	private boolean expect(NonTerminal nt) throws ParserException
	{

		if(accept(nt)) { return true; }
		printError(nt);
		return false;
	}

	private Token expectRetrieve(TokenKind tokenKind) throws ParserException
	{
		Token res = scanner.getToken();
		expect(tokenKind);
		return res;
	}

	private Token expectRetrieve(NonTerminal nt) throws ParserException
	{

		Token res = scanner.getToken();
		expect(nt);
		return res;
	}

	// ---------- Recursive descent methods ----------
	// TODO: integrate the symbol table into the recursive descent methods
	// below.

	// selector := IDENT { "[" expression "]" }
	private void selector() throws ParserException
	{
		enterRule(NonTerminal.SELECTOR);
		Token ident = expectRetrieve(TokenKind.IDENT);

		while (accept(TokenKind.L_BRACKET))
		{
			expression();
			expect(TokenKind.R_BRACKET);
		}
		tryResolveSymbol(ident, Symbol.Kind.CONST);
		tryResolveSymbol(ident, Symbol.Kind.VAR);

		exitRule(NonTerminal.SELECTOR);
	}

	// parameters := IDENT { "," IDENT }
	private void parameters() throws ParserException
	{
		enterRule(NonTerminal.PARAMETERS);
		do
		{
			Token ident = expectRetrieve(TokenKind.IDENT);
			tryResolveSymbol(ident, Symbol.Kind.CONST);
			tryResolveSymbol(ident, Symbol.Kind.VAR);
		}
		while (accept(TokenKind.COMMA));

		exitRule(NonTerminal.PARAMETERS);
	}

	// condition := "(" expression relop expression ")"
	private void condition() throws ParserException
	{

		enterRule(NonTerminal.CONDITION);

		expect(TokenKind.L_PAREN);
		expression();
		expect(NonTerminal.RELOP);
		expression();
		expect(TokenKind.R_PAREN);

		exitRule(NonTerminal.CONDITION);
	}

	// procedureCall := "::" IDENT "(" [ parameters ] ")"
	private void procedureCall() throws ParserException
	{

		enterRule(NonTerminal.PROCEDURE_CALL);

		expect(TokenKind.COLON);
		expect(TokenKind.COLON);
		Token ident = expectRetrieve(TokenKind.IDENT);
		expect(TokenKind.L_PAREN);
		if(have(NonTerminal.PARAMETERS))
		{
			parameters();
		}
		expect(TokenKind.R_PAREN);

		tryResolveSymbol(ident, Symbol.Kind.PROCEDURE);
		
		exitRule(NonTerminal.PROCEDURE_CALL);
	}

	// assignment := selector "=" ( expression | string_literal ) ";"
	private void assignment() throws ParserException
	{

		enterRule(NonTerminal.ASSIGNMENT);

		selector();
		expect(TokenKind.ASSIGN);
		if(have(NonTerminal.EXPRESSION))
		{
			expression();
		}
		else
		{
			expect(TokenKind.STRING_LITERAL);
		}
		expect(TokenKind.SEMICOLON);

		exitRule(NonTerminal.ASSIGNMENT);
	}

	// input := "input" "(" parameters ")" ";"
	private void input() throws ParserException
	{

		enterRule(NonTerminal.INPUT);

		expect(TokenKind.INPUT);
		expect(TokenKind.L_PAREN);
		parameters();
		expect(TokenKind.R_PAREN);
		expect(TokenKind.SEMICOLON);

		exitRule(NonTerminal.INPUT);
	}

	// output := "print" "(" parameters ")" ";"
	private void output() throws ParserException
	{

		enterRule(NonTerminal.OUTPUT);

		expect(TokenKind.PRINT);
		expect(TokenKind.L_PAREN);
		parameters();
		expect(TokenKind.R_PAREN);
		expect(TokenKind.SEMICOLON);

		exitRule(NonTerminal.OUTPUT);
	}

	// ifStatement := "if" condition "{" statementSequence "}"
	// [ "else" "{" statementSequence "}" ]
	private void ifStatement() throws ParserException
	{

		enterRule(NonTerminal.IF_STATEMENT);

		expect(TokenKind.IF);
		condition();
		expect(TokenKind.L_BRACE);
		statementSequence();
		expect(TokenKind.R_BRACE);
		if(accept(TokenKind.ELSE))
		{
			expect(TokenKind.L_BRACE);
			statementSequence();
			expect(TokenKind.R_BRACE);
		}

		exitRule(NonTerminal.IF_STATEMENT);
	}

	// whileStatement := "while" condition "{" statementSequence "}"
	private void whileStatement() throws ParserException
	{

		enterRule(NonTerminal.WHILE_STATEMENT);

		expect(TokenKind.WHILE);
		condition();
		expect(TokenKind.L_BRACE);
		statementSequence();
		expect(TokenKind.R_BRACE);

		exitRule(NonTerminal.WHILE_STATEMENT);
	}

	// returnStatement := "return" expression ";"
	private void returnStatement() throws ParserException
	{

		enterRule(NonTerminal.RETURN_STATEMENT);

		expect(TokenKind.RETURN);
		expression();
		expect(TokenKind.SEMICOLON);

		exitRule(NonTerminal.RETURN_STATEMENT);
	}

	// procedureStatement := procedureCall ";"
	private void procedureStatement() throws ParserException
	{

		enterRule(NonTerminal.PROCEDURE_STATEMENT);

		procedureCall();
		expect(TokenKind.SEMICOLON);

		exitRule(NonTerminal.PROCEDURE_STATEMENT);
	}

	// statement := assignment | input | output | ifStatement |
	// whileStatement | returnStatement | procedureStatement
	private void statement() throws ParserException
	{

		enterRule(NonTerminal.STATEMENT);

		if(have(NonTerminal.ASSIGNMENT))
		{
			assignment();
		}
		else if(have(NonTerminal.INPUT))
		{
			input();
		}
		else if(have(NonTerminal.OUTPUT))
		{
			output();
		}
		else if(have(NonTerminal.IF_STATEMENT))
		{
			ifStatement();
		}
		else if(have(NonTerminal.WHILE_STATEMENT))
		{
			whileStatement();
		}
		else if(have(NonTerminal.RETURN_STATEMENT))
		{
			returnStatement();
		}
		else if(have(NonTerminal.PROCEDURE_STATEMENT))
		{
			procedureStatement();
		}
		else
		{
			expect(NonTerminal.STATEMENT);
		}

		exitRule(NonTerminal.STATEMENT);
	}

	// statementSequence := statement { sretatement }
	private void statementSequence() throws ParserException
	{

		enterRule(NonTerminal.STATEMENT_SEQUENCE);

		do
		{
			statement();
		}
		while (have(NonTerminal.STATEMENT));

		exitRule(NonTerminal.STATEMENT_SEQUENCE);
	}

	// factor := selector | NUMBER | procedureCall | "(" expression ")"
	private void factor() throws ParserException
	{

		enterRule(NonTerminal.FACTOR);

		if(have(NonTerminal.SELECTOR))
		{
			selector();
		}
		else if(accept(TokenKind.NUMBER))
		{}
		else if(have(NonTerminal.PROCEDURE_CALL))
		{
			procedureCall();
		}
		else if(accept(TokenKind.L_PAREN))
		{
			expression();
			expect(TokenKind.R_PAREN);
		}
		else
		{
			expect(NonTerminal.FACTOR);
		}

		exitRule(NonTerminal.FACTOR);
	}

	// term := factor { op1 factor }
	private void term() throws ParserException
	{

		enterRule(NonTerminal.TERM);

		factor();
		while (accept(NonTerminal.OP1))
		{
			factor();
		}

		exitRule(NonTerminal.TERM);
	}

	// expression := term { op2 term }
	private void expression() throws ParserException
	{

		enterRule(NonTerminal.EXPRESSION);

		term();
		while (accept(NonTerminal.OP2))
		{
			term();
		}

		exitRule(NonTerminal.EXPRESSION);
	}

	// retType := "int" | "string" | "void" | "char"
	private Type retType() throws ParserException
	{
		enterRule(NonTerminal.RET_TYPE);
		Token rettype = expectRetrieve(NonTerminal.RET_TYPE);
		exitRule(NonTerminal.RET_TYPE);

		if(rettype.getLexeme().equals("int"))
		{
			return Type.newPrimitiveType("int");
		}
		else if(rettype.getLexeme().equals("char"))
		{
			return Type.newPrimitiveType("char");
		}
		else if(rettype.getLexeme().equals("string"))
		{
			return Type.newPrimitiveType("string");
		}
		else if(rettype.getLexeme().equals("void")) { return Type
				.newPrimitiveType("void"); }
		throw new ParserException(rettype.getLineNum(), rettype.getCharPos(),
				rettype.getLexeme());
	}

	// type := "int" | "char" | "string" | "array" NUMBER "of" type
	private Type type() throws ParserException
	{
		enterRule(NonTerminal.TYPE);

		if(accept(TokenKind.INT))
		{
			return Type.newPrimitiveType("int");
		}
		else if(accept(TokenKind.CHAR))
		{
			return Type.newPrimitiveType("char");
		}
		else if(accept(TokenKind.STRING))
		{
			return Type.newPrimitiveType("string");
		}
		else if(accept(TokenKind.ARRAY))
		{
			int dim = Integer.parseInt(expectRetrieve(TokenKind.NUMBER)
					.getLexeme());
			expect(TokenKind.OF);
			Type t = type();
			return Type.newArrayType(t, dim);
		}

		exitRule(NonTerminal.TYPE);
		return null;
	}

	// declarations := { "const" IDENT "=" NUMBER ";" | "var" IDENT { "," IDENT
	// } ":" type ";" }
	private void declarations() throws ParserException
	{
		enterRule(NonTerminal.DECLARATIONS);
		while (have(NonTerminal.DECLARATIONS))
		{
			if(accept(TokenKind.CONST))
			{
				// expect(TokenKind.IDENT);
				Token ident = expectRetrieve(TokenKind.IDENT);
				expect(TokenKind.ASSIGN);
				Token number = expectRetrieve(TokenKind.NUMBER);

				tryDeclareSymbol(
						ident,
						Symbol.newConstSymbol(ident.getLexeme(),
								Type.newPrimitiveType("int"),
								number.getLexeme()));

				expect(TokenKind.SEMICOLON);
			}
			else if(accept(TokenKind.VAR))
			{
				// expect(TokenKind.IDENT);
				LinkedList<Token> idents = new LinkedList<Token>();
				idents.add(expectRetrieve(TokenKind.IDENT));

				while (accept(TokenKind.COMMA))
				{
					idents.add(expectRetrieve(TokenKind.IDENT));
				}
				expect(TokenKind.COLON);
				Type var_type = type();

				for (Token t : idents)
				{
					tryDeclareSymbol(t,
							Symbol.newVarSymbol(t.getLexeme(), var_type));
				}
				expect(TokenKind.SEMICOLON);
			}
			else expect(NonTerminal.DECLARATIONS);
		}

		exitRule(NonTerminal.DECLARATIONS);
	}

	// procedureFormalParams := type IDENT { "," type IDENT }
	private void procedureFormalParams() throws ParserException
	{
		enterRule(NonTerminal.PROCEDURE_FORMAL_PARAMS);
		do
		{
			Type t = type();
			Token ident = expectRetrieve(TokenKind.IDENT);
			tryDeclareSymbol(ident, Symbol.newVarSymbol(ident.getLexeme(), t));
		}
		while (accept(TokenKind.COMMA));
		exitRule(NonTerminal.PROCEDURE_FORMAL_PARAMS);
	}

	// procedureDeclarations := { retType IDENT "(" [ procedureFormalParams ]
	// ")" "{"
	// declarations procedureDeclarations statementSequence "}" }
	private void procedureDeclarations() throws ParserException
	{
		enterRule(NonTerminal.PROCEDURE_DECLARATIONS);

		while (have(NonTerminal.PROCEDURE_DECLARATIONS))
		{
			Type rettype = retType();
			Token ident = expectRetrieve(TokenKind.IDENT);
			tryDeclareSymbol(ident,
					Symbol.newProcedureSymbol(ident.getLexeme(), rettype));
			enterScope();
			expect(TokenKind.L_PAREN);
			
			if(have(NonTerminal.PROCEDURE_FORMAL_PARAMS))
			{
				procedureFormalParams();
			}
			expect(TokenKind.R_PAREN);
			expect(TokenKind.L_BRACE);
			if(have(NonTerminal.DECLARATIONS))
			{
				declarations();
			}
			if(have(NonTerminal.PROCEDURE_DECLARATIONS))
			{
				procedureDeclarations();
			}
			statementSequence();
			expect(TokenKind.R_BRACE);
			exitScope();
		}

		exitRule(NonTerminal.PROCEDURE_DECLARATIONS);
	}

	// program := declarations procedureDeclarations
	// "main" "(" ")" "{" declarations statementSequnce "}"
	private void program() throws ParserException
	{

		enterRule(NonTerminal.PROGRAM);

		if(have(NonTerminal.DECLARATIONS))
		{
			declarations();
		}
		if(have(NonTerminal.PROCEDURE_DECLARATIONS))
		{
			procedureDeclarations();
		}
		// Special case for main()
		expect(TokenKind.MAIN);
		expect(TokenKind.L_PAREN);
		expect(TokenKind.R_PAREN);
		enterScope();
		expect(TokenKind.L_BRACE);
		if(have(NonTerminal.DECLARATIONS))
		{
			
			declarations();
		}
		statementSequence();
		expect(TokenKind.R_BRACE);
		exitScope();
		
		expect(TokenKind.EOF);

		exitRule(NonTerminal.PROGRAM);
	}

}
