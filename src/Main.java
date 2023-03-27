import static java.lang.Thread.sleep;

import java.util.ArrayDeque;

public class Main {

    static ArrayDeque<Integer> queue = new ArrayDeque<Integer>();
    static int capacity = 5;

    static class Generator {

        private int step = 0;

        protected void write() {
            while (true) {
                synchronized (this) {
                    if (queue.size() >= capacity) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    queue.addLast(step);
                    System.out.println("Generated " + step++);
                    notifyAll();
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        protected void read() {
            while (true) {
                synchronized (this) {
                    if (queue.size() < 1) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    System.out.println("Value " + queue.removeFirst() + " was read.");
                    --step;
                    notifyAll();
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Generator generator = new Generator();

        Thread thread_g, thread_r;
        thread_g = new Thread(() -> {
            generator.write();
        }
        );
        thread_r = new Thread(() -> {
            generator.read();
        });
        thread_g.start();
        thread_r.start();
        thread_g.join();
        thread_r.join();
    }
}

