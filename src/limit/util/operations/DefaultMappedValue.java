package limit.util.operations;

import java.util.function.Function;

/**
 * A class that represents a default value for a value that may or may
 * not be present.
 * 
 * @param <D> The type of the default value
 * @param <V> The type of the stored value
 * @complete Under normal circumstances, this code can be considered
 *           complete. Except for bugfixing purposes, there is almost
 *           no conceivable need to modify this code any further.
 */
public class DefaultMappedValue<D, V>
{
	/* The default value after computation */
	private D defaultValue;
	/* The backing value before computation */
	private MappedValue<V> mappedValue;
	/* The mapping function applied when backing value is present */
	private Function<V, D> mapping;
	
	/**
	 * Creates a new DefaultMappedValue with the given default value and
	 * mapping function.
	 * 
	 * @param defaultValue The default value after computation
	 * @param mapping      The mapping function applied when backing value
	 *                     is present
	 */
	public DefaultMappedValue(D defaultValue, Function<V, D> mapping)
	{
		this.mappedValue = new MappedValue<>();
		this.valueD(defaultValue);
		this.mapping(mapping);
	}
	
	/**
	 * Sets the mapping function for this DefaultMappedValue.
	 * 
	 * @param mapping The mapping function applied when backing value is
	 *                present
	 */
	public void mapping(Function<V, D> mapping)
	{
		this.mapping = mapping;
	}
	
	/**
	 * Returns the mapping function for this DefaultMappedValue.
	 * 
	 * @return The mapping function applied when backing value is present
	 */
	public Function<V, D> mapping()
	{
		return this.mapping;
	}
	
	/**
	 * Returns whether the backing value is present.
	 * 
	 * @return Whether the backing value is present
	 */
	public boolean present()
	{
		return this.mappedValue.present();
	}
	
	/**
	 * Sets the default value for this DefaultMappedValue.
	 * 
	 * @param defaultValue The default value after computation
	 */
	public void valueD(D defaultValue)
	{
		this.defaultValue = defaultValue;
	}
	
	/**
	 * Sets the backing value for this DefaultMappedValue.
	 * 
	 * @param storedValue The backing value before computation
	 */
	public void valueV(V storedValue)
	{
		this.mappedValue.valueV(storedValue);
	}
	
	/**
	 * Returns the backing value, or a fallback value if the backing value
	 * is not present.
	 * 
	 * @param mapping  The mapping function applied to the backing value
	 * @param fallback The fallback value if the backing value is not
	 *                 present
	 * @param <O>      The type of the returned value
	 * @return The mapped value, or the fallback value if the backing
	 *         value is not present
	 */
	public <O> O value(Function<V, O> mapping, O fallback)
	{
		return this.mappedValue.value(mapping, fallback);
	}
	
	/**
	 * Returns the value computed from the mapping function, or the
	 * default value if the backing value is not present.
	 * 
	 * @return The computed value, or the default value if the backing
	 *         value is not present
	 */
	public D value()
	{
		return this.mappedValue.value(this.mapping, this.defaultValue);
	}
	
	/**
	 * Returns the default value for this DefaultMappedValue.
	 * 
	 * @return The default value after computation
	 */
	public D valueD()
	{
		return this.defaultValue;
	}
	
	/**
	 * Returns the backing value for this DefaultMappedValue.
	 * 
	 * @return The backing value before computation
	 */
	public V valueV()
	{
		return this.mappedValue.valueV();
	}
}
