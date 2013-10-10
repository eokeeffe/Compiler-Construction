/**
 * Represents a full token.
 */
public class Token {

  private TokenKind tokenKind;
  private String lexeme;
  private int lineNum;
  private int charPos;

  public Token(TokenKind tokenKind, String lexeme, int lineNum, int charPos) {
    this.tokenKind = tokenKind;
    this.lexeme = lexeme;
    this.lineNum = lineNum;
    this.charPos = charPos;
  }

  public TokenKind getTokenKind() {
    return tokenKind;
  }

  public String getLexeme() {
    return lexeme;
  }

  public int getLineNum() {
    return lineNum;
  }

  public int getCharPos() {
    return charPos;
  }
}
