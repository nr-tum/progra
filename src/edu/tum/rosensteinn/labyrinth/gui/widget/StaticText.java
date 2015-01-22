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
import edu.tum.rosensteinn.labyrinth.gui.Screen;
import edu.tum.rosensteinn.labyrinth.gui.Window;

public class StaticText extends Widget {

    private String text;

    public StaticText(String text) {
        this.setText(text);
    }

    public final void setText(String text) {
        this.text = (text != null ? text : "");
    }

    public final String getText() {
        return this.text;
    }

    @Override
    public boolean isStatic() {
        return true;
    }

    @Override
    public void onChildAdded(Widget child) {
        child.remove();
        throw new UnsupportedOperationException("StaticText does not accept child widgets.");
    }

    @Override
    public void computeSize(Point availableSpace) {
        this.size = new Point(this.text.length(), 1);
    }

    @Override
    public void paint(Window window, Point offset) {
        Screen screen = window.getScreen();
        Styling styling = this.getStyling();
        Point pos = this.position.add(offset);
        screen.moveCursor(pos.x, pos.y);
        screen.applyForegroundColor(styling.textFgColor);
        screen.applyBackgroundColor(styling.textBgColor);
        screen.putString(this.text);
    }

}
