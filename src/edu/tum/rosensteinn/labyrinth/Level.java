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

import edu.tum.rosensteinn.labyrinth.gui.Event;
import edu.tum.rosensteinn.labyrinth.entity.*;

/**
 * A level represents a collection of {@link Entity} objects represented
 * in a two dimensional field where each field can hold zero or more
 * entities.
 */
public class Level {

    private final Point size;
    private final EntityList[][] fields;

    private final EntitySet entities = new EntitySet();
    private final EntitySet removedEntities = new EntitySet();
    private final EntitySet addedEntities = new EntitySet();

    /**
     * Create a new level with the specified size. The size of the
     * level can not be altered later on.
     *
     * @param size           The size of the level.
     */
    public Level(Point size) {
        this.size = size;
        this.fields = new EntityList[size.x][size.y];

        // Initialize all fields with an empty list.
        for (int x = 0; x < size.x; ++x) {
            for (int y = 0; y < size.y; ++y) {
                this.fields[x][y] = new EntityList();
            }
        }
    }

    /**
     * Saves the Level's current state to a {@link java.util.Properties}
     * object.
     *
     * @param props          The object that should receive the data. You
     *                       may pass null to create an empty object.
     * @return The properties object.
     */
    public final java.util.Properties save(java.util.Properties props) {
        if (props == null) {
            props = new java.util.Properties();
        }
        props.setProperty("Width", "" + this.size.x);
        props.setProperty("Height", "" + this.size.y);
        Player player = this.getPlayer();
        if (player != null) {
            props.setProperty("Player", player.save());
        }
        for (int x = 0; x < this.size.x; ++x) {
            for (int y = 0; y < this.size.y; ++y) {
                Entity entity = this.getEntityAt(new Point(x, y));
                if (entity == null)
                    continue;

                String key = String.format("%d,%d", x, y);
                String value = null;
                if (entity instanceof Wall) {
                    value = "0";
                }
                else if (entity instanceof Entrance) {
                    value = "1";
                }
                else if (entity instanceof Exit) {
                    value = "2";
                }
                else if (entity instanceof StaticThreat) {
                    value = "3";
                }
                else if (entity instanceof Enemy) {
                    value = "4";
                }
                else if (entity instanceof Key) {
                    value = "5";
                }

                if (value != null) {
                    props.setProperty(key, value);
                }
            }
        }
        return props;
    }

    /**
     * Propagates the specified {@link Event} to the entities in the level.
     *
     * @param event          The event to pass on to all entities.
     */
    public void event(Event event) {
        for (Entity entity : this.entities) {
            entity.onEvent(this, event);
        }
    }

    /**
     * This method propagates the update to all entities it contains.
     * Entities that are queued for adding or removal are processed
     * before and after the update and will be updated as well. If an
     * entity creates another entity during an update phase, this
     * <b>new entity</b> will not get the same update phase.
     *
     * @param deltaTime
     */
    public void update(double deltaTime) {
        // Update all entities.
        for (Entity entity : this.entities) {
            entity.prevLocation.copyFrom(entity.location);
            entity.update(this, deltaTime);
        }


        // Clear all fields that store entity locations for quick access.
        // We will restore it later in this function with the updated
        // entity locations.
        for (int x = 0; x < this.size.x; ++x) {
            for (int y = 0; y < this.size.y; ++y) {
                this.fields[x][y].clear();
            }
        }

        // Update the fields array.
        for (Entity entity : this.entities) {
            this.assignEntityToField(entity);
        }
        this.commitChanges();
    }

