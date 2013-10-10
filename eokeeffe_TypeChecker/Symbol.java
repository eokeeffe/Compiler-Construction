
import java.util.ArrayList;

/**
 * Represents a symbol in the symbol table.
 * 
 * A symbol is composed of four fields: name, type, kind, value. Depending on
 * the symbol's kind (variable, constant, procedure, or type), some of the
 * fields may be null. For a procedure symbol, the type is the return type of
 * the procedure.
 */
public class Symbol
{

	public enum Kind
	{
		VAR, CONST, PROCEDURE, TYPE;
	}

	private String name;
	private Type type;
	private Kind kind;
	private String value;
	private ArrayList<Symbol> arguements;

	private Symbol(String name, Type type, Kind kind, String value)
	{
		this.name = name;
		this.type = type;
		this.kind = kind;
		this.value = value;
		// so the memory doesn't go into overdrive with each var
		if(this.kind == Symbol.Kind.PROCEDURE)
		{
			arguements = new ArrayList<Symbol>();
		}
	}

	public Symbol()
	{
		// empty constructor
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("N: ").append(name).append("  ");
		sb.append("C: ").append(kind).append("  ");
		if(type != null)
		{
			sb.append("T: ").append(type).append("  ");
		}
		if(value != null)
		{
			sb.append("V: ").append(value).append("  ");
		}
		return sb.toString();
	}

	// ----- Factory methods -----
	public static Symbol newVarSymbol(String name, Type type)
	{
		return new Symbol(name, type, Kind.VAR, null);
	}

	public static Symbol newConstSymbol(String name, Type type,
			String constValue)
	{
		return new Symbol(name, type, Kind.CONST, constValue);
	}

	public static Symbol newProcedureSymbol(String name, Type retType)
	{
		return new Symbol(name, retType, Kind.PROCEDURE, null);
	}

	public static Symbol newTypeSymbol(String name)
	{
		return new Symbol(name, null, Kind.TYPE, null);
	}

	// ----- Setters -----
	public void addArguements(ArrayList<Symbol> args)
	{
		/**
		 * Used for setting the arguements of a procedure
		 */
		for (Symbol c : args)
		{
			arguements.add(c);
		}
	}

	// ----- Getters -----
	public String getName()
	{
		return name;
	}

	public Type getType()
	{
		return type;
	}

	public Kind getKind()
	{
		return kind;
	}

	public String getValue()
	{
		return value;
	}

	public ArrayList<Symbol> getArguements()
	{
		return arguements;
	}
}
