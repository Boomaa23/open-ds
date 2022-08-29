package com.boomaa.opends.headless;

import com.boomaa.opends.util.Parameter;

import java.util.function.Supplier;

class StickySupplier<T> {
    private final Supplier<T> supplier;
    private T element;

    public StickySupplier(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        if (element == null && !Parameter.HEADLESS.isPresent()) {
            element = supplier.get();
        }
        return element;
    }
}
