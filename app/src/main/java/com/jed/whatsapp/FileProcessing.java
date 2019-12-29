package com.jed.whatsapp;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

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
    public static boolean isInitialized = false;
//    private static File uploadedFile = null;
    private static Uri uploadedFileURI = null;
//    private static Intent userIntent = null;

    private static String fileContents = "";
    private static List<String> fileContentLines = new ArrayList<String>();
    private static List<Date> messageTimeStamp = new ArrayList<Date>();
    private static List<String> sender = new ArrayList<String>();
    private static List<String> messageBody = new ArrayList<String>();
    private static List<Message> conversationHistory = new ArrayList<Message>();

    // ACCESSOR METHODS


//    public static File getUploadedFile() {
//        return uploadedFile;
//    }

    public static Uri getUploadedFileURI() { return uploadedFileURI; }

//    public static Intent getUserIntent() { return userIntent; }

    public static String getFileContents() {
        return fileContents;
    }

    public static List<String> getFileContentLines() { return fileContentLines; }

    public static List<Date> getMessageTimeStamp() { return messageTimeStamp; }

    public static List<String> getSender() { return sender; }

    public static List<String> getMessageBody() { return messageBody; }

    public static List<Message> getConversationHistory() { return conversationHistory; }

    // LOGIC METHODS
    public static void setIsInitialized(boolean init) { isInitialized = init; }

//    public static void setUploadedFile(File f) { uploadedFile = f; }

    public static void setUploadedFileURI(Uri u) { uploadedFileURI = u; }

//    public static void setUserIntent(Intent userIntent) {
//        FileProcessing.userIntent = userIntent;
//    }

    public static void readFile(Uri fileURI, Context fileContext, boolean fromCloud) throws IOException {

        // WIPE INTERNAL STATE BEFORE FILE READ
        reset();

        // PERFORM FILE READ (LOCAL VS CLOUD STORAGE)
        BufferedReader br;
        if (!fromCloud) {
            ContentResolver fileCS = fileContext.getContentResolver();
            InputStream fileIS = fileCS.openInputStream(fileURI);
            br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(fileIS)));
        } else {
            URL url = new URL(fileURI.toString());
            HttpURLConnection con =(HttpURLConnection) url.openConnection();
            con.setConnectTimeout(30000);
            br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        }

        String line;

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
            }
        }
        br.close();
        FileProcessing.setIsInitialized(true);
    }

    static void reset() {
        fileContents = "";
        fileContentLines.clear();
        messageTimeStamp.clear();
        sender.clear();
        messageBody.clear();
    }

    static void addMessageTimeStamp(Date myTimeStamp) {
        if (myTimeStamp != null) {
            messageTimeStamp.add(myTimeStamp);
        }
    }

    static void addMessageBody(String myMessageBody) {
        if (myMessageBody != null) {
            messageBody.add(myMessageBody);
        }
    }

    static void addSender(String mySender) {
        if (mySender != null && mySender != "") {
            sender.add(mySender);
        }
    }

    static Date retrieveDate(String line) {
        try {
            if (line.substring(18, 19).equals("m")) {
                return parseDateString(line.substring(0, 19));
            } else if (line.substring(19, 20).equals("m")) {
                return parseDateString(line.substring(0, 20));
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    static String retrieveSender(String line) {
        Pattern p = Pattern.compile("m - .*?:");
        Matcher m = p.matcher(line);
        try {
            String senderName = null;
            if (m.find()) {
                senderName = m.group();
                return senderName.substring(4, senderName.length() - 1);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    static String retrieveMessageBody(String line) {
        Pattern p = Pattern.compile(".*m - .*?: ");
        Matcher m = p.matcher(line);
        if (m.find()) {
            String removedBody = m.group();
            String messageBody = line.substring(removedBody.length(), line.length());
            return messageBody;
        } else {
            return null;
        }
    }

//    static List<String> extractLines(String fileContents) {
//        try {
//            return Arrays.asList(fileContents.split("\n"));
//        } catch (Exception e) {
//            return null;
//        }
//    }

    static Date parseDateString(String dt) {
        try {
            dt = dt.replaceFirst(" pm", " PM").replaceFirst(" am", " AM");
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy, HH:mm a", Locale.US);
            return format.parse(dt);
        } catch (Exception e) {
            return null;
        }
    }
}