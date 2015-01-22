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
import edu.tum.rosensteinn.labyrinth.gui.*;

import com.googlecode.lanterna.input.Key;

/**
 * This widget class displays a list of entries that can be selected
 * by the user.
 */
public class SelectionBox extends Widget {

    private static class Entry {
        public final String label;
        public final ActionListener action;
        public Entry(String label, ActionListener action) {
            this.label = label;
            this.action = action;
        }
    }

    private final java.util.ArrayList<Entry> items;
    private int activeItem;
    private int scroll;
    private int maxHeight;

    public SelectionBox() {
        super();
        this.items = new java.util.ArrayList<>();
        this.activeItem = 0;
        this.scroll = 0;
        this.maxHeight = 0;
    }

    public final void clearEntries() {
        this.items.clear();
        this.activeItem = 0;
    }

    /**
     * Adds an entry to the selection box. The {@link ActionListener}
     * will be invoked with the {@link SelectionBox} as the {@code
     * source} parameter and {@code null} for {@code data}.
     *
     * @param label
     * @param action
     */
    public final void addEntry(String label, ActionListener action) {
        this.items.add(new Entry(label, action));
        this.setDisplayChanged();
    }

    /**
     * @param index
     * @return The label at the specified index.
     */
    public final String getLabel(int index) {
        return this.items.get(index).label;
    }

    /**
     * Sets the index of the active entry.
     *
     * @param index
     */
    public final void setActive(int index) {
        if (index < 0) {
            index = 0;
        }
        else if (index >= this.items.size()) {
            index = this.items.size() - 1;
        }
        this.activeItem = index;
        this.setDisplayChanged();
    }

    /**
     * @return The index of the active item. This could be out of range
     *         of the actual item list.
     */
    public final int getActive() {
        return this.activeItem;
    }

    /**
     * Sets the maximum height of the selection box. Changing the active
     * item might scroll the view. Pass {@code 0} to specify no maximum
     * height.
     *
     * @param height
     */
    public final void setMaximumHeight(int height) {
        this.maxHeight = height;
    }


    /**
     * Makes sure the active item is scroll into the view.
     */
    private void scrollIntoView() {
        if (this.activeItem < this.scroll) {
            this.scroll = this.activeItem;
        }
        else if (this.maxHeight > 0) {
            if (this.activeItem >= (this.scroll + this.maxHeight)) {
                this.scroll = this.activeItem - this.maxHeight + 1;
            }
        }
    }


    @Override
    public final boolean isStatic() {
        return false;
    }

    @Override
    public final void onFocusChange(boolean gained) {
        this.setDisplayChanged();
    }

    @Override
    public final boolean onEvent(Window window, Event event) {
        boolean handled = false;
        if (event instanceof KeyboardEvent) {
            Key key = ((KeyboardEvent) event).key;
            switch (key.getKind()) {
                case ArrowDown:
                    if (this.activeItem < (this.items.size() - 1)) {
                        this.activeItem++;
                        this.scrollIntoView();
                        handled = true;
                    }
                    break;
                case ArrowUp:
                    if (this.activeItem > 0) {
                        this.activeItem--;
                        this.scrollIntoView();
                        handled = true;
                    }
                    break;
                case Enter:
                    if (this.activeItem >= 0 && this.activeItem < this.items.size()) {
                        Entry entry = this.items.get(this.activeItem);
                        if (entry.action != null) {
                            entry.action.doAction(window, this, null);
                        }
                        handled = true;
                    }
                    break;
            }
            if (handled) {
                this.setDisplayChanged();
            }
            return handled;
        }
        return false;
    }

    @Override
    public final void computeSize(Point availableSize) {
        int width = 0;
        for (Entry entry : this.items) {
            width = Math.max(entry.label.length(), width);
        }
        width += 4; // 4 characters to highlight the active item.
        int height = this.items.size();

        // Limit to maximum entries displayed and take scrollbar to
        // the right in consideration.
        if (this.maxHeight > 0 && height > this.maxHeight) {
            height = this.maxHeight;
            width += 2; // 2 character width for the scrollbar.
        }

        this.size = new Point(width, height);
    }

    @Override
    public final void paint(Window window, Point offset) {
        Screen screen = window.getScreen();
        Styling styling = this.getStyling();
        Point pos = this.position.add(offset);

        // todo: Make sure the background of the selection box is
        // filled with the background color (same for the items).

        int itemCount = this.items.size();
        boolean hasFocus = this.hasFocus();
        boolean hasScrollbar = this.maxHeight > 0 && itemCount > this.maxHeight;

        int width = this.size.x;
        if (hasScrollbar) width -= 2;

        int start = this.scroll;
        int end = itemCount;
        if (this.maxHeight > 0) {
            end = Math.min(itemCount, start + this.maxHeight);
        }

        for (int index = start; index < end; ++index) {
            Entry entry = this.items.get(index);
            int yoff = pos.y + index - start;

            screen.applyForegroundColor(styling.textFgColor);
            screen.applyBackgroundColor(styling.textBgColor);
            if (index == this.activeItem && hasFocus) {
                screen.applyForegroundColor(styling.highlightFgColor);
                screen.applyBackgroundColor(styling.highlightBgColor);
                screen.moveCursor(pos.x, yoff);
                screen.putCharacter('<');
                screen.moveCursor(pos.x + width - 1, yoff);
                screen.putCharacter('>');
            }

            String label = entry.label;
            int xoff = (width - label.length()) / 2;
            screen.moveCursor(pos.x + xoff, yoff);
            screen.putString(label);
        }

        if (hasScrollbar && itemCount > 0) {
            screen.applyForegroundColor(styling.textFgColor);
            if (this.scroll > 0) {
                screen.moveCursor(pos.x + this.size.x - 1, pos.y);
                screen.putCharacter('\u25B2');
            }
            if (this.scroll < (itemCount - this.maxHeight)) {
                screen.moveCursor(pos.x + this.size.x - 1, pos.y + this.size.y - 1);
                screen.putCharacter('\u25BC');
            }
        }
    }

}
