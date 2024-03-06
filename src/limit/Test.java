package limit;

import java.util.List;

public class Test
{
	public static void main(String[] args)
	{
		error("abc", List.of(new LexScope(0, "{")));
		error("abc", List.of(new LexScope(0, "\"")), "abcde");
		error("abc", "abcde");
	}
	
	private static void error(String format, List<? extends Position<String>> positions,
		Object... args)
	{
		System.out.println("error 1");
	}
	
	private static void error(String format, Object... args)
	{
		System.out.println("error 2");
	}
}
