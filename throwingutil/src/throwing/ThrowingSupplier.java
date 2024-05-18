package throwing;

import java.util.function.Consumer;
import java.util.function.Supplier;

@FunctionalInterface
public interface ThrowingSupplier<T> extends Supplier<T>, ExceptionFlowController
{
	public abstract T get_() throws Exception;
	
	@Override
	default T get()
	{
		try
		{
			return get_();
		}
		catch (Exception e)
		{
			handle(e);
		}
		return null;
	}
	
	static <T> Supplier<T> of(ThrowingSupplier<T> tc, Consumer<Exception> h)
	{
		return new ThrowingSupplier<T>()
		{
			public T get_() throws Exception
			{
				return tc.get();
			}
			
			public void handle(Exception e)
			{
				h.accept(e);
			}
		};
	}
	
	static <T> Supplier<T> of(ThrowingSupplier<T> tc)
	{
		return tc;
	}
}