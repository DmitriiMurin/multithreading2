package ru.netology.murin.multithreading.lesson2.task1;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

//Задача 1. Робот-доставщик
public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();
    public static final AtomicInteger expectedRCount = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            Thread thread = new Thread(Main::analiseRoot);
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join(); // зависаем, ждём когда поток объект которого лежит в thread завершится
        }

        sizeToFreq.entrySet().stream().sorted((e1, e2) -> -(e1.getKey().compareTo(e2.getKey()))).forEach(e -> System.out.println("количество повторений " + e.getKey() + " встретилось " + e.getValue() + " раз"));

        AtomicReference<Integer> actual = new AtomicReference<>(0);
        sizeToFreq.forEach((key, value) -> actual.set(actual.get() + key * value));

        System.out.println("expected = " + expectedRCount.get() + ", actual = " + actual.get());
    }

    public static void analiseRoot() {
        String root = generateRoute("RLRFR", 100);
        int rCount = Long.valueOf(root.chars().filter(c -> c == 'R').count()).intValue();
        System.out.println(root + " -> " + rCount);
        synchronized (Main.class) {
            sizeToFreq.merge(rCount, 1, Integer::sum);
        }
        expectedRCount.addAndGet(rCount);
    }


    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }
}
