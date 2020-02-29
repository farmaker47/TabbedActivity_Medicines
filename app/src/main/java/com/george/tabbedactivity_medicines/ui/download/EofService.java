package com.george.tabbedactivity_medicines.ui.download;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.george.tabbedactivity_medicines.ui.DetailsActivity;

public class EofService extends IntentService {

    public EofService() {
        super("EofService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String url = "";
        String name = "";
        if (intent.hasExtra(DetailsActivity.URL_EOF_SERVICE) && intent.hasExtra(DetailsActivity.NAME_EOF_SERVICE)) {
            url = intent.getStringExtra(DetailsActivity.URL_EOF_SERVICE);
            name = intent.getStringExtra(DetailsActivity.NAME_EOF_SERVICE);
            FileDownloader.downloadFromInternet(this, url, name);

        }
    }
}
