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

/**
 * Base class for views that can be displayed in a {@link Window}. A view
 * is always modal and will receive all events from the {@link Window} when
 * it is active.
 */
public abstract class View {

    /**
     * This field contains the {@link View} that will be displayed when
     * the current view is dismissed.
     */
    protected View backView = null;

    /**
     * Returns the view that should be displayed when this view is
     * dismissed.
     *
     * @return
     */
    public final View getBack() {
        return this.backView;
    }

    /**
     * Sets the view that sits behind this one.
     *
     * @param back The new backview.
     */
    public final void setBack(View back) {
        this.backView = back;
    }

    /**
     * Sent when an event is sent from a {@link Window}.
     *
     * @param window
     * @param event
     */
    public void onEvent(Window window, Event event) {
        // intentionally left blank
    }

    /**
     * Sent when the terminal resized while the view is active.
     *
     * @param window
     * @param size
     */
    public void onResized(Window window, Point size) {
        // intentionally left blank
    }

    /**
     * Called when the view becomes the active view in a {@link Window}.
     * No rendering should be done in this method.
     *
     * @param window
     */
    public void onFocus(Window window) {
        // intentionally left blank
    }

    /**
     * Called to render the view on the {@link Terminal}.
     *
     * @param window
     * @param initial Set to {@code true} when this is the first time
     *                the view is rendered in this window or the window
     *                has been resized.
     */
    public void render(Window window, boolean initial) {
        // intentionally left blank
    }

}
