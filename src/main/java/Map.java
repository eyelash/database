public class Map {
	private static final int MIN_LENGTH = 2;
	private static final int MAX_LENGTH = 3;

	private static interface Node {
		String getFirstKey();
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
		@Override public String getFirstKey() {
			return keys[0];
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
			if (values.length < MAX_LENGTH) {
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
			} else {
				Leaf leaf0 = new Leaf(MIN_LENGTH);
				Leaf leaf1 = new Leaf(MIN_LENGTH);
				assert MIN_LENGTH + MIN_LENGTH == values.length + 1;
				for (int i = 0; i < index; i++) {
					if (i < MIN_LENGTH) {
						leaf0.keys[i] = keys[i];
						leaf0.values[i] = values[i];
					} else {
						leaf1.keys[i - MIN_LENGTH] = keys[i];
						leaf1.values[i - MIN_LENGTH] = values[i];
					}
				}
				if (index < MIN_LENGTH) {
					leaf0.keys[index] = key;
					leaf0.values[index] = value;
				} else {
					leaf1.keys[index - MIN_LENGTH] = key;
					leaf1.values[index - MIN_LENGTH] = value;
				}
				for (int i = index; i < values.length; i++) {
					if (i + 1 < MIN_LENGTH) {
						leaf0.keys[i + 1] = keys[i];
						leaf0.values[i + 1] = values[i];
					} else {
						leaf1.keys[i + 1 - MIN_LENGTH] = keys[i];
						leaf1.values[i + 1 - MIN_LENGTH] = values[i];
					}
				}
				return new Node[] {leaf0, leaf1};
			}
		}
	}

	private static class INode implements Node {
		private String firstKey;
		private Node[] children;
		private INode(int length) {
			children = new Node[length];
		}
		private INode(Node[] children) {
			firstKey = children[0].getFirstKey();
			this.children = children;
		}
		@Override public String getFirstKey() {
			return firstKey;
		}
		@Override public String get(String key) {
			int index;
			for (index = 0; index < children.length - 1; index++) {
				if (children[index + 1].getFirstKey().compareTo(key) > 0) {
					break;
				}
			}
			return children[index].get(key);
		}
		@Override public Node[] put(String key, String value) {
			int index;
			for (index = 0; index < children.length - 1; index++) {
				if (children[index + 1].getFirstKey().compareTo(key) > 0) {
					break;
				}
			}
			Node[] newChildren = children[index].put(key, value);
			if (newChildren.length == 1) {
				INode node = new INode(children.length);
				for (int i = 0; i < index; i++) {
					node.children[i] = children[i];
				}
				node.children[index] = newChildren[0];
				for (int i = index + 1; i < children.length; i++) {
					node.children[i] = children[i];
				}
				node.firstKey = node.children[0].getFirstKey();
				return new Node[] {node};
			} else {
				assert newChildren.length == 2;
				if (children.length < MAX_LENGTH) {
					INode node = new INode(children.length + 1);
					for (int i = 0; i < index; i++) {
						node.children[i] = children[i];
					}
					node.children[index] = newChildren[0];
					node.children[index + 1] = newChildren[1];
					for (int i = index + 1; i < children.length; i++) {
						node.children[i + 1] = children[i];
					}
					node.firstKey = node.children[0].getFirstKey();
					return new Node[] {node};
				} else {
					INode node0 = new INode(MIN_LENGTH);
					INode node1 = new INode(MIN_LENGTH);
					assert MIN_LENGTH + MIN_LENGTH == children.length + 1;
					for (int i = 0; i < index; i++) {
						if (i < MIN_LENGTH) {
							node0.children[i] = children[i];
						} else {
							node1.children[i - MIN_LENGTH] = children[i];
						}
					}
					if (index < MIN_LENGTH) {
						node0.children[index] = newChildren[0];
					} else {
						node1.children[index - MIN_LENGTH] = newChildren[0];
					}
					if (index + 1 < MIN_LENGTH) {
						node0.children[index + 1] = newChildren[1];
					} else {
						node1.children[index + 1 - MIN_LENGTH] = newChildren[1];
					}
					for (int i = index + 1; i < children.length; i++) {
						if (i + 1 < MIN_LENGTH) {
							node0.children[i + 1] = children[i];
						} else {
							node1.children[i + 1 - MIN_LENGTH] = children[i];
						}
					}
					node0.firstKey = node0.children[0].getFirstKey();
					node1.firstKey = node1.children[0].getFirstKey();
					return new Node[] {node0, node1};
				}
			}
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
			return new Map(new INode(newRoots));
		}
	}

	public Map delete(String key) {
		return null;
	}
}
