package limit.core.exception;

/**
 * @complete Under normal circumstances, this code can be considered
 *           complete. Except for bugfixing purposes, there is almost
 *           no conceivable need to modify this code any further.
 */
public class LexScopeException extends LexException
{
	private static final long serialVersionUID = 5122015965368556286L;
	
	public LexScopeException(String cause, Object... args)
	{
		super(cause.formatted(args));
	}
}