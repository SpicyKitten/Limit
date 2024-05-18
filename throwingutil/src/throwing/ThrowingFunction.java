package throwing;

import java.util.function.Consumer;
import java.util.function.Function;

@FunctionalInterface
public interface ThrowingFunction<T, R> extends Function<T, R>, ExceptionFlowController
{
	R apply_(T t) throws Exception;

	@Override
	default R apply(T t)
	{
		try
		{
			return this.apply_(t);
		}
		catch(Exception e)
		{
			this.handle(e);
			return null;
		}
	}

	static <T, R> ThrowingFunction<T, R> of(ThrowingFunction<T, R> tf, Consumer<Exception> h)
	{
		return new ThrowingFunction<>()
		{
			@Override
			public R apply_(T t) throws Exception
			{
				return tf.apply_(t);
			}

			@Override
			public void handle(Exception e)
			{
				h.accept(e);
			}
		};
	}

	static <T, R> Function<T, R> of(ThrowingFunction<T, R> tf)
	{
		return tf;
	}
}
