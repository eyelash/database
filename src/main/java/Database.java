import java.util.function.Consumer;
import java.util.function.Function;

public class Database {
	public static class ReadTransaction {
		private Map map;
		private ReadTransaction(Map map) {
			this.map = map;
		}
		public String get(String key) {
			return map.get(key);
		}
	}
	public static enum TransactionResult {
		COMMIT, ROLLBACK
	}
	public static class WriteTransaction {
		private Map map;
		private WriteTransaction(Map map) {
			this.map = map;
		}
		public String get(String key) {
			return map.get(key);
		}
		public void put(String key, String value) {
			map = map.put(key, value);
		}
		public void delete(String key) {
			map = map.delete(key);
		}
	}

	private Map map;

	public Database() {
		map = new Map();
	}

	public void readTransaction(Consumer<ReadTransaction> consumer) {
		ReadTransaction transaction = new ReadTransaction(map);
		consumer.accept(transaction);
	}

	public synchronized void writeTransaction(Function<WriteTransaction, TransactionResult> function) {
		WriteTransaction transaction = new WriteTransaction(map);
		if (function.apply(transaction) == TransactionResult.COMMIT) {
			map = transaction.map;
		}
	}
}
