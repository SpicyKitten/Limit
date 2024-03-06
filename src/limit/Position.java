package limit;

public sealed interface Position<R>
	permits LexScope
{
	int position();
	
	R representation();
}
