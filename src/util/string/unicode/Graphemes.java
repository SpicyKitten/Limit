package src.util.string.unicode;

import java.text.BreakIterator;

public class Graphemes implements Iterable<String>
{
	private BreakIterator boundary;
	private String contents;
	
	private Graphemes(BreakIterator iterator, String str)
	{
		this.boundary = iterator;
		this.boundary.setText(str);
		this.contents = str;
	}
	
	public Graphemes(String str, int index)
	{
		this(str.substring(index));
	}
	
	public Graphemes(String str)
	{
		this(BreakIterator.getCharacterInstance(), str);
	}
	
	@Override
	public java.util.Iterator<String> iterator()
	{
		return new Iterator();
	}
	
	private class Iterator implements java.util.Iterator<String>
	{
		private int start = Graphemes.this.boundary.first();
		private int end = Graphemes.this.boundary.next();
		
		@Override
		public String next()
		{
			var result = Graphemes.this.contents.substring(this.start, this.end);
			this.start = this.end;
			this.end = Graphemes.this.boundary.next();
			return result;
		}
		
		@Override
		public boolean hasNext()
		{
			return this.end != BreakIterator.DONE;
		}
	}
	
	public static String next(String str)
	{
		var graphemes = new Graphemes(str);
		return graphemes.iterator().next();
	}
}
