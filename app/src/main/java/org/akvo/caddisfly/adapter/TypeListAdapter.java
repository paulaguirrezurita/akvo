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

package org.akvo.caddisfly.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.akvo.caddisfly.R;
import org.akvo.caddisfly.model.TestInfo;

public class TypeListAdapter extends ArrayAdapter<TestInfo> {

    private final Activity mActivity;

    private final TestInfo[] mTestInfoArray;

    public TypeListAdapter(Activity activity, TestInfo[] testInfoArray) {
        super(activity, R.layout.row_calibrate, testInfoArray);
        mActivity = activity;
        mTestInfoArray = testInfoArray;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = mActivity.getLayoutInflater();
        @SuppressLint("ViewHolder") View rowView = inflater.inflate(R.layout.row_type, parent, false);

        TestInfo testInfo = mTestInfoArray[position];

        //todo: review this code
        if (testInfo.getCode().equals("TEMPE")) {
            rowView.setVisibility(View.GONE);
        } else {
            TextView ppmText = (TextView) rowView.findViewById(R.id.ppmText);
            ppmText.setText(testInfo.getName(mActivity.getResources().getConfiguration().locale.getLanguage()));
        }
        return rowView;
    }
}
