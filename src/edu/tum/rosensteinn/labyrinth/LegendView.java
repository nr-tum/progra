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

import com.googlecode.lanterna.terminal.Terminal;

public class LegendView extends WidgetView {

    private class LegendWidget extends Widget {

        private final Entity.Visual visual;
        private final String text;

        public LegendWidget(Entity.Visual visual, String text) {
            super();
            this.visual = visual;
            this.text = text;
        }

        @Override
        public void computeSize(Point availableSpace) {
            this.size = new Point(this.text.length() + 2, 1);
        }

        @Override
        public void paint(Window window, Point offset) {
            Screen screen = window.getScreen();
            Point pos = this.position.add(offset);
            screen.moveCursor(pos.x, pos.y);
            screen.applyForegroundColor(this.visual.fg);
            screen.applyBackgroundColor(this.visual.bg);
            screen.putCharacter(this.visual.c);
            screen.applyForegroundColor(Terminal.Color.DEFAULT);
            screen.applyBackgroundColor(Terminal.Color.DEFAULT);
            screen.putCharacter(' ');
            screen.putString(this.text);
        }

    }

    public LegendView(Styling styling) {
        super(styling);
        this.init();
    }

    private void init() {
        VBoxLayout layout = new VBoxLayout();
        this.getRoot().add(layout);
        layout.add(new LegendWidget(Player.visual, "You"));
        layout.add(new LegendWidget(Entrance.visual, "Where you're coming from"));
        layout.add(new LegendWidget(Exit.visual, "Where you're going to"));
        layout.add(new LegendWidget(Key.visual, "What you're searching for"));
        layout.add(new LegendWidget(StaticThreat.visual, "What kills you"));
        layout.add(new LegendWidget(Enemy.visual, "What hunts you down"));
        layout.add(new LegendWidget(Wall.visual, "What ..eh"));
    }

    @Override
    public void onEvent(Window window, Event event) {
        if (event instanceof KeyboardEvent) {
            switch (((KeyboardEvent) event).key.getKind()) {
                case Escape:
                case Enter:
                    window.popView();
                    return;
            }
        }
    }

}
