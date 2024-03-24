/**
 * @author SpicyKitten<rathavilash@gmail.com>
 */
open module limit
{
	requires avi.utils.throwing;
	requires org.junit.jupiter.api;
	requires org.assertj.core;
	requires org.junit.platform.commons;
	requires it.unimi.dsi.fastutil;
	requires com.ibm.icu;
	
	exports limit.core.exception;
	exports limit.core.lexer;
	exports limit.core.parser;
	exports limit.core.position;
	exports limit.core.shell;
	exports limit.core.token;
	exports limit.util.operations;
	exports limit.util.reflection;
	exports limit.util.string;
	exports limit.util.string.unicode;
}