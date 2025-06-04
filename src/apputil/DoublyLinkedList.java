package apputil;

public class DoublyLinkedList<T> extends SinglyLinkedList<T> {

    Node<T> headNode, tailNode;
    int size = 0;

    @Override
    public void addHead(T obj) {
        Node<T> newNode = new Node<>(null, obj, null);
        if(headNode != null){
            headNode.setPrev(newNode);
            newNode.setNext(headNode);
        }
        headNode = newNode;
        if (tailNode == null) tailNode = newNode;
        size++;
    }

    static class Node<T> extends SinglyLinkedList.Node<T>{

        private Node<T> prev;

        Node(Node<T> previousPointer, T obj, SinglyLinkedList.Node<T> nextPointer) {
            super(obj, nextPointer);
            prev = previousPointer;
        }

        void setPrev(Node<T> prev) {
            this.prev = prev;
        }

        Node<T> getPrev(){
            return prev;
        }
    }

    public String drawTick(int n, int max, int labelNumber){
        if(n == max){
            if (max == 4) return " " + labelNumber;
            return "";
        }
        return "-" + (drawTick(n+1, max, labelNumber));
    }

}
