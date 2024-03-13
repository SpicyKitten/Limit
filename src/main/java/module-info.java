/**
 * @author SpicyKitten<rathavilash@gmail.com>
 */
open module limit
{
	requires avi.utils.throwing;
	
	exports limit.core.exception;
	exports limit.core.lexer;
	exports limit.core.parser;
	exports limit.core.position;
	exports limit.core.shell;
	exports limit.core.token;
}