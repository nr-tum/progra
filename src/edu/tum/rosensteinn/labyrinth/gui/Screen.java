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

import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.Terminal.SGR;
import com.googlecode.lanterna.terminal.Terminal.Color;

/**
 * A wrapper for the {@link Terminal} class. It was originally planned
 * to buffer the printed characters and then blit the Terminal in one
 * go, but unfortunately this was (unexpectedly) utterly slow.
 *
 * Oh, and why I don't use the lanterna {@code Screen} class? Basically,
 * simply because IT SUCKS. No seriously, it just doesn't work. And if I
 * used it wrong, it is not me to blame but the (spare) documentation.
 */
public class Screen {

    private final Terminal terminal;
    private final java.util.Stack<Point> offsetStack;

    public Screen(Terminal terminal) {
        this.terminal = terminal;
        this.offsetStack = new java.util.Stack<>();
    }

    public void startScreen() {
        this.terminal.enterPrivateMode();
    }

    public void stopScreen() {
        this.terminal.exitPrivateMode();
    }

    public void pushOffset(Point off) {
        this.offsetStack.push(off);
    }

    public void popOffset() {
        this.offsetStack.pop();
    }

    public Point getSize() {
        return new Point(this.terminal.getTerminalSize());
    }

    public void applyBackgroundColor(Color color) {
        this.terminal.applyBackgroundColor(color);
    }

    public void applyForegroundColor(Color color) {
        this.terminal.applyForegroundColor(color);
    }

    public void moveCursor(int x, int y) {
        if (!this.offsetStack.isEmpty()) {
            Point p = this.offsetStack.peek();
            x += p.x;
            y += p.y;
        }
        this.terminal.moveCursor(x, y);
    }

    public void putCharacter(char c) {
        this.terminal.putCharacter(c);
    }

    public void putString(String string) {
        for (int index = 0; index < string.length(); ++index) {
            this.terminal.putCharacter(string.charAt(index));
        }
    }

    public void drawRectangle(char c, int x, int y, int w, int h) {
        if (!this.offsetStack.isEmpty()) {
            Point p = this.offsetStack.peek();
            x += p.x;
            y += p.y;
        }
        for (int ix = x; ix <= (x + w); ++ix) {
            for (int iy = y; iy <= (y + h); ++iy) {
                this.terminal.moveCursor(ix, iy);
                this.terminal.putCharacter(c);
            }
        }
    }

    public void clear() {
        this.offsetStack.clear();
        this.terminal.moveCursor(0, 0);
        Point size = this.getSize();
        int iterations = size.x * size.y;
        for (int i = 0; i < iterations; ++i) {
            this.terminal.putCharacter(' ');
        }
    }

}

// ---------------------------------------------------------------------------
// PS: Below my attempt on a buffered Screen.
// ---------------------------------------------------------------------------


/**
 * Buffer for the {@link com.googlecode.lanterna.terminal.Terminal} class
 * that is used to render in this package. Advantages of this technique are
 * increased performance since every "pixel" will only be rendered once
 * and there will be no flashing/blinking during the render process since
 * the {@link Screen} is blitted on the output terminal in one go.
 */
class Screen__ {

    private static class Pixel {
        public Color fg;
        public Color bg;
        public SGR sgr;
        public char c;
        public Pixel() {
            fg = Color.DEFAULT;
            bg = Color.DEFAULT;
            sgr = null;
            c = ' ';
        }
    }

    public static enum CursorBehaviour {
        Steady,
        NextLine;
    }

    private Color fg = Color.DEFAULT;
    private Color bg = Color.DEFAULT;
    private SGR sgr = null;
    private int x = 0;
    private int y = 0;
    private CursorBehaviour cursorBehaviour = CursorBehaviour.Steady;
    private Point size;
    private Pixel[][] pixels;

    /**
     * Initialize the Screen with the specified size. The next-line behaviour
     * will be initialized with {@code false}.
     *
     * @param size
     */
    public Screen__(Point size) {
        super();

        if (size.x < 0 || size.y < 0) {
            throw new IllegalArgumentException("illegal size");
        }

        this.size = size;
        this.pixels = new Pixel[Math.max(30, size.x)][Math.max(30, size.y)];
        for (int x = 0; x < size.x; ++x) {
            for (int y = 0; y < size.y; ++y) {
                this.pixels[x][y] = new Pixel();
            }
        }
    }

    /**
     * @return The current size of the screen.
     */
    public final Point getSize() {
        return this.size;
    }

