package cw_final;

public class SimpleQueue<T> {
    private Object[] data;
    private int head;
    private int tail;
    private int size;

    public SimpleQueue() { this(8); }
    public SimpleQueue(int initialCapacity) {
        data = new Object[Math.max(2, initialCapacity)];
        head = tail = size = 0;
    }

    public boolean isEmpty() { return size == 0; }
    public int size() { return size; }

    //Enqueue
    public void enqueue(T value) {
        ensureCapacity(size + 1);
        data[tail] = value;
        tail = (tail + 1) % data.length;
        size++;
    }

    //Dequeue
    @SuppressWarnings("unchecked")
    public T dequeue() {
        if (isEmpty()) return null;
        T val = (T) data[head];
        data[head] = null;
        head = (head + 1) % data.length;
        size--;
        return val;
    }

    //Peek
    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) return null;
        return (T) data[head];
    }

    //Capacity
    private void ensureCapacity(int minCapacity) {
        if (minCapacity <= data.length) return;
        int newCap = data.length * 2;
        while (newCap < minCapacity) newCap *= 2;
        Object[] newData = new Object[newCap];
        for (int i = 0; i < size; i++) {
            newData[i] = data[(head + i) % data.length];
        }
        data = newData;
        head = 0;
        tail = size;
    }
}
