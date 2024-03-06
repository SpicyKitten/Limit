package limit;

import java.util.ArrayList;
import java.util.Scanner;

import limit.exception.LexException;

public class Shell
{
	public static void main(String[] args)
	{
		var currentTokens = new ArrayList<Token>();
		var parser = new Parser();
		var lexer = new Lexer();
		for(var tok : Token.tokens)
		{
			System.out.println(tok);
		}
		try(var scanner = new Scanner(new UnclosableInputStream(System.in)))
		{
			while(scanner.hasNextLine())
			{
				var input = scanner.nextLine();
				System.out.println("Input: %s".formatted(input));
				var tokens = lexer.lex(input);
				currentTokens.addAll(tokens);
				lexer.check();
				System.out.println("New tokens: %s".formatted(tokens));
				System.out.println(lexer);
			}
			parser.parse(currentTokens);
		}
		catch(LexException lexException)
		{
			System.err.println(lexer);
		}
	}
}
