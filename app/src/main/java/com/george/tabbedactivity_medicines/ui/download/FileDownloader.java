package com.george.tabbedactivity_medicines.ui.download;

import android.content.Context;
import android.util.Log;

import com.george.tabbedactivity_medicines.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FileDownloader {

    private static final String TAG = "FileDownloader";

    private static final int MEGABYTE = 1024 * 1024;

    public static void downloadFile(String fileUrl, File directory) {
        try {
            Log.v(TAG, "downloadFile() invoked ");
            Log.v(TAG, "downloadFile() fileUrl " + fileUrl);
            Log.v(TAG, "downloadFile() directory " + directory);

            URL url = new URL(fileUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();

            //OVERWRITE with false parameter
            FileOutputStream fileOutputStream = new FileOutputStream(directory,false);
            int totalSize = urlConnection.getContentLength();

            byte[] buffer = new byte[MEGABYTE];
            int bufferLength = 0;
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, bufferLength);
            }
            fileOutputStream.close();
            Log.v(TAG, "downloadFile() completed ");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "downloadFile() error" + e.getMessage());
            Log.e(TAG, "downloadFile() error" + e.getStackTrace());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG, "downloadFile() error" + e.getMessage());
            Log.e(TAG, "downloadFile() error" + e.getStackTrace());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "downloadFile() error" + e.getMessage());
            Log.e(TAG, "downloadFile() error" + e.getStackTrace());
        }
    }

    public static void downloadFromInternet(Context context, String urlToUse, String namePdf) {

        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;

        //the path where DB already exists
        String path = "/data/data/com.george.tabbedactivity_medicines/databases/";

        File dir = new File(path);
        if (!dir.exists())
            dir.mkdirs();

        try {
            URL url = new URL(urlToUse);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            Log.e(context.getString(R.string.server_returned_http) + connection.getResponseCode()
                    + " " + connection.getResponseMessage(), context.getString(R.string.server));
            Log.e("NAME_PDF",namePdf);
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e(context.getString(R.string.server_returned_http) + connection.getResponseCode()
                        + " " + connection.getResponseMessage(), context.getString(R.string.server));
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();

            File fToPut = new File(dir, namePdf);

            /// set append to false if you want to overwrite
            output = new FileOutputStream(fToPut, false);

            byte data[] = new byte[4096];
            long total = 0;
            int count;

            //validating data if it is null
            if (input == null) {
                Log.e("DATA_NULL", "NULL");
            }

            while ((count = input.read(data)) != -1) {
                total += count;
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            e.toString();
            //Logging if data failed to come
            Log.e("FAILED", "DOWNLOAD_FAILED");
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }

    }
}
