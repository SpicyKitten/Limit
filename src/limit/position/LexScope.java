package limit.core.position;

import static java.util.Map.entry;

import java.util.Map;
import java.util.Set;

import limit.core.exception.LexScopeException;

public record LexScope(int position, String delimiter) implements Position<String>
{
	// @formatter:off
	private static Map<String, String> terminators = Map.ofEntries(
		entry("(", ")"),
		entry("[", "]"),
		entry("{", "}"),
		entry("\"", "\"")
	);
	// @formatter:on
	
	public static Set<String> initiators()
	{
		return terminators.keySet();
	}
	
	public LexScope
	{
		if(!initiators().contains(delimiter))
		{
			throw new LexScopeException("Added scope for unexpected delimiter `%s`!", delimiter);
		}
	}
	
	@Override
	public String representation()
	{
		return String.valueOf(this.delimiter);
	}
	
	/**
	 * @param terminator A charactor to close the scope with
	 * @return whether this scope can be closed with the given terminator
	 */
	public boolean matches(String terminator)
	{
		return terminators.get(this.delimiter).equals(terminator);
	}
}
