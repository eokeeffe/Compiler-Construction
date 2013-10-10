package src;

import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;

/**
 * The lexical analyzer component of the compiler. This is responsible for
 * converting a given source file into a list of tokens.
 */
public class Scanner
{

	private PushbackReader in;
	private StringBuilder lexeme;
	private String lex;
	private Token tokenName;
	private int lineNum; // Not really necessary for this lab, but will be
							// helpful when
	private int charPos; // debugging later labs.

	private int nextLineNum;
	private int nextCharPos;
	private int nextChar; // contains the next character (-1 == EOF)
	private int EOF = -1;

	public Scanner(String sourceFilename) throws IOException
	{

		in = new PushbackReader(new FileReader(sourceFilename));
		// lexeme = new StringBuilder();

		// initialize nextChar, nextLineNum, nextCharPos and possibly other
		// fields

		lineNum = 0;
		charPos = 0;
		nextChar = 0;
		nextLineNum = 1;
		nextCharPos = 1;
	}

	private void readchar()
	{

		/**
		 * Read in a character from the current input stream and if EOF then
		 * print the EOF token in the file
		 */
		try
		{
			nextChar = in.read();
		}
		catch (IOException ioe)
		{
			System.err.println("Exception while reading in character");
			System.exit(1);
		}
		switch (nextChar)
		{
			case -1:
			{
				nextChar = EOF;
				++charPos;
				// lexeme.append((char) nextChar);
				// lex += (char) nextChar;
				break;
			}
			case 65535:
			{
				// this is an error case , as the pushback reader
				// has difficulty dealing with files that don't have an
				// EOF in the file it will return 65535 which is
				// an overflow that results in -1 for reading
				System.err.println("File Doesn't contain an EOF");
				nextChar = EOF;
				break;
			}
			case '\n':
			{
				++lineNum;
				++nextLineNum;
				charPos = 0;
				break;
			}
			case '\t':
			case '\r':
			{
				break;
			}
			default:
			{
				++charPos;
				++nextCharPos;
				// lexeme.append((char) nextChar);
				lex += (char) nextChar;
			}
		}
	}

	private void unreadchar()
	{

		/**
		 * Push back a character into the front of the buffered stream
		 */
		switch (nextChar)
		{
			case '\n':
			{
				--lineNum;
				--nextLineNum;
				break;
			}
			default:
			{
				--charPos;
				--nextCharPos;
			}
		}

		lex = (String) lex.subSequence(0, lex.length() - 1);

		// System.out.println(lexeme+"::Last character at:"+lexeme.length());
		try
		{
			in.unread(nextChar);
		}
		catch (IOException ioe)
		{
			System.err.println("Exception while reading in character");
			System.exit(1);
		}
	}

	/**
	 * Returns the next token in the source file.
	 * 
	 * Also updates the current lexeme, line number, and character position so
	 * subsequent access to those fields yield corresponding values for the
	 * returned token.
	 */
	@SuppressWarnings("unused")
	public Token next() throws IOException
	{
		/**
		 * After the first character of the current token has been read ,
		 * go through the keyword statements before going to ident if the first character
		 * is a letter . If a number then process until something not a number comes up
		 */
		lex = "";
		readchar();

		// label the main loop to read in the tokens
		StateMachine: while (nextChar != EOF)
		{
			switch ((char) nextChar)
			{
				case ' ':
				case '\t':
				case '\r':
				case '\n':
				{
					// eat the whitespace,tab,newline characters
					readchar();
					continue;
				}
				case 'a':{return process_a();}
				case 'c':{return process_c();}
				case 'e':{return process_e();}
				case 'i':{return process_i();}
				case 'm':{return process_m();}
				case 'o':{return process_o();}
				case 'p':{return process_p();}
				case 'r':{return process_r();}
				case 's':{return process_s();}
				case 'v':{return process_v();}
				case 'w':{return process_w();}
				case '!':
				{
					readchar();
					if (nextChar == '=')
					{
						writeToLexeme();
						return Token.NEQ;
					}
					else
					{
						writeToLexeme();
						return Token.ERROR;
					}
				}
				case '+':{writeToLexeme();return Token.ADD;}
				case '-':{writeToLexeme(); return Token.SUB;}
				case '*':{writeToLexeme();return Token.MULT;}
				case '/':{writeToLexeme();return Token.DIV;}
				case '=':
				{
					readchar();
					if (nextChar != '=')
					{
						unreadchar();
						writeToLexeme();
						return Token.ASSIGN;
					}
					else
					{
						writeToLexeme();
						return Token.EQUAL;
					}
				}
				case '<':
				{
					readchar();
					if (nextChar == '=')
					{
						writeToLexeme();
						return Token.LESSER_EQUAL;
					}
					else if (Character.isWhitespace(nextChar))
					{
						writeToLexeme();
						return Token.LESS_THAN;
					}
					else if (!Character.isLetterOrDigit(nextChar))
					{
						unreadchar();
						return Token.LESS_THAN;
					}
					else
					{
						unreadchar();
						writeToLexeme();
						return Token.LESS_THAN;
					}

				}
				case '>':
				{
					readchar();
					if (nextChar == '=')
					{
						writeToLexeme();
						return Token.GREATER_EQUAL;
					}
					else if (Character.isWhitespace(nextChar))
					{
						writeToLexeme();
						return Token.GREATER_THAN;
					}
					else if (!Character.isLetterOrDigit(nextChar))
					{
						unreadchar();
						return Token.GREATER_THAN;
					}
					else
					{
						unreadchar();
						writeToLexeme();
						return Token.GREATER_THAN;
					}
				}
				case '{':
				{writeToLexeme();return Token.L_BRACE;}
				case '}':{writeToLexeme();return Token.R_BRACE;}
				case '(':{writeToLexeme();return Token.L_PAREN;}
				case ')':{writeToLexeme();return Token.R_PAREN;}
				case '[':{writeToLexeme();return Token.L_BRACKET;}
				case ']':{writeToLexeme();return Token.R_BRACKET;}
				case '"':{writeToLexeme();return Token.STRING_LITERAL;}
				case ':':{writeToLexeme();return Token.COLON;}
				case ';':{writeToLexeme();return Token.SEMICOLON;}
				case '.':{writeToLexeme();return Token.PERIOD;}
				case ',':{writeToLexeme();return Token.COMMA;}
				case '0':// deal with a number
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
				{
					return process_number();
				}
				default:
				{
					return ident();
				}
			}
		}
		if (nextChar == EOF)
		{
			writeToLexeme();
			return Token.EOF;
		}

		return Token.ERROR;
	}

