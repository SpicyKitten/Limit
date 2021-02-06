package limit;

public class Token
{
	public static final Token T_CARAT = new Token("^");
	public static final Token T_STAR = new Token("*");
	public static final Token T_LPAREN = new Token("(");
	public static final Token T_RPAREN = new Token(")");
	public static final Token T_LBRACK = new Token("[");
	public static final Token T_RBRACK = new Token("]");
	public static final Token T_LCURLY = new Token("{");
	public static final Token T_RCURLY = new Token("}");
	public static final Token T_PLUS = new Token("+");
	public static final Token T_MINUS = new Token("-");
	public static final Token T_SLASH = new Token("/");
	public static final Token T_MOD = new Token("%");
	public static final Token T_EQUALS = new Token("=");
	public static final Token T_QUOTE = new Token("\"");
	public static final Token T_BACK = new Token("\\");
	public static final Token T_NOT = new Token("!");
	public static final Token T_EQEQ = new Token("==");
	public static final Token T_NEQ = new Token("!=");
	public static final Token T_PRINT = new Token("print");
	public static final Token T_LET = new Token("let");
	public static final Token T_VAR = new Token("var");
	public static final Token T_SUBST = new Token("substitute");
	public static final Token T_INTO = new Token("into");
	public static final Token T_TRUE = new Token("true");
	public static final Token T_FALSE = new Token("false");
	public static final Token T_EXIT = new Token("exit");
	public static final Token[] keywords =
		{ T_PRINT, T_LET, T_VAR, T_SUBST, T_INTO, T_TRUE, T_FALSE, T_EXIT };
	public static final Token[] tokens =
		{ T_CARAT, T_STAR, T_LPAREN, T_RPAREN, T_LBRACK, T_RBRACK, T_LCURLY, T_RCURLY, T_PLUS,
			T_MINUS, T_SLASH, T_MOD, T_EQUALS, T_QUOTE, T_BACK, T_NOT, T_EQEQ, T_NEQ, T_PRINT,
			T_LET, T_VAR, T_SUBST, T_INTO, T_TRUE, T_FALSE, T_EXIT };
	private String contents;

	public Token(String str)
	{
		this.contents = str;
	}

	public String getContents()
	{
		return this.contents;
	}
}
