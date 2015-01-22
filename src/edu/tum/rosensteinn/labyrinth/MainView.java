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
import edu.tum.rosensteinn.labyrinth.entity.*;

public class MainView extends WidgetView {

    private LevelSelectView newGameView;
    private LevelSelectView loadGameView;

    public MainView(Styling styling) {
        super(styling);
        this.newGameView = new LevelSelectView(Main.levelsFolder, styling);
        this.loadGameView = new LevelSelectView(Main.savesFolder, styling);
        this.init();
    }

    private void init() {
        Styling styling = this.getRoot().getStyling();
        VBoxLayout layout = new VBoxLayout();
        SelectionBox box = new SelectionBox();
        layout.add(box);
        box.addEntry("New Game", new ActionListener.PushView(this.newGameView));
        box.addEntry("Load Game", new ActionListener.PushView(this.loadGameView));
        box.addEntry("Quit", new ActionListener.Quit());
        this.getRoot().add(layout);
    }

}
