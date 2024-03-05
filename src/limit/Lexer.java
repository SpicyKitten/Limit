package limit;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import limit.exception.LexException;

public class Lexer
{
	private static final String[] NUMBER_REGEX =
		{ "[0-9]", "^[0-9]+" };
	private static final String[] IDENTIFIER_KEYWORD_REGEX =
		{ "[_A-Za-z]", "^[_A-Za-z][_A-Za-z0-9]*" };
	private static final String[] WHITESPACE_REGEX =
		{ "[ \n\r]", "^[ \n\r]+" };
	private String input;
	private int cursor;
	private State state;
	private Optional<LexException> errorState;
	private ArrayDeque<Integer> interpolationScopes;
	
	public Lexer()
	{
		this.input = "";
		this.state = State.READING_INPUT;
		this.errorState = Optional.empty();
		this.interpolationScopes = new ArrayDeque<>();
	}
	
	public List<Token> lex(String input)
	{
		var result = new ArrayList<Token>();
		setInput(input);
		while(hasNextToken())
		{
			result.add(getNextToken());
		}
		return result;
	}
	
	private void setInput(String input)
	{
		this.input = input;
		this.cursor = 0;
	}
	
	private Token getNextToken()
	{
		if(!hasNextToken())
		{
			return Token.T_EMPTY;
		}
		var rest = this.input.substring(this.cursor);
		var first = rest.charAt(0);
		var firstStr = "" + first;
		switch(this.state)
		{
			case READING_INPUT ->
			{
				if(firstStr.matches(NUMBER_REGEX[0]))
				{
					return getNumber(rest);
				}
				else if(firstStr.matches(IDENTIFIER_KEYWORD_REGEX[0]))
				{
					return getIdentifierOrKeyword(rest);
				}
				else if(firstStr.matches(WHITESPACE_REGEX[0]))
				{
					return getWhitespace(rest);
				}
				else if(first == Token.T_QUOTE.getChar())
				{
					transition(State.READING_STRING);
					advance(1);
					openInterpolationScope();
					return Token.T_QUOTE;
				}
				else if(first == Token.T_LCURLY.getChar())
				{
					updateInterpolationScope(1);
					advance(1);
					return Token.T_LCURLY;
				}
				else if(first == Token.T_RCURLY.getChar())
				{
					if(updateInterpolationScope(-1))
					{
						transition(State.READING_STRING);
					}
					advance(1);
					return Token.T_RCURLY;
				}
			}
			case READING_STRING ->
			{
				return getString(rest);
			}
			case READ_STRING ->
			{
				if(first == Token.T_QUOTE.getChar())
				{
					transition(State.READING_INPUT);
					advance(1);
					closeInterpolationScope();
					return Token.T_QUOTE;
				}
				else if(first == Token.T_LCURLY.getChar())
				{
					transition(State.READING_INPUT);
					advance(1);
					updateInterpolationScope(1);
					return Token.T_LCURLY;
				}
				else if(first == Token.T_RCURLY.getChar())
				{
					throw error("Unexpected end of string interpolation at index %s!", this.cursor);
				}
			}
		}
		throw error("Unexpected character: \'%s\' at index %s", first, this.cursor);
	}
	
	private Token getString(String rest)
	{
		var idx = 0;
		var QUOTE_CHAR = Token.T_QUOTE.getChar();
		var LCURLY_CHAR = Token.T_LCURLY.getChar();
		var RCURLY_CHAR = Token.T_RCURLY.getChar();
		var terminators = Arrays.asList(QUOTE_CHAR, LCURLY_CHAR, RCURLY_CHAR);
		while(idx < rest.length())
		{
			// if(rest.charAt(idx) == QUOTE_CHAR || rest.charAt(idx) ==
			// LCURLY_CHAR)
			if(terminators.contains(rest.charAt(idx)))
			{
				transition(State.READ_STRING);
				break;
			}
			idx++;
		}
		advance(idx);
		return new Token(rest.substring(0, idx), TokenType.T_STRING_LITERAL);
	}
	
	private Token getNumber(String rest)
	{
		var regex = NUMBER_REGEX[1];
		var pattern = Pattern.compile(regex);
		var matcher = pattern.matcher(rest);
		if(matcher.find(0))
		{
			var match = matcher.group();
			advance(match.length());
			return new Token(match, TokenType.T_NUMBER);
		}
		throw error("Unexpected number at index %s", this.cursor);
	}
	
	private Token getIdentifierOrKeyword(String rest)
	{
		var regex = IDENTIFIER_KEYWORD_REGEX[1];
		var pattern = Pattern.compile(regex);
		var matcher = pattern.matcher(rest);
		if(matcher.find(0))
		{
			var match = matcher.group();
			advance(match.length());
			var token = Token.parseKeyword(match);
			if(token != Token.T_NONE)
			{
				return token;
			}
			for(var keyword : Token.keywords)
			{
				if(match.equalsIgnoreCase(keyword.getValue()))
				{
					backtrack(match.length());
					throw error("Syntax error: invalid keyword `%s` (did you mean `%s`?)", match,
						keyword.getValue());
				}
			}
			return new Token(match, TokenType.T_IDENTIFIER);
		}
		throw error("Unexpected identifier or keyword at index %s", this.cursor);
	}
	
	private Token getWhitespace(String rest)
	{
		var regex = WHITESPACE_REGEX[1];
		var pattern = Pattern.compile(regex);
		var matcher = pattern.matcher(rest);
		if(matcher.find(0))
		{
			var match = matcher.group();
			advance(match.length());
			return new Token(match, TokenType.T_WHITESPACE);
		}
		throw error("Unexpected whitespace at index %s", this.cursor);
	}
	
	private LexException error(String format, Object... args)
	{
		this.errorState = Optional.of(new LexException(format.formatted(args)));
		return this.errorState.orElseThrow();
	}
	
	/**
	 * Advance by N chars
	 */
	private void advance(int N)
	{
		this.cursor += N;
	}
	
	private void backtrack(int N)
	{
		this.cursor -= N;
	}
	
	private void transition(State state)
	{
		this.state = state;
	}
	
	private boolean hasNextToken()
	{
		return this.cursor < this.input.length();
	}
	
	private void openInterpolationScope()
	{
		this.interpolationScopes.push(0);
	}
	
	private void closeInterpolationScope()
	{
		var closedScope = this.interpolationScopes.pop();
		if(closedScope != 0)
		{
			throw error("Interpolation scope ends with unbalanced brace count: %s", closedScope);
		}
	}
	
	/**
	 * @return Whether the current interpolation scope has ended
	 */
	private boolean updateInterpolationScope(int modifier)
	{
		/* interpolation scopes are irrelevant if we're not inside a string */
		if(this.interpolationScopes.isEmpty())
		{
			return false;
		}
		System.out.print("Scopes: " + this.interpolationScopes + "->");
		this.interpolationScopes.push(this.interpolationScopes.pop() + modifier);
		System.out.println(this.interpolationScopes);
		return this.interpolationScopes.peek() == 0;
	}
	
	/**
	 * Converts the Lexer to string format.
	 * @return a string representation of the Lexer
	 */
	@Override
	public String toString()
	{
		var pointer = " ".repeat(this.cursor) + "^";
		var prefix = " ".repeat(this.cursor);
		var message =
			this.errorState.map(err -> prefix + err.getMessage()).orElse("Lexer is in good state");
		return """
			[Lexer state=%s scopes=%s]
			%s
			%s
			%s""".formatted(this.state, this.interpolationScopes, this.input, pointer, message);
	}
	
	private enum State
	{
		READING_INPUT, READING_STRING, READ_STRING;
	}
}
