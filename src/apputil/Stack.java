package apputil;

import java.util.EmptyStackException;

public interface Stack<E> {

    public int size();

    boolean isEmpty();

    public void push(E e);

    public E pop() throws EmptyStackException;

    public E peek() throws EmptyStackException;

    public void fill(E ...e);
}
