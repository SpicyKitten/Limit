package limit.util.operations;

import java.util.function.Function;

/**
 * A class that represents a value that may or may not be present, and
 * provides a way to map the value to a different type.
 * 
 * @param <V> The type of the stored value
 * @complete Under normal circumstances, this code can be considered
 *           complete. Except for bugfixing purposes, there is almost
 *           no conceivable need to modify this code any further.
 */
public class MappedValue<V>
{
	private V storedValue;
	
	/**
	 * Creates a new MappedValue.
	 */
	public MappedValue()
	{
		this.storedValue = null;
	}
	
	/**
	 * Returns whether the value is present.
	 * 
	 * @return Whether the value is present
	 */
	public boolean present()
	{
		return this.storedValue != null;
	}
	
	/**
	 * Maps the value to a different type, using the given mapping
	 * function.
	 * 
	 * @param mapping  The mapping function applied to the value
	 * @param fallback The fallback value if the value is not present
	 * @param <D>      The type of the returned value
	 * @return The mapped value, or the fallback value if the value is not
	 *         present
	 */
	public <D> D value(Function<V, D> mapping, D fallback)
	{
		return this.storedValue == null ? fallback : mapping.apply(this.storedValue);
	}
	
	/**
	 * Sets the value for this MappedValue.
	 * 
	 * @param storedValue The value to be stored
	 */
	public void valueV(V storedValue)
	{
		this.storedValue = storedValue;
	}
	
	/**
	 * Returns the value for this MappedValue.
	 * 
	 * @return The value stored in this MappedValue
	 */
	public V valueV()
	{
		return this.storedValue;
	}
}