package com.boomaa.opends.headless.elements;

import java.util.function.Supplier;
import javax.swing.JComponent;

public abstract class HideComponent<T extends JComponent> extends HideBase<T> {
    protected boolean selected;
    protected boolean enabled;

    public HideComponent(Supplier<T> elementSupplier) {
        super(elementSupplier);
    }

    public boolean isEnabled() {
        return !isHeadless() ? getElement().isEnabled() : enabled;
    }

    public void setEnabled(boolean enabled) {
        if (!isHeadless()) {
            getElement().setEnabled(enabled);
        } else {
            this.enabled = enabled;
        }
    }
}
