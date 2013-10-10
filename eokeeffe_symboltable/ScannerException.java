package src;
/**
 * Encapsulates the exceptions Scanner can cause.
 */
public class ScannerException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ScannerException() {
    super();
  }

  public ScannerException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public ScannerException(String message, Throwable cause) {
    super(message, cause);
  }

  public ScannerException(String message) {
    super(message);
  }

  public ScannerException(Throwable cause) {
    super(cause);
  }
}
