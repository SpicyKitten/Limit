package limit.util.reflection;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FieldReflector
{
	record FieldInfo(Field field, int modifiers)
	{
		public boolean is(int... modifiers)
		{
			for(var modifier : modifiers)
			{
				if(!is(modifier))
				{
					return false;
				}
			}
			return true;
		}
		
		public boolean is(int modifier)
		{
			return (this.modifiers & modifier) != 0;
		}
	}
	
	public static List<Field> getFields(Class<?> clazz, int... modifiers)
	{
		// @formatter:off
		return Arrays.stream(clazz.getDeclaredFields())
			.map(field -> new FieldInfo(field, field.getModifiers()))
			.filter(field -> field.field.getType() == clazz)
			.filter(field -> field.is(modifiers))
			.map(FieldInfo::field)
			.collect(Collectors.toList());
		// @formatter:on
	}
}