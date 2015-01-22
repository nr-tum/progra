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

/**
 * Interface for actions that happen during events.
 */
public interface ActionListener {

    public void doAction(Window window, Object source, Object data);

    public static class ChangeView implements ActionListener {
        private final View view;
        public ChangeView(View view) {
            this.view = view;
        }
        @Override
        public void doAction(Window window, Object source, Object data) {
            window.setView(this.view);
        }
    }

    public static class PushView implements ActionListener {
        private final View view;
        public PushView(View view) {
            this.view = view;
        }
        @Override
        public void doAction(Window window, Object source, Object data) {
            window.pushView(this.view);
        }
    }

    public static class PopView implements ActionListener {
        @Override
        public void doAction(Window window, Object source, Object data) {
            window.popView();
        }
    }

    public static class Quit implements ActionListener {
        @Override
        public void doAction(Window window, Object source, Object data) {
            window.close();
        }
    }

}
