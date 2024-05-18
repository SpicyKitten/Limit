package throwing;

import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowingConsumer<T> extends Consumer<T>, ExceptionFlowController
{
	void accept_(T t) throws Exception;

	@Override
	default void accept(T t)
	{
		try
		{
			this.accept_(t);
		}
		catch (Exception e)
		{
			this.handle(e);
		}
	}

	static <T> Consumer<T> of(ThrowingConsumer<T> tc, Consumer<Exception> h)
	{
		return new ThrowingConsumer<>()
		{
			@Override
			public void accept_(T t) throws Exception
			{
				tc.accept_(t);
			}

			@Override
			public void handle(Exception e)
			{
				h.accept(e);
			}
		};
	}

	static <T> Consumer<T> of(ThrowingConsumer<T> tc)
	{
		return tc;
	}
}
