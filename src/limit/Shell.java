package limit;

import java.util.Scanner;

public class Shell
{
	public static void main(String[] args)
	{
		try(var scanner = new Scanner(new UnclosableInputStream(System.in)))
		{
			while(scanner.hasNextLine())
			{
				var input = scanner.nextLine();

			}
		}
	}
}
