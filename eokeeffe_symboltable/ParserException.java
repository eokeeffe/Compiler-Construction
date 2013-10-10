package src;
/**
 * Encapsulates the exceptions Parser can cause.
 */
public class ParserException extends Exception {

  private static final long serialVersionUID = 1L;

  private int lineNumber;
  private int charPosition;

  public ParserException(int lineNumber, int charPosition, String message, Throwable cause) {
    super(message, cause);
    this.lineNumber = lineNumber;
    this.charPosition = charPosition;
  }

  public ParserException(int lineNumber, int charPosition, String message) {
    super(message);
    this.lineNumber = lineNumber;
    this.charPosition = charPosition;
  }

  public ParserException(int lineNumber, int charPosition, Throwable cause) {
    super(cause);
    this.lineNumber = lineNumber;
    this.charPosition = charPosition;
  }

  @Override
  public String getMessage() {
    StringBuilder sb = new StringBuilder();
    sb.append("Line ").append(lineNumber).append(" Char ").append(charPosition)
      .append(": ").append(super.getMessage());
    return sb.toString();
  }

  // --------------- Getters ---------------
  public int getLineNumber() {
    return lineNumber;
  }

  public int getCharPosition() {
    return charPosition;
  }
}
