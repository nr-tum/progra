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
import edu.tum.rosensteinn.labyrinth.Level;
import com.googlecode.lanterna.terminal.Terminal.Color;

/**
 * This class represents an Enemy actor that will kill the player
 * instantly.
 */
public class Enemy extends Actor {

    public static final Entity.Visual visual = new Entity.Visual(
            '\u2638', Color.RED, Color.DEFAULT);

    private Direction direction = Direction.Down;
    private int speed = 3;
    private final double directionChangeProbability = 0.10;

    public void chooseNewDirection(Level level) {
        Point pos = this.location.toPoint();
        java.util.ArrayList<Direction> choices
                = new java.util.ArrayList<>();
        if (level.getEntityAt(pos.x - 1, pos.y) == null)
            choices.add(Direction.Left);
        if (level.getEntityAt(pos.x + 1, pos.y) == null)
            choices.add(Direction.Right);
        if (level.getEntityAt(pos.x, pos.y - 1) == null)
            choices.add(Direction.Up);
        if (level.getEntityAt(pos.x, pos.y + 1) == null)
            choices.add(Direction.Down);
        if (choices.isEmpty()) {
            this.direction = null;
        }
        else {
            this.direction = choices.get((int) (Math.random() * choices.size()));
        }
    }

    @Override
    public Location calculateNewLocation(Level level, double deltaTime) {
        if (Math.random() < this.directionChangeProbability) {
            this.chooseNewDirection(level);
        }
        if (this.direction == null) {
            return this.location;
        }
        return this.direction.updateLocation(this.location, this.speed * deltaTime);
    }


    @Override
    public Visual getVisual() {
        return Enemy.visual;
    }

}