	public Token process_i()
	{
		/**
		 * Possible keywords 
		 * if
		 * int
		 * input
		 */
		readchar();
		if (nextChar == 'n')
		{
			// input,int
			// else treat as ident
			readchar();
			if (nextChar == 'p')
			{
				readchar();
				if (nextChar == 'u')
				{
					readchar();
					if (nextChar == 't')
					{
						readchar();
						if (Character.isWhitespace(nextChar))
						{
							// definetly an input with a space
							// before
							writeToLexeme();
							return Token.INPUT;
						}
						else if (nextChar == '(')
						{
							unreadchar();
							// definetly an input
							writeToLexeme();
							return Token.INPUT;
						}
						else if (!Character.isLetterOrDigit(nextChar))
						{
							unreadchar();
							writeToLexeme();
							return Token.INPUT;
						}
						else
						{
							return ident();
						}
					}
					else
					{
						return ident();
					}
				}
				else
				{
					return ident();
				}

			}
			else if (nextChar == 't')
			{
				readchar();
				if (Character.isWhitespace(nextChar))
				{
					// definetly an int
					writeToLexeme();
					return Token.INT;
				}
				else if (!Character.isLetterOrDigit(nextChar))
				{
					// definetly an int
					unreadchar();
					writeToLexeme();
					return Token.INT;
				}
				else
				{
					return ident();
				}
			}
			else
			{// not an input or int
				return ident();
			}
		}
		else if (nextChar == 'f')
		{// if statement
			readchar();
			if (Character.isWhitespace(nextChar))
			{
				writeToLexeme();
				return Token.IF;
			}
			else if (nextChar == '(')
			{
				unreadchar();
				writeToLexeme();
				return Token.IF;
			}
			else if (!Character.isLetterOrDigit(nextChar))
			{
				unreadchar();
				writeToLexeme();
				return Token.IF;
			}
			else
			{
				return ident();
			}
		}
		else
		{// begins with i no , more matches
			return ident();
		}
	}

	public Token process_p()
	{
		/**
		 * Possible keywords
		 * print
		 */
		readchar();
		if (nextChar == 'r')
		{
			readchar();
			if (nextChar == 'i')
			{
				readchar();
				if (nextChar == 'n')
				{
					readchar();
					if (nextChar == 't')
					{
						readchar();
						if (Character.isWhitespace(nextChar))
						{// print statement
							writeToLexeme();
							return Token.PRINT;
						}
						else if (nextChar == '(')
						{// print statement
							unreadchar();
							writeToLexeme();
							return Token.PRINT;
						}
						else if (!Character.isLetterOrDigit(nextChar))
						{
							unreadchar();
							writeToLexeme();
							return Token.PRINT;
						}
						else
						{// not a print statement
							return ident();
						}
					}
					else
					{
						return ident();
					}
				}
				else
				{
					return ident();
				}
			}
			else
			{
				return ident();
			}
		}
		else
		{
			return ident();
		}
	}

