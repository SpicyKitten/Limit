package src.limit.exception;

import java.util.Collection;
import java.util.List;

import src.limit.position.Position;

/**
 * @complete Under normal circumstances, this code can be considered
 *           complete. Except for bugfixing purposes, there is almost
 *           no conceivable need to modify this code any further.
 */
public class LexException extends IllegalStateException
{
	private static final long serialVersionUID = 3205300631890122410L;
	private Collection<? extends Position<String>> positions = List.of();
	
	public LexException(String s)
	{
		super(s);
	}

	public LexException(String formatted, Collection<? extends Position<String>> positions)
	{
		super(formatted);
		this.positions = positions;
	}
	
	public Collection<? extends Position<String>> getPositions()
	{
		return this.positions;
	}
}