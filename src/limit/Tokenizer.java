package limit;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class Tokenizer
{
	private String input;
	private int cursor;
	private State state;
	private ArrayDeque<Integer> interpolationScopes;

	public Tokenizer()
	{
		this.input = "";
		this.state = State.READING_INPUT;
		this.interpolationScopes = new ArrayDeque<>();
	}

	public List<Token> tokenize(String input)
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
		switch (this.state)
		{
			case READING_INPUT ->
			{
				if(Character.isDigit(rest.charAt(0)))
				{
					return getNumber(rest);
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
				if(rest.charAt(0) == Token.T_QUOTE.getChar())
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
			}
		}
		throw new IllegalStateException(
			"Unexpected character: \'" + rest.charAt(0) + "\' at index " + this.cursor);
	}

	private Token getString(String rest)
	{
		var idx = 0;
		var QUOTE_CHAR = Token.T_QUOTE.getChar();
		var LCURLY_CHAR = Token.T_LCURLY.getChar();
		while(idx < rest.length())
		{
			if(rest.charAt(idx) == QUOTE_CHAR || rest.charAt(idx) == LCURLY_CHAR)
			{
				transition(State.READ_STRING);
				break;
			}
			idx++;
		}
		advance(idx);
		return new Token(rest.substring(0, idx), TokenType.STRING);
	}

	private Token getNumber(String rest)
	{
		var idx = 0;
		while(idx < rest.length() && Character.isDigit(rest.charAt(idx)))
		{
			idx++;
		}
		advance(idx);
		return new Token(rest.substring(0, idx), TokenType.NUMBER);
	}

	/**
	 * Advance by N chars
	 */
	private void advance(int N)
	{
		this.cursor += N;
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
		this.interpolationScopes.pop();
	}

	/**
	 * @return Whether the current interpolation scope has ended
	 */
	private boolean updateInterpolationScope(int modifier)
	{
		System.out.print("Scopes: " + this.interpolationScopes + "->");
		if(this.interpolationScopes.isEmpty())
		{
			return false;
		}
		this.interpolationScopes.push(this.interpolationScopes.pop() + modifier);
		System.out.println(this.interpolationScopes);
		return this.interpolationScopes.peek() == 0;
	}

	private enum State
	{
		READING_INPUT, READING_STRING, READ_STRING;
	}
}
