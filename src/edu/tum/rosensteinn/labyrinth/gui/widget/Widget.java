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
import edu.tum.rosensteinn.labyrinth.gui.Window;

/**
 * This is the base class for widgets. A widget represents an element in
 * the user interface. A static widget for instance is simple text, a
 * non-static widget is an input text or list box. Non-static widgets
 * can gain the focus and receive events.
 *
 * Widgets are a hierarchical data structure. The {@link Root} widget
 * manages the focus of widgets.
 */
public abstract class Widget {

    /**
     * The size of the widget in which it was rendered on the terminal.
     * It is calculated by the widget itself from {@link computeSize()}.
     */
    protected Point size = new Point();

    /**
     * The position of the widget is usually calculated by the parent
     * widget, as it is responsible for the layout of its children. This
     * is usually done in {@link #computeLayout()} but can already happen
     * in {@link #computeSize()} if the algorithm permits it.
     */
    protected Point position = new Point();

    /**
     * The parent widget.
     */
    private Widget parent = null;

    /**
     * The previous widget.
     */
    private Widget previous = null;

    /**
     * The next widget.
     */
    private Widget next = null;

    /**
     * The first child.
     */
    private Widget child = null;

    /**
     * The last child.
     */
    private Widget lastChild = null;

    /**
     * The styling of the widget. Basic styling information can be
     * retrieved from this object.
     */
    private Styling styling = null;

    /**
     * Finds the {@link Root} of the widget and notifies it that
     * the display has changed. Does nothing if the widget is not in
     * a hierarchy.
     */
    public void setDisplayChanged() {
        Root root = this.getRoot();
        if (root != null) {
            root.setDisplayChanged();
        }
    }

    /**
     * Finds the {@link Root} of the widget and notifies it that
     * the layout needs to be recalculated. Does nothing if the widget
     * is not in a hierarchy.
     */
    public void setLayoutChanged() {
        Root root = this.getRoot();
        if (root != null) {
            root.setLayoutChanged();
        }
    }

    /**
     * @return The size of the widget.
     */
    public final Point getSize() {
        return this.size;
    }

    /**
     * @return The position of the widget.
     */
    public final Point getPosition() {
        return this.size;
    }

    /**
     * Returns the {@link Root} at the top of the widget hierarchy. The
     * {@link Root} widget will return itsel from this method.
     *
     * @return The {@link Root} widget or {@code null}.
     */
    public final Root getRoot() {
        Widget widget = this;
        while (widget != null) {
            if (widget instanceof Root) {
                return (Root) widget;
            }
            widget = widget.parent;
        }
        return null;
    }

    /**
     * @return The parent {@link Widget}.
     */
    public final Widget getParent() {
        return this.parent;
    }

    /**
     * @return The first child {@link Widget}.
     */
    public final Widget getChild() {
        return this.child;
    }

    /**
     * @return The last child {@link Widget}.
     */
    public final Widget getLastChild() {
        return this.lastChild;
    }

    /**
     * @return The neighbor of this {@link Widget}.
     */
    public final Widget getNext() {
        return this.next;
    }

    /**
     * @return The predecessor of this {@link Widget}.
     */
    public final Widget getPrevious() {
        return this.previous;
    }

    /**
     * @return An iterator for the child objects. This iterator is not
     *         safe to modifications in the hierarchy during iteration.
     */
    public Iterable<Widget> iterChildren() {
        return new WidgetIterator(this.child);
    }

    /**
     * @return The {@link Styling} of the widget. Inherits the parent styling
     *         if none is set in this widget.
     */
    public Styling getStyling() {
        Widget current = this;
        while (current != null && current.styling != null) {
            current = current.parent;
        }
        if (current != null && current.styling != null) {
            return current.styling;
        }
        return Styling.defaultStyling;
    }

    /**
     * Sets the {@link Styling} of this widget.
     *
     * @param styling
     */
    public void setStyling(Styling styling) {
        this.styling = styling;
    }

    /**
     * @return {@const true} if this widget has the focus. If it is not
     *         attached to a {@link Root} widget, this function will always
     *         return {@const false}.
     */
    public final boolean hasFocus() {
        Root root = this.getRoot();
        if (root == null) {
            return false;
        }
        return root.getFocusWidget() == this;
    }

    /**
     * Sets the focus of the widget. Note that this is only possible if
     * the widget is in a hierarchy with a {@link Root} and is a non-static
     * widget.
     */
    public final void setFocus() {
        if (!this.isStatic()) {
            Root root = this.getRoot();
            if (root != null) {
                root.setFocusWidget(this);
            }
        }
    }

