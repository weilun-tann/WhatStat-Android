package com.jed.whatsapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.String;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.*;

public class FileProcessing {
    // ATTRIBUTES
    private static boolean initialized = false;
    private static File uploadedFile = null;
    private static Intent userIntent = null;
    private static String fileContents = "";
    private static List<String> fileContentLines = new ArrayList<String>();
    private static List<Date> messageTimeStamp = new ArrayList<Date>();
    private static List<String> sender = new ArrayList<String>();
    private static List<String> messageBody = new ArrayList<String>();
    private static List<Message> conversationHistory = new ArrayList<Message>();

    // ACCESSOR METHODS

    public static boolean isInitialized() { return initialized; }

    public static File getUploadedFile() {
        return uploadedFile;
    }

    public static Intent getUserIntent() { return userIntent; }

    public static String getFileContents() {
        return fileContents;
    }

    public static List<String> getFileContentLines() { return fileContentLines; }

    public static List<Date> getMessageTimeStamp() { return messageTimeStamp; }

    public static List<String> getSender() { return sender; }

    public static List<String> getMessageBody() { return messageBody; }

    public static List<Message> getConversationHistory() { return conversationHistory; }

    // LOGIC METHODS
    public static void setInitialized(boolean init) { initialized = init; }

    public static void setUploadedFile(File f) { uploadedFile = f; }

    public static void setUserIntent(Intent userIntent) {
        FileProcessing.userIntent = userIntent;
    }

    public static void readFile(Uri fileURI, Context fileContext) throws IOException {

        // WIPE INTERNAL STATE BEFORE FILE READ
        wipeInternalState();

        // PERFORM FILE READ
//        StringBuilder text = new StringBuilder();
        InputStream fileIS = fileContext.getContentResolver().openInputStream(fileURI);
        BufferedReader br =
                new BufferedReader(new InputStreamReader(Objects.requireNonNull(fileIS)));
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
//        fileContents = text.toString();
//        fileContentLines = extractLines(fileContents);
        br.close();
        // Set intialized to true
        FileProcessing.setInitialized(true);
    }

    static void wipeInternalState() {
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

//    static void retrieveConversationHistory() {
//
//        if (fileContentLines.size() == 0) { return; }
//
//        // EXTRACT THE 3 ELEMENTS
//        for (String line : fileContentLines) {
//            Date lineDate = retrieveDate(line);
//            String lineSender = retrieveSender(line);
//            String lineMessageBody = retrieveMessageBody(line);
//            if ((lineDate != null) &
//                    (lineSender != null) &
//                    (lineMessageBody != null)) {
//                addMessageTimeStamp(lineDate);
//                addSender(lineSender);
//                addMessageBody(lineMessageBody);
//                conversationHistory
//                        .add(new Message(lineDate, lineSender, lineMessageBody));
//            }
//        }
//
//        // @DEBUG
//        System.out.println("sender.size() : " + sender.size());
//        System.out.println("messageTimeStamp.size() : " + messageTimeStamp.size());
//        System.out.println("messageBody.size() : " + messageBody.size());
//        System.out.println("conversationHistory.size() : " + conversationHistory.size());
//    }


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