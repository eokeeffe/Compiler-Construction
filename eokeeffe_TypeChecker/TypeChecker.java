import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import src.Parser.ErrorType;

public class TypeChecker
{
	public TypeChecker()
	{}

	// ---------- Type Instance methods ----------------/
	// Check the instance is what is being looked for //
	public static boolean arrayInstance(Type t)
	{
		if(t instanceof Type.ArrayType) { return true; }
		return false;
	}

	public static boolean primInstance(Type t)
	{
		if(t instanceof Type.PrimitiveType) { return true; }
		return false;
	}

	public static boolean charInstance(Type t)
	{
		if(t == Type.newPrimitiveType("char")) { return true; }
		return false;
	}

	public static boolean intInstance(Type t)
	{
		if(t == Type.newPrimitiveType("int")) { return true; }
		return false;
	}

	public static boolean stringInstance(Type t)
	{
		if(t == Type.newPrimitiveType("string")) { return true; }
		return false;
	}

	public static Type getArrayBaseType(Type t)
	{
		/**
		 * Iteratively move down the base types
		 */
		while (!primInstance(t))
		{
			t = ((Type.ArrayType) t).getBaseType();
		}
		return t;
	}

	public static Type arrayBase(Type t)
	{
		return ((Type.ArrayType) t).getBaseType();
	}

	public int getArrayArity(Type t)
	{
		/**
		 * While the type is not a primitive get to the base type and count the
		 * number of levels there are , this is the arity
		 */
		int arity = 0;
		do
		{
			arity++;
			t = ((Type.ArrayType) t).getBaseType();
		}
		while (!primInstance(t));
		return arity;
	}

	// ---------- Type Check methods ----------------/
	// Check the type/semantics conform to EBNF //
	public int selectorCheck(Node lhs, Node rhs, Symbol id, Token ident, int arity) throws ParserException
	{
		if(arrayInstance(lhs.getType()) && !intInstance(rhs.getType()))
		{
			Parser.printError(ErrorType.MISMATCH_ERROR, ident, Parser.ERR_MISMATCH_INVALID_REF + "base = "
					+ arrayBase(lhs.getType()) + ", index = " + rhs.getType());
		}
		else if(!arrayInstance(lhs.getType()))
		{
			Parser.printError(ErrorType.MISMATCH_ERROR, ident,
					Parser.ERR_MISMATCH_INVALID_REF + "base = " + lhs.getType() + ", index = " + rhs.getType());
		}
		else
		{
			arity++;
		}
		return arity;
	}

	public static void assignementExprCheck(Node lhs, Token op, Node rhs) throws ParserException
	{
		checkForArrayError(1, lhs, rhs, op);
		if((lhs.getType() != rhs.getType()))
		{
			if(stringInstance(lhs.getType()))
			{
				// this is the deep string search , so if a string is in there
				// somewhere then we can find it
				if(!Parser.stringPresent)
				{
					Parser.printError(ErrorType.MISMATCH_ERROR, op, Parser.ERR_MISMATCH_INVALID_ASSIGN + lhs.getType()
							+ " <- " + rhs.getType());
				}
			}
			else
			{
				Parser.printError(ErrorType.MISMATCH_ERROR, op, Parser.ERR_MISMATCH_INVALID_ASSIGN + lhs.getType()
						+ " <- " + rhs.getType());
			}
		}
	}

	public static void assignementLiteralCheck(Node lhs, Token op) throws ParserException
	{
		if(!TypeChecker.stringInstance(lhs.getType()))
		{
			Parser.printError(ErrorType.MISMATCH_ERROR, op, Parser.ERR_MISMATCH_INVALID_ASSIGN + " " + lhs.getType()
					+ " <- " + Type.newPrimitiveType("string"));
		}
	}

