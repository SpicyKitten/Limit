package limit;

import java.util.Arrays;

public class Token
{
	public static final Token T_EMPTY = new Token("");
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
	public static final Token T_EOF = new Token("\0");
	public static final Token T_COMMA = new Token(",");
	public static final Token T_NOT = new Token("!");
	public static final Token T_SEMI = new Token(";");
	public static final Token T_MORE = new Token(">");
	public static final Token T_LESS = new Token("<");
	public static final Token T_MORE_EQ = new Token(">=");
	public static final Token T_LESS_EQ = new Token("<=");
	public static final Token T_EQ_EQ = new Token("==");
	public static final Token T_NOT_EQ = new Token("!=");
	public static final Token T_PLUS_EQ = new Token("+=");
	public static final Token T_MINUS_EQ = new Token("-=");
	public static final Token T_TIMES_EQ = new Token("*=");
	public static final Token T_POWER_EQ = new Token("^=");
	public static final Token T_MOD_EQ = new Token("%=");
	public static final Token T_LAMBDA = new Token("->");
	public static final Token T_AND = new Token("&&");
	public static final Token T_OR = new Token("||");
	public static final Token T_CLASS = new Token("class", TokenType.T_CLASS);
	public static final Token T_NEW = new Token("new", TokenType.T_NEW);
	public static final Token T_THIS = new Token("this", TokenType.T_THIS);
	public static final Token T_PRINT = new Token("print", TokenType.T_PRINT);
	public static final Token T_LET = new Token("let", TokenType.T_LET);
	public static final Token T_VAR = new Token("var", TokenType.T_VAR);
	public static final Token T_SUBST = new Token("substitute", TokenType.T_SUBST);
	public static final Token T_INTO = new Token("into", TokenType.T_INTO);
	public static final Token T_TRUE = new Token("true", TokenType.T_TRUE);
	public static final Token T_FALSE = new Token("false", TokenType.T_FALSE);
	public static final Token T_EXIT = new Token("exit", TokenType.T_EXIT);
	public static final Token[] zeroCharacterTokens =
		{ T_EMPTY };
	public static final Token[] singleCharacterTokens =
		{ T_CARAT, T_STAR, T_LPAREN, T_RPAREN, T_LBRACK, T_RBRACK, T_LCURLY, T_RCURLY, T_PLUS,
			T_MINUS, T_SLASH, T_MOD, T_EQUALS, T_QUOTE, T_BACK, T_EOF, T_COMMA, T_NOT, T_SEMI,
			T_MORE, T_LESS };
	public static final Token[] twoCharacterTokens =
		{ T_MORE_EQ, T_LESS_EQ, T_EQ_EQ, T_NOT_EQ, T_PLUS_EQ, T_MINUS_EQ, T_TIMES_EQ, T_POWER_EQ,
			T_MOD_EQ, T_LAMBDA, T_AND, T_OR };
	public static final Token[] keywords =
		{ T_CLASS, T_NEW, T_THIS, T_PRINT, T_LET, T_VAR, T_SUBST, T_INTO, T_TRUE, T_FALSE, T_EXIT };
	public static final Token[] tokens = unpack(singleCharacterTokens,
		unpack(twoCharacterTokens, unpack(keywords)));
	private String value;
	private TokenType type;
	
	public Token(String value, TokenType type)
	{
		this.value = value;
		this.type = type;
	}

	public Token(String value)
	{
		this.value = value;
		this.type = TokenType.T_UNSPECIFIED;
	}

	public String getValue()
	{
		return this.value;
	}

	public char getChar()
	{
		if(this.value.length() > 1 || this.value.length() < 0)
		{
			throw new IllegalStateException("Called getChar on Token that is not 1 char!");
		}
		return this.value.charAt(0);
	}

	public TokenType getType()
	{
		return this.type;
	}

	@Override
	public String toString()
	{
		return "Token[v=%s, t=%s]".formatted(this.value, this.type);
	}

	@SafeVarargs
	private static <E> E[] unpack(E[] most, E... rest)
	{
		var copy = Arrays.copyOf(most, most.length + rest.length);
		System.arraycopy(rest, 0, copy, most.length, rest.length);
		return copy;
	}
}