    /**
     * Adds the {@code widget} to the end of the child list of this
     * widget. If this widget does not support child widgets, an
     * exception may be thrown.
     *
     * @param widget
     */
    public final void add(Widget widget) {
        assert((this.lastChild == null) == (this.child == null));
        widget.parent = this;
        widget.next = null;
        widget.previous = this.lastChild;
        if (this.lastChild != null) {
            this.lastChild.next = widget;
        }
        else {
            this.child = widget;
        }
        this.lastChild = widget;

        this.onChildAdded(widget);
        Root root = this.getRoot();
        if (root != null) {
            root.setLayoutChanged();
        }
    }

    /**
     * Inserts this widget after the specified widget {@code other}. If
     * you're re-using the widget from a hierarchy, you must call {@link
     * #remove()} first.
     *
     * @param other
     */
    public final void insertAfter(Widget other) {
        this.next = other.next;
        this.previous = other;
        this.previous.next = this;
        if (this.next != null) {
            this.next.previous = this;
        }
        this.parent = other.parent;

        this.parent.onChildAdded(this);
        Root root = this.getRoot();
        if (root != null) {
            root.setLayoutChanged();
        }
    }

    /**
     * Inserts this widget before the widget {@code other}. If you're
     * re-using the widget from a hierarchy, you must call {@link #remove()}
     * first.
     *
     * @param other
     */
    public final void insertBefore(Widget other) {
        this.previous = other.previous;
        this.next = other;
        this.next.previous = this;
        if (this.previous != null) {
            this.previous.next = this;
        }
        this.parent = other.parent;

        this.parent.onChildAdded(this);
        Root root = this.getRoot();
        if (root != null) {
            root.setLayoutChanged();
        }
    }

    /**
     * Removes this widget from the hierarchy.
     */
    public final void remove() {
        Root root = this.getRoot();
        if (root != null) {
            root.setLayoutChanged();
        }

        if (this.parent != null && this.parent.child == this) {
            this.parent.child = this.next;
        }
        if (this.parent != null && this.parent.lastChild == this) {
            this.parent.lastChild = this.previous;
        }
        if (this.next != null) {
            this.next.previous = this.previous;
        }
        if (this.previous != null) {
            this.previous.next = this.previous;
        }
        this.parent = null;
        this.next = null;
        this.previous = null;
    }

    /**
     * A non-static widget can receive events. Only one widget can have
     * the focus at a time, no other widget will receive events. A static
     * widget will never gain focus.
     *
     * @return {@const true} if the widget is static, {@code false} if not.
     *         The default implementation returns {@code true}.
     */
    public boolean isStatic() {
        return true;
    }

    /**
     * Here, subclasses must compute the size of the widget and fill it
     * in the {@link #size} field. The position of child elements should
     * usually be calculated in {@link #computeLayout()} but may already
     * happend in this method for convenience.
     *
     * @param availableSpace The space available from the parent element.
     *                       The widget may use it completely, or not.
     */
    public abstract void computeSize(Point availableSpace);

    /**
     * This method must be implemented by subclasses to update the position
     * of its child widgets. {@link #computeSize()} has already been called
     * before this method. Subclasses may skip implementing this function if
     * the position of child elements was already set in {@link
     * #computeSize()}.
     */
    public void computeLayout() {
        // Intentionally left blank.
    }

    /**
     * This method is called to render the widget on a terminal. The
     * size and position of the widget has already been calculated at
     * the time this is called. The {@code offset} parameter should be
     * taken into account when painting the terminal.
     *
     * @param terminal       The terminal to paint the widget on to.
     * @param offset         The offset to add to the widgets position
     *                       for painting.
     */
    public void paint(Window window, Point offset) {
        for (Widget childWidget : this.iterChildren()) {
            childWidget.paint(window, offset);
        }
    }

    /**
     * A non-static widget that has the focus will receive events via
     * this method.
     *
     * @param window
     * @param event          The event object.
     * @return {@code true} if the event was handled, {@code false} if not.
     */
    public boolean onEvent(Window window, Event event) {
        return false;
    }

    /**
     * This method is called if this widget gains focus or looses its
     * focus. The state can be determined by the {@code gained} variable.
     *
     * @param gained         {@code true} if the widget has gained the
     *                       focus, {@code false} if it has lost the focus.
     */
    public void onFocusChange(boolean gained) {
        // Intentionally left blank.
    }

    /**
     * This method is called right before a child widget is inserted
     * under this widget from {@link #insertUnder()}. Some widgets throw
     * exceptions to prevent any child widgets to be added. It is suggested
     * to call {@code widget.remove()} before
     *
     * @param widget
     */
    public void onChildAdded(Widget widget) {
        // Intentionally left blank.
    }

}
