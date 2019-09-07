public class Map {
	private static final int MIN_LENGTH = 2;
	private static final int MAX_LENGTH = 3;

	private static interface Node {
		String getFirstKey();
		String get(String key);
		Node[] put(String key, String value);
		Node delete(String key);
		boolean needsRebalance();
		Node[] rebalanceRight(Node right);
		Node[] rebalanceLeft(Node left);
	}

	private static <T> void arraysSet(T[] array0, T[] array1, int index, T value) {
		if (index < array0.length) {
			array0[index] = value;
		} else {
			array1[index - array0.length] = value;
		}
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
					arraysSet(leaf0.keys, leaf1.keys, i, keys[i]);
					arraysSet(leaf0.values, leaf1.values, i, values[i]);
				}
				arraysSet(leaf0.keys, leaf1.keys, index, key);
				arraysSet(leaf0.values, leaf1.values, index, value);
				for (int i = index; i < values.length; i++) {
					arraysSet(leaf0.keys, leaf1.keys, i + 1, keys[i]);
					arraysSet(leaf0.values, leaf1.values, i + 1, values[i]);
				}
				return new Node[] {leaf0, leaf1};
			}
		}
		@Override public Node delete(String key) {
			int index;
			for (index = 0; index < values.length; index++) {
				if (keys[index].equals(key)) {
					break;
				}
			}
			if (index == values.length) {
				return this;
			}
			Leaf leaf = new Leaf(values.length - 1);
			for (int i = 0; i < index; i++) {
				leaf.keys[i] = keys[i];
				leaf.values[i] = values[i];
			}
			for (int i = index + 1; i < values.length; i++) {
				leaf.keys[i - 1] = keys[i];
				leaf.values[i - 1] = values[i];
			}
			return leaf;
		}
		@Override public boolean needsRebalance() {
			return values.length < MIN_LENGTH;
		}
		@Override public Node[] rebalanceRight(Node rightNode) {
			Leaf right = (Leaf)rightNode;
			if (values.length + right.values.length <= MAX_LENGTH) {
				Leaf leaf = new Leaf(values.length + right.values.length);
				for (int i = 0; i < values.length; i++) {
					leaf.keys[i] = keys[i];
					leaf.values[i] = values[i];
				}
				for (int i = 0; i < right.values.length; i++) {
					leaf.keys[values.length + i] = right.keys[i];
					leaf.values[values.length + i] = right.values[i];
				}
				return new Node[] {leaf};
			} else {
				Leaf leaf0 = new Leaf(values.length + 1);
				Leaf leaf1 = new Leaf(right.values.length - 1);
				for (int i = 0; i < values.length; i++) {
					leaf0.keys[i] = keys[i];
					leaf0.values[i] = values[i];
				}
				leaf0.keys[values.length] = right.keys[0];
				leaf0.values[values.length] = right.values[0];
				for (int i = 1; i < right.values.length; i++) {
					leaf1.keys[i - 1] = right.keys[i];
					leaf1.values[i - 1] = right.values[i];
				}
				return new Node[] {leaf0, leaf1};
			}
		}
		@Override public Node[] rebalanceLeft(Node leftNode) {
			Leaf left = (Leaf)leftNode;
			if (left.values.length + values.length <= MAX_LENGTH) {
				Leaf leaf = new Leaf(left.values.length + values.length);
				for (int i = 0; i < left.values.length; i++) {
					leaf.keys[i] = left.keys[i];
					leaf.values[i] = left.values[i];
				}
				for (int i = 0; i < values.length; i++) {
					leaf.keys[left.values.length + i] = keys[i];
					leaf.values[left.values.length + i] = values[i];
				}
				return new Node[] {leaf};
			} else {
				Leaf leaf0 = new Leaf(left.values.length - 1);
				Leaf leaf1 = new Leaf(values.length + 1);
				for (int i = 0; i < left.values.length - 1; i++) {
					leaf0.keys[i] = left.keys[i];
					leaf0.values[i] = left.values[i];
				}
				leaf1.keys[0] = left.keys[left.values.length - 1];
				leaf1.values[0] = left.values[left.values.length - 1];
				for (int i = 0; i < values.length; i++) {
					leaf1.keys[i + 1] = keys[i];
					leaf1.values[i + 1] = values[i];
				}
				return new Node[] {leaf0, leaf1};
			}
		}
	}

	private static class INode implements Node {
		private final String firstKey;
		private final Node[] children;
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
				Node[] children0 = new Node[children.length];
				for (int i = 0; i < index; i++) {
					children0[i] = children[i];
				}
				children0[index] = newChildren[0];
				for (int i = index + 1; i < children.length; i++) {
					children0[i] = children[i];
				}
				return new Node[] {new INode(children0)};
			} else {
				assert newChildren.length == 2;
				if (children.length < MAX_LENGTH) {
					Node[] children0 = new Node[children.length + 1];
					for (int i = 0; i < index; i++) {
						children0[i] = children[i];
					}
					children0[index] = newChildren[0];
					children0[index + 1] = newChildren[1];
					for (int i = index + 1; i < children.length; i++) {
						children0[i + 1] = children[i];
					}
					return new Node[] {new INode(children0)};
				} else {
					Node[] children0 = new Node[MIN_LENGTH];
					Node[] children1 = new Node[MIN_LENGTH];
					assert MIN_LENGTH + MIN_LENGTH == children.length + 1;
					for (int i = 0; i < index; i++) {
						arraysSet(children0, children1, i, children[i]);
					}
					arraysSet(children0, children1, index, newChildren[0]);
					arraysSet(children0, children1, index + 1, newChildren[1]);
					for (int i = index + 1; i < children.length; i++) {
						arraysSet(children0, children1, i + 1, children[i]);
					}
					return new Node[] {new INode(children0), new INode(children1)};
				}
			}
		}
		@Override public Node delete(String key) {
			int index;
			for (index = 0; index < children.length - 1; index++) {
				if (children[index + 1].getFirstKey().compareTo(key) > 0) {
					break;
				}
			}
			Node newChild = children[index].delete(key);
			if (newChild.needsRebalance()) {
				Node[] newChildren;
				if (index == 0) {
					newChildren = newChild.rebalanceRight(children[index + 1]);
				} else {
					index--;
					newChildren = newChild.rebalanceLeft(children[index]);
				}
				if (newChildren.length == 2) {
					Node[] children0 = new Node[children.length];
					for (int i = 0; i < index; i++) {
						children0[i] = children[i];
					}
					children0[index] = newChildren[0];
					children0[index + 1] = newChildren[1];
					for (int i = index + 2; i < children.length; i++) {
						children0[i] = children[i];
					}
					return new INode(children0);
				} else {
					assert newChildren.length == 1;
					Node[] children0 = new Node[children.length - 1];
					for (int i = 0; i < index; i++) {
						children0[i] = children[i];
					}
					children0[index] = newChildren[0];
					for (int i = index + 2; i < children.length; i++) {
						children0[i - 1] = children[i];
					}
					return children0.length == 1 ? children0[0] : new INode(children0);
				}
			} else {
				Node[] children0 = new Node[children.length];
				for (int i = 0; i < index; i++) {
					children0[i] = children[i];
				}
				children0[index] = newChild;
				for (int i = index + 1; i < children.length; i++) {
					children0[i] = children[i];
				}
				return new INode(children0);
			}
		}
		@Override public boolean needsRebalance() {
			return children.length < MIN_LENGTH;
		}
		@Override public Node[] rebalanceRight(Node rightNode) {
			INode right = (INode)rightNode;
			if (children.length + right.children.length <= MAX_LENGTH) {
				Node[] children0 = new Node[children.length + right.children.length];
				for (int i = 0; i < children.length; i++) {
					children0[i] = children[i];
				}
				for (int i = 0; i < right.children.length; i++) {
					children0[children.length + i] = right.children[i];
				}
				return new Node[] {new INode(children0)};
			} else {
				Node[] children0 = new Node[children.length + 1];
				Node[] children1 = new Node[right.children.length - 1];
				for (int i = 0; i < children.length; i++) {
					children0[i] = children[i];
				}
				children0[children.length] = right.children[0];
				for (int i = 1; i < right.children.length; i++) {
					children1[i - 1] = right.children[i];
				}
				return new Node[] {new INode(children0), new INode(children1)};
			}
		}
		@Override public Node[] rebalanceLeft(Node leftNode) {
			INode left = (INode)leftNode;
			if (left.children.length + children.length <= MAX_LENGTH) {
				Node[] children0 = new Node[left.children.length + children.length];
				for (int i = 0; i < left.children.length; i++) {
					children0[i] = left.children[i];
				}
				for (int i = 0; i < children.length; i++) {
					children0[left.children.length + i] = children[i];
				}
				return new Node[] {new INode(children0)};
			} else {
				Node[] children0 = new Node[left.children.length - 1];
				Node[] children1 = new Node[children.length + 1];
				for (int i = 0; i < left.children.length - 1; i++) {
					children0[i] = left.children[i];
				}
				children1[0] = left.children[left.children.length - 1];
				for (int i = 0; i < children.length; i++) {
					children1[i + 1] = children[i];
				}
				return new Node[] {new INode(children0), new INode(children1)};
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
		Node newRoot = root.delete(key);
		return new Map(newRoot);
	}
}
