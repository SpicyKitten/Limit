package limit.core.lexer;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import limit.core.exception.LexException;
import limit.core.position.LexCursor;
import limit.core.position.LexScope;
import limit.core.position.Position;
import limit.core.token.Token;
import limit.core.token.TokenType;
import limit.util.operations.DefaultMappedValue;

public class Lexer
{
	private static final String[] NUMBER_REGEX =
		{ "[0-9]", "^[0-9]+" };
	private static final String[] IDENTIFIER_KEYWORD_REGEX =
		{ "[_A-Za-z]", "^[_A-Za-z][_A-Za-z0-9]*" };
	private static final String[] WHITESPACE_REGEX =
		{ "[ \n\r]", "^[ \n\r]+" };
	private static final String[] NON_TOKEN_REGEX =
		{ "[ \n\r_A-Za-z0-9]" };
	private String input;
	private LexCursor cursor;
	private State state;
	private DefaultMappedValue<String, LexException> errorState;
	private ArrayDeque<Integer> interpolationScopes;
	private ArrayDeque<LexScope> scopes;
	
	public Lexer()
	{
		this.input = "";
		this.state = State.READING_INPUT;
		this.errorState = new DefaultMappedValue<>("Lexer is in good state", Throwable::getMessage);
		this.interpolationScopes = new ArrayDeque<>();
		this.scopes = new ArrayDeque<>();
	}
	
	public List<Token> lex(String... input)
	{
		var result = new ArrayList<Token>();
		setInput(String.join("\n", input));
		while(hasNextToken())
		{
			result.add(getNextToken());
		}
		check();
		return result;
	}
	
	public void check()
	{
		if(!this.scopes.isEmpty())
		{
			// @formatter:off
			var positions = this.scopes
				.stream()
				.mapToInt(LexScope::position)
				.mapToObj(String::valueOf)
				.sorted(Collections.reverseOrder())
				.collect(Collectors.joining(", "));
			// @formatter:on
			throw error("Unclosed scope at %s".formatted(positions), this.scopes);
		}
		var unclosedScopes = new StringBuilder();
		for(var scope : this.interpolationScopes)
		{
			unclosedScopes.append("}".repeat(scope)).append('\"');
			error("Unclosed interpolation scope(s): %s".formatted(unclosedScopes.toString()));
		}
		if(this.errorState.present())
		{
			throw this.errorState.valueV();
		}
	}
	
	private void setInput(String input)
	{
		this.input = input;
		this.cursor = new LexCursor(0);
	}
	
