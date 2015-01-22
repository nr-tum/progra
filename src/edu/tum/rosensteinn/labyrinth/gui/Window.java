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

import com.googlecode.lanterna.input.Key;

import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.terminal.swing.SwingTerminal;

/**
 * This class is a wrapper around the {@link SwingTerminal} and {@link
 * Screen} classes to manage the opening and closing of the terminal
 * window as well as resize events.
 */
public class Window {

    private class WindowAdapter extends java.awt.event.WindowAdapter {
        @Override
        public void windowClosing(java.awt.event.WindowEvent event) {
            close();
        }
    }

    private class ResizeListener implements Terminal.ResizeListener {
        @Override
        public void onResized(TerminalSize terminalSize) {
            isInitial = true;
            if (view != null) {
                int cols = terminalSize.getColumns();
                int rows = terminalSize.getRows();
                Point size = new Point(cols, rows);
                view.onResized(Window.this, size);
            }
        }
    }

    private final SwingTerminal terminal;
    private final Screen screen;
    private View view;
    private boolean open;
    private boolean resizable;
    private boolean isInitial;

    /* object initializer () */ {
        this.terminal = new SwingTerminal();
        this.terminal.addResizeListener(new ResizeListener());
        this.screen = new Screen(this.terminal);
        this.view = null;
        this.open = false;
        this.resizable = true;
        this.isInitial = false;
    }

    public Window() {
    }

    public Window(View view) {
        this.setView(view);
    }

    public Window(View view, int width, int height, boolean resizable) {
        this.setView(view);
        this.setSize(new Point(width, height));
        this.resizable = resizable;
    }

    /**
     * Opens the {@link SwingTerminal}.
     */
    public synchronized final void open() {
        this.terminal.enterPrivateMode();
        this.terminal.getJFrame().addWindowListener(new WindowAdapter());
        this.terminal.getJFrame().setResizable(this.resizable);
        this.terminal.setCursorVisible(false);
        this.open = true;
    }

    /**
     * Close the {@link SwingTerminal}.
     */
    public synchronized final void close() {
        this.terminal.exitPrivateMode();
        this.open = false;
    }

    /**
     * @return {@code true} if the Window is opened, {@code false} if not.
     */
    public synchronized final boolean isOpen() {
        return this.open;
    }

    /**
     * @return The {@link Screen} object wrapped by this Window.
     */
    public final Screen getScreen() {
        return this.screen;
    }

    /**
     * @return The {@link SwingTerminal} object wrapped by this Window.
     */
    public final SwingTerminal getTerminal() {
        return this.terminal;
    }

    /**
     * Changes the resizable status of the Window.
     *
     * @param resizable
     */
    public final void setResizable(boolean resizable) {
        this.resizable = resizable;
        javax.swing.JFrame frame = this.terminal.getJFrame();
        if (frame != null) {
            frame.setResizable(resizable);
        }
    }

    /**
     * Changes the size of the window.
     * @param size
     */
    public final void setSize(Point size) {
        TerminalSize tsize = this.terminal.getTerminalSize();
        tsize.setColumns(size.x);
        tsize.setRows(size.y);
    }

    /**
     * @return The active {@link View}
     */
    public final View getView() {
        return this.view;
    }

    /**
     * Sets the active view.
     *
     * @param view           The view to make active.
     */
    public final void setView(View view) {
        if (this.view != view) {
            this.isInitial = true;
        }
        this.view = view;
        if (this.view != null) {
            this.view.onFocus(this);
        }
    }

    /**
     * Pushes the specified {@code view} atop. This will set the back view
     * of the new view to the current.
     *
     * @param view
     */
    public final void pushView(View view) {
        view.setBack(this.view);
        this.setView(view);
    }

    /**
     * Dismisses the current view, revealing the view beneath. This can
     * lead to no view being displayed.
     */
    public final void popView() {
        if (this.view != null) {
            this.setView(this.view.getBack());
        }
    }

    /**
     * Reads input events from the terminal and dispatches them to
     * the current view. This method should be called in the main
     * loop of the application. It will send a {@link RecurringEvent}
     * and {@link KeyboardEvent} for each key read from the terminal
     * to the active view.
     */
    public final void dispatchEvents() {
        if (this.view != null) {
            this.view.onEvent(this, new ReocurringEvent());
        }
        Key key = this.terminal.readInput();
        while (key != null) {
            if (this.view != null) {
                this.view.onEvent(this, new KeyboardEvent(key));
            }
            key = this.terminal.readInput();
        }
    }

    /**
     * Redraws the active view on the terminal. This function synchronizes
     * the terminal so that it can be painted without data races.
     */
    public final void redraw() {
        // Render the active view or clear the screen if there is
        // no active view.
        if (this.view != null) {
            this.view.render(this, this.isInitial);
            this.isInitial = false;
        }
        else {
            this.screen.applyBackgroundColor(Terminal.Color.BLACK);
            this.screen.clear();
        }

        // Blit the screen on the terminal.
        // this.screen.completeRefresh();
    }

}
