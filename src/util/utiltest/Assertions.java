package src.util.utiltest;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

public class Assertions
{
	static void assertFalse(boolean condition)
	{
		if(condition)
		{
			throw new AssertionError("condition was true!");
		}
	}
	
	static void assertTrue(boolean condition)
	{
		if(!condition)
		{
			throw new AssertionError("condition was false!");
		}
	}
	
	static void assertEquals(Object o1, Object o2)
	{
		if(o1 == null && o2 == null)
		{
			return;
		}
		if(o1 == null || o2 == null || !o1.equals(o2))
		{
			throw new AssertionError("%s!= %s".formatted(o1, o2));
		}
	}
	
	static void assertEquals(Collection<?> c1, Collection<?> c2)
	{
		assertEquals(c1.stream(), c2.stream());
	}
	
	static void assertEquals(Stream<?> s1, Stream<?> s2)
	{
		Iterator<?> iter1 = s1.iterator(), iter2 = s2.iterator();
		while(iter1.hasNext() && iter2.hasNext())
		{
			assertEquals(iter1.next(), iter2.next());
		}
		assertTrue(!iter1.hasNext() && !iter2.hasNext());
	}
}
