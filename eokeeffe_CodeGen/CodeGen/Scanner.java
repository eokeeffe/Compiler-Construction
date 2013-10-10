
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;

/**
 * The lexical analyzer component of the compiler. This is responsible for
 * converting a given source file into a list of tokens.
 */
public class Scanner {

  private LineNumberReader in;
  private StringBuilder lexeme;
  private TokenKind tokenKind;
  private int lineNum;
  private int charPos;

  private int nextLineNum;
  private int nextCharPos;
  private int nextChar;  //contains the next character (-1 == EOF)

  // Construct the set of keywords
  @SuppressWarnings("serial")
  private static final Map<String, TokenKind> keywords = new HashMap<String, TokenKind>() {{
    for (TokenKind token : TokenKind.values()) {
      String lexeme = token.defaultLexeme;
      if (lexeme.length() > 0 && Character.isLetter(lexeme.charAt(0)) && ! lexeme.equals("ERROR"))
        put(token.defaultLexeme, token);
    }
  }};

  // ---------- Constructors ----------
  public Scanner(String sourceFilename) throws ScannerException {
    try {
      in = new LineNumberReader(new FileReader(sourceFilename));
    } catch (IOException e) {
      throw new ScannerException("Cannot open " + sourceFilename, e);
    }
    lexeme = new StringBuilder();
    nextLineNum = 1;
    nextCharPos = 0;
    nextChar = Integer.MAX_VALUE;
    readChar();
  }

  // ---------- Main work ----------
  private void readChar() throws ScannerException {
    if (nextChar == '\n') {
      nextLineNum++;
      nextCharPos = 0;
    }
    nextCharPos++;

    try {
      nextChar = in.read();
      lexeme.append((char) nextChar);
    } catch (IOException e) {
      throw new ScannerException("Cannot read next char at line=" + nextLineNum +
                                 ", charPos=" + nextCharPos, e);
    }
  }

  /**
   * Returns the next token in the source file.
   *
   * Also updates the current lexeme, line number, and character position so
   * subsequent access to those fields yield corresponding values for the
   * returned token.
   */
  public TokenKind next() throws ScannerException {
    // Strip comments and whitespace
    while (nextChar == '#' || Character.isWhitespace(nextChar)) {
      if (nextChar == '#') {
        while (nextChar != '\n' && nextChar != -1) readChar();
      } else {
        while (Character.isWhitespace(nextChar)) readChar();
      }
    }

    lexeme.setLength(0); lexeme.append((char) nextChar);
    tokenKind = TokenKind.ERROR;
    lineNum = nextLineNum;
    charPos = nextCharPos;

    // Main switch-case
    boolean shouldReadChar = true;

    switch (nextChar) {
      case -1:
        tokenKind = TokenKind.EOF;
        break;
      case ';':
        tokenKind = TokenKind.SEMICOLON;
        break;
      case '.':
        tokenKind = TokenKind.PERIOD;
        break;
      case '(':
        tokenKind = TokenKind.L_PAREN;
        break;
      case ')':
        tokenKind = TokenKind.R_PAREN;
        break;
      case '{':
        tokenKind = TokenKind.L_BRACE;
        break;
      case '}':
        tokenKind = TokenKind.R_BRACE;
        break;
      case '[':
        tokenKind = TokenKind.L_BRACKET;
        break;
      case ']':
        tokenKind = TokenKind.R_BRACKET;
        break;
      case ',':
        tokenKind = TokenKind.COMMA;
        break;
      case '+':
        tokenKind = TokenKind.ADD;
        break;
      case '-':
        tokenKind = TokenKind.SUB;
        break;
      case '*':
        tokenKind = TokenKind.MULT;
        break;
      case '/':
        tokenKind = TokenKind.DIV;
        break;
      case ':':
        tokenKind = TokenKind.COLON;
        break;
      case '=':
        readChar();
        if (nextChar == '=') tokenKind = TokenKind.EQUAL;
        else { tokenKind = TokenKind.ASSIGN; shouldReadChar = false; }
        break;
      case '<':
        readChar();
        if (nextChar == '=') tokenKind = TokenKind.LESSER_EQUAL;
        else { tokenKind = TokenKind.LESS_THAN; shouldReadChar = false; }
        break;
      case '>':
        readChar();
        if (nextChar == '=') tokenKind = TokenKind.GREATER_EQUAL;
        else { tokenKind = TokenKind.GREATER_THAN; shouldReadChar = false; }
        break;
      case '!':
        readChar();
        if (nextChar == '=') tokenKind = TokenKind.NEQ;
        else { tokenKind = TokenKind.ERROR; shouldReadChar = false; }
        break;
      case '"':
        tokenKind = TokenKind.STRING_LITERAL;
        lexeme.setLength(0);                                            // remove opening quote
        do {
          readChar();
        } while (nextChar != '"' && nextChar != -1);

        if (nextChar == '"') lexeme.deleteCharAt(lexeme.length() - 1);  // remove ending quote
        else { tokenKind = TokenKind.ERROR; shouldReadChar = false; }
        break;
    }

    // Scan identifier/keyword and number
    if (tokenKind == TokenKind.ERROR && shouldReadChar) {
      if (Character.isDigit(nextChar)) {
        tokenKind = TokenKind.NUMBER;
        do {
          readChar();
        } while (Character.isDigit(nextChar));
        shouldReadChar = false;

      } else if (Character.isLetter(nextChar)) {
        do {
          readChar();
        } while (Character.isLetterOrDigit(nextChar));
        shouldReadChar = false;
        // check for keyword
        String identifier = lexeme.substring(0, lexeme.length() - 1);
        tokenKind = keywords.get(identifier);
        if (tokenKind == null) tokenKind = TokenKind.IDENT;
      }
    }

    // Get the next lookahead character
    if (shouldReadChar) readChar();
    lexeme.deleteCharAt(lexeme.length() - 1);

    return tokenKind;
  }

  @Override
  public String toString() {
    return "(L" + getLineNum() + " C" + getCharPos() + ") " + getTokenKind() + getLexeme();
  }

  // ---------- Accessors ----------
  public TokenKind getTokenKind() {
    return tokenKind;
  }

  public String getLexeme() {
    if (tokenKind == TokenKind.EOF) return "";
    return lexeme.toString();
  }

  public int getLineNum() {
    return lineNum;
  }

  public int getCharPos() {
    return charPos;
  }

  public Token getToken() {
    return new Token(getTokenKind(), getLexeme(), getLineNum(), getCharPos());
  }
}
