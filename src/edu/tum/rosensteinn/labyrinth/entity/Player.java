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

import com.googlecode.lanterna.terminal.Terminal.Color;
import java.util.Locale;

import edu.tum.rosensteinn.labyrinth.Level;
import edu.tum.rosensteinn.labyrinth.DataFormatException;
import edu.tum.rosensteinn.labyrinth.gui.Event;
import edu.tum.rosensteinn.labyrinth.gui.KeyboardEvent;

/**
 * This {@link Entity} implementation represents the player that must
 * find his way through the maze.
 */
public class Player extends Actor {

    public static final Entity.Visual visual = new Entity.Visual(
            '\u263B', Color.MAGENTA, Color.DEFAULT);

    /**
     * The number of lives that remain.
     */
    public int lives;

    /**
     * The number of maximum lives that the Player can have.
     */
    public int maxLives;

    /**
     * The direction in which the Player is facing.
     */
    public Direction direction;

    /**
     * The number of fields the Player travels per second.
     */
    public int speed;

    /**
     * The number of keys picked up.
     */
    public int keys;

    /**
     * True if the player has won.
     */
    public boolean won;

    /**
     * {@code true} if the Player is currently moving, {@code false} if
     * it is not. A player may stop moving if it hits a solid entity.
     */
    public boolean moving;

    public Player(int maxLives, Direction direction, int speed) {
        super();
        this.lives = maxLives;
        this.maxLives = maxLives;
        this.direction = direction;
        this.speed = speed;
        this.won = false;
        this.keys = 0;
        this.moving = false;
    }

    /**
     * Converts the player to a string that can be parsed via {@link
     * #load(String)}.
     *
     * @return The data string.
     */
    public final String save() {
        return "" + this.lives + " " + this.maxLives + " "
                  + this.direction.ordinal() + " " + this.speed + " "
                  + this.keys + " " + this.moving + " " + this.location.x + " "
                  + this.location.y;
    }

    @Override
    public final int getZDepth() {
        return 100;
    }

    @Override
    public final void onCollision(Level level, Entity other) {
        if (other == null) { // collision with level boundaries
            this.moving = false;
            return;
        }
        else if (other.isSolid()) {
            this.moving = false;
        }

        if (other instanceof Key) {
            level.removeEntity(other);
            this.keys += 1;
        }
        else if(other instanceof StaticThreat) {
            this.lives -= 1;
        }
        else if (other instanceof Enemy) {
            this.lives = 0;
        }
        else if (other instanceof Exit) {
            if (level.getEntitiesByClass(Key.class).isEmpty()) {
                this.won = true;
            }
        }
    }

    @Override
    public final boolean onEvent(Level level, Event event) {
        if (event instanceof KeyboardEvent) {
            com.googlecode.lanterna.input.Key key = ((KeyboardEvent) event).key;
            switch (key.getKind()) {
                case ArrowLeft:
                    this.moving = true;
                    this.direction = Direction.Left;
                    return true;
                case ArrowRight:
                    this.moving = true;
                    this.direction = Direction.Right;
                    return true;
                case ArrowUp:
                    this.moving = true;
                    this.direction = Direction.Up;
                    return true;
                case ArrowDown:
                    this.moving = true;
                    this.direction = Direction.Down;
                    return true;
            }
            if (key.getCharacter() == ' ') {
                this.moving = false;
                return true;
            }
        }
        return false;
    }

    @Override
    public final Location calculateNewLocation(Level level, double deltaTime) {
        if (!this.moving) {
            return this.location;
        }
        return this.direction.updateLocation(this.location, this.speed * deltaTime);
    }

    @Override
    public final Visual getVisual() {
        return Player.visual;
    }

    // -----------------------------------------------------------------------

    /**
     * Parses player information from a string and returns the player.
     * The data must be formatted as {@code <lives> <maxLives> <direction>
     * <speed> <keys> <moving> <location.x> <location.y>}.
     *
     * @param data
     * @throws DataFormatException
     * @return The {@link Player} that has been read in.
     */
    public static Player load(String data) {
        java.util.Scanner scanner = new java.util.Scanner(data);
        scanner.useLocale(Locale.US);

        Player player = new Player(0, null, 0);
        boolean satisfied = false;
        String place = "";
        do {
            if (!scanner.hasNextInt()) {
                place = "lives";
                break;
            }
            player.lives = scanner.nextInt();
            if (!scanner.hasNextInt()) {
                place = "maxLives";
                break;
            }
            player.maxLives = scanner.nextInt();
            if (!scanner.hasNextInt()) {
                place = "direction";
                break;
            }
            int direction = scanner.nextInt();
            if (direction < 0 || direction >= Direction.values().length)
                throw new DataFormatException("invalid direction index");
            player.direction = Direction.values()[direction];
            if (!scanner.hasNextInt()) {
                place = "speed";
                break;
            }
            player.speed = scanner.nextInt();
            if (!scanner.hasNextInt()) {
                place = "keys";
                break;
            }
            player.keys = scanner.nextInt();
            if (!scanner.hasNextBoolean()) {
                place = "moving";
                break;
            }
            player.moving = scanner.nextBoolean();
            if (!scanner.hasNextDouble()) {
                place = "location.x";
                break;
            }
            player.location.x = scanner.nextDouble();
            if (!scanner.hasNextDouble()) {
                place = "location.y";
                break;
            }
            player.location.y = scanner.nextDouble();
            satisfied = true;
        } while (false);
        if (!satisfied)
            throw new DataFormatException("invalid data structure (" + place + ": " + scanner.next() + ")");
        return player;
    }


}
