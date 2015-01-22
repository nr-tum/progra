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
import edu.tum.rosensteinn.labyrinth.tools.FpsTimer;
import com.googlecode.lanterna.terminal.Terminal;

public class Main {

    public static java.io.File levelsFolder = new java.io.File("./levels");
    public static java.io.File savesFolder = new java.io.File("./saves");

    public static void showInfo(Window window, String message) {
        Styling styling = new Styling();
        styling.editBgColor = Terminal.Color.BLUE;
        styling.editFgColor = Terminal.Color.WHITE;
        View view = new MessageView(message, styling);
        window.pushView(view);
    }

    public static void showError(Window window, String message) {
        Styling styling = new Styling();
        styling.editBgColor = Terminal.Color.RED;
        styling.editFgColor = Terminal.Color.WHITE;
        View view = new MessageView(message, styling);
        window.pushView(view);
    }

    public static Level loadLevel(java.io.File file) throws DataFormatException {
        if (!file.isFile()) {
            throw new DataFormatException("'%s' does not exist.", file);
        }

        // Load the properties file.
        java.util.Properties props = new java.util.Properties();
        try {
            props.load(new java.io.FileInputStream(file));
        }
        catch (java.io.IOException e) {
            System.err.println("Error loading Properties file:");
            e.printStackTrace();
            throw new DataFormatException(e.getMessage());  // todo: Implement the cause
        }

        // Load the level from the properties file.
        Level level = null;
        level = Level.readFromProperties(props);

        // Create a player at a random entrance.
        if (level.getPlayer() == null) {
            EntityList entrances = level.getEntitiesByClass(Entrance.class);
            if (entrances.isEmpty()) {
                throw new DataFormatException("level contains no entrances");
            }
            Entity entrance = entrances.get((int) (Math.random() * entrances.size()));
            Player player = new Player(3, null, 5);
            player.location.copyFrom(entrance.location);
            level.addEntity(player);
            level.commitChanges();
        }

        return level;
    }

    public static void main(String[] args) {
        // Create the Window with the MainView and initialize the
        // Terminal size.
        MainView mainView = new MainView(Styling.defaultStyling);
        Window window = new Window(mainView, 50, 30, false);
        window.open();

        // Main loop limited to a certain FPS.
        FpsTimer timer = new FpsTimer(60);
        while (window.isOpen()) {
            window.dispatchEvents();
            window.redraw();
            timer.sleep();
        }
        System.out.println("Main Thread End.");
    }

}
