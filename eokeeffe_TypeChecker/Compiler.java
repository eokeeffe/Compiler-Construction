import java.io.IOException;

/**
 * The main class of the compiler you will build.
 *
 * Be sure to modify the static member fields with your name, student ID, and
 * UCInet ID, so that the auto-grader can correctly attribute the submission to
 * you.
 */
@SuppressWarnings("unused")
public class Compiler {

  // TODO: correct with your information
  public static String studentName = "Evan O'keeffe";
  public static String studentID = "";
  public static String uciNetID = "";

  private String sourceFilename;

  public Compiler(String sourceFilename) throws IOException {
    this.sourceFilename = sourceFilename;
  }

  private String tokenToString(TokenKind token, Scanner scanner) {
    if (token.defaultLexeme.length() == 0)
      return token.toString().toLowerCase() + ":" + scanner.getLexeme();
    else
      return token.defaultLexeme;
  }

  /**
   * Lab 1: Returns a string listing all the tokens in the program.
   */
  private String computeTokens() {
    StringBuilder sb = new StringBuilder();
    Scanner scanner = new Scanner(sourceFilename);

    TokenKind token = scanner.next();
    while (token != TokenKind.EOF && token != TokenKind.ERROR) {
      sb.append(tokenToString(token, scanner)).append("\n");
      token = scanner.next();
    }
    sb.append(tokenToString(token, scanner)).append("\n");  // append the EOF or ERROR

    return sb.toString();
  }

  /**
   * Lab 1: Returns a string listing all the tokens (plus their line number and
   *        char position) in the program.
   */
  private String computeFullTokens() {
    StringBuilder sb = new StringBuilder();
    Scanner scanner = new Scanner(sourceFilename);

    TokenKind token = scanner.next();
    while (token != TokenKind.EOF && token != TokenKind.ERROR) {
      sb.append(scanner).append("\n");
      token = scanner.next();
    }
    sb.append(scanner).append("\n");  // append the EOF or ERROR

    return sb.toString();
  }

  /**
   * Lab 2: Returns a string representation of the recursive descent parse tree.
   */
  private String computeParseTree() {
    Parser parser = new Parser(false);
    try {
      parser.parse(sourceFilename);
    }
    catch (ParserException e) {/*System.out.println("ParserException: " + e.getMessage());*/}

    String ret = parser.getParseTree();
    for (String error : parser.getErrors()) {
      ret += error + "\n";
    }
    return ret;
  }

  /**
   * Lab 3: Returns a string representation of the symbol table. If an error was
   *        found, only the symbol table up to the error is printed.
   */
  private String computeSymbolTable() {
    Parser parser = new Parser();
    try {
      parser.parse(sourceFilename);
    } catch (ParserException e) {
    	e.getStackTrace();
      //System.out.println("ParserException: " + e.getMessage());
    }

    // Print the symbol table or error
    if (parser.getErrors().isEmpty()) {
      return parser.getSymbolTable().toString() + "\n";
    } else {
       String ret = "";
       for (String error : parser.getErrors()) {
         ret += error + "\n";
       }
       return ret;
    }
  }

  /**
   * Compiles the source file and returns the output of the compilation. This
   * is the method called by Tester.java to test your implementation.
   */
  public String compile() {
    //return computeTokens();
    //return computeFullTokens();
    //return computeParseTree();
    return computeSymbolTable();
  }
}
