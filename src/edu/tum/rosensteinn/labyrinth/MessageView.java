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

package edu.tum.rosensteinn.labyrinth;

import edu.tum.rosensteinn.labyrinth.gui.*;
import edu.tum.rosensteinn.labyrinth.gui.widget.Styling;
import com.googlecode.lanterna.input.Key;

/**
 * This view displays a message and even renders the view behind it.
 */
public class MessageView extends View {

    private String message;
    private Styling styling;

    public MessageView(String message, Styling styling) {
        super();
        this.setStyling(styling);
        this.setMessage(message);
    }

    public final void setStyling(Styling styling) {
        if (styling == null) {
            styling = Styling.defaultStyling;
        }
        this.styling = styling;
    }

    public final void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void onEvent(Window window, Event event) {
        if (event instanceof KeyboardEvent) {
            Key key = ((KeyboardEvent) event).key;
            switch (((KeyboardEvent) event).key.getKind()) {
                case Escape:
                case Enter:
                    window.popView();
                    return;
            }
        }
    }

    @Override
    public void render(Window window, boolean initial) {
        Screen screen = window.getScreen();
        Point size = screen.getSize();

        String[] lines = this.message.split("\\n");
        int width = 0;
        int height = lines.length;
        for (String line : lines) {
            width = Math.max(line.length(), width);
        }

        int xoff = (size.x - width) / 2;
        int yoff = (size.y - height) / 2;

        screen.applyForegroundColor(this.styling.editFgColor);
        screen.applyBackgroundColor(this.styling.editBgColor);
        screen.moveCursor(xoff - 1, yoff - 1);
        screen.drawRectangle(' ', xoff - 1, yoff - 1, width + 1, height + 1);

        for (int index = 0; index < lines.length; ++index) {
            screen.moveCursor(xoff, yoff++);
            screen.putString(lines[index]);
        }
    }

}
