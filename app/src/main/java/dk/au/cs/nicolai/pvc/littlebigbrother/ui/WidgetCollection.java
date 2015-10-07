package dk.au.cs.nicolai.pvc.littlebigbrother.ui;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Nicolai on 06-10-2015.
 */
public final class WidgetCollection extends HashSet<Widget> {
    public void showAll() {
        for (Widget widget :
                this) {
            widget.show();
        }
    }

    public void hideAll() {
        for (Widget widget :
                this) {
            widget.hide();
        }
    }

    public void add(Widget... widgets) {
        this.addAll(Arrays.asList(widgets));
    }

    public void remove(Widget... widgets) {
        this.removeAll(Arrays.asList(widgets));
    }
}
