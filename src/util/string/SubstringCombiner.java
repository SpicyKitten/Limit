package src.util.string;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import src.limit.token.Token;
import src.util.operations.Operations;

public class SubstringCombiner
{
	public static long countCombinations(String target, String[] substrings)
	{
		return countCombinations(target, Arrays.asList(substrings));
	}
	
	public static long countCombinationsIgnoring(String target, String[] substrings)
	{
		return countCombinationsIgnoring(target, Arrays.asList(substrings));
	}
	
	public static List<List<String>> findCombinationsIgnoring(String target, String[] substrings)
	{
		return findCombinationsIgnoring(target, Arrays.asList(substrings));
	}
	
	public static List<List<String>> findCombinations(String target, String[] substrings)
	{
		return findCombinations(target, Arrays.asList(substrings));
	}
	
	public static long countCombinations(String target, Iterable<String> substrings)
	{
		var dp = new long[target.length() + 1];
		dp[0] = 1;
		for(var i = 1; i <= target.length(); i++)
		{
			for(var substring : substrings)
			{
				if(target.startsWith(substring, i - substring.length()))
				{
					dp[i] += dp[i - substring.length()];
				}
			}
		}
		return dp[target.length()];
	}
	
	public static long countCombinationsIgnoring(String target, List<String> substrings)
	{
		// @formatter:off
		var breakdowns = substrings.stream()
			.collect(
				Collectors.toMap(
					Function.identity(),
					x -> countCombinations(x, substrings) - 1)
				);
		// @formatter:on
		var dp = new long[target.length() + 1];
		dp[0] = 1;
		substrings.sort(Comparator.comparingInt(String::length));
		var dp_last = Arrays.copyOf(dp, dp.length);
		for(var i = 1; i <= target.length(); i++)
		{
			for(var substring : substrings)
			{
				if(target.startsWith(substring, i - substring.length()))
				{
					System.out
						.println("`%s`, %s, +%s-%s".formatted(substring, i - substring.length(),
						dp[i - substring.length()], breakdowns.get(substring)));
					System.out.println("-dp_: " + Arrays.toString(dp));
					System.out.println("-lst: " + Arrays.toString(dp_last));
					dp[i] += Math.min(dp_last[i - substring.length()], 5);
					// dp_last[i] += dp[i - substring.length()] -
					// breakdowns.get(substring);
					System.out.println("+dp_: " + Arrays.toString(dp));
					System.out.println("+lst: " + Arrays.toString(dp_last));
					// dp[i] += dp[i - substring.length()] - breakdowns.get(substring) +
					// 1;
					// dp_last[i] += dp[i - substring.length()];
				}
			}
			System.out.println(Arrays.toString(dp));
		}
		System.out.println(Arrays.toString(dp));
		return dp[target.length()];
	}
	
	public static List<List<String>> findCombinationsIgnoring(String target,
		List<String> substrings)
	{
		substrings.sort(Comparator.comparing(String::length).reversed());
		// formatter:off
		var dp = Stream.generate(ArrayList<List<String>>::new)
			.limit(target.length() + 1)
			.collect(Collectors.toList());
		// formatter:on
		substrings.sort(Comparator.comparing(String::length).reversed());
		dp.get(0).add(new ArrayList<>());
		var min = 1;
		for(var i = 1; i <= target.length(); i++)
		{
			dp.add(new ArrayList<>());
			for(var substring : substrings)
			{
				if(target.startsWith(substring, i - substring.length()))
				{
					if(substring.length() < min)
					{
						break;
					}
					min = substring.length();
					for(var prev : dp.get(i - substring.length()))
					{
						var combination = new ArrayList<>(prev);
						combination.add(substring);
						dp.get(i).add(combination);
					}
				}
			}
		}
		return dp.get(target.length());
	}
	
	public static List<List<String>> findCombinations(String target, Iterable<String> substrings)
	{
		// @formatter:off
		var dp = Stream.generate(ArrayList<List<String>>::new)
			.limit(target.length() + 1)
			.collect(Collectors.toList());
		// @formatter:on
		dp.get(0).add(new ArrayList<>());
		for(var i = 1; i <= target.length(); i++)
		{
			for(var substring : substrings)
			{
				if(target.startsWith(substring, i - substring.length()))
				{
					for(var prev : dp.get(i - substring.length()))
					{
						var combination = new ArrayList<>(prev);
						combination.add(substring);
						dp.get(i).add(combination);
					}
				}
			}
		}
		return dp.get(target.length());
	}
	
	public static void main(String[] args)
	{
		var target = "==>=>";
		var substrings =
			Operations.map(Operations.unpackAll(Token.SYMBOLIC_TOKENS[3], Token.SYMBOLIC_TOKENS[2],
				Token.SYMBOLIC_TOKENS[1]),
				Token::value, String[]::new);
		System.out.println("Target: %s".formatted(target));
		System.out.println(findCombinations(target, substrings)); // Output: []
		System.out.println(countCombinations(target, substrings));
		System.out.println(findCombinationsIgnoring(target, substrings));
		System.out.println("ignoring: " + countCombinationsIgnoring(target, substrings));
		target = "==>";
		System.out.println("Target: %s".formatted(target));
		System.out.println(findCombinations(target, substrings)); // Output: [[==>], [==, >]]
		System.out.println(countCombinations(target, substrings));
		System.out.println(findCombinationsIgnoring(target, substrings));
		System.out.println("ignoring: " + countCombinationsIgnoring(target, substrings));
		target = "=>=>";
		System.out.println("Target: %s".formatted(target));
		System.out.println(findCombinations(target, substrings));
		System.out.println(countCombinations(target, substrings));
		System.out.println(findCombinationsIgnoring(target, substrings));
		System.out.println("ignoring: " + countCombinationsIgnoring(target, substrings));
//		target = "=>*=>=";
//		System.out.println("Target: %s".formatted(target));
//		System.out.println(findCombinations(target, substrings));
//		System.out.println(countCombinations(target, substrings));
//		System.out.println(findCombinationsIgnoring(target, substrings));
//		System.out.println("ignoring: " + countCombinationsIgnoring(target, substrings));
//		System.exit(0);
//		target =
//			"=>*=>=>=>=>>==>=>=>*=>=>=>=>>==>=>==";
//		System.out.println(findCombinations(target, substrings).size());
//		System.out.println(countCombinations(target, substrings));
	}
}