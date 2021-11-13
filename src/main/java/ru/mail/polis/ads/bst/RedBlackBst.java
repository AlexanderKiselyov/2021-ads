package ru.mail.polis.ads.bst;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * LLRB implementation of binary search tree.
 */
public class RedBlackBst<Key extends Comparable<Key>, Value>
        implements Bst<Key, Value> {

    private static final boolean BLACK = false;
    private static final boolean RED = true;
    private Node root;
    private int size;
    private Value removedValue;

    private class Node {
        Key key;
        Value value;
        Node left;
        Node right;
        boolean color;

        public Node(Key key, Value value, boolean color) {
            this.key = key;
            this.value = value;
            this.color = color;
        }
    }

    @Nullable
    @Override
    public Value get(@NotNull Key key) {
        return get(root, key);
    }

    private Value get(Node x, Key key) {
        if (x == null) {
            return null;
        }
        if (key.compareTo(x.key) < 0) {
            return get(x.left, key);
        }
        if (key.compareTo(x.key) > 0) {
            return get(x.right, key);
        }
        return x.value;
    }

    @Override
    public void put(@NotNull Key key, @NotNull Value value) {
        root = put(root, key, value);
        root.color = BLACK;
    }

    Node put(Node x, Key key, Value value) {
        if (x == null) {
            size++;
            return new Node(key, value, RED);
        }
        if (key.compareTo(x.key) < 0) {
            x.left = put(x.left, key, value);
        }
        else if (key.compareTo(x.key) > 0) {
            x.right = put(x.right, key, value);
        }
        else {
            x.value = value;
        }
        return fixUp(x);
    }

    @Nullable
    @Override
    public Value remove(@NotNull Key key) {
        root = delete(root, key);
        return removedValue;
    }

    private Node delete(Node x, Key key) {
        if (x == null) {
            removedValue = null;
            return null;
        }
        if (key.compareTo(x.key) < 0) {
            if (x.left != null) {
                if (!isRed(x.left) && !isRed(x.left.left)) {
                    x = moveRedLeft(x);
                }
                x.left = delete(x.left, key);
            }
        }
        else {
            if (isRed(x. left)) {
                x = rotateRight(x);
                x.right = delete(x.right, key);
            }
            else if (x.key.compareTo(key) == 0 && x.right == null) {
                size--;
                removedValue = x.value;
                return null;
            }
            else
            {
                if (x. right != null && !isRed(x.right) && !isRed(x.right.left)) {
                    x = moveRedRight(x);
                }
                if (x.key.compareTo(key) == 0) {
                    size--;
                    removedValue = x.value;
                    Node min = minNode(x.right);
                    x.key = min.key;
                    x.value = min.value;
                    x.right = deleteMin(x.right);
                }
                else {
                    x.right = delete(x.right, key);
                }
            }
        }
        return fixUp(x);
    }

    @Nullable
    @Override
    public Key min() {
        return root != null ? minNode(root).key : null;
    }

    @Nullable
    @Override
    public Value minValue() {
        return root != null ? minNode(root).value : null;
    }

    @Nullable
    @Override
    public Key max() {
        return root != null ? maxNode(root).key : null;
    }

    @Nullable
    @Override
    public Value maxValue() {
        return root != null ? maxNode(root).value : null;
    }

    @Nullable
    @Override
    public Key floor(@NotNull Key key) {
        Node floorNode = floor(root, key);
        return floorNode != null ? floorNode.key : null;
    }

    @Nullable
    @Override
    public Key ceil(@NotNull Key key) {
        Node ceilNode = ceil(root, key);
        return ceilNode != null ? ceilNode.key : null;
    }

    @Override
    public int size() {
        return size;
    }

    private boolean isRed(Node x) {
        return x != null && x.color == RED;
    }

    private Node rotateLeft(Node x) {
        Node y = x.right;
        x.right = y.left;
        y.left = x;
        y.color = x.color;
        x.color = RED;
        return y;
    }

    private Node rotateRight(Node y) {
        Node x = y.left;
        y.left = x.right;
        x.right = y;
        x.color = y.color;
        y.color = RED;
        return x;
    }

    private Node flipColors(Node x) {
        x.color = !x.color;
        x.left.color = !x.left.color;
        x.right.color = !x.right.color;
        return x;
    }

    private Node fixUp(Node x) {
        if (isRed(x.right) && !isRed(x.left)) {
            x = rotateLeft(x);
        }
        if (isRed(x.left) && isRed(x.left.left)) {
            x = rotateRight(x);
        }
        if (isRed(x.left) && isRed(x.right)) {
            flipColors(x);
        }
        return x;
    }

    private Node moveRedLeft(Node x) {
        flipColors(x);
        if (isRed(x.right.left)) {
            x.right = rotateRight(x.right);
            x = rotateLeft(x);
            flipColors(x);
        }
        return x;
    }

    private Node moveRedRight(Node x) {
        flipColors(x);
        if (isRed(x.left.left)) {
            x = rotateRight(x);
            flipColors(x);
        }
        return x;
    }

    private Node deleteMin(Node x) {
        if (x.left == null) {
            return null;
        }
        if (!isRed(x.left) && !isRed(x.left.left)) {
            x = moveRedLeft(x);
        }
        x.left = deleteMin(x.left);
        return fixUp(x);
    }

    private Node minNode(Node x) {
        if (x.left == null) {
            return x;
        }
        return minNode(x.left);
    }

    private Node maxNode(Node x) {
        if (x.right == null) {
            return x;
        }
        return maxNode(x.right);
    }

    private Node floor(Node x, Key key) {
        if (x == null) {
            return null;
        }
        if (key.compareTo(x.key) == 0) {
            return x;
        }
        if (key.compareTo(x.key) < 0) {
            return floor(x.left, key);
        }
        Node rightNode = floor(x.right, key);
        return (rightNode == null || key.compareTo(rightNode.key) < 0) ? x : rightNode;
    }

    private Node ceil(Node x, Key key) {
        if (x == null) {
            return null;
        }
        if (key.compareTo(x.key) == 0) {
            return x;
        } else if (key.compareTo(x.key) > 0) {
            return ceil(x.right, key);
        }
        Node leftNode = ceil(x.left, key);
        return (leftNode == null || key.compareTo(leftNode.key) > 0) ? x : leftNode;
    }
}