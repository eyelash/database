import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DatabaseTest {
	@Test
	public void testMap() {
		Map map = new Map();
		map = map.put("one", "uno");
		map = map.put("two", "due");
		map = map.put("three", "tre");
		assertEquals("uno", map.get("one"));
		assertEquals("due", map.get("two"));
		assertEquals("tre", map.get("three"));
	}
}
