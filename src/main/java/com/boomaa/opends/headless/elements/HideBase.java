package com.boomaa.opends.headless.elements;

import java.util.function.Supplier;

public abstract class HideBase<T> {
    private final LazySupplier<T> elementSupplier;

    public HideBase(Supplier<T> supplier) {
        this.elementSupplier = new LazySupplier<>(supplier);
    }

    public T getElement() {
        return elementSupplier.get();
    }

    public boolean isHeadless() {
        return getElement() == null;
    }
}
