

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Represents the symbol table of the compiler.
 */
public class SymbolTable
{
	
	/* parent scope of current table */
	private SymbolTable parent;
	/* symbols and names in current scope */
	private Map<String, Symbol> table;
	/* for getting the scope depth */
	private int recursiondepth;

	public SymbolTable()
	{
		/**
		 * we want to be able to write the root so instead of looking for a null
		 * we get an empty string or the parent is the child (concept is that of
		 * adam/eve neither have a parent as they are the first)
		 */
		this(0);
		// set the parent to the callers object
		this.parent = null;
		// define a new scope for variables
		this.table = new LinkedHashMap<String, Symbol>();
	}

	public SymbolTable(int recursiondepth)
	{
		/** for setting the scope depth */
		this.recursiondepth = recursiondepth;
	}

	public SymbolTable(SymbolTable parent)
	{
		/**
		 * the recursion to that of the parent + 1 set the parent to the callers
		 * object define a new scope for variables
		 */
		this(parent.recursiondepth + 1);
		this.parent = parent;
		this.table = new LinkedHashMap<String, Symbol>();
	}

	public boolean addSymbol(Symbol s)
	{
		/**
		 * Tries to add the Symbol s to the current scope in the symbol table.
		 * Returns true on success or false if the symbol already exists in the
		 * table.
		 */
		String realname = safeName(s.getName(), s.getKind());
		Symbol symbol = table.get(realname);
		if(symbol != null) { return false; }
		table.put(realname, s);
		return true;
	}

	public Symbol getSymbol(String name, Symbol.Kind kind)
	{

		/**
		 * Returns the symbol with the given name and kind in the current scope,
		 * or null if such a symbol does not exist.
		 */
		String sname = safeName(name, kind);
		SymbolTable p = this;
		while (p != null)
		{
			if(p.table.get(sname) != null)
			{
				Symbol temp = p.table.get(sname);
				return temp;
			}
			p = p.getParent();
		}
		return null;
	}

	public SymbolTable newScope()
	{
		/**
		 * Create a new scope in the symbol table and set it as the current
		 * scope.
		 * The new table will have the current symboltable as its parent
		 */
		return new SymbolTable(this);
	}

	public SymbolTable exitScope()
	{
		/**
		 * Delete the current scope and return to the current scope's parent scope.
		 * return the parent of the current table
		 */
		return this.parent;
	}

	public SymbolTable getParent()
	{
		/**
		 * return the parent node or the caller object
		 */
		return this.parent;
	}

	@Override
	public String toString()
	{

		/**
		 * Print the symbol table according to insertion order i.e parent of the
		 * program main is first and then proceeding functions are after
		 */
		StringBuilder res = new StringBuilder(
				"***** Symbol Table Contents *****\n");

		Stack<SymbolTable> parents = new Stack<SymbolTable>();

		SymbolTable p = this.parent;
		// push the current symboltable as the first
		parents.push(this);
		while (p != null)
		{
			// get all the rest of the parent symboltables
			// into the stack
			parents.push(p);
			p = p.getParent();
		}

		String arrow = "-->";
		int depth = 0;
		while (!parents.isEmpty())
		{
			p = parents.pop();
			String indent = new String();
			// for each of the depth add arrows to
			// show what scope the variables are in
			for (int i = 0; i < depth; i++)
			{
				indent += arrow;
			}
			// append the scoping arrows
			for (Symbol s : p.table.values())
			{
				res.append(indent + s.toString() + "\n");
			}
			depth++;
		}
		// System.err.println(res);
		return res.toString();
	}

	private String safeName(String string, Symbol.Kind kind)
	{
		/**
		 * For safe searching of symbols in the table we need to make sure
		 * symbols are properly identified so if a symbol has both a
		 * procedure/var we need to have a better key to search for
		 */

		if(kind == Symbol.Kind.PROCEDURE)
		{
			return string + "::procedure";// if symbol is a procedure
		}
		else
		{
			return string + "::var";// if symbol is anything but a procedure
		}
	}
}
