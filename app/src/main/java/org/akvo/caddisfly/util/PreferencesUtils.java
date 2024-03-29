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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PreferencesUtils {

    private PreferencesUtils() {
    }

    /**
     * Gets a preference key from strings
     * @return the string key
     * @param context the context
     * @param keyId   the key id
     */
    private static String getKey(Context context, int keyId) {
        return context.getString(keyId);
    }

    /**
     * Gets a boolean value from preferences
     * @return the stored boolean value
     * @param context      the context
     * @param keyId        the key id
     * @param defaultValue the default value
     */
    @SuppressWarnings("SameParameterValue")
    public static boolean getBoolean(Context context, int keyId, boolean defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(getKey(context, keyId), defaultValue);
    }

    /**
     * Sets a boolean value from preferences
     *
     * @param context the context
     * @param keyId   the key id
     * @param value   the value
     */
    @SuppressWarnings("SameParameterValue")
    public static void setBoolean(Context context, int keyId, boolean value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(getKey(context, keyId), value);
        editor.apply();

    }

    /**
     * Gets an integer value from preferences
     * @return stored int value
     * @param context      the context
     * @param keyId        the key id
     * @param defaultValue the default value
     */
    @SuppressWarnings("SameParameterValue")
    public static int getInt(Context context, String keyId, int defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(keyId, defaultValue);
    }

    public static void setInt(Context context, String keyId, int value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = sharedPreferences.edit();
        editor.putInt(keyId, value);
        editor.apply();
    }

    @SuppressWarnings("SameParameterValue")
    public static long getLong(Context context, int keyId) {
        return PreferencesUtils.getLong(context, getKey(context, keyId));
    }

    /**
     * Gets a long value from preferences
     * @return the stored long value
     * @param context the context
     * @param keyId   the key id
     */
    private static long getLong(Context context, String keyId) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPreferences.getLong(keyId, -1L);
    }

    /**
     * Sets a long value from preferences
     *
     * @param context the context
     * @param keyId   the key id
     * @param value   the value
     */
    @SuppressWarnings("SameParameterValue")
    public static void setLong(Context context, int keyId, long value) {
        setLong(context, getKey(context, keyId), value);
    }

    private static void setLong(Context context, String keyId, long value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = sharedPreferences.edit();
        editor.putLong(keyId, value);
        editor.apply();
    }

    /**
     * Gets a string value from preferences
     * @return the stored string value
     * @param context      the context
     * @param keyId        the key id
     * @param defaultValue default value
     */
    @SuppressWarnings("SameParameterValue")
    public static String getString(Context context, int keyId, String defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPreferences.getString(getKey(context, keyId), defaultValue);
    }

    @SuppressWarnings("SameParameterValue")
    public static void removeKey(Context context, int keyId) {
        PreferencesUtils.removeKey(context, getKey(context, keyId));
    }

    private static void removeKey(Context context, String keyId) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = sharedPreferences.edit();
        editor.remove(keyId);
        editor.apply();
    }

}