package limit.core.position;

public final class LexCursor implements Position<Void>
{
	private int position;
	
	public LexCursor(int position)
	{
		this.position = position;
	}
	
	public void advance(int N)
	{
		this.position += N;
	}
	
	public void backtrack(int N)
	{
		this.position -= N;
	}
	
	@Override
	public int position()
	{
		return this.position;
	}
	
	@Override
	public Void representation()
	{
		throw new UnsupportedOperationException("representation() is not supported for LexCursor!");
	}
}
