/**
 * СП та ОС. Лабораторна робота №3
 *
 * Варіант 10:
 * Процесів-виробників  – 2
 * Процесів-споживачів – 3
 *
 * Інформація, що поступає до споживачів:
 * Споживач_1 -> 1,3
 * Споживач_2 -> 2,3
 * Споживач_3 -> 1,2,3
 */

public class Work2 {

    public static void main(String[] args) {

        /**
         * Виведення заголовків таблиці
         */
        StringBuffer columnHeads =
                new StringBuffer("Дія\t\t\t\tБуфер\tКількість елементів\n");

        System.out.println(columnHeads);

        /**
         * Створення буферного пулу розміром 3
         */
        SynchronizedBuffer sharedLocation =
                new SynchronizedBuffer(3);

        sharedLocation.displayState("Початковий стан\t\t");

        /**
         * Створення виробників
         */
        Producer producer1 =
                new Producer(sharedLocation, 3);

        producer1.setName("Виробник_1");

        Producer producer2 =
                new Producer(sharedLocation, 3);

        producer2.setName("Виробник_2");

        /**
         * Створення споживачів
         */
        Consumer consumer1 =
                new Consumer(sharedLocation, 2);

        consumer1.setName("Споживач_1");

        Consumer consumer2 =
                new Consumer(sharedLocation, 2);

        consumer2.setName("Споживач_2");

        Consumer consumer3 =
                new Consumer(sharedLocation, 2);

        consumer3.setName("Споживач_3");

        /**
         * Запуск потоків
         */
        producer1.start();
        producer2.start();

        consumer1.start();
        consumer2.start();
        consumer3.start();
    }
}

/**
 * Клас кільцевого буфера
 */
class SynchronizedBuffer {

    /**
     * Максимальний розмір буфера
     */
    private int maxSize;

    /**
     * Масив елементів буфера
     */
    private int[] buffer;

    /**
     * Поточна кількість елементів
     */
    private int elementCount;

    /**
     * Позиція для читання
     */
    private int getPosition;

    /**
     * Позиція для запису
     */
    private int setPosition;

    /**
     * Конструктор буфера
     */
    public SynchronizedBuffer(int size) {

        maxSize = size;

        buffer = new int[maxSize];

        elementCount = 0;

        getPosition = 0;

        setPosition = 0;
    }

    /**
     * Метод запису елемента у буфер
     */
    public synchronized void set(int value) {

        /**
         * Ім’я поточного потоку
         */
        String name =
                Thread.currentThread().getName();

        /**
         * Якщо буфер повний — чекати
         */
        while (elementCount == maxSize) {

            try {

                System.out.println(name +
                        " робить спробу запису.");

                displayState("Буфер повний. "
                        + name + " чекає.\t");

                wait();
            }

            catch (InterruptedException exception) {

                exception.printStackTrace();
            }
        }

        /**
         * Запис елемента
         */
        buffer[setPosition] = value;

        elementCount++;

        displayState(name +
                " записує " + value + "\t\t");

        /**
         * Перехід до наступної позиції
         */
        if (setPosition == maxSize - 1)
            setPosition = 0;
        else
            setPosition++;

        /**
         * Повідомлення потоків
         */
        notifyAll();
    }

    /**
     * Метод читання елемента з буфера
     */
    public synchronized int get() {

        /**
         * Ім’я поточного потоку
         */
        String name =
                Thread.currentThread().getName();

        /**
         * Якщо буфер порожній — чекати
         */
        while (elementCount == 0) {

            try {

                System.out.println(name +
                        " робить спробу читання.");

                displayState("Буфер порожній. "
                        + name + " чекає.\t");

                wait();
            }

            catch (InterruptedException exception) {

                exception.printStackTrace();
            }
        }

        /**
         * Зчитування елемента
         */
        int temp = buffer[getPosition];

        /**
         * Очищення елемента
         */
        buffer[getPosition] = 0;

        elementCount--;

        displayState(name +
                " зчитує " + temp + "\t\t");

        /**
         * Перехід до наступної позиції
         */
        if (getPosition == maxSize - 1)
            getPosition = 0;
        else
            getPosition++;

        /**
         * Повідомлення потоків
         */
        notifyAll();

        return temp;
    }

    /**
     * Виведення стану буфера
     */
    public void displayState(String operation) {

        StringBuffer output =
                new StringBuffer(operation);

        /**
         * Виведення елементів буфера
         */
        for (int i = 0; i < maxSize; i++) {

            output.append(buffer[i] + " ");
        }

        /**
         * Виведення кількості елементів
         */
        output.append("\t" + elementCount + "\n");

        System.out.println(output);
    }
}

/**
 * Клас виробника
 */
class Producer extends Thread {

    /**
     * Посилання на буфер
     */
    private SynchronizedBuffer sharedLocation;

    /**
     * Кількість елементів для запису
     */
    private int elementsToWrite;

    /**
     * Конструктор
     */
    public Producer(SynchronizedBuffer shared,
                    int elements) {

        super("Producer");

        sharedLocation = shared;

        elementsToWrite = elements;
    }

    /**
     * Метод виконання потоку
     */
    public void run() {

        /**
         * Запис елементів у буфер
         */
        for (int count = 1;
             count <= elementsToWrite;
             count++) {

            try {

                /**
                 * Випадкова затримка
                 */
                Thread.sleep(
                        (int)(Math.random() * 3000));

                sharedLocation.set(count);
            }

            catch (InterruptedException exception) {

                exception.printStackTrace();
            }
        }

        /**
         * Повідомлення про завершення
         */
        System.err.println(getName() +
                " завершив роботу.\n" +
                "Записано значень: "
                + elementsToWrite + "\n");
    }
}

/**
 * Клас споживача
 */
class Consumer extends Thread {

    /**
     * Посилання на буфер
     */
    private SynchronizedBuffer sharedLocation;

    /**
     * Кількість елементів для читання
     */
    private int elementsToRead;

    /**
     * Конструктор
     */
    public Consumer(SynchronizedBuffer shared,
                    int elements) {

        super("Consumer");

        sharedLocation = shared;

        elementsToRead = elements;
    }

    /**
     * Метод виконання потоку
     */
    public void run() {

        int sum = 0;

        /**
         * Зчитування елементів
         */
        for (int count = 1;
             count <= elementsToRead;
             count++) {

            try {

                /**
                 * Випадкова затримка
                 */
                Thread.sleep(
                        (int)(Math.random() * 3000));

                sum += sharedLocation.get();
            }

            catch (InterruptedException exception) {

                exception.printStackTrace();
            }
        }

        /**
         * Повідомлення про завершення
         */
        System.err.println(getName() +
                " завершив роботу.\n" +
                "Зчитано значень: "
                + elementsToRead +
                "\nСума: " + sum + "\n");
    }
}