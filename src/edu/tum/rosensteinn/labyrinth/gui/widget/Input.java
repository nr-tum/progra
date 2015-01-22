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
 * Implements letting the user enter a string when it has the focus.
 */
public class Input extends Widget {

    private String title = "";
    private String value = "";
    private int scroll = 0;
    private int cursor = 0;
    private int width = 15;

    public ActionListener enterAction = null;
    public ActionListener escapeAction = null;

    public Input(String title, int width) {
        super();
        this.title = title;
        this.width = width;
    }

    public final String getValue() {
        return this.value;
    }

    public final void scrollIntoView() {
        if (this.cursor < this.scroll) {
            this.scroll = this.cursor;
        }
        else if (this.cursor >= (this.scroll + this.width)) {
            this.scroll = this.cursor - this.width + 1;
        }
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public void computeSize(Point availableSpace) {
        int width = Math.max(this.title.length(), this.width);
        int height = 1;
        if (!this.title.isEmpty()) {
            ++height;
        }
        this.size = new Point(width + 2, height + 2);
    }

    @Override
    public void onFocusChange(boolean gained) {
        this.setDisplayChanged();
    }

    @Override
    public boolean onEvent(Window window, Event event) {
        if (event instanceof KeyboardEvent) {
            Key key = ((KeyboardEvent) event).key;
            switch (key.getKind()) {
                case NormalKey:
                    this.value = this.value.substring(0, this.cursor)
                            + Character.toString(key.getCharacter())
                            + this.value.substring(this.cursor, this.value.length());
                    this.setDisplayChanged();
                    ++this.cursor;
                    this.scrollIntoView();
                    return true;
                case Backspace:
                    if (this.cursor > 0) {
                        this.value = this.value.substring(0, this.cursor - 1)
                                + this.value.substring(this.cursor, this.value.length());
                    }
                    this.setDisplayChanged();
                    if (this.cursor > 0)
                        --this.cursor;
                    this.scrollIntoView();
                    return true;
                case ArrowLeft:
                    if (this.cursor > 0) {
                        --this.cursor;
                        this.scrollIntoView();
                        this.setDisplayChanged();
                    }
                    return true;
                case ArrowRight:
                    if (this.cursor < this.value.length()) {
                        ++this.cursor;
                        this.scrollIntoView();
                        this.setDisplayChanged();
                    }
                    return true;
                case Enter:
                    if (this.enterAction != null) {
                        this.enterAction.doAction(window, this, null);
                        return true;
                    }
                    return true;
                case Escape:
                    if (this.escapeAction != null) {
                        this.escapeAction.doAction(window, this, null);
                    }
                    return true;
            }
            return true;
        }
        return false;
    }

    @Override
    public void paint(Window window, Point offset) {
        Screen screen = window.getScreen();
        Styling styling = this.getStyling();
        Point pos = this.position.add(offset);
        screen.applyBackgroundColor(styling.editBgColor);
        screen.drawRectangle(' ', pos.x, pos.y, this.size.x - 1, this.size.y - 1);

        screen.applyBackgroundColor(styling.editBgColor);
        screen.applyForegroundColor(styling.editFgColor);

        int start = this.scroll;
        int end = Math.min(start + this.width, this.value.length());
        int xoff = pos.x + 1;
        int yoff = pos.y;

        if (!this.title.isEmpty()) {
            screen.moveCursor(xoff, yoff);
            screen.putString(this.title);
            yoff += 2;
        }
        else {
            yoff += 1;
        }

        screen.moveCursor(xoff, yoff);
        screen.drawRectangle(' ', xoff, yoff, this.width - 1, 0);

        screen.moveCursor(xoff, yoff);
        for (int i = start; i <= end; ++i) {
            if (this.cursor == i) {
                screen.applyBackgroundColor(styling.textBgColor);
                screen.applyForegroundColor(styling.textFgColor);
            }
            char c = (i == end ? ' ' : this.value.charAt(i));
            screen.putCharacter(c);
            if (this.cursor == i) {
                screen.applyBackgroundColor(styling.editBgColor);
                screen.applyForegroundColor(styling.editFgColor);
            }
        }
    }

}
