package src;

public class Type {

  private static final Type INT = new PrimitiveType("int");
  private static final Type CHAR = new PrimitiveType("char");
  private static final Type STRING = new PrimitiveType("string");
  private static final Type VOID = new PrimitiveType("void");

  private Type() {}

  public static Type getType(String typeString)
  {
	  if(typeString.equals(Type.INT))
	  {
		  return Type.INT;
	  }
	  else if(typeString.equals(Type.CHAR))
	  {
		  return Type.CHAR;
	  } 
	  else if(typeString.equals(Type.STRING))
	  {
		  return Type.STRING;
	  }
	  else if(typeString.equals(Type.VOID))
	  {
		  return Type.VOID;
	  }
	  return null;
  }
  
  public static Type newPrimitiveType(String typeName) {
    Type res;
    if (typeName.equals("int")){ res = INT;}
    else if (typeName.equals("char")) {res = CHAR;}
    else if (typeName.equals("string")) {res = STRING;}
    else if (typeName.equals("void")) {res = VOID;}
    else {res = new PrimitiveType(typeName);}
    return res;
  }

  public static Type newArrayType(Type baseType, int dimension) {
    return new ArrayType(baseType, dimension);
  }

  // ---------- PrimitiveType static inner class ----------
  private static class PrimitiveType extends Type {
    private String name;

    public PrimitiveType(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  // ---------- ArrayType static inner class ----------
  private static class ArrayType extends Type {
    private Type baseType;
    private int dimension;

    public ArrayType(Type baseType, int dimension) {
      this.baseType = baseType;
      this.dimension = dimension;
    }

    @Override
    public String toString() {
      return "array " + dimension + " of " + baseType.toString();
    }
  }
  
 }
