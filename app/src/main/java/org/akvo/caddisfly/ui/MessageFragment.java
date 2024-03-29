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

package org.akvo.caddisfly.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import org.akvo.caddisfly.R;

public class MessageFragment extends DialogFragment {

    public static MessageFragment newInstance(String title, String message, int dilutionLevel) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        args.putInt("dilution", dilutionLevel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //getDialog().setTitle(title);

        final View view = inflater.inflate(R.layout.fragment_message, container, false);

        TextView titleView = (TextView) view.findViewById(R.id.title);
        final String title = getArguments().getString("title");
        titleView.setText(title);

        TextView messageTextView1 = (TextView) view.findViewById(R.id.messageTextView1);
        messageTextView1.setText(String.format(getString(R.string.highLevelsFound), title));

        TextView messageTextView2 = (TextView) view.findViewById(R.id.messageTextView2);
        final String message = getArguments().getString("message");
        messageTextView2.setText(message);

//        TextView dilutionTextView = (TextView) view.findViewById(R.id.dilutionTextView);
//        int dilutionLevel = getArguments().getInt("dilution", -1);
//        switch (dilutionLevel) {
//            case 0:
//                dilutionTextView.setVisibility(View.VISIBLE);
//                dilutionTextView.setText(R.string.hundredPercentSampleWater);
//                break;
//            case 1:
//                dilutionTextView.setVisibility(View.VISIBLE);
//                dilutionTextView.setText(R.string.fiftyPercentSampleWater);
//                break;
//            case 2:
//                dilutionTextView.setVisibility(View.VISIBLE);
//                dilutionTextView.setText(R.string.twentyFivePercentSampleWater);
//                break;
//        }

        Button button = (Button) view.findViewById(R.id.endSurveyButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageDialogListener listener = (MessageDialogListener) getActivity();
                dismiss();
                listener.onFinishDialog();
            }
        });

        return view;
    }

    public interface MessageDialogListener {
        void onFinishDialog();
    }
}
