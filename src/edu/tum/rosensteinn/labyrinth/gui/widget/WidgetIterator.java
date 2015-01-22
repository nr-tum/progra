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

import java.util.Iterator;

/**
 * This class implements iterating over {@link Widget}s, going from one
 * to the next in each step. This iterator is returned by {@link
 * Widget#iterChildren()}, initialized with the first child object.
 */
public class WidgetIterator implements Iterable<Widget>, Iterator<Widget> {

    private Widget current;

    public WidgetIterator(Widget start) {
        this.current = start;
    }

    @Override
    public Iterator<Widget> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return this.current != null;
    }

    @Override
    public Widget next() {
        Widget result = this.current;
        this.current = this.current.getNext();
        return result;
    }

    @Override
    public void remove() {
        if (this.current == null || this.current.getPrevious() == null) {
            throw new IllegalStateException();
        }
        this.current.getPrevious().remove();
    }



}
