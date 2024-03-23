package limit.util.string;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntConsumer;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import limit.core.token.Token;

/**
 * Aho-Corasick implementation. Based on <a href=
 * "https://github.com/kunigami/blog-examples/blob/master/aho-corasick/aho_corasick.py">a
 * Python implementation</a>
 *
 * @author SpicyKitten<rathavilash@gmail.com>
 * 
 */
public class AhoCorasickTrieNode
{
	/** nodes accessible via successful transitions */
	private Int2ObjectOpenHashMap<AhoCorasickTrieNode> children;
	/**
	 * nodes accessible via failure transitions, taking into account
	 * whether the subsequent transition is successful as well
	 */
	private Int2ObjectOpenHashMap<AhoCorasickTrieNode> failures;
	/** The String formed by traversing to this point in the trie */
	private String entry;
	/**
	 * The trie node corresponding to the longest suffix of {@link #entry}
	 */
	private AhoCorasickTrieNode suffix;
	/**
	 * The codepoints of all possible transitions for this trie
	 * (root-only)
	 */
	private IntSet alphabet;
	/** Book-keeping for node traversal: parent node */
	private AhoCorasickTrieNode parent;
	/** Book-keeping for construction */
	private boolean constructed;
	/** Dictionary link **/
	private AhoCorasickTrieNode next;
	
	public AhoCorasickTrieNode()
	{
		this.children = new Int2ObjectOpenHashMap<>();
		this.failures = new Int2ObjectOpenHashMap<>();
		this.entry = null;
		this.suffix = this;
		this.parent = this;
		this.constructed = false;
		this.next = null;
	}
	
	public List<String> matches()
	{
		var result = new ArrayList<String>();
		var resultSource = this;
		while(resultSource != null)
		{
			if(resultSource.entry != null)
			{
				result.add(resultSource.entry);
			}
			resultSource = resultSource.next;
		}
		return result;
	}
	
	public Set<String> alphabet()
	{
		if(!root())
		{
			throw new IllegalStateException("Called alphabet() on non-root node!");
		}
		if(this.alphabet == null)
		{
			return Set.of();
		}
		// @formatter:off
		return this.alphabet.intStream()
			.mapToObj(Character::toString)
			.collect(Collectors.toSet());
		// @formatter:on
	}
	
	public boolean constructed()
	{
		return this.constructed;
	}
	
	private void constructed(boolean constructed)
	{
		this.constructed = constructed;
	}
	
	public boolean root()
	{
		return this.parent == this;
	}
	
	public boolean hasFailure(int codepoint)
	{
		return this.failures.containsKey(codepoint);
	}
	
	public AhoCorasickTrieNode getFailure(int codepoint)
	{
		return this.failures.get(codepoint);
	}
	
	public boolean hasChild(int codepoint)
	{
		return this.children.containsKey(codepoint);
	}
	
	public AhoCorasickTrieNode getChild(int codepoint)
	{
		return this.children.get(codepoint);
	}
	
	private void createChild(int codepoint)
	{
		var child = new AhoCorasickTrieNode();
		addChild(codepoint, child);
	}
	
	private void addChild(int codepoint, AhoCorasickTrieNode child)
	{
		this.children.put(codepoint, child);
		child.parent = this;
	}
	
	public void addEntry(String entry)
	{
		if(!root())
		{
			throw new IllegalStateException("Can't add entries to a non-root node!");
		}
		if(this.alphabet == null)
		{
			this.alphabet = new IntOpenHashSet();
		}
		constructed(false);
		var delegate = new AhoCorasickTrieNode[]
			{ this };
		var lambda = (IntConsumer) (var c) -> {
			if(!delegate[0].hasChild(c))
			{
				delegate[0].createChild(c);
			}
			delegate[0] = delegate[0].getChild(c);
			this.alphabet.add(c);
		};
		entry.codePoints().forEachOrdered(lambda);
		delegate[0].entry = entry;
	}
	
	public void construct()
	{
		if(!root())
		{
			throw new IllegalStateException("Can't construct at a non-root node!");
		}
		if(this.constructed())
		{
			return;
		}
		var queue = new ObjectArrayFIFOQueue<AhoCorasickTrieNode>();
		queue.enqueue(this);
		while(!queue.isEmpty())
		{
			var node = queue.dequeue();
			this.alphabet.forEach((var codepoint) -> {
				var suffix = node.suffix;
				while(suffix != this && !suffix.hasChild(codepoint))
				{
					suffix = suffix.suffix;
				}
				/* failure transition depends on suffix link destination */
				var jumpNode = suffix.hasChild(codepoint) ? suffix.getChild(codepoint) : this;
				if(!node.hasChild(codepoint))
				{
					node.failures.put(codepoint, jumpNode);
					return;
				}
				var child = node.getChild(codepoint);
				if(jumpNode == child)
				{
					jumpNode = this;
				}
				child.suffix = jumpNode;
				child.next = jumpNode.entry != null ? jumpNode : jumpNode.next;
				queue.enqueue(child);
			});
		}
		this.constructed(true);
	}
	
	public List<int[]> search(String text)
	{
		if(!this.constructed)
		{
			construct();
		}
		var result = new ArrayList<int[]>();
		var codepointIterator = text.codePoints().iterator();
		var current = this;
		for(var index = 0; codepointIterator.hasNext(); index++)
		{
			var currentIndex = index;
			var codepoint = codepointIterator.nextInt();
			if(current.hasChild(codepoint))
			{
				current = current.getChild(codepoint);
			}
			else if(current.hasFailure(codepoint))
			{
				current = current.getFailure(codepoint);
			}
			else
			{
				current = this;
			}
			var matches = current.matches();
			if(!matches.isEmpty())
			{
				matches.forEach(match -> {
					result.add(new int[]
						{ currentIndex - match.length() + 1, currentIndex + 1 });
				});
			}
		}
		return result;
	}
	
	public String toString(AhoCorasickTrieNode node, int level)
	{
		var repr = this.entry == null ? "-" : "Node(%s)".formatted(this.entry);
		var result = new StringBuilder().append(repr);
		for(var child : this.children.int2ObjectEntrySet())
		{
			result.append("\n").append("  ".repeat(level))
				.append("%s: ".formatted(Character.toString(child.getIntKey())))
				.append(child.getValue().toString(node, level + 1));
		}
		return result.toString();
	}
	
	@Override
	public String toString()
	{
		return "<root>: %s".formatted(toString(this, 1));
	}
	
	public static void main(String[] args)
	{
		var root = new AhoCorasickTrieNode();
		for(var token : Token.ALL_TOKENS)
		{
			root.addEntry(token.value());
		}
		root.addEntry("==>");
		root.construct();
		var results = root.search("==>==>");
		for(var result : results)
		{
			System.out.println(Arrays.toString(result));
			System.out.println("==>==>".substring(result[0], result[1]));
		}
		System.out.println(root);
		System.out.println(root.alphabet());
	}
}
