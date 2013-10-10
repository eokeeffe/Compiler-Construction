
import java.io.IOException;
import java.io.PrintStream;
import java.io.*;

public class TesterLab5And6 {

  // TODO: Path to your spim executable
  private static final String SPIM_EXE = "/CG/spimdos.exe";

  // TODO: Number of header lines in your spim output
  private static final int SPIM_NUM_HEADER_LINES = 5;

  // TODO: you may need to adjust this if your computer is slower. This is the
  // amount of time (in milliseconds) for spim to generate its output. Setting
  // it too high though will make your tests run very slowly.
  private static final int SPIM_WAIT_TIME = 10;

  // Whether to suppress the PASS message for test cases that pass.
  private static final boolean SUPPRESS_PASS = false;

  // ---------- Start of Tester ----------
  private static String fileToString(String file, int skipLines) throws IOException {
    StringBuilder res = new StringBuilder();

    java.util.Scanner in = null;
    try {
      in = new java.util.Scanner(new File(file));
      int linesRead = 0;
      while (in.hasNextLine()) {
        String line = in.nextLine();
        if (linesRead >= skipLines) {
          res.append(line).append("\n");
        }
        linesRead++;
      }
    } finally {
      if (in != null) in.close();
    }

    return res.toString();
  }

  private static boolean testCompiler(File testFile) {
    String testFilename = testFile.getAbsolutePath();
    String inputFilename = testFilename.replaceAll(".tst$", ".in");
    String expectedOutputFilename = testFilename.replaceAll(".tst$", ".out");

    String asmFilename = testFilename + ".asm";
    String outputFilename = testFilename + ".out";

    boolean isCorrect = false;
    try {
    	PrintStream out = new PrintStream(new File(asmFilename));;
      Compiler compiler = new Compiler(testFilename);
      String result = compiler.compile();

      // Outputting .asm file
      
      out.print(result);
      out.close();

      // Executing spim
      /*ProcessBuilder pb = new ProcessBuilder(SPIM_EXE, "-file", asmFilename);
      pb.redirectErrorStream(true);
      pb.redirectInput(new File(inputFilename));
      pb.redirectOutput(new File(outputFilename));
      pb.start();*/

      // Wait a bit for output to be ready
      Thread.sleep(SPIM_WAIT_TIME);

      // Comparing results
      String expectedOutput = fileToString(expectedOutputFilename, 0);
      String output = fileToString(outputFilename, SPIM_NUM_HEADER_LINES);
      if (expectedOutput.equals(output)) isCorrect = true;
      else {
        String expectedString = expectedOutput.replaceAll("\\n", "\\\\n");
        String outputString = output.replaceAll("\\n", "\\\\n");
        System.out.println();
        System.out.println(testFile.getName() + " expected: " + expectedString);
        System.out.println(testFile.getName() + " received: " + outputString);
      }

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
      File testFile = new File(sourceFilename);
      if (testCompiler(testFile)) {
        if (! SUPPRESS_PASS) out.println("Test " + testFile.getName() + " PASSED");
        score++;
      } else {
        out.println("Test " + testFile.getName() + " ** FAILED **");
      }
    }
    out.println("Score: " + score + "/" + args.length);
  }
}
