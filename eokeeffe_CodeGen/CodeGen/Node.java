
public class Node
{
	private String name;
	private Type type;
	
	public Node(Type t)
	{
		type = t;
	}
	
	public Node(String lexeme,Type t)
	{
		name = lexeme;
		type = t;
	}
	
	public String getTokenName()
	{
		return name;
	}
	
	public Type getType()
	{
		return type;
	}
	
	@Override
	public String toString()
	{
		return type.toString();
	}
}
