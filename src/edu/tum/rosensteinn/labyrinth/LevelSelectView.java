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

import java.io.File;
import edu.tum.rosensteinn.labyrinth.gui.*;
import edu.tum.rosensteinn.labyrinth.gui.widget.*;

/**
 * Displays all {@code .properties} files in a directory and tries to
 * open the one selected by the user as a level.
 */
public class LevelSelectView extends WidgetView {

    private final File directory;
    private SelectionBox selectionBox;

    public LevelSelectView(File directory, Styling styling) {
        super(styling);
        this.directory = directory;
        this.init();
    }

    private void init() {
        VBoxLayout layout = new VBoxLayout();
        this.getRoot().add(layout);
        this.selectionBox = new SelectionBox();
        layout.add(this.selectionBox);
    }

    private void update() {
        this.selectionBox.clearEntries();
        ActionListener callback = (Window window, Object source, Object data) -> {
            SelectionBox bbox = (SelectionBox) source;
            this.onSelection(window, bbox.getLabel(bbox.getActive()));
        };
        for (String filename : this.directory.list()) {
            this.selectionBox.addEntry(filename, callback);
        }
        this.selectionBox.addEntry("Back", new ActionListener.PopView());
        this.selectionBox.setLayoutChanged();
    }

    private void onSelection(Window window, String filename) {
        java.io.File file = new java.io.File(this.directory, filename);
        Level level = null;
        try {
            level = Main.loadLevel(file);
        }
        catch (DataFormatException e) {
            Main.showError(window, e.getMessage());
            return;
        }
        LevelView view = new LevelView(this.getBack(), this.getRoot().getStyling(), level);
        window.pushView(view);
    }

    @Override
    public void onFocus(Window window) {
        this.update();
        super.onFocus(window);
    }

}
