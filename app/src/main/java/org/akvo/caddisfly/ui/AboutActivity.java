/*
 * Copyright (C) Stichting Akvo (Akvo Foundation)
 *
 * This file is part of Akvo Caddisfly
 *
 * Akvo Caddisfly is free software: you can redistribute it and modify it under the terms of
 * the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 * either version 3 of the License or any later version.
 *
 * Akvo Caddisfly is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License included below for more details.
 *
 * The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.akvo.caddisfly.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.akvo.caddisfly.Config;
import org.akvo.caddisfly.R;
import org.akvo.caddisfly.app.MainApp;

public class AboutActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView productView = (TextView) findViewById(R.id.textVersion);
        productView.setText(MainApp.getVersion(this));
        ImageView organizationView = (ImageView) findViewById(R.id.organizationImage);

        productView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openWebBrowser(Config.PRODUCT_WEBSITE);
            }
        });

        organizationView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openWebBrowser(Config.ORG_WEBSITE);
            }
        });

    }

    /**
     * Open a web Browser and navigate to given url
     *
     * @param url The url to navigate to
     */
    private void openWebBrowser(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

}