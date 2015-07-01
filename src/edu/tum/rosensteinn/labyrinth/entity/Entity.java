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

import edu.tum.rosensteinn.labyrinth.Level;
import edu.tum.rosensteinn.labyrinth.gui.Event;

import com.googlecode.lanterna.terminal.Terminal.Color;

/**
 * An entity describes a static or dynamic object in a level.
 */
public abstract class Entity {

    /**
     * Visual representation of an entity.
     */
    public static class Visual {
        public char c;
        public Color fg;
        public Color bg;
        public Visual(char c, Color fg, Color bg) {
            this.c = c;
            this.fg = fg;
            this.bg = bg;
        }
    }

    /**
     * The location of the entity in the 2 dimensional field. It is
     * internally using a floating-point precision but will be rounded
     * and converted to an integer to determine the fixed location on
     * the field.
     */
    public Location location = new Location();

    /**
     * The previous location of the entity. This is automatically set
     * by the {@link Level} before {@link #update(Level)} is called.
     */
    public Location prevLocation = new Location();

    /**
     * Keeps track if the entity is contained in a level or not.
     */
    private boolean isAlive_ = false;

    /**
     * @return {@code true} if the Entity is part of a {@link Level},
     *         {@code false} if it is not.
     */
    public final boolean isAlive() {
        return this.isAlive_;
    }

    /**
     * This method specifies if the entity is solid or if it can be
     * ignored during collision with another entity. All entities are
     * non-solid by default.
     *
     * @return {@code true} if the Entity is solid, {@code false} if
     *         it is not.
     */
    public boolean isSolid() {
        return false;
    }

    /**
     * Only one entity can be rendered at one "pixel". This method
     * determines the entities z-depth. The entity with the highest
     * z-depth will be rendered on a "pixel".
     *
     * @return The z-depth of the entity.
     */
    public int getZDepth() {
        return 0;
    }

    /**
     * This method is called on both ends when two entities enter the
     * same location.
     *
     * @param level
     * @param other          {@code null} when  the entity collides with
     *                       the level boundaries.
     */
    public void onCollision(Level level, Entity other) {
        // intentionally left blank
    }

    /**
     * This method is called when the entity was queued for adding to
     * the level. When this method is called, the entity has become a
     * real part of the level. One must call the super method when
     * overriding this to ensure {@link #isAlive()} will function.
     *
     * @param level
     */
    public void onAdd(Level level) {
        this.isAlive_ = true;
    }

    /**
     * This method is called when the entity was queued for removal from
     * the level. When this method is called, the entity has lost the
     * link to the level. One must call the super method when overriding
     * this to ensure {@link #isAlive()} will function.
     *
     * @param level
     */
    public void onRemove(Level level) {
        this.isAlive_ = false;
    }

    /**
     * Called on an event. The entity may prevent further propagation of
     * the event by returning {@const true}.
     *
     * @param level
     * @param event          The event.
     * @return {@code true} if the event was processed and propagation
     *         should be stopped, {@code false} if not.
     */
    public boolean onEvent(Level level, Event event) {
        return false;
    }

    /**
     * This method is called each frame to update the entities status. After
     * all entities have been updated, they will be rendered.
     *
     * @param level
     * @param deltaTime      A multiplier that should be used to scale values
     *                       based on seconds to adjust to the current frame
     *                       rate.
     */
    public void update(Level level, double deltaTime) {
        // intentionally left blank
    }

    /**
     * @return the visual representation of the entity.
     */
    public abstract Visual getVisual();

}
