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

import com.googlecode.lanterna.terminal.Terminal;

/**
 * This class contains styling information that can be used by certain
 * widgets.
 */
public class Styling {

    public Terminal.Color textFgColor = Terminal.Color.WHITE;
    public Terminal.Color textBgColor = Terminal.Color.BLACK;
    public Terminal.Color highlightFgColor = Terminal.Color.BLUE;
    public Terminal.Color highlightBgColor = Terminal.Color.BLACK;
    public Terminal.Color editFgColor = Terminal.Color.BLACK;
    public Terminal.Color editBgColor = Terminal.Color.WHITE;
    public Terminal.Color scrollColor = Terminal.Color.WHITE;
    public Terminal.Color backgroundColor = Terminal.Color.BLACK;

    public static final Styling defaultStyling = new Styling();

}
