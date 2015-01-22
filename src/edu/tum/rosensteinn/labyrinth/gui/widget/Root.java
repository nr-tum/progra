/**
 * Copyright (c) 2015  Niklas Rosenstein
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package edu.tum.rosensteinn.labyrinth.gui.widget;

import edu.tum.rosensteinn.labyrinth.Point;
import edu.tum.rosensteinn.labyrinth.gui.Event;
import edu.tum.rosensteinn.labyrinth.gui.KeyboardEvent;
import edu.tum.rosensteinn.labyrinth.gui.Window;

import com.googlecode.lanterna.input.Key;
/**
 * This class represents the root widget and manages the focus adaption
 * of its child widgets. It will cause the focus change when the TAB
 * key or CTRL+TAB key is pressed.
 *
 * There may only be one child widget inserted under the root widget.
 */
public class Root extends Widget {

    private boolean displayChanged = true;
    private boolean layoutChanged = true;
    private Widget focusWidget = null;

    /**
     * @return The widget that currently has focus.
     */
    public final Widget getFocusWidget() {
        return this.focusWidget;
    }

    /**
     * Sets the widget that has focus.
     *
     * @param widget
     */
    public final void setFocusWidget(Widget widget) {
        if (widget == null || !widget.isStatic()) {
            if (this.focusWidget != null) {
                this.focusWidget.onFocusChange(false);
            }
            this.focusWidget = widget;
            if (this.focusWidget != null) {
                this.focusWidget.onFocusChange(true);
            }
        }
    }

    /**
     * {@link #computeSize()} and {@link #computeLayout()} in one go. This
     * will reset the dirty-state of the {@link Root} widget.
     *
     * @param availableSize
     */
    public void reflow(Point availableSize) {
        this.computeSize(availableSize);
        this.computeLayout();
        this.layoutChanged = false;
    }

    public final boolean getDisplayChanged() {
        return this.displayChanged;
    }

    public final boolean getLayoutChanged() {
        return this.layoutChanged;
    }

    /**
     * Switches the focus to the next widget.
     */
    public final void focusNextWidget() {
        Widget widget = this.findNextFocusWidget(false);
        if (widget != null) {
            this.setFocusWidget(widget);
        }
    }

    /**
     * Switches the focus to the previous widget.
     */
    public final void focusPreviousWidget() {
        Widget widget = this.findNextFocusWidget(true);
        if (widget != null) {
            this.setFocusWidget(widget);
        }
    }

    // -----------------------------------------------------------------------

    private Widget nextWidget(Widget widget, boolean backwards) {
        if (widget == null) {
            return null;
        }
        if (backwards) {
            if (widget.getPrevious() != null) {
                return widget.getPrevious();
            }
            return widget.getParent();
        }
        else {
            if (widget.getChild() != null) {
                return widget.getChild();
            }
            else if (widget.getNext() != null) {
                return widget.getNext();
            }
            else if (widget.getParent() != null) {
                return widget.getParent().getNext();
            }
            return null;
        }
    }

    private Widget findNextFocusWidget(boolean backwards) {
        Widget current = this.focusWidget;
        if (current == null) {
            current = this.getChild();
        }
        else {
            current = this.nextWidget(current, backwards);
        }
        while (current != null && current.isStatic()) {
            current = this.nextWidget(current, backwards);
        }
        return current;
    }

    // ---------------------- Widget -----------------------------------------

    @Override
    public final void setDisplayChanged() {
        this.displayChanged = true;
    }

    @Override
    public final void setLayoutChanged() {
        this.displayChanged = true;
        this.layoutChanged = true;
    }

    @Override
    public boolean isStatic() {
        return true;
    }

    @Override
    public boolean onEvent(Window window, Event event) {
        boolean handled = false;
        if (this.focusWidget != null) {
            handled = this.focusWidget.onEvent(window, event);
        }
        if (!handled && event instanceof KeyboardEvent) {
            Key key = ((KeyboardEvent) event).key;
            boolean backwards = key.isCtrlPressed();
            if (key.getKind() == Key.Kind.Tab) {
                if (backwards) this.focusPreviousWidget();
                else this.focusNextWidget();
                handled = true;
            }
        }
        return handled;
    }

    @Override
    public void computeSize(Point availableSpace) {
        if (this.getChild() != null) {
            this.getChild().computeSize(availableSpace);
        }
    }

    @Override
    public void computeLayout() {
        if (this.getChild() != null) {
            this.getChild().computeLayout();
        }
        if (this.focusWidget == null) {
            this.focusNextWidget();
        }
    }

    @Override
    public void paint(Window window, Point offset) {
        if (this.getChild() != null) {
            this.getChild().paint(window, offset);
        }
        this.displayChanged = false;
    }

}
