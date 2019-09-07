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

	@Test
	public void testDatabase() {
		Database database = new Database();
		database.writeTransaction(transaction -> {
			transaction.put("one", "uno");
			transaction.put("two", "due");
			transaction.put("three", "tre");
			return Database.TransactionResult.COMMIT;
		});
		database.readTransaction(transaction -> {
			assertEquals("uno", transaction.get("one"));
			assertEquals("due", transaction.get("two"));
			assertEquals("tre", transaction.get("three"));
		});
	}
}
