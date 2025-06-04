package apputil;

import java.util.EmptyStackException;

public class ArrayStack<E> implements Stack<E> {

    private final static int CAPACITY = 100;
    private final E[] baseArray;
    private int index;
    private final int capacity;

    public ArrayStack(){
        this(CAPACITY);
    }

    public ArrayStack(int capacity){
        this.capacity = capacity;
        baseArray = (E[]) new Object[capacity];
        index = 0;
    }

    @Override
    public int size() {
        return index;
    }

    @Override
    public boolean isEmpty() {
        return index <= 0;
    }

    @Override
    public void push(E e) {
        if (size() == capacity) throw new ArrayIndexOutOfBoundsException();
        baseArray[index] = e;
        index++;
    }

    @Override
    public E pop() throws EmptyStackException {
        if (isEmpty()) throw new EmptyStackException();
        index--;
        E e = baseArray[index];
        baseArray[index] = null;
        return e;
    }

    @Override
    public E peek() throws EmptyStackException {
        if (this.isEmpty()) throw new EmptyStackException();
        return baseArray[index - 1];
    }

    @SafeVarargs
    @Override
    public final void fill(E... e) throws ArrayIndexOutOfBoundsException{
        if(e.length > capacity - index) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int t = index;
        for(int i = 0; i < e.length; i++){
            baseArray[t + i] = e[i];
            index++;
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(this.getClass().getName() + "\n");
        for(E e : baseArray)
            stringBuilder.append(e).append(" ");
        return stringBuilder.toString();
    }

    public E[] getBaseArray(){
        return baseArray;
    }
}
