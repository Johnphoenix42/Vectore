package apputil;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Tree<T> {

    private final Node<T> root;

    public Tree(T rootValue) {
        root = new Node<>(rootValue, null, new ArrayList<>());
    }

    public NodePosition<T> add(T nodeVal, NodePosition<T> parent, Consumer<T> onAdd) {
        Node<T> nodeParent = (Node<T>) parent;
        Node<T> node = new Node<>(nodeVal, nodeParent, new ArrayList<>());
        nodeParent.getChildren().add(node);
        onAdd.accept(nodeVal);
        return node;
    }

    public NodePosition<T> getRoot() {
        return root;
    }

    private static class Node<T> implements NodePosition<T> {

        private final T value;
        private final Node<T> parent;
        private final ArrayList<Node<T>> children;

        public Node(T value, Node<T> parent, ArrayList<Node<T>> children) {
            this.value = value;
            this.parent = parent;
            this.children = children;
        }

        public Node<T> getParent() {
            return parent;
        }

        @Override
        public T getElement() {
            return value;
        }

        public ArrayList<Node<T>> getChildren() {
            return children;
        }
    }
}
