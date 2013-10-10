
import java.util.Set;

public enum NonTerminal {

  RELOP("relop", ImmutableSetBuilder.<TokenKind>newInstance()
      .add(TokenKind.EQUAL)
      .add(TokenKind.NEQ)
      .add(TokenKind.LESS_THAN)
      .add(TokenKind.LESSER_EQUAL)
      .add(TokenKind.GREATER_THAN)
      .add(TokenKind.GREATER_EQUAL)
      .build()),
  OP1("op1", ImmutableSetBuilder.<TokenKind>newInstance()
      .add(TokenKind.MULT)
      .add(TokenKind.DIV)
      .build()),
  OP2("op2", ImmutableSetBuilder.<TokenKind>newInstance()
      .add(TokenKind.ADD)
      .add(TokenKind.SUB)
      .build()),

  SELECTOR("selector", ImmutableSetBuilder.<TokenKind>newInstance()
      .add(TokenKind.IDENT)
      .build()),
  PARAMETERS("parameters", ImmutableSetBuilder.<TokenKind>newInstance()
      .add(TokenKind.IDENT)
      .build()),
  CONDITION("condition", ImmutableSetBuilder.<TokenKind>newInstance()
      .add(TokenKind.L_PAREN)
      .build()),
  PROCEDURE_CALL("procedureCall", ImmutableSetBuilder.<TokenKind>newInstance()
      .add(TokenKind.COLON)
      .build()),

  ASSIGNMENT("assignment", ImmutableSetBuilder.<TokenKind>newInstance()
      .addAll(SELECTOR.firstSet)
      .build()),
  INPUT("input", ImmutableSetBuilder.<TokenKind>newInstance()
      .add(TokenKind.INPUT)
      .build()),
  OUTPUT("output", ImmutableSetBuilder.<TokenKind>newInstance()
      .add(TokenKind.PRINT)
      .build()),
  IF_STATEMENT("ifStatement", ImmutableSetBuilder.<TokenKind>newInstance()
      .add(TokenKind.IF)
      .build()),
  WHILE_STATEMENT("whileStatement", ImmutableSetBuilder.<TokenKind>newInstance()
      .add(TokenKind.WHILE)
      .build()),
  RETURN_STATEMENT("returnStatement", ImmutableSetBuilder.<TokenKind>newInstance()
      .add(TokenKind.RETURN)
      .build()),
  PROCEDURE_STATEMENT("procedureStatement", ImmutableSetBuilder.<TokenKind>newInstance()
      .addAll(PROCEDURE_CALL.firstSet)
      .build()),

  STATEMENT("statement", ImmutableSetBuilder.<TokenKind>newInstance()
      .addAll(ASSIGNMENT.firstSet)
      .addAll(INPUT.firstSet)
      .addAll(OUTPUT.firstSet)
      .addAll(IF_STATEMENT.firstSet)
      .addAll(WHILE_STATEMENT.firstSet)
      .addAll(RETURN_STATEMENT.firstSet)
      .addAll(PROCEDURE_STATEMENT.firstSet)
      .build()),
  STATEMENT_SEQUENCE("statementSequence", ImmutableSetBuilder.<TokenKind>newInstance()
      .addAll(STATEMENT.firstSet)
      .build()),

  FACTOR("factor", ImmutableSetBuilder.<TokenKind>newInstance()
      .addAll(SELECTOR.firstSet)
      .add(TokenKind.NUMBER)
      .addAll(PROCEDURE_CALL.firstSet)
      .add(TokenKind.L_PAREN)
      .build()),
  TERM("term", ImmutableSetBuilder.<TokenKind>newInstance()
      .addAll(FACTOR.firstSet)
      .build()),
  EXPRESSION("expression", ImmutableSetBuilder.<TokenKind>newInstance()
      .addAll(TERM.firstSet)
      .build()),

  RET_TYPE("retType", ImmutableSetBuilder.<TokenKind>newInstance()
      .add(TokenKind.INT)
      .add(TokenKind.STRING)
      .add(TokenKind.VOID)
      .add(TokenKind.CHAR)
      .build()),
  TYPE("type", ImmutableSetBuilder.<TokenKind>newInstance()
      .add(TokenKind.INT)
      .add(TokenKind.CHAR)
      .add(TokenKind.STRING)
      .add(TokenKind.ARRAY)
      .build()),

  DECLARATIONS("declarations", ImmutableSetBuilder.<TokenKind>newInstance()
      .add(TokenKind.CONST)
      .add(TokenKind.VAR)
      .build()),

  PROCEDURE_FORMAL_PARAMS("procedureFormalParams", ImmutableSetBuilder.<TokenKind>newInstance()
      .addAll(TYPE.firstSet)
      .build()),
  PROCEDURE_DECLARATIONS("procedureDeclarations", ImmutableSetBuilder.<TokenKind>newInstance()
      .addAll(RET_TYPE.firstSet)
      .build()),

  PROGRAM("program", ImmutableSetBuilder.<TokenKind>newInstance()
      .addAll(DECLARATIONS.firstSet)
      .addAll(PROCEDURE_DECLARATIONS.firstSet)
      .add(TokenKind.MAIN)
      .build());

  public final String name;
  public final Set<TokenKind> firstSet;

  NonTerminal(String name, Set<TokenKind> firstSet) {
    this.name = name;
    this.firstSet = firstSet;
  }
}
