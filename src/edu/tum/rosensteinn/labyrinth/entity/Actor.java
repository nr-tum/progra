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

/**
 * An Actor is a moving, or even "thinking" entity. An actor computes its
 * new location in {@link #calculateNewLocation(pacmania.Level, double)}.
 * If this location can not be reached due to another blocking entity,
 * the maximum distance is travelled and set in the Actor's {@link #location},
 * and then {@link #onCollision(pacmania.Level, pacmania.Entity)} will be
 * called with the solid entity.
 *
 * The way of an Actor can only be blocked if it is itself solid, which it
 * is by default.
 */
public abstract class Actor extends Entity {

    /**
     * Subclasses must implement this method to compute their new
     * location. This new location could be invalid due to another
     * blocking entity. If this is the case, {@link
     * #onNewLocationBlocked(Entity, Coordinate)} is called.
     *
     * @param level
     * @param deltaTime
     * @return The new location of the entity.
     */
    public abstract Location calculateNewLocation(Level level, double deltaTime);

    // Entity

    @Override
    public void update(Level level, double deltaTime) {
        Point levelSize = level.getSize();
        Location current = this.location.copy();
        Location next = this.calculateNewLocation(level, deltaTime);
        Point[] path = current.computePath(next);

        // Check if any other entities are crossed and invoke their
        // onCollision() method. If any of the entities is solid, this
        // one will stop here.
        boolean collision = false;
        for (int i = 1; i < path.length && !collision; ++i) {
            // Make sure the entity doesn't leave the level boundaries.
            Point pos = path[i];
            if (!levelSize.contains(pos)) {
                collision = true;
                this.onCollision(level, null);
                this.location = new Location(path[i - 1]);
                break;
            }

            // Get the entities at the current location.
            EntityList entities = level.getEntitiesAt(path[i]);
            if (entities == null || entities.isEmpty())
                continue;

            // Check each entitiy if it's solid.
            for (Entity entity : entities) {
                // It should never happen that we find the same entity
                // in the current field.
                assert(entity != this);

                // If the this and the other entity is solid, we can not
                // pass through it.
                if (entity.isSolid() && this.isSolid()) {
                    // Set the current entities location the farthest
                    // distance, near the entity it just collided with.
                    this.location = new Location(path[i - 1]);
                    collision = true;
                }

                this.onCollision(level, entity);
                entity.onCollision(level, this);
                if (collision) {
                    break;
                }
            }
        }

        // If the entity didn't collide with a solid entity, we will use
        // the calculated location as its new location.
        if (!collision) {
            this.location = next;
        }
    }

    @Override
    public boolean isSolid() {
        return true;
    }

}
