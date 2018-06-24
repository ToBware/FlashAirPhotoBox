package com.example.tblume.flashairphotobox;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.renderscript.ScriptGroup;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class FlashAirRequest {

    final static String ROOT_DIRECTORY = "DCIM";

    static public List<String> getLatestFileNames(int nElements) {
        List<String> fileNames = new ArrayList<String>();
        try {
            Vector<FlashAirFile> fileList = getFileList(FlashAirRequest.ROOT_DIRECTORY);
            if (!fileList.isEmpty()) {
                Collections.sort(fileList);
                FlashAirFile latestFile = fileList.lastElement();
                while (latestFile.isDirectory()) {

                    String currDir = latestFile.Directory + "/" + latestFile.FileName;
                    fileList = getFileList(currDir);
                    if (!fileList.isEmpty()) {
                        Collections.sort(fileList);
                        latestFile = fileList.lastElement();
                    } else {
                        break;
                    }
                }

                ArrayList<String> allImageFiles = new ArrayList<>();
                for (FlashAirFile file : fileList) {
                    if (file.isImageFile()) {
                        allImageFiles.add(file.Directory + "/" + file.FileName);
                    }
                }
                fileNames = allImageFiles.subList(
                        Math.max(allImageFiles.size() - nElements, 0),
                        allImageFiles.size());
            }
        } catch (Exception e) {

        } finally {

        }

        return fileNames;
    }

    static public Bitmap getLatestBitmap() {
        Bitmap resultBitmap = null;
        try {
            Vector<FlashAirFile> fileList = getFileList(FlashAirRequest.ROOT_DIRECTORY);
            if (fileList.size() != 0) {

                Collections.sort(fileList);
                FlashAirFile latestFile = fileList.lastElement();
                while (latestFile.isDirectory()) {
                    String currDir = latestFile.Directory + "/" + latestFile.FileName;
                    Vector<FlashAirFile> currFileList = getFileList(currDir);
                    if (!currFileList.isEmpty()) {

                        Collections.sort(currFileList);
                        latestFile = currFileList.lastElement();
                    }

                }

                if (latestFile.isImageFile()) {
                    resultBitmap = FlashAirRequest.getBitmap(latestFile.Directory + "/" + latestFile.FileName);
                }
            }

        } catch (Exception e) {

        } finally {

        }
        return resultBitmap;
    }

    static public Vector<FlashAirFile> getFileList(String directory) {

        Vector<FlashAirFile> fileList = new Vector<>();

        try {

            ArrayList<NameValuePair> httpParams = new ArrayList<>();
            httpParams.add(new BasicNameValuePair("DIR", directory));

            String resultSring = getString("http://flashair/command.cgi?op=100&" + URLEncodedUtils.format(httpParams, "UTF-8"));
            String[] lines = resultSring.split("\\r?\\n");

            for (int iLine = 1; iLine < lines.length; iLine = iLine + 1) {
                fileList.add(FlashAirFile.fromString(lines[iLine]));
            }
        } catch (Exception e) {

        } finally {
        }

        return fileList;
    }

    static public String getString(String command) {
        String result = "";
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(command);
            urlConnection = (HttpURLConnection)url.openConnection();
            //urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuffer strBuf = new StringBuffer();
            String str;
            while ((str = bufReader.readLine()) != null) {
                if (strBuf.toString() != "")
                    strBuf.append("\n");
                strBuf.append(str);
            }
            result = strBuf.toString();
        } catch (Exception e) {
            Log.e("ERROR", "ERROR: " + e.toString());
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result;
    }

    static public Bitmap getBitmap(String filePath) {
        Bitmap resultBitmap = null;
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL("http://flashair/" + filePath);
            urlConnection = (HttpURLConnection)url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] byteChunk = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = inputStream.read(byteChunk)) != -1) {
                byteArrayOutputStream.write(byteChunk, 0, bytesRead);
            }
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            BitmapFactory.Options bfOptions = new BitmapFactory.Options();
            bfOptions.inPurgeable = true;
            resultBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, bfOptions);
            byteArrayOutputStream.close();
            inputStream.close();
        } catch (Exception e) {
            Log.e("ERROR", "ERROR" + e.toString());
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return resultBitmap;
    }

    static public boolean getUpdateStatus() {

        boolean updateStatus = false;

        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL("http://flashair/command.cgi?op=102");
            urlConnection = (HttpURLConnection)url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String str = bufReader.readLine();
            if (str != null && str.equals("1")) {
                updateStatus = true;
            }
        } catch (Exception e) {
            Log.e("ERROR", "ERROR" + e.toString());
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return updateStatus;
    }
}
