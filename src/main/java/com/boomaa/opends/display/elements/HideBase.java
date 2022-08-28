package com.boomaa.opends.display.elements;

import com.boomaa.opends.util.Parameter;

import java.util.function.Supplier;
import javax.swing.JComponent;

public abstract class HideBase<T extends JComponent> {
    private final StickySupplier<T> elementSupplier;
    protected boolean selected;
    protected boolean enabled;

    public HideBase(Supplier<T> elementSupplier) {
        this.elementSupplier = new StickySupplier<>(elementSupplier);
    }

    public boolean isEnabled() {
        return getElement() != null ? getElement().isEnabled() : enabled;
    }

    public void setEnabled(boolean enabled) {
        if (getElement() != null) {
            getElement().setEnabled(enabled);
        } else {
            this.enabled = enabled;
        }
    }

    public T getElement() {
        return elementSupplier.get();
    }

    private static class StickySupplier<T> {
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
}
