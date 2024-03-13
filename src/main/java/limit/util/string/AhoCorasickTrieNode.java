package main.java.limit.util.string;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class AhoCorasickTrieNode
{
	/* */
	private Int2ObjectOpenHashMap<AhoCorasickTrieNode> children;
	private Int2ObjectOpenHashMap<AhoCorasickTrieNode> failure;
	
	public AhoCorasickTrieNode()
	{
		this.children = new Int2ObjectOpenHashMap<>();
	}
}
