public class Work2 {
    public static void main(String[] args) {

        System.out.println("Дія\t\t\tБуфер");

        SynchronizedBuffer buffer = new SynchronizedBuffer(3);
        buffer.displayState("Початковий стан");

        Producer p1 = new Producer(buffer, 3);
        p1.setName("Producer_1");

        Producer p2 = new Producer(buffer, 3);
        p2.setName("Producer_2");

        Consumer c1 = new Consumer(buffer, 2);
        c1.setName("Consumer_1");

        Consumer c2 = new Consumer(buffer, 2);
        c2.setName("Consumer_2");

        Consumer c3 = new Consumer(buffer, 2);
        c3.setName("Consumer_3");

        p1.start();
        p2.start();

        c1.start();
        c2.start();
        c3.start();
    }
}
class SynchronizedBuffer {
    private int[] buffer;
    private int count = 0;
    private int in = 0;
    private int out = 0;

    public SynchronizedBuffer(int size) {
        buffer = new int[size];
    }

    public synchronized void set(int value) {
        while (count == buffer.length) {
            try { wait(); } catch (Exception e) {}
        }

        buffer[in] = value;
        in = (in + 1) % buffer.length;
        count++;

        displayState(Thread.currentThread().getName() + " записує " + value);

        notifyAll();
    }

    public synchronized int get() {
        while (count == 0) {
            try { wait(); } catch (Exception e) {}
        }

        int value = buffer[out];
        buffer[out] = 0;
        out = (out + 1) % buffer.length;
        count--;

        displayState(Thread.currentThread().getName() + " зчитує " + value);

        notifyAll();

        return value;
    }

    public void displayState(String action) {
        System.out.print(action + "\t");
        for (int i : buffer)
            System.out.print(i + " ");
        System.out.println();
    }
}

class Producer extends Thread {
    private SynchronizedBuffer buffer;
    private int n;

    public Producer(SynchronizedBuffer b, int n) {
        buffer = b;
        this.n = n;
    }

    public void run() {
        for (int i = 1; i <= n; i++) {
            try { sleep((int)(Math.random()*1000)); } catch(Exception e){}
            buffer.set(i);
        }
    }
}

class Consumer extends Thread {
    private SynchronizedBuffer buffer;
    private int n;

    public Consumer(SynchronizedBuffer b, int n) {
        buffer = b;
        this.n = n;
    }

    public void run() {
        for (int i = 0; i < n; i++) {
            try { sleep((int)(Math.random()*1000)); } catch(Exception e){}
            buffer.get();
        }
    }
}