    /**
     * Set the size of the Screen.
     *
     * @param size           The new size of the screen.
     */
    public final void resize(Point size) {
        if (size.x < 0 || size.y < 0) {
            throw new IllegalArgumentException("illegal size");
        }

        boolean doResize;
        Point resizeTo = size;

        // If the new size is completely smaller than the size
        // we currently have, we only actually resize when the
        // size is reduced heavily.
        if (size.x < this.size.x && size.y < this.size.y) {
            doResize = size.x * size.y > this.size.x * this.size.y;
        }

        // Otherwise, we will need to resize anyway. But maybe we
        // could figure out a sligthly greater size than the request
        // size to prevent the next resize with slight increases to
        // cause just another resize?
        // PS: If you can make this shorter, please go ahead.
        else {
            doResize = true;
            resizeTo = new Point(
                    Math.max(size.x, this.size.x) + 10,  // todo: Make the margin derivable somehow.
                    Math.max(size.y, this.size.y) + 10);
        }

        if (!doResize) {
            System.out.println("No resize.");
            this.size = size;
            return;
        }

        long start = System.currentTimeMillis();

        // Allocate a new array for the Pixels and copy over as much
        // as possible.
        Pixel[][] newPixels = new Pixel[resizeTo.x][resizeTo.y];
        for (int x = 0; x < resizeTo.x; ++x) {
            for (int y = 0; y < resizeTo.y; ++y) {
                if (x < this.size.x && y < this.size.y) {
                    newPixels[x][y] = this.pixels[x][y];
                }
                else {
                    newPixels[x][y] = new Pixel();
                }
            }
        }

        this.size = resizeTo;
        System.out.println("Resize time: " + (System.currentTimeMillis() - start) + "ms");
    }

    /**
     * Sets the behaviour of the cursor when it hits the boundaries of the
     * screen in a call to {@link #putCharacter(char)}. The initial value
     * is {@link CursorBehaviour#Steady}).
     *
     * @param behaviour
     */
    public final void setCursorBehaviour(CursorBehaviour behaviour) {
        this.cursorBehaviour = behaviour;
    }

    /**
     * Moves the cursor to the specified position.
     *
     * @param x
     * @param y
     */
    public final void moveCursor(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Overwrites the character at the current location and moves the
     * cursor about one unit in positive x direction. The cursor could
     * move into the next line if the behaviour is set to {@link
     * CursorBehaviour#NextLine}).
     *
     * @param c
     */
    public final void putCharacter(char c) {
        // Make sure the cursor is not out of bounds.
        if (this.x < 0 || this.y < 0) return;
        if (this.x >= this.size.x || this.y >= this.size.y) return;

        Pixel pixel = this.pixels[this.x][this.y];
        pixel.c = c;
        pixel.fg = this.fg;
        pixel.bg = this.bg;
        pixel.sgr = this.sgr;

        ++this.x;
        if (this.cursorBehaviour == CursorBehaviour.NextLine) {
            if (this.x >= this.size.x) {
                this.x = 0;
            }
        }
    }

    /**
     * Puts the String {@code text} via {@link #putCharacter(char)} from
     * the current cursor location.
     *
     * @param text
     */
    public final void putString(String text) {
        for (int index = 0; index < text.length(); ++index) {
            this.putCharacter(text.charAt(index));
        }
    }

    /**
     * Changes the foreground color to the specified value. All future
     * calls to {@link #putCharacter(char)} will use that color.
     *
     * @param color
     */
    public final void applyForegroundColor(Color color) {
        if (color == null) {
            color = Color.DEFAULT;
        }
        this.fg = color;
    }

    /**
     * Changes the background color to the specified value. All future
     * calls to {@link #putCharacter(char)} will use that color.
     *
     * @param color
     */
    public final void applyBackgroundColor(Color color) {
        if (color == null) {
            color = Color.DEFAULT;
        }
        this.bg = color;
    }

    /**
     * Sets the specified SGR value for all future calls to {@link
     * #putCharacter(char)}.
     *
     * @param sgr
     */
    public final void applySGR(SGR sgr) {
        this.sgr = sgr;
    }

    /**
     * Clears the screen and leaves you with blank space. Unlike the lanterna
     * terminal {@code clearScreen()} method, this method will use the
     * active background color for clearing the screen.
     */
    public final void clearScreen() {
        for (int x = 0; x < this.size.x; ++x) {
            for (int y = 0; y < this.size.y; ++y) {
                Pixel pixel = this.pixels[x][y];
                pixel.fg = this.fg;
                pixel.bg = this.bg;
                pixel.sgr = null;
                pixel.c = ' ';
            }
        }
    }

    /**
     * Blits the contents of the screen on the lanterna {@link Terminal}.
     * This method will synchronize the terminal object until the contents
     * have been fully copied.
     *
     * @param terminal
     */
    public final void blit(Terminal terminal) {
        synchronized (terminal) {
            long start = System.currentTimeMillis();
            terminal.applySGR(SGR.RESET_ALL);

            Color prevFg = null;
            Color prevBg = null;
            SGR prevSgr = null;

            for (int x = 0; x < this.size.x; ++x) {
                for (int y = 0; y < this.size.y; ++y) {
                    Pixel pixel = this.pixels[x][y];

                    terminal.applyForegroundColor(pixel.fg);
                    terminal.applyBackgroundColor(pixel.bg);
                    terminal.applySGR(pixel.sgr);
                    terminal.moveCursor(x, y);
                    terminal.putCharacter(pixel.c);
                }
            }
            System.out.println("Blit time: " + (System.currentTimeMillis() - start) + "ms");
        }
    }

}
