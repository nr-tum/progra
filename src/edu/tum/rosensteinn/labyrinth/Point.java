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

import com.googlecode.lanterna.terminal.TerminalSize;

/**
 * This class represents an integral <b>immutable</b> coordinate in two
 * dimensional space.
 */
public class Point {

    public final int x, y;

    public Point() {
        this.x = 0;
        this.y = 0;
    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(TerminalSize size) {
        this.x = size.getColumns();
        this.y = size.getRows();
    }

    public Point add(Point other) {
        return new Point(this.x + other.x, this.y + other.y);
    }

    public Point sub(Point other) {
        return new Point(this.x - other.x, this.y - other.y);
    }

    /**
     * Assumes {@code this} is used as a size determining value,
     * returns {@code true} if {@code p} is within that area, {@code false}
     * if not.
     *
     * @param p
     * @return
     */
    public boolean contains(Point p) {
        if (p.x < 0 || p.y < 0) {
            return false;
        }
        else if (p.x >= this.x || p.y >= this.y) {
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public boolean equals(Object otherObj) {
        if (otherObj instanceof Point) {
            Point other = (Point) otherObj;
            return other.x == this.x && other.y == this.y;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (5132 << (this.x % 31)) * 5 << (this.y % 31) + this.x * this.y;
    }

    @Override
    public String toString() {
        return "Coordinate(" + this.x + ", " + this.y + ")";
    }

}
