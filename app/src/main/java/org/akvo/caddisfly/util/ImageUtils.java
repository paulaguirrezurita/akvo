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

package org.akvo.caddisfly.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;

public class ImageUtils {

    private ImageUtils() {
    }

    private static Bitmap getRoundedShape(Bitmap scaleBitmapImage, int diameter) {

        Bitmap targetBitmap = Bitmap.createBitmap(diameter,
                diameter, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) diameter - 1) / 2,
                ((float) diameter - 1) / 2,
                (((float) diameter) / 2),
                Path.Direction.CCW
        );

        canvas.clipPath(path);
        targetBitmap.setHasAlpha(true);
        canvas.drawBitmap(scaleBitmapImage,
                new Rect(0, 0, scaleBitmapImage.getWidth(), scaleBitmapImage.getHeight()),
                new Rect(0, 0, diameter, diameter), null
        );
        return targetBitmap;
    }

    public static Bitmap getBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap, int sampleLength) {

        int[] pixels = new int[sampleLength * sampleLength];

        bitmap.getPixels(pixels, 0, sampleLength,
                (bitmap.getWidth() - sampleLength) / 2,
                (bitmap.getHeight() - sampleLength) / 2,
                sampleLength,
                sampleLength);
        bitmap = Bitmap.createBitmap(pixels, 0, sampleLength,
                sampleLength,
                sampleLength,
                Bitmap.Config.ARGB_8888);
        bitmap = ImageUtils.getRoundedShape(bitmap, sampleLength);
        bitmap.setHasAlpha(true);
        return bitmap;
    }
}
