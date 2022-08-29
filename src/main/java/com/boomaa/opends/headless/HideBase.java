package com.boomaa.opends.headless;

import java.util.function.Supplier;

public abstract class HideBase<T> {
    private final StickySupplier<T> elementSupplier;

    public HideBase(Supplier<T> supplier) {
        this.elementSupplier = new StickySupplier<>(supplier);
    }

    public T getElement() {
        return elementSupplier.get();
    }

    public boolean isHeadless() {
        return getElement() == null;
    }
}