	public static void termCheck(Node lhs, Node rhs, Token op) throws ParserException
	{
		/**
		 * int and char instances can be multiplied by each other , but not with
		 * each other
		 */
		checkForArrayError(0, lhs, rhs, op);
		if(!(TypeChecker.intInstance(lhs.getType()) && TypeChecker.intInstance(rhs.getType())))
		{
			Parser.printError(ErrorType.MISMATCH_ERROR, op, Parser.ERR_MISMATCH_INVALID_OP + op.getLexeme() + ", "
					+ lhs.getType() + ", " + rhs.getType());
		}
	}

	public static void expressionCheck(Node lhs, Node rhs, Token op, Token sym) throws ParserException
	{
		/**
		 * So anything can be added with a string, apart from that make sure the
		 * types are the same
		 */
		checkForArrayError(0, lhs, rhs, op);
		if(stringInstance(lhs.getType()) || stringInstance(rhs.getType()))
		{
			Parser.stringPresent = true;// set the deep string flag , so if a
			// string is somewhere in a long expression , the whole expression
			// becomes a string
			return;
		}
		else if(lhs.getType() != rhs.getType())
		{
			Parser.printError(ErrorType.MISMATCH_ERROR, op, Parser.ERR_MISMATCH_INVALID_OP + op.getLexeme() + ", "
					+ lhs.getType() + ", " + rhs.getType());
		}
	}

	public static void conditionCheck(Node lhs, Token op, Node rhs) throws ParserException
	{
		/**
		 * Check whether we are comparing an array first (error) then simply
		 * check if the 2 sides are of the same type
		 */
		checkForArrayError(0, lhs, rhs, op);
		if(Parser.stringPresent)
		{
			if(!stringInstance(lhs.getType()))
			{
				Parser.printError(ErrorType.MISMATCH_ERROR, op, Parser.ERR_MISMATCH_INVALID_OP + op.getLexeme() + ", "
						+ lhs.getType() + ", " + Type.newPrimitiveType("string"));
			}
		}
		else if(lhs.getType() != rhs.getType())
		{
			Parser.printError(ErrorType.MISMATCH_ERROR, op, Parser.ERR_MISMATCH_INVALID_OP + op.getLexeme() + ", "
					+ lhs.getType() + ", " + rhs.getType());
		}
	}

	public void ioCheck() throws ParserException
	{
		do
		{
			Token ident = Parser.expectRetrieve(TokenKind.IDENT);
			Symbol c = Parser.tryResolveSymbol(ident, Symbol.Kind.VAR);
			if(arrayInstance(c.getType()))
			{
				Parser.printError(ErrorType.MISMATCH_ERROR, Parser.sym, Parser.ERR_MISMATCH_INVALID_ARG + c.getName()
						+ ", " + c.getType());
			}
		}
		while (Parser.accept(TokenKind.COMMA));
	}

