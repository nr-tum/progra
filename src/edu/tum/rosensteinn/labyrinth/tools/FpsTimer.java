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

package edu.tum.rosensteinn.labyrinth.tools;

/**
 * A convenient class to adjust the thread sleeping rate based on
 * a fixed framerate. Before the screen is updated, one must call
 * {@link #beginTimer()}. When the update is finished and the current
 * thread shall sleep until the next frame must be rendered,
 * {@link #endTimer()} must be called.
 */
public class FpsTimer {

    private long lastTime;
    private float fps;

    public FpsTimer(float fps) {
        this.fps = fps;
        this.lastTime = System.currentTimeMillis();
    }

    /**
     * Call this method after the screen update to sleep until the
     * complete frame time is filled.
     */
    public void sleep() {
        long delta = System.currentTimeMillis() - this.lastTime;
        long frameTime = (long) ((1.0 / this.fps) * 1000);
        if (delta < frameTime) {
            try {
                Thread.sleep(frameTime - delta);
            }
            catch (InterruptedException e) {
                ; // intentionally left blank
            }
        }
        this.lastTime = System.currentTimeMillis();
    }

}