	public Token process_e()
	{
		/**
		 * Possible keywords
		 * else
		 */
		readchar();
		if (nextChar == 'l')
		{
			readchar();
			if (nextChar == 's')
			{
				readchar();
				if (nextChar == 'e')
				{
					readchar();
					if (Character.isWhitespace(nextChar))
					{
						writeToLexeme();
						return Token.ELSE;
					}
					else if (nextChar == '(')
					{
						unreadchar();
						writeToLexeme();
						return Token.ELSE;
					}
					else if (!Character.isLetterOrDigit(nextChar))
					{
						unreadchar();
						writeToLexeme();
						return Token.ELSE;
					}
					else
					{
						return ident();
					}
				}
				else
				{
					return ident();
				}
			}
			else
			{
				return ident();
			}
		}
		else
		{
			return ident();
		}
	}

	public Token process_w()
	{
		/**
		 * Possible keywords
		 * while
		 */
		readchar();
		if (nextChar == 'h')
		{
			readchar();
			if (nextChar == 'i')
			{
				readchar();
				if (nextChar == 'l')
				{
					readchar();
					if (nextChar == 'e')
					{
						readchar();
						if (Character.isWhitespace(nextChar))
						{// more than likely a while statement
							writeToLexeme();
							return Token.WHILE;
						}
						else if (nextChar == '(')
						{// definetly a while statement
							unreadchar();
							writeToLexeme();
							return Token.WHILE;
						}
						else if (!Character.isLetterOrDigit(nextChar))
						{
							unreadchar();
							writeToLexeme();
							return Token.WHILE;
						}
						else
						{
							return ident();
						}
					}
					else
					{
						return ident();
					}
				}
				else
				{
					return ident();
				}
			}
			else
			{
				return ident();
			}
		}
		else
		{
			return ident();
		}
	}

	public Token process_c()
	{
		/**
		 * Possible keywords
		 * char
		 * const
		 */
		readchar();
		if (nextChar == 'h')
		{
			readchar();
			if (nextChar == 'a')
			{
				readchar();
				if (nextChar == 'r')
				{
					readchar();
					if (Character.isWhitespace(nextChar)
							|| !Character.isLetterOrDigit(nextChar))
					{
						writeToLexeme();
						return Token.CHAR;
					}
					else
					{
						return ident();
					}
				}
				else
				{
					return ident();
				}
			}
			else
			{
				return ident();
			}
		}
		else if (nextChar == 'o')
		{
			readchar();
			if (nextChar == 'n')
			{
				readchar();
				if (nextChar == 's')
				{
					readchar();
					if (nextChar == 't')
					{
						readchar();
						if (Character.isWhitespace(nextChar)
								|| !Character.isLetterOrDigit(nextChar))
						{
							writeToLexeme();
							return Token.CONST;
						}
						else
						{
							return ident();
						}
					}
					else
					{
						return ident();
					}
				}
				else
				{
					return ident();
				}
			}
			else
			{
				return ident();
			}
		}
		else
		{
			return ident();
		}
	}

	public Token process_r()
	{
		/**
		 * Possible keywords
		 * return
		 */
		readchar();
		if (nextChar == 'e')
		{
			readchar();
			if (nextChar == 't')
			{
				readchar();
				if (nextChar == 'u')
				{
					readchar();
					if (nextChar == 'r')
					{
						readchar();
						if (nextChar == 'n')
						{
							readchar();
							if (Character.isWhitespace(nextChar)
									|| !Character.isLetterOrDigit(nextChar))
							{
								writeToLexeme();
								return Token.RETURN;
							}
							else
							{
								return ident();
							}
						}
						else
						{
							return ident();
						}
					}
					else
					{
						return ident();
					}
				}
				else
				{
					return ident();
				}
			}
			else
			{
				return ident();
			}
		}
		else
		{
			return ident();
		}
	}

	public Token process_s()
	{
		/**
		 * Possible keywords
		 * string
		 */
		readchar();
		if (nextChar == 't')
		{
			readchar();
			if (nextChar == 'r')
			{
				readchar();
				if (nextChar == 'i')
				{
					readchar();
					if (nextChar == 'n')
					{
						readchar();
						if (nextChar == 'g')
						{
							readchar();
							if (Character.isWhitespace(nextChar)
									|| !Character.isLetterOrDigit(nextChar))
							{
								writeToLexeme();
								return Token.STRING;
							}
							else
							{
								return ident();
							}
						}
						else
						{
							return ident();
						}
					}
					else
					{
						return ident();
					}
				}
				else
				{
					return ident();
				}
			}
			else
			{
				return ident();
			}
		}
		else
		{
			return ident();
		}
	}

