package com.jed.whatsapp;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileProcessing {

    // ATTRIBUTES
    private static String TAG = "FileProcessing";
    private static boolean initialized = false;
    private static String fileName = null;
    private static Uri uploadedFileURI = null;
    private static List<Date> messageTimeStamp = new ArrayList<Date>();
    private static List<String> sender = new ArrayList<String>();
    private static List<String> messageBody = new ArrayList<String>();
    private static List<Message> conversationHistory = new ArrayList<Message>();

    // ACCESSOR METHODS
    public static boolean isInitialized() {
        return FileProcessing.initialized;
    }

    public static String getFileName() {
        return FileProcessing.fileName;
    }

    public static Uri getUploadedFileURI() {
        return FileProcessing.uploadedFileURI;
    }

    public static List<Date> getMessageTimeStamp() {
        return FileProcessing.messageTimeStamp;
    }

    public static List<String> getSender() {
        return FileProcessing.sender;
    }

    public static List<String> getMessageBody() {
        return FileProcessing.messageBody;
    }

    public static List<Message> getConversationHistory() {
        return conversationHistory;
    }

    // LOGIC METHODS
    public static void setInitialized(boolean init) {
        FileProcessing.initialized = init;
    }

    public static void setUploadedFileURI(Uri u) {
        FileProcessing.uploadedFileURI = u;
    }

    public static void setFileName(String fileName) {
        FileProcessing.fileName = fileName;
    }

    public static void readFile(Uri fileURI, Context fileContext, boolean fromCloud) throws IOException {

        // WIPE INTERNAL STATE BEFORE FILE READ
        FileProcessing.reset();

        // SET FILE NAME
        FileProcessing.fileName = getFileName(fileContext, fileURI);

        // PERFORM FILE READ (LOCAL VS CLOUD STORAGE)
        BufferedReader br;
        if (!fromCloud) {
            // LOCAL READ
            ContentResolver fileCS = fileContext.getContentResolver();
            InputStream fileIS = fileCS.openInputStream(fileURI);
            br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(fileIS)));
        } else {
            // CLOUD READ
            URL url = new URL(fileURI.toString());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(30000);
            br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        }

        // PERFORM FILE PROCESSING INTO MESSAGE OBJECTS
        String line;
        int totalMsg = 0;
        int processedMsg = 0;

        while ((line = br.readLine()) != null) {
            Date lineDate = retrieveDate(line);
            String lineSender = retrieveSender(line);
            String lineMessageBody = retrieveMessageBody(line);
            if ((lineDate != null) &
                    (lineSender != null) &
                    (lineMessageBody != null)) {
                addMessageTimeStamp(lineDate);
                addSender(lineSender);
                addMessageBody(lineMessageBody);
                conversationHistory
                        .add(new Message(lineDate, lineSender, lineMessageBody));
                processedMsg++;
            }
            totalMsg++;
        }
        Log.d(TAG, "Messages Processed : " + processedMsg + " / " + totalMsg);
        br.close();
        FileProcessing.setInitialized(true);
    }

    static void reset() {
        FileProcessing.initialized = false;
        FileProcessing.fileName = null;
        FileProcessing.messageTimeStamp.clear();
        FileProcessing.sender.clear();
        FileProcessing.messageBody.clear();
    }

    private static void addMessageTimeStamp(Date myTimeStamp) {
        if (myTimeStamp != null) {
            messageTimeStamp.add(myTimeStamp);
        }
    }

    private static void addMessageBody(String myMessageBody) {
        if (myMessageBody != null) {
            messageBody.add(myMessageBody);
        }
    }

    private static void addSender(String mySender) {
        if (mySender != null && mySender != "") {
            sender.add(mySender);
        }
    }

    private static Date retrieveDate(String line) {
        try {
            // AM/PM TIME STAMP
            Pattern p = Pattern.compile(".* -");
            Matcher m = p.matcher(line);
            if (m.find()) {
                String timeStamp = m.group();
                timeStamp = timeStamp.substring(0, timeStamp.length() - 2);
                return parseDateString(timeStamp);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    private static Date parseDateString(String dt) {
        try {
            // AM/PM TIMESTAMP
            dt = dt.replaceFirst(" pm", " PM").replaceFirst(" am", " AM");
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy, HH:mm a", Locale.US);
            return format.parse(dt);
        } catch (Exception e) {
            // 24H TIMESTAMP
            try {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.US);
                return format.parse(dt);
            } catch (Exception f) {
                return null;
            }
        }
    }

    private static String retrieveSender(String line) {
        Pattern p = Pattern.compile(" - .*?:");
        Matcher m = p.matcher(line);
        try {
            String senderName = null;
            if (m.find()) {
                senderName = m.group();
                return senderName.substring(3, senderName.length() - 1);
            } else {
                return null;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String retrieveMessageBody(String line) {
        Pattern p = Pattern.compile(".* - .*?: ");
        Matcher m = p.matcher(line);
        if (m.find()) {
            String removedBody = m.group();
            String messageBody = line.substring(removedBody.length());
            return messageBody;
        } else {
            return null;
        }
    }


    /**
     * Utility function to retrieve file name from URI
     *
     * @param uri : URI of the file to extract name from
     * @return fileName of the file represented by its URI
     */
    public static String getFileName(Context applicationContext, Uri uri) {
        ContentResolver cr = applicationContext.getContentResolver();
        String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
        Cursor metaCursor = cr.query(uri, projection, null, null, null);
        String fileName = "Unnamed File";
        if (metaCursor != null) {
            try {
                if (metaCursor.moveToFirst()) {
                    fileName = metaCursor.getString(0);
                }
            } finally {
                metaCursor.close();
            }
        }
        return fileName.substring(0, fileName.length() - 4);
    }
}