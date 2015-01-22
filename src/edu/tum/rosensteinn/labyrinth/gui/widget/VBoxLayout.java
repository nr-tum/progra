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

import com.googlecode.lanterna.terminal.Terminal;

/**
 * Lays out items vertically and centers them.
 */
public class VBoxLayout extends Widget {

    private int width = 0;
    private int height = 0;

    @Override
    public void computeSize(Point availableSpace) {
        this.width = 0;
        this.height = 0;
        this.size = availableSpace;

        for (Widget child : this.iterChildren()) {
            Point childSpace = availableSpace.sub(new Point(0, this.height));
            child.computeSize(childSpace);
            this.width = Math.max(child.size.x, this.width);
            this.height += child.size.y;
        }
    }

    @Override
    public void computeLayout() {
        int xoff = this.position.x;
        int yoff = Math.max(this.size.y - height, 0) / 2;
        for (Widget child : this.iterChildren()) {
            child.position = new Point(
                xoff + Math.max(this.size.x - child.size.x, 0) / 2,
                yoff);
            yoff += child.size.y;
        }
    }

}
