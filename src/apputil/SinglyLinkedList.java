package apputil;

import java.util.List;

public class SinglyLinkedList<T> {

    protected Node<T> headNode, tailNode;
    protected int size;

    public SinglyLinkedList(){
        headNode = null;
        tailNode = null;
        size = 0;
    }

    public SinglyLinkedList(List<T> list){
        this();
        for(T obj : list){
            addTail(obj);
        }
    }

    public SinglyLinkedList(List<T> list, boolean addToHead){
        this();
        for(T obj : list){
            addHead(obj);
        }
    }

    public int size(){
        return size;
    }

    public void addTail(T obj){
        Node<T> newNode = new Node<>(obj, null);
        if (tailNode != null) tailNode.setNext(newNode);
        tailNode = newNode;
        if(headNode == null) headNode = newNode;
        size++;
    }

    public void addHead(T obj){
        Node<T> newNode = new Node<>(obj, null);
        if (headNode != null) newNode.setNext(headNode);
        headNode = newNode;
        if(tailNode == null) tailNode = newNode;
        size++;
    }

    public void  removeHead() throws IllegalAccessException{
        if(size <= 0) throw new IllegalAccessException("Cause : Removing from an already empty list.");
        Node<T> newHead = headNode.getNext();
        headNode.setNext(null);
        headNode = newHead;
        size--;
    }

    public void  removeTail() throws IllegalAccessException {
        if (size <= 0) throw new IllegalAccessException("Cause : Removing from an already empty list.");
        Node<T> cursorNode = headNode;
        int counter = 0;
        while (counter < size){
            Node<T> next = cursorNode.getNext();
            if(next == tailNode) break;
            cursorNode = next;
            counter++;
        }
        cursorNode.setNext(null);
        tailNode = cursorNode;
        size--;
    }

    public void reverse(){
        Node<T> tempHolderNode = new Node<>(headNode.obj, headNode.getNext());
        Node<T> previousNode = tailNode;
        Node<T> currentNode = headNode;
        int i = 0;
        while(i < size()){
            tempHolderNode.setNext(headNode.getNext());
            headNode.setNext(tailNode.getNext());
            previousNode = headNode;
            i++;
        }

    }

    protected static class Node<T> {

        T obj;
        Node<T> next;

        Node(T obj, Node<T> nextPointer){
            this.obj = obj;
            next = nextPointer;
        }

        public Node<T> getNext() {
            return next;
        }

        public void setNext(Node<T> next) {
            this.next = next;
        }

        public T getObj() {
            return obj;
        }

        public void setObj(T obj) {
            this.obj = obj;
        }

    }
}
