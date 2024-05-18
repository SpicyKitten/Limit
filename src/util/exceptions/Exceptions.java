package src.util.exceptions;

import java.util.Arrays;

public class Exceptions
{
	public static void notImplemented()
	{
		var frame = StackWalker.getInstance().walk(s -> s.skip(1).findFirst()).get();
		var name = frame.getMethodName();
		var file = frame.getFileName();
		var line = frame.getLineNumber();
		var message = "method \"%s\" in (%s:%s) not implemented yet!";
		var exception =
			new UnsupportedOperationException(message.formatted(name, file, line));
		var stackTrace = exception.getStackTrace();
		exception.setStackTrace(Arrays.copyOfRange(stackTrace, 1, stackTrace.length));

		throw exception;
	}
}
