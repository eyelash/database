public class Map {
	private static interface Node {
		String get(String key);
		Node[] put(String key, String value);
	}

	private static class Leaf implements Node {
		private final String[] keys;
		private final String[] values;
		private Leaf(int length) {
			keys = new String[length];
			values = new String[length];
		}
		private Leaf() {
			this(0);
		}
		@Override public String get(String key) {
			for (int i = 0; i < values.length; i++) {
				if (keys[i].equals(key)) {
					return values[i];
				}
			}
			return null;
		}
		@Override public Node[] put(String key, String value) {
			int index;
			for (index = 0; index < values.length; index++) {
				if (keys[index].compareTo(key) > 0) {
					break;
				}
			}
			Leaf leaf = new Leaf(values.length + 1);
			for (int i = 0; i < index; i++) {
				leaf.keys[i] = keys[i];
				leaf.values[i] = values[i];
			}
			leaf.keys[index] = key;
			leaf.values[index] = value;
			for (int i = index; i < values.length; i++) {
				leaf.keys[i + 1] = keys[i];
				leaf.values[i + 1] = values[i];
			}
			return new Node[] {leaf};
		}
	}

	private final Node root;

	private Map(Node root) {
		this.root = root;
	}

	public Map() {
		root = new Leaf();
	}

	public String get(String key) {
		return root.get(key);
	}

	public Map put(String key, String value) {
		Node[] newRoots = root.put(key, value);
		if (newRoots.length == 1) {
			return new Map(newRoots[0]);
		} else {
			return null;
		}
	}

	public Map delete(String key) {
		return null;
	}
}
