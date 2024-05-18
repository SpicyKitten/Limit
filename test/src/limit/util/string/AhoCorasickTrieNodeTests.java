package tests.limit.util.string;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import limit.core.token.Token;
import limit.util.string.AhoCorasickTrieNode;

public class AhoCorasickTrieNodeTests
{
	@Test
	public void testCreation()
	{
		var node = new AhoCorasickTrieNode();
		assertTrue(node != null);
	}
	
	@Test
	public void testAlphabet()
	{
		var node = new AhoCorasickTrieNode();
		var alphabet = node.alphabet();
		assertTrue(alphabet != null);
	}
	
	@Test
	public void testAddEntry()
	{
		var node = new AhoCorasickTrieNode();
		for(var entry : new String[]
			{ "aaa", "aab", "abaa", "bbb", "bcad" })
		{
			node.addEntry(entry);
		}
		var result = new ArrayList<>(node.alphabet());
		Collections.sort(result);
		assertEquals(result, List.of("a", "b", "c", "d"));
	}
	
	@Test
	public void testUnicode()
	{
		var node = new AhoCorasickTrieNode();
		for(var entry : new String[]
			{ "aaa", "aab", "abaa", "bbb", "bcad", "🤯", "何か" })
		{
			node.addEntry(entry);
		}
		var result = new ArrayList<>(node.alphabet());
		var correct = List.of("🤯", "a", "b", "c", "d", "何", "か");
		assertThat(result).containsExactlyInAnyOrderElementsOf(correct);
	}
	
	@Test
	public void testSearch()
	{
		var root = new AhoCorasickTrieNode();
		for(var token : Token.ALL_TOKENS)
		{
			root.addEntry(token.value());
		}
		root.addEntry("==>");
		root.construct();
		var result = root.search("==>==>");
		var correct = List.of(new int[]
			{ 0, 1 }, new int[]
			{ 0, 2 }, new int[]
			{ 1, 2 }, new int[]
			{ 0, 3 }, new int[]
			{ 1, 3 }, new int[]
			{ 2, 3 }, new int[]
			{ 2, 4 }, new int[]
			{ 3, 5 }, new int[]
			{ 3, 4 }, new int[]
			{ 4, 5 }, new int[]
			{ 3, 6 }, new int[]
			{ 4, 6 }, new int[]
			{ 5, 6 });
		assertThat(result).containsExactlyInAnyOrderElementsOf(correct);
	}
}
