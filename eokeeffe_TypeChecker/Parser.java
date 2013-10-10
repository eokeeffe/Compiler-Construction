

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Parser
{
	public enum ErrorType
	{
		UNDEFINED_ERROR, REDEFINED_ERROR, MISMATCH_ERROR, ARITY_ERROR;
	}

	public static final String ERR_MISMATCH_INVALID_ARG = "Invalid argument: ";
	public static final String ERR_MISMATCH_INVALID_RET = "Invalid return: ";
	public static final String ERR_MISMATCH_INVALID_ASSIGN = "Invalid assignment: ";
	public static final String ERR_MISMATCH_INVALID_OP = "Invalid operator usage: ";
	public static final String ERR_MISMATCH_INVALID_REF = "Invalid array reference: ";
	public static final String ERR_MISMATCH_INVALID_CALL = "Invalid call: ";
	protected static Scanner scanner;
	private static SymbolTable symbolTable;
	private StringBuilder parseTreeBuffer;
	private int recursionDepth;
	private static List<String> errors;
	private static boolean doSemanticChecks;
	// get the token for the current procedure
	protected static Token sym;
	// for doing deep expr checking with strings,final check on main for return
	// type
	protected static boolean stringPresent = false, io = false, mainReturn = false;
	private TypeChecker checker = new TypeChecker();

	public Parser(boolean doSemanticChecks)
	{
		Parser.doSemanticChecks = doSemanticChecks;
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
	private static void addTestCaseErrorMessage()
	{
		// For comparing output: please use this format as it will be used to
		// compare output results.
		if(doSemanticChecks)
		{
			StringBuilder errorMessage = new StringBuilder();
			if(scanner.getTokenKind() == TokenKind.ERROR)
			{
				errorMessage.append("Syntax Error (Scanner): SCAN_ERROR on symbol " + scanner.getLexeme());
			}
			else
			{
				errorMessage.append("Syntax Error (Parser):  on symbol " + scanner.getLexeme());
			}
			errorMessage.append("\n");
			errorMessage.append("Error Found on Line Number: ").append(scanner.getLineNum()).append("\n");
			errorMessage.append(symbolTable);
			errors.add(errorMessage.toString());
		}
		else
		{
			errors.add("Error: " + scanner.getTokenKind() + "  " + scanner.getLexeme());
		}
	}

	protected static void printError(TokenKind expectedToken) throws ParserException
	{
		// We are terminating the parse here, but better compilers will do error
		// recovery and keep going to find more syntax errors.
		addTestCaseErrorMessage();
		String errorMessage = "Error: found " + scanner.getTokenKind() + " (" + scanner.getLexeme() + ")"
				+ " but expected: " + expectedToken;
		throw new ParserException(scanner.getLineNum(), scanner.getCharPos(), errorMessage);
	}

	protected void printError(NonTerminal nt) throws ParserException
	{
		addTestCaseErrorMessage();
		String errorMessage = "Error: found " + scanner.getTokenKind() + " (" + scanner.getLexeme() + ")"
				+ " but expected: " + nt + " with " + nt.firstSet;
		throw new ParserException(scanner.getLineNum(), scanner.getCharPos(), errorMessage);
	}

	protected static void printError(ErrorType error, Token tokenName) throws ParserException
	{
		printError(error, tokenName, null);
	}

	protected static void printError(ErrorType errorType, Token nameToken, String message) throws ParserException
	{
		String errorMessage = "SEMANTIC ERROR: " + errorType + " on symbol " + nameToken.getLexeme();
		StringBuilder outputError = new StringBuilder();
		outputError.append(errorMessage).append("\n");
		outputError.append("Error Found on Line Number: " + nameToken.getLineNum())
				.append(", Char Position: " + nameToken.getCharPos()).append("\n");
		if(message != null)
		{
			outputError.append(message).append("\n");
		}
		outputError.append(symbolTable);
		errors.add(outputError.toString());
		throw new ParserException(nameToken.getLineNum(), nameToken.getCharPos(), errorMessage);
	}

	public List<String> getErrors()
	{
		return errors;
	}

	// ---------- Symbol Table management methods ----------
	private void initSymbolTable()
	{
		symbolTable = new SymbolTable();
		// add the following symbols to the init table
		symbolTable.addSymbol(Symbol.newTypeSymbol("int"));
		symbolTable.addSymbol(Symbol.newTypeSymbol("char"));
		symbolTable.addSymbol(Symbol.newTypeSymbol("string"));
		symbolTable.addSymbol(Symbol.newTypeSymbol("void"));
	}

	private void tryDeclareSymbol(Token nameToken, Symbol symbol) throws ParserException
	{
		if(!(symbolTable.addSymbol(symbol)))
		{
			printError(ErrorType.REDEFINED_ERROR, nameToken);
		}
	}

	protected static Symbol tryResolveSymbol(Token nameToken, Symbol.Kind kind) throws ParserException
	{
		Symbol temp = symbolTable.getSymbol(nameToken.getLexeme(), kind);
		if(temp != null)
		{
			return temp;
		}
		else
		{
			if(!mainReturn)
			{
				printError(ErrorType.UNDEFINED_ERROR, nameToken);
			}
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
	private static boolean have(TokenKind tokenKind)
	{
		return scanner.getTokenKind() == tokenKind;
	}

	private boolean have(NonTerminal nt)
	{
		return nt.firstSet.contains(scanner.getTokenKind());
	}

	protected static boolean accept(TokenKind tokenKind)
	{
		if(have(tokenKind))
		{
			scanner.next();
			return true;
		}
		return false;
	}

	protected boolean accept(NonTerminal nt)
	{
		if(have(nt))
		{
			scanner.next();
			return true;
		}
		return false;
	}

	protected static boolean expect(TokenKind tokenKind) throws ParserException
	{
		if(accept(tokenKind)) return true;
		printError(tokenKind);
		return false;
	}

	protected boolean expect(NonTerminal nt) throws ParserException
	{
		if(accept(nt)) { return true; }
		printError(nt);
		return false;
	}

	protected static Token expectRetrieve(TokenKind tokenKind) throws ParserException
	{
		Token res = scanner.getToken();
		expect(tokenKind);
		return res;
	}

	protected Token expectRetrieve(NonTerminal nt) throws ParserException
	{
		Token res = scanner.getToken();
		expect(nt);
		return res;
	}

	// ---------- Recursive descent methods ---------- //
	// selector := IDENT { "[" expression "]" }
	private Node selector() throws ParserException
	{
		enterRule(NonTerminal.SELECTOR);
		Token ident = expectRetrieve(TokenKind.IDENT);
		Symbol id = tryResolveSymbol(ident, Symbol.Kind.VAR);
		Node lhs = new Node(id.getName(), id.getType());
		int arity = 0;// arity of an array ident
		while (accept(TokenKind.L_BRACKET))
		{
			Node rhs = expression();
			arity = checker.selectorCheck(lhs, rhs, id, ident, arity);
			if(arity == checker.getArrayArity(id.getType()))
			{
				// turn the lhs node type into the base type
				lhs = new Node(id.getName(), TypeChecker.getArrayBaseType(id.getType()));
			}
			else if(arity > checker.getArrayArity(id.getType()))
			{
				// using to much arity so set back to full type
				lhs = new Node(id.getName(), id.getType());
			}
			expect(TokenKind.R_BRACKET);
		}
		exitRule(NonTerminal.SELECTOR);
		stringPresent = false;
		return lhs;
	}

	// parameters := IDENT { "," IDENT }
	private void parameters() throws ParserException
	{
		enterRule(NonTerminal.PARAMETERS);
		if(io)
		{
			checker.ioCheck();
		}
		else
		{
			Symbol proc = tryResolveSymbol(sym, Symbol.Kind.PROCEDURE);
			checker.funcCheck(proc, proc.getArguements());
		}
		exitRule(NonTerminal.PARAMETERS);
	}

	// condition := "(" expression relop expression ")"
	private void condition() throws ParserException
	{
		enterRule(NonTerminal.CONDITION);
		expect(TokenKind.L_PAREN);
		Node lhs = expression();
		Token op = scanner.getToken();
		expect(NonTerminal.RELOP);
		Node rhs = expression();
		// type check the inputs
		TypeChecker.conditionCheck(lhs, op, rhs);
		expect(TokenKind.R_PAREN);
		exitRule(NonTerminal.CONDITION);
		stringPresent = false;
	}

	// procedureCall := "::" IDENT "(" [ parameters ] ")"
	private Node procedureCall() throws ParserException
	{
		io = false;// set the parameters to deal with func arguements
		enterRule(NonTerminal.PROCEDURE_CALL);
		expect(TokenKind.COLON);
		expect(TokenKind.COLON);
		sym = expectRetrieve(TokenKind.IDENT);
		Symbol proc = tryResolveSymbol(sym, Symbol.Kind.PROCEDURE);
		Node node = new Node(proc.getName(), proc.getType());
		expect(TokenKind.L_PAREN);
		if(have(NonTerminal.PARAMETERS))
		{
			parameters();
		}
		expect(TokenKind.R_PAREN);
		exitRule(NonTerminal.PROCEDURE_CALL);
		return node;
	}

	// assignment := selector "=" ( expression | string_literal ) ";"
	private void assignment() throws ParserException
	{
		// reset each time for an assignement
		stringPresent = false;
		enterRule(NonTerminal.ASSIGNMENT);
		Node rhs, lhs = selector();
		Token op = scanner.getToken();
		expect(TokenKind.ASSIGN);
		if(have(NonTerminal.EXPRESSION))
		{
			rhs = expression();
			TypeChecker.assignementExprCheck(lhs, op, rhs);
		}
		else
		{
			TypeChecker.assignementLiteralCheck(lhs, op);
			expect(TokenKind.STRING_LITERAL);
		}
		expect(TokenKind.SEMICOLON);
		exitRule(NonTerminal.ASSIGNMENT);
		stringPresent = false;
	}

	// input := "input" "(" parameters ")" ";"
	private void input() throws ParserException
	{
		io = true;// set the parameter function to deal with an IO func
		enterRule(NonTerminal.INPUT);
		sym = expectRetrieve(TokenKind.INPUT);
		expect(TokenKind.L_PAREN);
		parameters();
		expect(TokenKind.R_PAREN);
		expect(TokenKind.SEMICOLON);
		exitRule(NonTerminal.INPUT);
	}

	// output := "print" "(" parameters ")" ";"
	private void output() throws ParserException
	{
		io = true;// set the parameter function to deal with an IO func
		enterRule(NonTerminal.OUTPUT);
		sym = expectRetrieve(TokenKind.PRINT);
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
		// get the current function symbol if it exists
		Symbol func = tryResolveSymbol(sym, Symbol.Kind.PROCEDURE);
		// make current ident as the return token
		sym = expectRetrieve(TokenKind.RETURN);
		Node expr = expression();
		// do the type check on func symbol type and expr type
		TypeChecker.returnCheck(func, expr);
		expect(TokenKind.SEMICOLON);
		exitRule(NonTerminal.RETURN_STATEMENT);
		stringPresent = false;
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

	// statementSequence := statement { statement }
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
	private Node factor() throws ParserException
	{
		enterRule(NonTerminal.FACTOR);
		Node n = null;
		if(have(NonTerminal.SELECTOR))
		{
			n = selector();
		}
		else if(accept(TokenKind.NUMBER))
		{
			// if the token is a number then return most
			// generic number i.e int
			n = new Node(Type.newPrimitiveType("int"));
		}
		else if(have(NonTerminal.PROCEDURE_CALL))
		{
			n = procedureCall();
		}
		else if(accept(TokenKind.L_PAREN))
		{
			n = expression();
			expect(TokenKind.R_PAREN);
		}
		else
		{
			expect(NonTerminal.FACTOR);
		}
		exitRule(NonTerminal.FACTOR);
		// System.out.println("F:"+n.getType());
		return n;
	}

	// term := factor { op1 factor }
	private Node term() throws ParserException
	{
		enterRule(NonTerminal.TERM);
		Node lhs = factor();
		Token op = scanner.getToken();
		while (accept(NonTerminal.OP1))
		{
			Node rhs = factor();
			TypeChecker.termCheck(lhs, rhs, op);
			op = scanner.getToken();
		}
		exitRule(NonTerminal.TERM);
		return lhs;
	}

	// expression := term { op2 term }
	private Node expression() throws ParserException
	{
		enterRule(NonTerminal.EXPRESSION);
		Token sym = scanner.getToken();
		Node lhs = term();
		Token op = scanner.getToken();// get the first operation
		while (accept(NonTerminal.OP2))
		{
			Node rhs = term();
			// do the type checking for expression
			TypeChecker.expressionCheck(lhs, rhs, op, sym);
			op = scanner.getToken();
		}
		exitRule(NonTerminal.EXPRESSION);
		return lhs;
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
		else if(rettype.getLexeme().equals("void")) { return Type.newPrimitiveType("void"); }
		throw new ParserException(rettype.getLineNum(), rettype.getCharPos(), rettype.getLexeme());
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
			int dim = Integer.parseInt(expectRetrieve(TokenKind.NUMBER).getLexeme());
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
				tryDeclareSymbol(ident,
						Symbol.newConstSymbol(ident.getLexeme(), Type.newPrimitiveType("int"), number.getLexeme()));
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
					tryDeclareSymbol(t, Symbol.newVarSymbol(t.getLexeme(), var_type));
				}
				expect(TokenKind.SEMICOLON);
			}
			else expect(NonTerminal.DECLARATIONS);
		}
		exitRule(NonTerminal.DECLARATIONS);
	}

	// procedureFormalParams := type IDENT { "," type IDENT }
	private ArrayList<Symbol> procedureFormalParams() throws ParserException
	{
		/**
		 * For the declaration of a procedure we need to store the parameter
		 * types in the order they are defined, so store them in a list and send
		 * it back
		 */
		ArrayList<Symbol> args = new ArrayList<Symbol>();
		enterRule(NonTerminal.PROCEDURE_FORMAL_PARAMS);
		do
		{
			Type t = type();
			Token ident = expectRetrieve(TokenKind.IDENT);
			// simply make sure no parameters are arrays
			if(TypeChecker.arrayInstance(t))
			{
				printError(ErrorType.MISMATCH_ERROR, sym, ERR_MISMATCH_INVALID_ARG + ident.getLexeme() + ", " + t);
			}
			tryDeclareSymbol(ident, Symbol.newVarSymbol(ident.getLexeme(), t));
			args.add(tryResolveSymbol(ident, Symbol.Kind.VAR));
		}
		while (accept(TokenKind.COMMA));
		exitRule(NonTerminal.PROCEDURE_FORMAL_PARAMS);
		return args;
	}

	// procedureDeclarations := { retType IDENT "(" [ procedureFormalParams ]
	// ")" "{"
	// declarations procedureDeclarations statementSequence "}" }
	private void procedureDeclarations() throws ParserException
	{
		/**
		 * Small changes made to implement parameter checking now we store the
		 * list of args into the symbol object so we can look them up later
		 */
		enterRule(NonTerminal.PROCEDURE_DECLARATIONS);
		ArrayList<Symbol> args = new ArrayList<Symbol>();
		while (have(NonTerminal.PROCEDURE_DECLARATIONS))
		{
			Type rettype = retType();
			Token ident = sym = expectRetrieve(TokenKind.IDENT);
			tryDeclareSymbol(ident, Symbol.newProcedureSymbol(ident.getLexeme(), rettype));
			enterScope();
			expect(TokenKind.L_PAREN);
			if(have(NonTerminal.PROCEDURE_FORMAL_PARAMS))
			{
				args = procedureFormalParams();
			}
			Symbol proc = tryResolveSymbol(ident, Symbol.Kind.PROCEDURE);
			proc.addArguements(args);
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
		mainReturn = true;
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
