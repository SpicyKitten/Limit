package throwing;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * As long as you know what you're constructing, this class will let you create
 * a throwing lambda without having to type out the full class name
 *
 * @author ratha
 */
public class Throwing
{
	public static void run(ThrowingRunnable... trs)
	{
		for(var tr : trs)
		{
			tr.run();
		}
	}

	public static void run(Stream<ThrowingRunnable> trs)
	{
		trs.forEach(ThrowingRunnable::run);
	}

	public static void run(Stream<ThrowingRunnable> trStream, ThrowingConsumer<Exception> tc)
	{
		trStream.forEach(tr -> Throwing.of(tr, tc).run());
	}

	public static Runnable of(ThrowingRunnable tr)
	{
		return ThrowingRunnable.of(tr);
	}

	public static Runnable of(ThrowingRunnable tr, Consumer<Exception> c)
	{
		return ThrowingRunnable.of(tr, c);
	}

	public static <T> Consumer<T> of(ThrowingConsumer<T> tc)
	{
		return ThrowingConsumer.of(tc);
	}

	public static <T> Consumer<T> of(ThrowingConsumer<T> tc, Consumer<Exception> c)
	{
		return ThrowingConsumer.of(tc, c);
	}

	public static <T> Supplier<T> of(ThrowingSupplier<T> ts)
	{
		return ThrowingSupplier.of(ts);
	}

	public static <T> Supplier<T> of(ThrowingSupplier<T> ts, Consumer<Exception> c)
	{
		return ThrowingSupplier.of(ts, c);
	}

	public static <T, R> Function<T, R> of(ThrowingFunction<T, R> tf)
	{
		return ThrowingFunction.of(tf);
	}

	public static <T, R> Function<T, R> of(ThrowingFunction<T, R> tf, Consumer<Exception> c)
	{
		return ThrowingFunction.of(tf, c);
	}
}
