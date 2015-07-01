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

package edu.tum.rosensteinn.labyrinth.gui;

import edu.tum.rosensteinn.labyrinth.Point;
import edu.tum.rosensteinn.labyrinth.gui.widget.Root;
import edu.tum.rosensteinn.labyrinth.gui.widget.Styling;

/**
 * A view to render a widget hierarchy.
 */
public class WidgetView extends View {

    private final Root root = new Root();
    private boolean clearScreen = true;

    public WidgetView(Styling styling) {
        super();
        this.root.setStyling(styling);
    }

    public final void setClearScreen(boolean clearScreen) {
        this.clearScreen = clearScreen;
    }

    public final Root getRoot() {
        return this.root;
    }

    @Override
    public void onEvent(Window window, Event event) {
        this.root.onEvent(window, event);
    }

    @Override
    public void onResized(Window window, Point size) {
        this.root.setLayoutChanged();
    }

    @Override
    public void onFocus(Window window) {
        this.root.setLayoutChanged();
    }

    @Override
    public void render(Window window, boolean initial) {
        if (!initial && !this.root.getDisplayChanged()) {
            return;
        }

        Screen screen = window.getScreen();
        Point size = screen.getSize();
        if (size.x <= 0 || size.y <= 0) {
            return;
        }

        // Clear the screen.
        if (this.clearScreen) {
            Styling styling = this.root.getStyling();
            screen.applyBackgroundColor(styling.backgroundColor);
            screen.clear();
        }

        // Reflow, if necessary.
        if (this.root.getLayoutChanged()) {
            this.root.reflow(size);
        }

        this.root.paint(window, new Point(0, 0));
    }

}
