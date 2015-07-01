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
import edu.tum.rosensteinn.labyrinth.gui.widget.*;

import com.googlecode.lanterna.input.Key;

/**
 * This view is displayed when the user presses the ESC button while
 * being in the game.
 */
public class PauseView extends WidgetView {

    private final View mainView;
    private final Level level;

    public PauseView(View mainView, Styling styling, Level level) {
        super(styling);
        this.mainView = mainView;
        this.level = level;
        this.init();
    }

    private void init() {
        Styling styling = this.getRoot().getStyling();
        VBoxLayout layout = new VBoxLayout();
        this.getRoot().add(layout);
        SelectionBox box = new SelectionBox();
        layout.add(box);
        box.addEntry("Continue", new ActionListener.PopView());
        box.addEntry("View Legend", new ActionListener.PushView(new LegendView(styling)));
        box.addEntry("Save Game", new ActionListener.PushView(new SaveView(styling, this.level)));
        box.addEntry("Main Menu", new ActionListener.ChangeView(this.mainView));
    }

    @Override
    public void onEvent(Window window, Event event) {
        if (event instanceof KeyboardEvent) {
            Key key = ((KeyboardEvent) event).key;
            if (key.getKind() == Key.Kind.Escape) {
                window.popView();
                return;
            }
        }
        super.onEvent(window, event);
    }

}
