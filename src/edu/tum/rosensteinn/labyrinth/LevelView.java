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

import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal;

/**
 * This class renders the level, opens the pause menu when ESC is pressed
 * and forwards events to the level entities.
 */
public class LevelView extends View {

    private long lastUpdate;
    private final Styling styling;
    private final Level level;
    private final View mainView;
    private final PauseView pauseView;
    private final Player player;
    private final EntityList keys;

    public LevelView(View mainView, Styling styling, Level level) {
        super();
        this.styling = styling;
        this.level = level;
        this.mainView = mainView;
        this.pauseView = new PauseView(mainView, styling, level);
        this.lastUpdate = -1;
        this.player = level.getPlayer();
        this.keys = level.getEntitiesByClass(edu.tum.rosensteinn.labyrinth.entity.Key.class);
    }

    private void update() {
        double deltaTime = 0.0;
        long curr = System.currentTimeMillis();
        if (this.lastUpdate > 0) {
            deltaTime = (double) (curr - this.lastUpdate) / 1000.0;
        }
        this.lastUpdate = curr;
        this.level.update(deltaTime);
    }

    @Override
    public void onFocus(Window window) {
        // If there is no player in this level, tell it!
        if (this.player == null) {
            window.setView(this.mainView);
            Main.showError(window, "Invalid level: contains\nno Player Entity!");
            return;
        }
    }

    @Override
    public void onEvent(Window window, Event event) {
        if (event instanceof KeyboardEvent) {
            Key key = ((KeyboardEvent) event).key;
            if (key.getKind() == Key.Kind.Escape) {
                window.pushView(this.pauseView);
                this.lastUpdate = -1;
            }
            else {
                this.level.event(event);
            }
        }
        else if (event instanceof ReocurringEvent) {
            this.update();
            Player player = this.level.getPlayer();
            if (player == null || player.lives <= 0) {
                window.setView(this.mainView);
                Main.showInfo(window, "    Game Over.    ");
                return;
            }
            else if (player != null && player.won) {
                window.setView(this.mainView);
                Main.showInfo(window, "   Congratulations!   ");
                return;
            }
        }
    }

    @Override
    public void render(Window window, boolean initial) {
        Screen screen = window.getScreen();
        Point screenSize = screen.getSize();

        // Render the health and key bar into the top line.
        screen.moveCursor(0, 0);
        screen.applyForegroundColor(Terminal.Color.BLACK);
        screen.applyBackgroundColor(Terminal.Color.GREEN);
        for (int i = 0; i < screenSize.x; ++i) {
            screen.putCharacter(' ');
        }

        screen.moveCursor(0, 0);
        screen.putString("Life: ");
        screen.applyForegroundColor(Terminal.Color.RED);
        for (int i = 0; i < this.player.lives; ++i) {
            screen.putCharacter('\u2665'); // Heart
        }

        int keysTotal = this.keys.size();

        String text = "Keys: ";
        screen.moveCursor(screenSize.x - text.length() - keysTotal, 0);
        screen.applyForegroundColor(Terminal.Color.BLACK);
        screen.putString(text);
        for (Entity key : this.keys) {
            Entity.Visual v = key.getVisual();
            if (key.isAlive()) {
                screen.applyForegroundColor(Terminal.Color.BLACK);
            }
            else {
                screen.applyForegroundColor(Terminal.Color.YELLOW);
            }
            screen.putCharacter(v.c);
        }

        // Shrink the screen size by one and introduce the vertical offset.
        screenSize = new Point(screenSize.x, screenSize.y - 1);
        screen.pushOffset(new Point(0, 1));

        Point levelSize = this.level.getSize();
        Point playerPos = player.location.toPoint();
        Point levelOffset = new Point(
                -(playerPos.x - (screenSize.x / 2)),
                -(playerPos.y - (screenSize.y / 2)));

        for (int x = 0; x < screenSize.x; ++x) {
            for (int y = 0; y < screenSize.y; ++y) {
                screen.moveCursor(x, y);
                Point entityPos = new Point(x, y).sub(levelOffset);
                Entity entity = level.getEntityAt(entityPos);
                if (entity == null) {
                    screen.applyBackgroundColor(Terminal.Color.BLACK);
                    screen.putCharacter(' ');
                }
                else {
                    Entity.Visual v = entity.getVisual();
                    screen.applyForegroundColor(v.fg);
                    screen.applyBackgroundColor(v.bg);
                    screen.putCharacter(v.c);
                }
            }
        }

        screen.popOffset();
    }

}
