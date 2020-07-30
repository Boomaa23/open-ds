package com.boomaa.opends.display.elements;

public class MultiValueLabel extends HideableLabel {
    private final String[] textPossible;

    public MultiValueLabel(boolean displayedByDefault, String... textPossible) {
        super(displayedByDefault, textPossible[0]);
        this.textPossible = textPossible;
    }

    public void changeToDisplay(int index, boolean displayNow) {
        super.setText(textPossible[index]);
        if (displayNow) {
            super.forceDisplay();
        }
    }
}
