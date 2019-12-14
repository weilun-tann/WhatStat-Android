package com.jed.whatsapp;

import java.io.File;
import java.lang.String;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.*;

public class FileProcessing {
    // ATTRIBUTES
    private static File uploadedFile;
    private static String fileContents = "";
    private static List<String> fileContentLines = new ArrayList<String>();
    private static List<Date> messageTimeStamp = new ArrayList<Date>();
    private static List<String> sender = new ArrayList<String>();
    private static List<String> messageBody = new ArrayList<String>();
    private static List<Message> conversationHistory = new ArrayList<Message>();

    // ACCESSOR METHODS
    public static String getFileContents() {
        return fileContents;
    }

    public static List<String> getFileContentLines() {
        return fileContentLines;
    }

    public static List<Date> getMessageTimeStamp() {
        return messageTimeStamp;
    }

    public static List<String> getSender() {
        return sender;
    }

    public static List<String> getMessageBody() {
        return messageBody;
    }

    public static List<Message> getConversationHistory() {
        return conversationHistory;
    }

    // LOGIC METHODS
    public static void setUploadedFile(File f) {
        uploadedFile = f;
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
        if (mySender != null) {
            sender.add(mySender);
        }
    }

    static void retrieveConversationHistory() {
        if (fileContents.equals("")) {
            return;
        }

        // EXTRACT THE 3 ELEMENTS
        String[] fileContentLines = extractLines(fileContents);
        for (String line : fileContentLines) {
//        forLoopCounter++;
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
//          noNullDate++;
            } else {
//          nullDate++;
            }
        }

        // DOUBLE CONFIRM OUR ELEMENTS
//      System.out.println("DOUBLE CONFIRM");
//      System.out.println("sender.toSet() : ${sender.toSet()}");
//      System.out.println(messageTimeStamp);
//      System.out.println(messageBody);
//
        System.out.println("s1 : ${sender.length}");
        System.out.println("s2 : ${messageTimeStamp.length}");
        System.out.println("s3 : ${messageBody.length}");
        System.out.println("s4 : ${conversationHistory.length}");
    }


    static Date retrieveDate(String line) {
        try {
            if (line.substring(18, 19) == "m") {
                return parseDateString(line.substring(0, 19));
            } else if (line.substring(19, 20) == "m") {
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
            String senderName = m.group(1);
            return senderName.substring(4, senderName.length() - 1);
        } catch (Exception e) {
            return null;
        }
    }

    static String retrieveMessageBody(String line) {
        Pattern p = Pattern.compile(".*:.*?: ");
        Matcher m = p.matcher(line);

        try {
            String removedBody = m.group(1);
            String messageBody = line.substring(removedBody.length(), line.length());
            return messageBody;
        } catch (Exception e) {
            return null;
        }
    }

    static String[] extractLines(String fileContents) {
        try {
            String[] fileContentLines = fileContents.split("\n");
            return fileContentLines;
        } catch (Exception e) {
            System.out.println("Empty fileContents");
            return null;
        }
    }

    static Date parseDateString(String dt) {
        try {
            dt = dt.replaceFirst(" pm", " PM").replaceFirst(" am", " AM");
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy, HH:mm a", Locale.US);
            return format.parse(dt);
        } catch (Exception e) {
            System.out.println("DateParse failed on : " + dt);
            return null;
        }
    }
}