	public Token process_v()
	{
		/**
		 * Possible keywords
		 * var
		 * void
		 */
		readchar();
		if (nextChar == 'o')
		{
			readchar();
			if (nextChar == 'i')
			{
				readchar();
				if (nextChar == 'd')
				{
					readchar();
					if (Character.isWhitespace(nextChar)
							|| !Character.isLetterOrDigit(nextChar))
					{
						writeToLexeme();
						return Token.VOID;
					}
					else
					{
						return ident();
					}
				}
				else
				{
					return ident();
				}
			}
			else
			{
				return ident();
			}
		}
		else if (nextChar == 'a')
		{
			readchar();
			if (nextChar == 'r')
			{
				readchar();
				if (Character.isWhitespace(nextChar)
						|| !Character.isLetterOrDigit(nextChar))
				{
					writeToLexeme();
					return Token.VAR;
				}
				else
				{
					return ident();
				}
			}
			else
			{
				return ident();
			}
		}
		else
		{
			return ident();
		}
	}

	public Token process_a()
	{
		/**
		 * Possible keywords
		 * array
		 */
		readchar();
		if (nextChar == 'r')
		{
			readchar();
			if (nextChar == 'r')
			{
				readchar();
				if (nextChar == 'a')
				{
					readchar();
					if (nextChar == 'y')
					{
						readchar();
						if (Character.isWhitespace(nextChar)
								|| !Character.isLetterOrDigit(nextChar))
						{
							writeToLexeme();
							return Token.ARRAY;
						}
						else if (nextChar == '[')
						{
							unreadchar();
							writeToLexeme();
							return Token.ARRAY;
						}
						else
						{
							return ident();
						}
					}
					else
					{
						return ident();
					}
				}
				else
				{
					return ident();
				}
			}
			else
			{
				return ident();
			}
		}
		else
		{
			return ident();
		}
	}

	public Token process_m()
	{
		/**
		 * Possible keywords
		 * main
		 */
		readchar();
		if (nextChar == 'a')
		{
			readchar();
			if (nextChar == 'i')
			{
				readchar();
				if (nextChar == 'n')
				{
					readchar();
					if (Character.isWhitespace(nextChar))
					{
						writeToLexeme();
						return Token.MAIN;
					}
					else if (nextChar == '(')
					{
						unreadchar();
						writeToLexeme();
						return Token.MAIN;
					}
					else if (!Character.isLetterOrDigit(nextChar))
					{
						unreadchar();
						writeToLexeme();
						return Token.MAIN;
					}
					else
					{
						return ident();
					}
				}
				else
				{
					return ident();
				}
			}
			else
			{
				return ident();
			}
		}
		else
		{
			return ident();
		}
	}

	public Token process_o()
	{
		/**
		 * Possible keywords
		 * of
		 */
		// of Type keyword
		readchar();
		if (nextChar == 'f')
		{
			writeToLexeme();
			return Token.OF;
		}
		else
		{
			return ident();
		}
	}

	public Token ident()
	{
		/**
		 * dealing with ident token
		 * can be a letter{letter|digits}
		 */
		while (Character.isLetterOrDigit(nextChar) && (nextChar != EOF))
		{
			readchar();
		}
		if ((!Character.isLetterOrDigit(lex.charAt(lex.length() - 1))))
		{
			unreadchar();
		}
		// System.out.println(lex+"::Last character at:"+lex.length());
		writeToLexeme();
		return Token.IDENT;
	}

	public Token process_number()
	{
		/**
		 * Process a number which can be
		 * digit{digit}
		 */
		while (Character.isDigit(nextChar))
		{
			readchar();
		}
		// check to make sure that the last character is a digit
		if (Character.isDigit(lex.charAt(lex.length() - 1)))
		{
			writeToLexeme();
			// System.out.println("No Pushback:"+lex);
			return Token.NUMBER;
		}
		else
		{
			unreadchar();// unread the last character back to the
							// front of the buffer
			writeToLexeme();
			// System.out.println("Pushback:"+lex);
			return Token.NUMBER;
		}
	}
	
	public void writeToLexeme()
	{
		/**
		 * Problem with the stringbuilder in my version
		 * safer to use this to prevent over strenous use
		 * of arraycopy which is memory heavy function
		 */
		String temp = "";
		for (int i = 0; i < lex.length(); i++)
		{
			if (!Character.isWhitespace(lex.charAt(i)))
			{
				// System.out.println(lex.charAt(i));
				temp += lex.charAt(i);
			}
		}
		lexeme = new StringBuilder(temp);
	}

	@Override
	public String toString()
	{

		return tokenName + ": " + getLexeme() + " (L" + getLineNum() + ",C"
				+ getCharPos() + ")";
	}

	// ---------- Accessors ----------
	public Token getToken()
	{

		return tokenName;
	}

	public String getLexeme()
	{

		return lexeme.toString();
	}

	public int getLineNum()
	{

		return lineNum;
	}

	public int getCharPos()
	{

		return charPos;
	}
}
