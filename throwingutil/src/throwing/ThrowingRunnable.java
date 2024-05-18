package throwingutil.src.throwing;

import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowingRunnable extends Runnable, ExceptionFlowController
{
	void run_() throws Exception;

	@Override
	default void run()
	{
		try
		{
			run_();
		}
		catch (Exception e)
		{
			handle(e);
		}
	}

	static Runnable of(ThrowingRunnable tr, Consumer<Exception> h)
	{
		return new ThrowingRunnable()
		{
			@Override
			public void run_() throws Exception
			{
				tr.run_();
			}

			@Override
			public void handle(Exception e)
			{
				h.accept(e);
			}
		};
	}

	static Runnable of(ThrowingRunnable tr)
	{
		return tr;
	}
}
