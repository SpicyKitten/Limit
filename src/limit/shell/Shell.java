package limit.core.shell;

import java.util.ArrayList;
import java.util.Scanner;

import limit.core.exception.LexException;
import limit.core.lexer.Lexer;
import limit.core.parser.Parser;
import limit.core.token.Token;

public class Shell
{
	public static void main(String[] args)
	{
		Token.summarize();
		var currentTokens = new ArrayList<Token>();
		var parser = new Parser();
		var lexer = new Lexer();
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
				System.out.print("> ");
			}
			parser.parse(currentTokens);
		}
		catch(LexException lexException)
		{
			System.err.println(lexer);
		}
	}
}
