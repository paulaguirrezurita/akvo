/*
 *  Copyright (C) Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo Caddisfly
 *
 *  Akvo Caddisfly is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo Caddisfly is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.akvo.caddisfly.model;

public class ResultRange {
    private final double mValue;
    private int mColor;
    //private final double mMultiplier;

    public ResultRange(double value, int color) {
        mValue = value;
        mColor = color;
        //mMultiplier = 100 / (100 - dilution);
    }

    public double getValue() {
        return mValue;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int value) {
        mColor = value;
    }

    //public double getMultiplier() {
    //return mMultiplier;
    //}

}