	public void funcCheck(Symbol proc, ArrayList<Symbol> args) throws ParserException
	{
		/**
		 * Slightly more awkward than IO check The order of the lists here
		 * matter so firstly go through the arguements and put them into another
		 * list during the input check that they are the same order as the real
		 * inputs and activate a flag isCorrect as false if there's a Mismatch
		 * error
		 * 
		 * else if the 2 lists are of different lengths then show an Arity error
		 */
		ListIterator<Symbol> iter = args.listIterator();
		ArrayList<Symbol> givenArgs = new ArrayList<Symbol>();
		Symbol correctSymbol = iter.next();
		boolean isCorrect = true;
		do
		{
			Token ident = Parser.expectRetrieve(TokenKind.IDENT);
			Symbol givenArg = Parser.tryResolveSymbol(ident, Symbol.Kind.VAR);
			if(arrayInstance(givenArg.getType()))
			{
				Parser.printError(ErrorType.MISMATCH_ERROR, Parser.sym,
						Parser.ERR_MISMATCH_INVALID_ARG + givenArg.getName() + ", " + givenArg.getType());
			}
			else
			{
				givenArgs.add(givenArg);
				if(iter.hasNext())
				{
					correctSymbol = iter.next();
				}
				if(givenArg.getType() != correctSymbol.getType())
				{
					isCorrect = false;
				}
			}
		}
		while (Parser.accept(TokenKind.COMMA));
		if(!isCorrect || (args.size() != givenArgs.size()))
		{
			String pattern = proc.getName() + "(";
			String pattern2 = "(";
			Iterator<Symbol> i1 = args.iterator();
			Iterator<Symbol> i2 = givenArgs.iterator();
			while (i1.hasNext())
			{
				pattern += i1.next().getType();
				if(i1.hasNext())
				{
					pattern += ", ";
				}
			}
			while (i2.hasNext())
			{
				pattern2 += i2.next().getType();
				if(i2.hasNext())
				{
					pattern2 += ", ";
				}
			}
			pattern += ")";
			pattern2 += ")";
			// System.out.Parser.println(Parser.ERR_MISMATCH_INVALID_CALL +
			// pattern +
			// " with "+ pattern2);
			if(args.size() != givenArgs.size())
			{
				Parser.printError(ErrorType.ARITY_ERROR, Parser.sym, Parser.ERR_MISMATCH_INVALID_CALL + pattern
						+ " with " + pattern2);
			}
			else
			{
				Parser.printError(ErrorType.MISMATCH_ERROR, Parser.sym, Parser.ERR_MISMATCH_INVALID_CALL + pattern
						+ " with " + pattern2);
			}
		}
	}

	public static void returnCheck(Symbol func, Node expr) throws ParserException
	{
		/**
		 * if func is null then its the main function that is being compared to
		 * (i.e the void type)
		 */
		if(func == null)
		{
			if(!expr.getType().equals("void"))
			{
				Parser.printError(ErrorType.MISMATCH_ERROR, Parser.sym, Parser.ERR_MISMATCH_INVALID_RET + "expected = "
						+ Type.newPrimitiveType("void") + ", received = " + expr.getType());
			}
		}
		else if(func.getType() != expr.getType())
		{
			Parser.printError(ErrorType.MISMATCH_ERROR, Parser.sym, Parser.ERR_MISMATCH_INVALID_RET + "expected = "
					+ func.getType() + ", received = " + expr.getType());
		}
	}

	public static void checkForArrayError(int Check, Node lhs, Node rhs, Token op) throws ParserException
	{
		/**
		 * Check for any instances of an array type here
		 */
		if(Check == 0)
		{
			String MM = Parser.ERR_MISMATCH_INVALID_OP + op.getLexeme() + ", " + lhs.getType() + ", " + rhs.getType();
			if(arrayInstance(lhs.getType()))
			{
				if(arrayInstance(rhs.getType()))
				{
					Parser.printError(ErrorType.MISMATCH_ERROR, op, MM);
				}
				else
				{
					Parser.printError(ErrorType.MISMATCH_ERROR, op, MM);
				}
			}
			else if(arrayInstance(rhs.getType()))
			{
				Parser.printError(ErrorType.MISMATCH_ERROR, op, MM);
			}
		}
		else if(Check == 1)
		{
			if(arrayInstance(lhs.getType()))
			{
				if(arrayInstance(rhs.getType()))
				{
					Parser.printError(
							ErrorType.MISMATCH_ERROR,
							op,
							Parser.ERR_MISMATCH_INVALID_ASSIGN + arrayBase(lhs.getType()) + " <- "
									+ arrayBase(rhs.getType()));
				}
				else
				{
					Parser.printError(
							ErrorType.MISMATCH_ERROR,
							op,
							Parser.ERR_MISMATCH_INVALID_ASSIGN + arrayBase(lhs.getType()) + " <- "
									+ rhs.getType());
				}
			}
			else if(arrayInstance(rhs.getType()))
			{
				Parser.printError(ErrorType.MISMATCH_ERROR, op, Parser.ERR_MISMATCH_INVALID_ASSIGN + lhs.getType()
						+ " <- " + arrayBase(rhs.getType()));
			}
		}
	}
}
