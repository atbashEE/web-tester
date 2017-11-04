/*
 * Copyright 2017 Rudy De Busscher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.atbash.ee.test.ui.util;

/**
 * A point representing a location in {@code (x,y)} coordinate space,
 * specified in integer precision.
 */
public class Point {

    /**
     * The top coordinate of this <code>Point</code>.
     * If no top coordinate is set it will default to 0.
     */
    private int top;

    /**
     * The left coordinate of this <code>Point</code>.
     * If no left coordinate is set it will default to 0.
     */
    private int left;

    /**
     * Constructs and initializes a point at the origin
     * (0,&nbsp;0) of the coordinate space.
     */
    public Point() {
    }

    /**
     * Constructs and initializes a point at the specified
     * {@code (x,y)} location in the coordinate space.
     *
     * @param top  the top coordinate of the newly constructed <code>Point</code>
     * @param left the left coordinate of the newly constructed <code>Point</code>
     */
    public Point(int top, int left) {
        this.top = top;
        this.left = left;
    }

    /**
     * Returns the top coordinate of this <code>Point</code> in
     * <code>int</code> precision.
     *
     * @return the top coordinate of this <code>Point</code>.
     */
    public int getTop() {
        return top;
    }

    /**
     * Sets the top coordinate of this <code>Point</code>.
     *
     * @param top the top coordinate of the new location
     */
    public void setTop(int top) {
        this.top = top;
    }

    /**
     * Returns the left coordinate of this <code>Point</code> in
     * <code>int</code> precision.
     *
     * @return the left coordinate of this <code>Point</code>.
     */
    public int getLeft() {
        return left;
    }

    /**
     * Sets the left coordinate of this <code>Point</code>.
     *
     * @param left the left coordinate of the new location
     */
    public void setLeft(int left) {
        this.left = left;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + left;
        result = prime * result + top;
        return result;
    }

    /**
     * Determines whether or not two points are equal. Two instances of
     * <code>Point</code> are equal if the values of their
     * <code>left</code> and <code>top</code> member fields, representing
     * their position in the coordinate space, are the same.
     *
     * @param obj an object to be compared with this <code>Point</code>
     * @return <code>true</code> if the object to be compared is
     * an instance of <code>Point</code> and has
     * the same values; <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Point other = (Point) obj;
        if (left != other.left) {
            return false;
        }
        if (top != other.top) {
            return false;
        }
        return true;
    }


    /**
     * Returns a string representation of this point and its location
     * in the {@code (x,y)} coordinate space. This method is
     * intended to be used only for debugging purposes, and the content
     * and format of the returned string may vary between implementations.
     * The returned string may be empty but may not be <code>null</code>.
     *
     * @return a string representation of this point
     */
    @Override
    public String toString() {
        return "Point [top=" + top + ", left=" + left + "]";
    }
}
