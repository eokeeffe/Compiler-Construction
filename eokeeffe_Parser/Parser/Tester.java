
//Tester.java

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Tester
{

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{

		if (args.length < 1)
		{
			System.out
					.println("Parser Tester Usage: java Tester testFileToScan1 [testFileToScan2]");
			System.exit(1);
		}
		int score = 0;
		for (String sourceFile : args)
		{
			try
			{
				Parser p = new Parser(sourceFile);
				p.program();
			}
			catch (Parser.ParserException parser_ex)
			{
			}
			System.out.print("Test " + sourceFile + " ");
			if (filesContentEqual(sourceFile + ".out", sourceFile + ".exp"))
			{
				System.out.println("PASSED");
				score++;
			}
			else
			{
				System.out.println("* FAILED *");
			}
		}
		System.out.println("----------------------------");
		System.out.println("Score: " + score + "/" + args.length);
	}

	/**
	 * Test equality of testOut and testExp files content.
	 * 
	 * @param testOut
	 *            output of scanner
	 * @param testExp
	 *            expected output
	 * @return true or false
	 */
	public static boolean filesContentEqual(String testOut, String testExp)
	{

		try
		{
			String testOutStr = fileToString(testOut);
			String testExpStr = fileToString(testExp);

			if (testOutStr.equalsIgnoreCase(testExpStr)) return true;
		}
		catch (FileNotFoundException fnfEx)
		{
			System.out.print("\nFILE NOT FOUND: " + testOut + " ");
		}

		return false;
	}

	/**
	 * File to string
	 * 
	 * @param file
	 *            The file to be turned into a String
	 * @return The file as String encoded in the platform default encoding
	 * @throws FileNotFoundException
	 */
	public static String fileToString(String file) throws FileNotFoundException
	{

		String result = null;
		DataInputStream in = null;

		try
		{
			File f = new File(file);
			byte[] buffer = new byte[(int) f.length()];
			in = new DataInputStream(new FileInputStream(f));
			in.readFully(buffer);
			result = new String(buffer);
			result = result.replaceAll("[\\r\\n]+", " ");
		}
		catch (FileNotFoundException fnfEx)
		{
			throw fnfEx;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new RuntimeException("IO problem in fileToString", e);
		}
		finally
		{
			try
			{
				in.close();
			}
			catch (Exception e)
			{ /* ignore it */
			}
		}
		return result;
	}

}