package limit.core.token;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import limit.util.operations.Operations;
import limit.util.reflection.FieldReflector;
import throwing.ThrowingSupplier;

public class Token
{
	public static final Token T_NONE = null;
	public static final Token T_EMPTY = new Token("");
	public static final Token T_CARAT = new Token("^");
	public static final Token T_STAR = new Token("*");
	public static final Token T_LPAREN = new Token("(");
	public static final Token T_RPAREN = new Token(")");
	public static final Token T_LBRACK = new Token("[");
	public static final Token T_RBRACK = new Token("]");
	public static final Token T_LCURLY = new Token("{");
	public static final Token T_RCURLY = new Token("}");
	public static final Token T_LANGLE = new Token("<");
	public static final Token T_RANGLE = new Token(">");
	public static final Token T_PLUS = new Token("+");
	public static final Token T_MINUS = new Token("-");
	public static final Token T_SLASH = new Token("/");
	public static final Token T_MOD = new Token("%");
	public static final Token T_EQUALS = new Token("=");
	public static final Token T_TICK = new Token("\'");
	public static final Token T_QUOTE = new Token("\"");
	public static final Token T_BACK = new Token("\\");
	public static final Token T_EOF = new Token("\0");
	public static final Token T_COMMA = new Token(",");
	public static final Token T_NOT = new Token("!");
	public static final Token T_SEMI = new Token(";");
	public static final Token T_COLON = new Token(":");
	// reference x& a la C++ style
	public static final Token T_REF = new Token("&");
	public static final Token T_PIPE = new Token("|");
	public static final Token T_AT = new Token("@");
	public static final Token T_DOT = new Token(".");
	public static final Token T_DOTS = new Token("...");
	public static final Token T_MORE_EQ = new Token(">=");
	public static final Token T_LESS_EQ = new Token("<=");
	public static final Token T_EQ_EQ = new Token("==");
	public static final Token T_NOT_EQ = new Token("!=");
	public static final Token T_PLUS_EQ = new Token("+=");
	public static final Token T_MINUS_EQ = new Token("-=");
	public static final Token T_TIMES_EQ = new Token("*=");
	public static final Token T_POWER_EQ = new Token("^=");
	public static final Token T_MOD_EQ = new Token("%=");
	// continuation fn ( params ) -> continuation { body }
	public static final Token T_CONT = new Token("->");
	public static final Token T_LAMBDA = new Token("=>");
	public static final Token T_AND = new Token("and");
	public static final Token T_OR = new Token("or");
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
	public static final Token[][] SYMBOLIC_TOKENS;
	public static final Token[] KEYWORD_TOKENS;
	static
	{
		record TokenInfo(List<Token> keywords, Map<Integer, List<Token>> tokensByLength)
		{
		}
		var tokenInfo = ThrowingSupplier.of(() -> {
			int[] modifiers =
				{ Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL };
			var fields = FieldReflector.getFields(Token.class, modifiers);
			var tokens = new ArrayList<Token>();
			for(Field field : fields)
			{
				var token = (Token) field.get(null);
				tokens.add(token);
			}
			// @formatter:off
			var keywords = tokens.stream()
				.filter(Objects::nonNull)
				.filter(token -> Operations.any(token.value.codePoints(), Character::isAlphabetic))
				.collect(Collectors.toList());
			
			var tokensByLength = tokens.stream()
				.filter(Objects::nonNull)
				.filter(Predicate.not(keywords::contains))
				.collect(Collectors.groupingBy(token -> token.value.length()));
			// @formatter:on
			return new TokenInfo(keywords, tokensByLength);
		}).get();
		KEYWORD_TOKENS = tokenInfo.keywords.toArray(Token[]::new);
		var tokensByLength = tokenInfo.tokensByLength;
		SYMBOLIC_TOKENS = new Token[tokensByLength.size()][];
		for(var entry : tokensByLength.entrySet())
		{
			SYMBOLIC_TOKENS[entry.getKey()] = entry.getValue().toArray(Token[]::new);
		}
	}
	public static final Token[] ALL_TOKENS =
		Operations.unpackAll(SYMBOLIC_TOKENS[1], SYMBOLIC_TOKENS[2], SYMBOLIC_TOKENS[3],
			KEYWORD_TOKENS);
	
	public static void summarize()
	{
		System.out.println(Arrays.toString(KEYWORD_TOKENS));
		System.out.println(Arrays.toString(SYMBOLIC_TOKENS[0]));
		System.out.println(Arrays.toString(SYMBOLIC_TOKENS[1]));
		System.out.println(Arrays.toString(SYMBOLIC_TOKENS[2]));
		System.out.println(Arrays.toString(SYMBOLIC_TOKENS[3]));
		System.out.println(Arrays.toString(ALL_TOKENS));
		System.out.println("""
			%d keywords
			%d 0-length tokens
			%d 1-length tokens
			%d 2-length tokens
			%d 3-length tokens
			""".strip().formatted(KEYWORD_TOKENS.length, SYMBOLIC_TOKENS[0].length,
			SYMBOLIC_TOKENS[1].length, SYMBOLIC_TOKENS[2].length, SYMBOLIC_TOKENS[3].length));
	}
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
	
	public static Token parseToken(String input)
	{
		for(var token : Token.ALL_TOKENS)
		{
			if(token.value.equals(input))
			{
				return token;
			}
		}
		throw new IllegalArgumentException("Invalid token as input: %s".formatted(input));
	}
	
	/**
	 * Parses a keyword token from the input string.
	 *
	 * @param input The string to parse
	 * @return The parsed token.
	 */
	public static Token parseKeyword(String input)
	{
		for(Token token : Token.KEYWORD_TOKENS)
		{
			if(token.value.equals(input))
			{
				return token;
			}
		}
		return Token.T_NONE;
	}
	
	/**
	 * Parses a token from the input string.
	 *
	 * @param input      The string to parse.
	 * @param characters The max number of characters possible in the
	 *                   parsed token.
	 * @return The parsed token.
	 */
	public static Token parseToken(String input, int characters)
	{
		for(var token : Token.ALL_TOKENS)
		{
			if(token.value.length() <= characters && token.value.equals(input))
			{
				return token;
			}
		}
		throw new IllegalArgumentException("Invalid token as input: %s".formatted(input));
	}
	
	public String value()
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
		if(this.type == TokenType.T_UNSPECIFIED)
		{
			return "Token[v=`%s`]".formatted(this.value);
		}
		return "Token[v=`%s`, t=`%s`]".formatted(this.value, this.type);
	}
}
