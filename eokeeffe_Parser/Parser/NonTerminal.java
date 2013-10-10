

//NonTerminals.java
import java.util.HashSet;

public enum NonTerminal
{
	CONDITION("condition", new HashSet<Token>()
	{
		private static final long serialVersionUID = 1L;
		{
			add(Token.L_PAREN);
		}
	}), PROCEDURE_CALL("procedureCall", new HashSet<Token>()
	{
		private static final long serialVersionUID = 1L;
		{
			add(Token.COLON);
		}
	}), SELECTOR("selector", new HashSet<Token>()
	{
		private static final long serialVersionUID = 1L;
		{
			// TODO
			add(Token.IDENT);
		}
	}), ASSIGNMENT("assignment", new HashSet<Token>()
	{
		private static final long serialVersionUID = 1L;
		{
			// TODO
			add(Token.IDENT);
			add(Token.ASSIGN);
		}
	}), DECLARATIONS("declarations", new HashSet<Token>()
	{
		private static final long serialVersionUID = 1L;
		{
			add(Token.CONST);
			add(Token.VAR);
		}
	}), FACTOR("factor", new HashSet<Token>()
	{
		private static final long serialVersionUID = 1L;
		{
			// TODO
			add(Token.NUMBER);
			add(Token.IDENT);
			add(Token.COLON);
			add(Token.L_PAREN);
		}
	}), TERM("term", new HashSet<Token>()
	{
		private static final long serialVersionUID = 1L;
		{
			// TODO
			addAll(FACTOR.firstSet);
		}
	}), EXPRESSION("expression", new HashSet<Token>()
	{
		private static final long serialVersionUID = 1L;
		{
			// TODO
			addAll(FACTOR.firstSet);
		}
	}), IF_STATEMENT("ifStatement", new HashSet<Token>()
	{
		private static final long serialVersionUID = 1L;
		{
			// TODO
			add(Token.IF);
		}
	}), INPUT("input", new HashSet<Token>()
	{
		private static final long serialVersionUID = 1L;
		{
			// TODO
			add(Token.INPUT);
		}
	}), OUTPUT("output", new HashSet<Token>()
	{
		private static final long serialVersionUID = 1L;
		{
			// TODO
			add(Token.PRINT);
		}
	}), PARAMETERS("parameters", new HashSet<Token>()
	{
		private static final long serialVersionUID = 1L;
		{
			// TODO
			add(Token.IDENT);
		}
	}), RET_TYPE("retType", new HashSet<Token>()
	{
		private static final long serialVersionUID = 1L;
		{
			// TODO
			add(Token.INT);
			add(Token.CHAR);
			add(Token.VOID);
			add(Token.STRING);
		}
	}), PROCEDURE_DECLARATIONS("procedureDeclarations", new HashSet<Token>()
	{
		private static final long serialVersionUID = 1L;
		{
			// TODO
			add(Token.VOID);
			
			add(Token.INT);
			add(Token.CHAR);
			add(Token.VOID);
			add(Token.STRING);
			
		}
	}), TYPE("type", new HashSet<Token>()
	{
		private static final long serialVersionUID = 1L;
		{
			// TODO
			add(Token.INT);
			add(Token.CHAR);
			add(Token.VOID);
			add(Token.STRING);
			add(Token.ARRAY);
			add(Token.OF);
		}
	}), PROCEDURE_FORMAL_PARAMS("procedureFormalParams", new HashSet<Token>()
	{
		private static final long serialVersionUID = 1L;
		{
			// TODO
			add(Token.IDENT);
			
			add(Token.INT);
			add(Token.CHAR);
			add(Token.VOID);
			add(Token.STRING);
			add(Token.ARRAY);
			add(Token.OF);
			
		}
	}), PROCEDURE_STATEMENT("procedureStatement", new HashSet<Token>()
	{
		private static final long serialVersionUID = 1L;
		{
			// TODO
			add(Token.COLON);
			add(Token.IDENT);
			add(Token.IDENT);
		}
	}), RETURN_STATEMENT("returnStatement", new HashSet<Token>()
	{
		private static final long serialVersionUID = 1L;
		{
			// TODO
			add(Token.RETURN);
			
			addAll(EXPRESSION.firstSet);
			
			add(Token.SEMICOLON);
		}
	}), WHILE_STATEMENT("whileStatement", new HashSet<Token>()
	{
		private static final long serialVersionUID = 1L;
		{
			// TODO
			add(Token.WHILE);
		}
	}), STATEMENT("statement", new HashSet<Token>()
	{
		private static final long serialVersionUID = 1L;
		{
			// TODO
			addAll(ASSIGNMENT.firstSet);
			addAll(INPUT.firstSet);
			addAll(OUTPUT.firstSet);
			addAll(IF_STATEMENT.firstSet);
			addAll(WHILE_STATEMENT.firstSet);
			addAll(RETURN_STATEMENT.firstSet);
			addAll(PROCEDURE_STATEMENT.firstSet);
			
			add(Token.IDENT);
			
		}
	}), STATEMENT_SEQUENCE("statementSequence", new HashSet<Token>()
	{
		private static final long serialVersionUID = 1L;
		{
			// TODO
			addAll(STATEMENT.firstSet);
		}
	}), PROGRAM("program", new HashSet<Token>()
	{
		private static final long serialVersionUID = 1L;
		{
			// TODO
			add(Token.MAIN);
			//add(Token.CONST);add(Token.INT);add(Token.CHAR);add(Token.VOID);add(Token.STRING);
			
			addAll(DECLARATIONS.firstSet);
			addAll(PROCEDURE_DECLARATIONS.firstSet);
		}
	})
	, OP1("op1", new HashSet<Token>()
	{
		private static final long serialVersionUID = 1L;
		{
			add(Token.MULT);
			add(Token.DIV);
		}
	})
	, OP2("op2", new HashSet<Token>()
	{
		private static final long serialVersionUID = 1L;
		{
			add(Token.ADD);
			add(Token.SUB);
		}
	})
	, RELOP("relop", new HashSet<Token>()
	{
		private static final long serialVersionUID = 1L;
		{
			add(Token.EQUAL);
			add(Token.NEQ);
			
			add(Token.LESS_THAN);
			add(Token.LESSER_EQUAL);
			
			add(Token.GREATER_THAN);
			add(Token.GREATER_EQUAL);
		}
	});

	public final String lexeme;
	public final HashSet<Token> firstSet = new HashSet<Token>();

	NonTerminal(String lexeme, HashSet<Token> t)
	{

		this.lexeme = lexeme;
		firstSet.addAll(t);
	}
}
