package limit.util.operations;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.function.UnaryOperator;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Operations
{
	public static void main(String[] args)
	{
		var first = Operations.filter(new long[]
			{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 8, 5 }, x -> x % 2 == 1);
		System.out.println(Arrays.toString(first));
		var second = Operations.mapToInt(first, (x -> (int) x - 1));
		System.out.println(Arrays.toString(second));
		var third = Operations.mapToDouble(second, x -> x * 1.5);
		System.out.println(Arrays.toString(third));
		var fourth = Operations.mapToObj(third, "12 >= %s"::formatted, String[]::new);
		System.out.println(Arrays.toString(fourth));
		var fifth = Operations.map(fourth, x -> x + "abcd", String[]::new);
		System.out.println(Arrays.toString(fifth));
		var sixth = Operations.all(fifth, x -> x.length() > 3);
		System.out.println("All items longer than 3 characters: %b".formatted(sixth));
		var seventh = Operations.any(fifth, x -> x.length() < 3);
		System.out.println("Any items longer than 3 characters: %b".formatted(seventh));
		var eighth = Operations.none(fifth, x -> x.length() < 3);
		System.out.println("No items longer than 3 characters: %b".formatted(eighth));
		var ninth = Operations.map(fifth, String::toUpperCase, String[]::new);
		System.out.println(Arrays.toString(ninth));
		var tenth =
			Operations.map(ninth, (Function<String, String[]>) x -> x.split("A")[0].split(" >= "),
			String[][]::new);
		System.out.println("Tenth:");
		Arrays.stream(tenth).map(Arrays::toString).forEach(System.out::println);
		var eleventh =
			Operations.filter(tenth, (Predicate<String[]>) x -> Double.valueOf(x[1]) > 7,
				String[][]::new);
		System.out.println("Eleventh:");
		Arrays.stream(eleventh).map(Arrays::toString).forEach(System.out::println);
		var twelfth =
			Operations.mapToDouble(eleventh,
				(ToDoubleFunction<String[]>) x -> Double.parseDouble(x[1]));
		System.out.println("Twelfth: %s".formatted(Arrays.toString(twelfth)));
	}
	
	public static <T> boolean any(Iterator<T> iterator, Predicate<T> predicate)
	{
		while(iterator.hasNext())
		{
			if(predicate.test(iterator.next()))
			{
				return true;
			}
		}
		return false;
	}
	
	public static <T> boolean all(Iterator<T> iterator, Predicate<T> predicate)
	{
		while(iterator.hasNext())
		{
			if(!predicate.test(iterator.next()))
			{
				return false;
			}
		}
		return true;
	}
	
	public static <T> boolean none(Iterator<T> iterator, Predicate<T> predicate)
	{
		while(iterator.hasNext())
		{
			if(predicate.test(iterator.next()))
			{
				return false;
			}
		}
		return true;
	}
	
	public static <T> Iterator<T> filter(Iterator<T> iterator, Predicate<T> predicate)
	{
		return new Iterator<>()
		{
			private T recent;
			private boolean fetched;
			
			@Override
			public boolean hasNext()
			{
				if(!this.fetched)
				{
					while(iterator.hasNext())
					{
						this.recent = iterator.next();
						if(predicate.test(this.recent))
						{
							return (this.fetched = true);
						}
					}
				}
				return this.fetched;
			}
			
			@Override
			public T next()
			{
				if(this.fetched)
				{
					this.fetched = false;
					return this.recent;
				}
				this.recent = null;
				throw new NoSuchElementException();
			}
		};
	}
	
	public static <T, R> Iterator<R> map(Iterator<T> iterator, Function<T, R> mapping)
	{
		return new Iterator<>()
		{
			public boolean hasNext()
			{
				return iterator.hasNext();
			}
			
			@Override
			public R next()
			{
				return mapping.apply(iterator.next());
			}
		};
	}
	
	public static <T> Iterator<T> map(Iterator<T> iterator, UnaryOperator<T> mapping)
	{
		return map(iterator, mapping::apply);
	}
	
	public static <T> boolean any(Iterable<T> iterable, Predicate<T> predicate)
	{
		return any(iterable.iterator(), predicate);
	}
	
	public static <T> boolean all(Iterable<T> iterable, Predicate<T> predicate)
	{
		return all(iterable.iterator(), predicate);
	}
	
	public static <T> boolean none(Iterable<T> iterable, Predicate<T> predicate)
	{
		return none(iterable.iterator(), predicate);
	}
	
	public static <T> Iterable<T> filter(Iterable<T> iterable, Predicate<T> predicate)
	{
		return () -> filter(iterable.iterator(), predicate);
	}
	
	public static <T, R> Iterable<R> map(Iterable<T> iterable, Function<T, R> mapping)
	{
		return () -> map(iterable.iterator(), mapping);
	}
	
	public static <T> Iterable<T> map(Iterable<T> iterable, UnaryOperator<T> mapping)
	{
		Function<T, T> function = mapping::apply;
		return map(iterable, function);
	}
	
	public static <T> boolean any(Stream<T> stream, Predicate<T> predicate)
	{
		return stream.anyMatch(predicate);
	}
	
	public static boolean any(IntStream stream, IntPredicate predicate)
	{
		return stream.anyMatch(predicate);
	}
	
	public static boolean any(DoubleStream stream, DoublePredicate predicate)
	{
		return stream.anyMatch(predicate);
	}
	
	public static boolean any(LongStream stream, LongPredicate predicate)
	{
		return stream.anyMatch(predicate);
	}
	
	public static <T> boolean all(Stream<T> stream, Predicate<T> predicate)
	{
		return stream.allMatch(predicate);
	}
	
	public static boolean all(IntStream stream, IntPredicate predicate)
	{
		return stream.allMatch(predicate);
	}
	
	public static boolean all(DoubleStream stream, DoublePredicate predicate)
	{
		return stream.allMatch(predicate);
	}
	
	public static boolean all(LongStream stream, LongPredicate predicate)
	{
		return stream.allMatch(predicate);
	}
	
	public static <T> boolean none(Stream<T> stream, Predicate<T> predicate)
	{
		return stream.noneMatch(predicate);
	}
	
	public static boolean none(IntStream stream, IntPredicate predicate)
	{
		return stream.noneMatch(predicate);
	}
	
	public static boolean none(DoubleStream stream, DoublePredicate predicate)
	{
		return stream.noneMatch(predicate);
	}
	
	public static boolean none(LongStream stream, LongPredicate predicate)
	{
		return stream.noneMatch(predicate);
	}
	
	public static <T> Stream<T> filter(Stream<T> stream, Predicate<T> predicate)
	{
		return stream.filter(predicate);
	}
	
	public static IntStream filter(IntStream stream, IntPredicate predicate)
	{
		return stream.filter(predicate);
	}
	
	public static DoubleStream filter(DoubleStream stream, DoublePredicate predicate)
	{
		return stream.filter(predicate);
	}
	
	public static LongStream filter(LongStream stream, LongPredicate predicate)
	{
		return stream.filter(predicate);
	}
	
	public static <T, R> Stream<R> map(Stream<T> stream, Function<T, R> mapping)
	{
		return stream.map(mapping);
	}
	
	public static <T> Stream<T> map(Stream<T> stream, UnaryOperator<T> mapping)
	{
		return stream.map(mapping);
	}
	
	public static <T> IntStream map(Stream<T> stream, ToIntFunction<T> mapping)
	{
		return stream.mapToInt(mapping);
	}
	
	public static <T> DoubleStream map(Stream<T> stream, ToDoubleFunction<T> mapping)
	{
		return stream.mapToDouble(mapping);
	}
	
	public static <T> LongStream map(Stream<T> stream, ToLongFunction<T> mapping)
	{
		return stream.mapToLong(mapping);
	}
	
	public static <R> Stream<R> map(IntStream stream, IntFunction<R> mapping)
	{
		return stream.mapToObj(mapping);
	}
	
	public static IntStream map(IntStream stream, IntUnaryOperator mapping)
	{
		return stream.map(mapping);
	}
	
	public static DoubleStream map(IntStream stream, IntToDoubleFunction mapping)
	{
		return stream.mapToDouble(mapping);
	}
	
	public static LongStream map(IntStream stream, IntToLongFunction mapping)
	{
		return stream.mapToLong(mapping);
	}
	
	public static <R> Stream<R> map(DoubleStream stream, DoubleFunction<R> mapping)
	{
		return stream.mapToObj(mapping);
	}
	
	public static IntStream map(DoubleStream stream, DoubleToIntFunction mapping)
	{
		return stream.mapToInt(mapping);
	}
	
	public static DoubleStream map(DoubleStream stream, DoubleUnaryOperator mapping)
	{
		return stream.map(mapping);
	}
	
	public static LongStream map(DoubleStream stream, DoubleToLongFunction mapping)
	{
		return stream.mapToLong(mapping);
	}
	
	public static <R> Stream<R> map(LongStream stream, LongFunction<R> mapping)
	{
		return stream.mapToObj(mapping);
	}
	
	public static IntStream map(LongStream stream, LongToIntFunction mapping)
	{
		return stream.mapToInt(mapping);
	}
	
	public static DoubleStream map(LongStream stream, LongToDoubleFunction mapping)
	{
		return stream.mapToDouble(mapping);
	}
	
	public static LongStream map(LongStream stream, LongUnaryOperator mapping)
	{
		return stream.map(mapping);
	}
	
	public static <T> boolean any(T[] array, Predicate<T> predicate)
	{
		return any(Arrays.stream(array), predicate);
	}
	
	public static <T> boolean all(T[] array, Predicate<T> predicate)
	{
		return all(Arrays.stream(array), predicate);
	}
	
	public static <T> boolean none(T[] array, Predicate<T> predicate)
	{
		return none(Arrays.stream(array), predicate);
	}
	
	public static <T> T[] filter(T[] array, Predicate<T> predicate, IntFunction<T[]> generator)
	{
		return filter(Arrays.stream(array), predicate).toArray(generator);
	}
	
	public static <T, R> R[] map(T[] array, Function<T, R> mapping, IntFunction<R[]> generator)
	{
		return map(Arrays.stream(array), mapping).toArray(generator);
	}
	
	public static <T> T[] map(T[] array, UnaryOperator<T> mapping, IntFunction<T[]> generator)
	{
		return map(Arrays.stream(array), mapping).toArray(generator);
	}
	
	public static <T> int[] mapToInt(T[] array, ToIntFunction<T> mapping)
	{
		return map(Arrays.stream(array), mapping).toArray();
	}
	
	public static <T> double[] mapToDouble(T[] array, ToDoubleFunction<T> mapping)
	{
		return map(Arrays.stream(array), mapping).toArray();
	}
	
	public static <T> long[] mapToLong(T[] array, ToLongFunction<T> mapping)
	{
		return map(Arrays.stream(array), mapping).toArray();
	}
	
	public static boolean any(int[] array, IntPredicate predicate)
	{
		return Arrays.stream(array).anyMatch(predicate);
	}
	
	public static boolean all(int[] array, IntPredicate predicate)
	{
		return Arrays.stream(array).allMatch(predicate);
	}
	
	public static boolean none(int[] array, IntPredicate predicate)
	{
		return Arrays.stream(array).noneMatch(predicate);
	}
	
	public static int[] filter(int[] array, IntPredicate predicate)
	{
		return Arrays.stream(array).filter(predicate).toArray();
	}
	
	public static <R> R[] mapToObj(int[] array, IntFunction<R> mapping, IntFunction<R[]> generator)
	{
		return map(Arrays.stream(array), mapping).toArray(generator);
	}
	
	public static int[] map(int[] array, IntUnaryOperator mapping)
	{
		return Arrays.stream(array).map(mapping).toArray();
	}
	
	public static double[] mapToDouble(int[] array, IntToDoubleFunction mapping)
	{
		return map(Arrays.stream(array), mapping).toArray();
	}
	
	public static long[] mapToLong(int[] array, IntToLongFunction mapping)
	{
		return map(Arrays.stream(array), mapping).toArray();
	}
	
	public static boolean any(double[] array, DoublePredicate predicate)
	{
		return Arrays.stream(array).anyMatch(predicate);
	}
	
	public static boolean all(double[] array, DoublePredicate predicate)
	{
		return Arrays.stream(array).allMatch(predicate);
	}
	
	public static boolean none(double[] array, DoublePredicate predicate)
	{
		return Arrays.stream(array).noneMatch(predicate);
	}
	
	public static double[] filter(double[] array, DoublePredicate predicate)
	{
		return Arrays.stream(array).filter(predicate).toArray();
	}
	
	public static <R> R[] mapToObj(double[] array, DoubleFunction<R> mapping,
		IntFunction<R[]> generator)
	{
		return map(Arrays.stream(array), mapping).toArray(generator);
	}
	
	public static int[] mapToInt(double[] array, DoubleToIntFunction mapping)
	{
		return map(Arrays.stream(array), mapping).toArray();
	}
	
	public static double[] map(double[] array, DoubleUnaryOperator mapping)
	{
		return map(Arrays.stream(array), mapping).toArray();
	}
	
	public static long[] mapToLong(double[] array, DoubleToLongFunction mapping)
	{
		return map(Arrays.stream(array), mapping).toArray();
	}
	
	public static boolean any(long[] array, LongPredicate predicate)
	{
		return Arrays.stream(array).anyMatch(predicate);
	}
	
	public static boolean all(long[] array, LongPredicate predicate)
	{
		return Arrays.stream(array).allMatch(predicate);
	}
	
	public static boolean none(long[] array, LongPredicate predicate)
	{
		return Arrays.stream(array).noneMatch(predicate);
	}
	
	public static long[] filter(long[] array, LongPredicate predicate)
	{
		return Arrays.stream(array).filter(predicate).toArray();
	}
	
	public static <R> R[] mapToObj(long[] array, LongFunction<R> mapping,
		IntFunction<R[]> generator)
	{
		return map(Arrays.stream(array), mapping).toArray(generator);
	}
	
	public static int[] mapToInt(long[] array, LongToIntFunction mapping)
	{
		return map(Arrays.stream(array), mapping).toArray();
	}
	
	public static double[] mapToDouble(long[] array, LongToDoubleFunction mapping)
	{
		return map(Arrays.stream(array), mapping).toArray();
	}
	
	public static long[] map(long[] array, LongUnaryOperator mapping)
	{
		return map(Arrays.stream(array), mapping).toArray();
	}
}
