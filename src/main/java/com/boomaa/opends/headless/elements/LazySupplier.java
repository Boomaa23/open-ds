package com.boomaa.opends.headless.elements;

import com.boomaa.opends.util.Parameter;

import java.util.function.Supplier;

public class LazySupplier<T> {
    private final Supplier<T> supplier;
    private T element;

    public LazySupplier(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        if (element == null && !Parameter.HEADLESS.isPresent()) {
            element = supplier.get();
        }
        return element;
    }
}
