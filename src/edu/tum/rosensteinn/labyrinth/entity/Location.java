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

/**
 * This class represents a <b>mutable</b> floating-point coordinate in two
 * dimensional space. It can be converted to an integral immutable coordinate
 * using the {@link #toPoint()} method.
 */
public final class Location {

    public double x, y;

    public Location() {
        this.x = 0.0;
        this.y = 0.0;
    }

    public Location(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Location(Point coord) {
        this.x = (double) coord.x;
        this.y = (double) coord.y;
    }

    public Location(Location other) {
        this.x = other.x;
        this.y = other.y;
    }

    public Location copy() {
        return new Location(this);
    }

    public void copyFrom(Location other) {
        this.x = other.x;
        this.y = other.y;
    }

    public Point toPoint() {
        int ix = (int) Math.round(this.x);
        int iy = (int) Math.round(this.y);
        return new Point(ix, iy);
    }

    /**
     * This method implements Bresenham's line algorithm to compute the
     * coordinates the lie between the old and new location. A list of
     * {@link Point} objects is returned.
     *
     * @param  dest          The destination location.
     * @return An array of {@link Point} objects that represent the
     *         locations that object moved through. This will be a list
     *         of a single item if it remains at its original position.
     */
    public Point[] computePath(Location dest) {
        // See https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm

        final java.util.ArrayList<Point> result
                = new java.util.ArrayList<>();
        final Point from = this.toPoint();
        final Point to = dest.toPoint();
        final int x0, y0, x1, y1, signX, signY;

        x0 = from.x;
        y0 = from.y;
        x1 = to.x;
        y1 = to.y;
        signX = ((x1 - x0) < 0 ? -1 : 1);
        signY = ((y1 - y0) < 0 ? -1 : 1);

        // Special handling for vertical lines. deltaX would be zero.
        if (x1 == x0) {
            for (int iy = y0; iy != (y1 + signY); iy += signY) {
                result.add(new Point(x0, iy));
            }
            return result.toArray(new Point[0]);
        }

        double deltaX, deltaY, error, deltaError;
        deltaX = x1 - x0;
        deltaY = y1 - y0;
        error = 0;
        deltaError = Math.abs(deltaY / deltaX);

        int iy = y0;
        for (int ix = x0; ix != (x1 + signX); ix += signX) {
            Point current = new Point(ix, iy);
            result.add(current);

            error += deltaError;
            while (error >= 0.5) {
                Point next = new Point(ix, iy);
                if (!next.equals(current)) {
                    result.add(next);
                }

                iy += signY;
                error -= 1.0;
            }
        }

        return result.toArray(new Point[0]);
    }

    @Override
    public String toString() {
        return "Location(" + this.x + ", " + this.y + ")";
    }

}