    /**
     * Removes the specified {@link Entity} from the level. This entity
     * will not be removed immediately but queued from removal.
     *
     * @param entity         The entity to remove.
     * @return {@code true} when the entity was actually an entity of
     *         the level, {@code false} if it was not actually part of it.
     */
    public boolean removeEntity(Entity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("can not removenull Entity");
        }
        if (!this.entities.contains(entity)) {
            // If the entity was added within this update frame and is
            // now removed, we'll just pull it from the added queue again.
            if (this.addedEntities.remove(entity)) {
                entity.onRemove(this);
            }
            return false;
        }
        this.removedEntities.add(entity);
        return true;
    }

    /**
     * Adds the specified {@link Entity} to the level. This entity will not
     * be added immediately but queued for adding.
     *
     * @param entity         The entity to add.
     * @return {@code true} when the entity was already part of the level,
     *         false if not.
     */
    public boolean addEntity(Entity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("can not add null Entity");
        }
        if (this.entities.contains(entity)) {
            // If the entity was removed withing this update, we'll undo
            // the removal.
            if (this.removedEntities.remove(entity)) {
                entity.onAdd(this);
            }
            return false;
        }
        this.addedEntities.add(entity);
        return true;
    }

    /**
     * @param pos            The coordinate to get the entities for.
     * @return The {@link EntityList} at the specified coordinate. {@code
     *         null} is returned if the coordinate is out of bounds.
     */
    public EntityList getEntitiesAt(Point pos) {
        boolean valid = pos.x >= 0 && pos.x < this.size.x &&
                        pos.y >= 0 && pos.y < this.size.y;
        if (valid) {
            return this.fields[pos.x][pos.y];
        }
        return null;
    }

    public EntityList getEntitiesAt(int x, int y) {
        return this.getEntitiesAt(new Point(x, y));
    }

    /**
     * @param pos            The entity coordinate.
     * @return The entity with the highest z-depth, or {@code null} if
     *         there is no entity at this position.
     */
    public Entity getEntityAt(Point pos) {
        EntityList list = this.getEntitiesAt(pos);
        if (list == null || list.isEmpty()) {
            return null;
        }
        java.util.Iterator<Entity> it = list.iterator();
        Entity result;
        for (result = it.next(); it.hasNext();) {
            Entity curr = it.next();
            if (curr.getZDepth() > result.getZDepth()) {
                result = curr;
            }
        }
        return result;
    }

    public Entity getEntityAt(int x, int y) {
        return this.getEntityAt(new Point(x, y));
    }

    /**
     * @param pos
     * @return {@code true} if there is a solid entity the specified
     *         position, {@code false} if not.
     */
    public boolean isSolidAt(Point pos) {
        EntityList list = this.getEntitiesAt(pos);
        if (list == null || list.isEmpty()) {
            return false;
        }
        for (Entity entity : list) {
            if (entity.isSolid()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param x
     * @param y
     * @return
     */
    public boolean isSolidAt(int x, int y) {
        return this.isSolidAt(new Point(x, y));
    }

    /**
     * @param cls            The class to search for.
     * @return A set of entities by class.
     */
    public EntityList getEntitiesByClass(java.lang.Class<? extends Entity> cls) {
        EntityList result = new EntityList();
        for (Entity entity : this.entities) {
            if (cls.isAssignableFrom(entity.getClass())) {
                result.add(entity);
            }
        }
        return result;
    }

    /**
     * @return The {@link Player} entity, or {@code null}.
     */
    public Player getPlayer() {
        EntityList list = this.getEntitiesByClass(Player.class);
        if (list.isEmpty()) {
            return null;
        }
        else if (list.size() > 1) {
            System.err.println("WARNING: Multiple Player entities found.");
        }
        return (Player) list.get(0);
    }

    /**
     * @return The size of the level.
     */
    public Point getSize() {
        return this.size;
    }

    /**
     * This method commits changes to the entity sets. It should never
     * be called by an entity, especially not in its {@link
     * Entity#update(Level, double)}) method.
     */
    public final void commitChanges() {
        this.entities.addAll(this.addedEntities);
        this.entities.removeAll(this.removedEntities);

        for (Entity entity : this.addedEntities) {
            this.assignEntityToField(entity);
            entity.onAdd(this);
        }
        for (Entity entity : this.removedEntities) {
            entity.onRemove(this);
            this.removeEntityFromField(entity);
        }

        this.addedEntities.clear();
        this.removedEntities.clear();
    }

    // ----------------------------------------------------------------------

    private void assignEntityToField(Entity entity) {
        Point pos = entity.location.toPoint();
        boolean contained = (
                pos.x >= 0 && pos.x < this.size.x &&
                pos.y >= 0 && pos.y < this.size.y);
        if (contained) {
            this.fields[pos.x][pos.y].add(entity);
        }
    }

    private void removeEntityFromField(Entity entity) {
        Point pos = entity.location.toPoint();
        boolean contained = (
                pos.x >= 0 && pos.x < this.size.x &&
                pos.y >= 0 && pos.y < this.size.y);
        if (contained) {
            this.fields[pos.x][pos.y].remove(entity);
        }
    }

    /**
     * Reads the Level from a Properties object.
     *
     * @throws DataFormatException If the level data is invalid.
     */
    public static Level readFromProperties(java.util.Properties props)
        throws DataFormatException
    {
        // Read in the Width and Height properties and convert them directly
        // to integers. If the conversion fails, the property value must
        // either be invalid or non-existent.
        int width, height;
        try {
            width  = Integer.parseInt(props.getProperty("Width"));
            height = Integer.parseInt(props.getProperty("Height"));
            if (width <= 0 || height <= 0)
                throw new NumberFormatException();
        }
        catch (NumberFormatException e) {
            throw new DataFormatException("Invalid Width and/or Height.");
        }

        // Check if there is a player saved with this level.
        Player player = null;
        String playerData = props.getProperty("Player");
        if (playerData != null) {
            try {
                player = Player.load(playerData);
            }
            catch (DataFormatException e) {
                throw new DataFormatException("invalid Player data: " + e.getMessage());
            }
        }

        // Create the new Level object and fill it with the data
        // from the Properties object.
        Level level = new Level(new Point(width, height));
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // Construct the property key for the current field
                // and read the property value.
                String key = String.format("%d,%d", x, y);
                String value = props.getProperty(key);
                if (value == null || value.isEmpty())
                    continue;

                // Parse the property value into a number.
                int entityType;
                try {
                    entityType = Integer.parseInt(value);
                }
                catch (NumberFormatException e) {
                    throw new DataFormatException("'%s' is invalid", key);
                }

                // Translate the parsed entityType to an enumeration value.
                Entity entity = null;
                switch (entityType) {
                    case 0:
                        entity = new Wall();
                        break;
                    case 1:
                        entity = new Entrance();
                        break;
                    case 2:
                        entity = new Exit();
                        break;
                    case 3:
                        entity = new StaticThreat();
                        break;
                    case 4:
                        entity = new Enemy();
                        break;
                    case 5:
                        entity = new Key();
                        break;
                    default:
                        throw new DataFormatException(
                                "Unknown Entity type at '%s'", key);
                }

                if (entity != null) {
                    entity.location = new Location(x, y);
                    level.addEntity(entity);
                }
            }
        }

        level.commitChanges();

        // Make sure the player would not be placed on a solid entity.
        if (player != null) {
            if (level.isSolidAt(player.location.toPoint())) {
                throw new DataFormatException(
                        "Player location is occupied with solid entity");
            }
            level.addEntity(player);
            level.commitChanges();
        }
        return level;
    }

}
