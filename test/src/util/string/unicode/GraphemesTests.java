package test.src.util.string.unicode;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import src.util.string.unicode.Graphemes;

public class GraphemesTests
{
	@Test
	public void testGraphemes()
	{
		var graphemes =
			new Graphemes("नमस्ते👨‍👩‍👧‍👦１２３４５６７８９０1234567890š我想吐<=;'`ḱṷṓnḱṷṓni⃰Ⅶ");
		var list = new ArrayList<>();
		graphemes.forEach(list::add);
		assertThat(list.size()).isEqualTo(43);
	}
	
	@Test
	public void testCreate()
	{
		var graphemes = new Graphemes("﷽", 0);
		var list = new ArrayList<>();
		graphemes.forEach(list::add);
		assertThat(list.size()).isEqualTo(1);
	}
}
