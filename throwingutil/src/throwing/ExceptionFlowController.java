package throwing;

/**
 * Controls exception flow by piping it into a handler.
 */
public interface ExceptionFlowController
{
	default void handle(Exception e)
	{
		ThrowingUtil.raise(e);
	}
}
