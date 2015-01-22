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

package edu.tum.rosensteinn.labyrinth.entity;

import edu.tum.rosensteinn.labyrinth.Point;
import edu.tum.rosensteinn.labyrinth.gui.Screen;

import com.googlecode.lanterna.terminal.Terminal.Color;

/**
 * Represents a wall.
 */
public class Wall extends Entity {

    @Override
    public boolean isSolid() {
        return true;
    }

    public static final Entity.Visual visual = new Entity.Visual(
            ' ', Color.DEFAULT, Color.WHITE);

    @Override
    public Visual getVisual() {
        return Wall.visual;
    }
}
