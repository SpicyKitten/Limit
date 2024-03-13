package limit.util.string;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AugmentedTrieNodeTests
{
	@Test
	public void testCreation()
	{
		var node = new AugmentedTrieNode();
		Assertions.assertTrue(node != null);
	}
}
