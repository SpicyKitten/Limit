package limit;

import java.util.ArrayList;
import java.util.Scanner;

public class Shell
{
	public static void main(String[] args)
	{
		var currentTokens = new ArrayList<Token>();
		var parser = new Parser();
		var tokenizer = new Tokenizer();
		for(var tok : Token.tokens)
		{
			System.out.println(tok);
		}
		try (var scanner = new Scanner(new UnclosableInputStream(System.in)))
		{
			while(scanner.hasNextLine())
			{
				var input = scanner.nextLine();
				System.out.println("Input: " + input);
				var tokens = tokenizer.tokenize(input);
				currentTokens.addAll(tokens);
				System.out.println(currentTokens);
				parser.parse(currentTokens);
			}
		}
	}
}
