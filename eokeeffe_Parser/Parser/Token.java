
/**
 * Only represents a token name returned by the lexical analyzer.
 *
 * A complete token is actually a 2-tuple (name, lexeme) where name is an
 * instance of this enum and lexeme is the sequence of characters in the source
 * program that matches the pattern for the token. The lexeme is stored in the
 * Scanner class.
 */
public enum Token {
  // Keywords
  ARRAY     ("array"),
  OF        ("of"),
  INPUT     ("input"),
  PRINT     ("print"),
  IF        ("if"),
  ELSE      ("else"),
  WHILE     ("while"),
  VAR       ("var"),
  CONST     ("const"),
  MAIN      ("main"),
  INT       ("int"),
  VOID      ("void"),
  STRING    ("string"),
  CHAR      ("char"),
  RETURN    ("return"),


  // Symbols
  SEMICOLON     (";"),
  PERIOD        ("."),
  L_PAREN       ("("),
  R_PAREN       (")"),
  L_BRACE       ("{"),
  R_BRACE       ("}"),
  L_BRACKET     ("["),
  R_BRACKET     ("]"),
  COLON         (":"),
  COMMA         (","),
  ASSIGN        ("="),
  ADD           ("+"),
  SUB           ("-"),
  MULT          ("*"),
  DIV           ("/"),
  EQUAL         ("=="),
  NEQ           ("!="),
  GREATER_THAN  (">"),
  GREATER_EQUAL (">="),
  LESS_THAN     ("<"),
  LESSER_EQUAL  ("<="),

  // Tokens with no default lexeme
  IDENT         (""),
  STRING_LITERAL(""),
  NUMBER        (""),

  // Special tokens
  EOF           ("<EOT>"),
  ERROR         ("ERROR");

  public final String defaultLexeme;

  private Token(String defaultLexeme) {
    this.defaultLexeme = defaultLexeme;
  }
}
