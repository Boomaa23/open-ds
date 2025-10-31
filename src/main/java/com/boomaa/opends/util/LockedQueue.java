package com.boomaa.opends.util;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class LockedQueue<E> {
    private final Lock lock;
    private final List<E> queue;

    public LockedQueue() {
        this.lock = new ReentrantLock();
        this.queue = new LinkedList<>();
    }

    public void add(E e) {
        execLockedAction(queue::add, e);
    }

    public void clear() {
        execLockedAction((e) -> queue.clear(), null);
    }

    public void forEach(Consumer<? super E> action) {
        execLockedAction(queue::forEach, action);
    }

    private <T> void execLockedAction(Consumer<T> action, T item) {
        lock.lock();
        try {
            action.accept(item);
        } finally {
            lock.unlock();
        }
    }
}
