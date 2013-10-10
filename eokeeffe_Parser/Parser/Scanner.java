
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;

/**
 * The lexical analyzer component of the compiler. This is responsible for
 * converting a given source file into a list of tokens.
 */
public class Scanner
{

	private LineNumberReader in;
	private String lexeme;
	private Token tokenName;
	private int lineNum;
	private int charPos;

	private int nextLineNum;
	private int nextCharPos;
	private int nextChar; // contains the next character (-1 == EOF)

	// Construct the set of keywords
	@SuppressWarnings("serial")
	private static final Map<String, Token> keywords = new HashMap<String, Token>()
	{
		{
			for (Token token : Token.values())
			{
				String lexeme = token.defaultLexeme;
				if (lexeme.length() > 0 && Character.isLetter(lexeme.charAt(0))
						&& !lexeme.equals("ERROR")) put(token.defaultLexeme,
						token);
			}
		}
	};

	// ---------- Constructors ----------
	public Scanner(String sourceFilename) throws IOException
	{

		in = new LineNumberReader(new FileReader(sourceFilename));
		lexeme = null;
		nextLineNum = 1;
		nextCharPos = 0;
		nextChar = Integer.MAX_VALUE;
		readChar();
	}

	// ---------- Main work ----------
	private void readChar() throws IOException
	{

		if (nextChar == '\n')
		{
			nextLineNum++;
			nextCharPos = 0;
		}
		nextChar = in.read();
		nextCharPos++;
	}

	/**
	 * Returns the next token in the source file.
	 * 
	 * Also updates the current lexeme, line number, and character position so
	 * subsequent access to those fields yield corresponding values for the
	 * returned token.
	 */
	public Token next() throws IOException
	{

		// Strip comments and whitespace
		while (nextChar == '#' || Character.isWhitespace(nextChar))
		{
			if (nextChar == '#')
			{
				while (nextChar != '\n' && nextChar != -1)
					readChar();
			}
			else
			{
				while (Character.isWhitespace(nextChar))
					readChar();
			}
		}

		tokenName = Token.ERROR;
		lineNum = nextLineNum;
		charPos = nextCharPos;

		// Main switch-case
		boolean shouldReadChar = true;

		switch (nextChar)
		{
			case -1:
				tokenName = Token.EOF;
				break;
			case ';':
				tokenName = Token.SEMICOLON;
				break;
			case '.':
				tokenName = Token.PERIOD;
				break;
			case '(':
				tokenName = Token.L_PAREN;
				break;
			case ')':
				tokenName = Token.R_PAREN;
				break;
			case '{':
				tokenName = Token.L_BRACE;
				break;
			case '}':
				tokenName = Token.R_BRACE;
				break;
			case '[':
				tokenName = Token.L_BRACKET;
				break;
			case ']':
				tokenName = Token.R_BRACKET;
				break;
			case ':':
				tokenName = Token.COLON;
				break;
			case ',':
				tokenName = Token.COMMA;
				break;
			case '+':
				tokenName = Token.ADD;
				break;
			case '-':
				tokenName = Token.SUB;
				break;
			case '*':
				tokenName = Token.MULT;
				break;
			case '/':
				tokenName = Token.DIV;
				break;
			case '=':
				readChar();
				if (nextChar == '=') tokenName = Token.EQUAL;
				else
				{
					tokenName = Token.ASSIGN;
					shouldReadChar = false;
				}
				break;
			case '<':
				readChar();
				if (nextChar == '=') tokenName = Token.LESSER_EQUAL;
				else
				{
					tokenName = Token.LESS_THAN;
					shouldReadChar = false;
				}
				break;
			case '>':
				readChar();
				if (nextChar == '=') tokenName = Token.GREATER_EQUAL;
				else
				{
					tokenName = Token.GREATER_THAN;
					shouldReadChar = false;
				}
				break;
			case '!':
				readChar();
				if (nextChar == '=') tokenName = Token.NEQ;
				else
				{
					tokenName = Token.ERROR;
					shouldReadChar = false;
				}
				break;
			case '"':
				tokenName = Token.STRING_LITERAL;
				StringBuilder buffer = new StringBuilder();
				do
				{
					readChar();
					buffer.append((char) nextChar);
				}
				while (nextChar != '"' && nextChar != -1);

				if (nextChar == '"') lexeme = buffer.substring(0,
						buffer.length() - 1);
				else
				{
					tokenName = Token.ERROR;
					shouldReadChar = false;
				}
				break;
		}
		this.lexeme = this.tokenName.defaultLexeme;
		// Scan identifier/keyword and number
		if (tokenName == Token.ERROR && shouldReadChar)
		{
			if (Character.isDigit(nextChar))
			{
				tokenName = Token.NUMBER;
				StringBuilder buffer = new StringBuilder();
				do
				{
					buffer.append((char) nextChar);
					readChar();
				}
				while (Character.isDigit(nextChar));
				lexeme = buffer.toString();
				shouldReadChar = false;

			}
			else if (Character.isLetter(nextChar))
			{
				StringBuilder buffer = new StringBuilder();
				do
				{
					buffer.append((char) nextChar);
					readChar();
				}
				while (Character.isLetterOrDigit(nextChar));
				lexeme = buffer.toString();
				shouldReadChar = false;

				// check for keyword
				tokenName = keywords.get(lexeme);
				if (tokenName == null) tokenName = Token.IDENT;
			}
		}

		// Get the next lookahead character
		if (shouldReadChar) readChar();
		return tokenName;
	}

	@Override
	public String toString()
	{

		String lexeme = (tokenName.defaultLexeme.length() == 0) ? ": "
				+ getLexeme() : "";
		return "(L" + getLineNum() + " C" + getCharPos() + ") " + getToken()
				+ lexeme;
	}

	// ---------- Accessors ----------
	public Token getToken()
	{

		return tokenName;
	}

	public String getLexeme()
	{

		return lexeme;
	}

	public int getLineNum()
	{

		return lineNum;
	}

	public int getCharPos()
	{

		return charPos;
	}

	public static void main(String[] args)
	{

		System.out.println(Character.isWhitespace('\n'));
	}
}