	private Token getNextToken()
	{
		if(!hasNextToken())
		{
			return Token.T_EMPTY;
		}
		var rest = this.input.substring(this.cursor.position());
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
					openScope(firstStr);
					advance(1);
					openInterpolationScope();
					return Token.T_QUOTE;
				}
				else if(first == Token.T_LCURLY.getChar())
				{
					openScope(firstStr);
					updateInterpolationScope(1);
					advance(1);
					return Token.T_LCURLY;
				}
				else if(first == Token.T_RCURLY.getChar())
				{
					closeScope(firstStr);
					if(updateInterpolationScope(-1))
					{
						transition(State.READING_STRING);
					}
					advance(1);
					return Token.T_RCURLY;
				}
				else if(first == Token.T_LPAREN.getChar())
				{
					openScope(firstStr);
					advance(1);
					return Token.T_LPAREN;
				}
				else if(first == Token.T_RPAREN.getChar())
				{
					closeScope(firstStr);
					advance(1);
					return Token.T_RPAREN;
				}
				else if(first == Token.T_LBRACK.getChar())
				{
					openScope(firstStr);
					advance(1);
					return Token.T_LBRACK;
				}
				else if(first == Token.T_RBRACK.getChar())
				{
					closeScope(firstStr);
					advance(1);
					return Token.T_RBRACK;
				}
				else
				{
					System.out.println(rest);
					advance(1);
					return Token.T_EMPTY;
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
					closeScope(firstStr);
					advance(1);
					closeInterpolationScope();
					return Token.T_QUOTE;
				}
				else if(first == Token.T_LCURLY.getChar())
				{
					transition(State.READ_LCURLY);
					advance(1);
					return Token.T_EMPTY;
				}
				else if(first == Token.T_RCURLY.getChar())
				{
					transition(State.READ_RCURLY);
					advance(1);
					return Token.T_EMPTY;
				}
			}
			case READ_LCURLY ->
			{
				if(first == Token.T_LCURLY.getChar())
				{
					transition(State.READING_STRING);
					advance(1);
				}
				else
				{
					transition(State.READING_INPUT);
					backtrack(1);
					openScope("{");
					// change state only, we need to read this character again
					advance(1);
					updateInterpolationScope(1);
				}
				return Token.T_LCURLY;
			}
			case READ_RCURLY ->
			{
				if(first == Token.T_RCURLY.getChar())
				{
					transition(State.READING_STRING);
					advance(1);
					return Token.T_RCURLY;
				}
				else
				{
					backtrack(1);
					throw error("`}` without corresponding `{` at index %s!", this.cursor);
				}
			}
			default
				-> throw new IllegalArgumentException("Unexpected value: %s".formatted(this.state));
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
			for(var keyword : Token.KEYWORD_TOKENS)
			{
				if(match.equalsIgnoreCase(keyword.value()))
				{
					backtrack(match.length());
					throw error("Syntax error: invalid keyword `%s` (did you mean `%s`?)", match,
						keyword.value());
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
	
	private LexException error(String format, Collection<? extends Position<String>> positions,
		Object... args)
	{
		this.state = State.ERROR;
		this.errorState.valueV(new LexException(format.formatted(args), positions));
		return this.errorState.valueV();
	}
	
	private LexException error(String format, Object... args)
	{
		this.state = State.ERROR;
		this.scopes.clear();
		this.errorState.valueV(new LexException(format.formatted(args)));
		return this.errorState.valueV();
	}
	
	/**
	 * Advance by N chars
	 */
	private void advance(int N)
	{
		this.cursor.advance(N);
	}
	
	private void backtrack(int N)
	{
		this.cursor.backtrack(N);
	}
	
	private void transition(State state)
	{
		System.out.println("new state: %s".formatted(state));
		this.state = state;
	}
	
	private boolean hasNextToken()
	{
		return this.cursor.position() < this.input.length() && this.state != State.ERROR;
	}
	
	private void openScope(String delimiter)
	{
		this.scopes.push(new LexScope(this.cursor.position(), delimiter));
	}
	
	private void closeScope(String delimiter)
	{
		if(this.scopes.isEmpty())
		{
			throw error("Unbalanced %s at index %s", delimiter, this.cursor);
		}
		var scope = this.scopes.pop();
		if(!scope.matches(delimiter))
		{
			throw error("Mismatching %s%s at index %s", scope.representation(), delimiter,
				this.cursor);
		}
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
	
	private Collection<? extends Position<?>> getErrorPositions()
	{
		var positions = this.errorState.value(LexException::getPositions, List.of(this.cursor));
		return positions.isEmpty() ? List.of(this.cursor) : positions;
	}
	
	/**
	 * Converts the Lexer to string format.
	 * 
	 * @return a string representation of the Lexer
	 */
	@Override
	public String toString()
	{
		var positions = getErrorPositions();
		var leftmost = positions.stream().mapToInt(Position::position).min().getAsInt();
		var rightmost = positions.stream().mapToInt(Position::position).max().getAsInt();
		var pointer = new StringBuilder(" ".repeat(rightmost + 1));
		for(var position : positions)
		{
			pointer.setCharAt(position.position(), '^');
		}
		var prefix = " ".repeat(leftmost);
		var message = prefix + this.errorState.value();
		return """
			[Lexer state=%s scopes=%s]
			%s
			%s
			%s""".formatted(this.state, this.interpolationScopes, this.input, pointer, message);
	}
	
	private enum State
	{
		READING_INPUT, READING_STRING, READ_STRING, READ_LCURLY, READ_RCURLY, ERROR;
	}
}
