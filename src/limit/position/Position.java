package limit.core.position;

public sealed interface Position<R>
	permits LexScope, LexCursor
{
	int position();
	
	R representation();
}
