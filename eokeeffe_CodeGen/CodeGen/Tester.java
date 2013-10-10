
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Class to test your compiler implementation.
 *
 * You may modify this file however you wish; the modifications though will be
 * ignored by the auto-grader.
 */
public class Tester {

  private static final boolean SUPPRESS_PASS = false;
  private static final boolean GENERATE_OUTPUTS = true;

  /**
   * Read file and return its content as a string.
   *
   * @param file The file to be turned into a String
   * @return The file as String encoded in the platform default encoding
   */
  private static String fileToString(String file) throws IOException {
    String result = null;
    DataInputStream in = null;

    try {
      File f = new File(file);
      byte[] buffer = new byte[(int) f.length()];
      in = new DataInputStream(new FileInputStream(f));
      in.readFully(buffer);
      result = new String(buffer);
      result = result.replaceAll("\r\n", "\n");
      result = result.replaceAll("\r", "\n");
    } finally {
      if (in != null) in.close();
    }
    return result;
  }

  /**
   * Tests the compiler implementation with the given sourceFile. The file with
   * the expected output is assumed to to have the name sourceFile + ".exp".
   */
  private static boolean isCompilerCorrect(File sourceFile, boolean generateOutputs) {
    String sourceFilename = sourceFile.getAbsolutePath();
    boolean isCorrect = false;
    try {
      Compiler compiler = new Compiler(sourceFilename);
      String result = compiler.compile();
      if (generateOutputs) {
        PrintStream out = new PrintStream(new File(sourceFilename + ".out"));
        out.print(result);
        out.close();
      }
      String expectedResult = fileToString(sourceFilename + ".exp");
      isCorrect = result.equals(expectedResult);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return isCorrect;
  }

  // ---------- main() ----------
  public static void main(String[] args) {
    if (args.length < 1) {
      System.out.println("Usage: java Tester <testFile1> <testFile2> ...");
      System.exit(1);
    }

    int score = 0;
    PrintStream out = System.out;
    for (String sourceFilename : args) {
      File sourceFile = new File(sourceFilename);
      if (isCompilerCorrect(sourceFile, GENERATE_OUTPUTS)) {
        if (! SUPPRESS_PASS) out.println("Test " + sourceFile.getName() + " PASSED");
        score++;
      } else {
        out.println("Test " + sourceFile.getName() + " ** FAILED **");
      }
    }
    out.println("Score: " + score + "/" + args.length);
    out.close();
  }
}
