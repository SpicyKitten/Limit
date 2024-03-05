package limit.exception;

/**
 * @complete Under normal circumstances, this code can be considered
 *           complete. Except for bugfixing purposes, there is almost
 *           no conceivable need to modify this code any further.
 */
public class LexException extends IllegalStateException
{
	private static final long serialVersionUID = 3205300631890122410L;
	
	public LexException(String s)
	{
		super(s);
	}
}