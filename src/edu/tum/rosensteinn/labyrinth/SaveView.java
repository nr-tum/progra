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

public class SaveView extends WidgetView {

    private final Level level;

    public SaveView(Styling styling, Level level) {
        super(styling);
        this.level = level;
        this.init();
    }

    private void init() {
        Styling styling = this.getRoot().getStyling();
        VBoxLayout layout = new VBoxLayout();
        this.getRoot().add(layout);
        Input input = new Input("Filename:", 25);
        input.escapeAction = new ActionListener.PopView();
        input.enterAction = (Window window, Object source, Object data) -> {
            this.saveLevel(window, input.getValue());
        };
        layout.add(input);
    }

    private void saveLevel(Window window, String filename) {
        window.setView(this.getBack());

        java.io.File file = new java.io.File(Main.savesFolder, filename);
        java.util.Properties props = level.save(null);
        try {
            props.store(new java.io.FileOutputStream(file), "Comment Here");  // todo: Spezieller Kommentar hier?
        }
        catch (java.io.IOException e) {
            Main.showError(window, e.getMessage());
            return;
        }
        Main.showInfo(window, "Saved.");
    }

}
