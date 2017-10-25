package com.fpliu.newton.ui.image.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SoundEffectConstants;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class SuperCheckBox extends CompoundButton {

    private boolean canChecked = true;

    public SuperCheckBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SuperCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SuperCheckBox(Context context) {
        super(context);
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return CheckBox.class.getName();
    }

    public boolean canChecked() {
        return canChecked;
    }

    public void setCanChecked(boolean canChecked) {
        this.canChecked = canChecked;
    }

    @Override
    public void toggle() {
        if (canChecked) {
            super.toggle();
        }
    }
}
