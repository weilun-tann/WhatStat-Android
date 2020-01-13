package com.jed.whatsapp;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Metrics {

    // ATTRIBUTES
    private static final String TAG = "Metrics";
    private static boolean initialized = false;
    private static int senderOneTotalMessages = 0;
    private static int senderTwoTotalMessages = 0;
    private static int senderOneTotalWords = 0;
    private static int senderTwoTotalWords = 0;
    private static int senderOneTotalMedia = 0;
    private static int senderTwoTotalMedia = 0;
    private static int senderOneTotalReplyTimingInMinutes = 0;
    private static int senderTwoTotalReplyTimingInMinutes = 0;
    private static float senderOneAverageReplyTimingInHours = 0;
    private static float senderTwoAverageReplyTimingInHours = 0;
    private static List<Float> senderOneReplyTimeInMinutes = new ArrayList<>();
    private static List<Date> senderOneTimeStamp = new ArrayList<>();
    private static List<Float> senderTwoReplyTimeInMinutes = new ArrayList<>();
    private static List<Date> senderTwoTimeStamp = new ArrayList<>();
    private static List<String> uniqueSenderList = new ArrayList<>();

    // ACCESSOR/MUTATOR METHODS
    public static boolean isInitialized() {
        return initialized;
    }
    public static List<Date> getSenderOneTimeStamp() {
        return senderOneTimeStamp;
    }
    public static List<Float> getSenderOneReplyTimeInMinutes() { return senderOneReplyTimeInMinutes; }
    public static List<Date> getSenderTwoTimeStamp() {
        return senderTwoTimeStamp;
    }
    public static List<Float> getSenderTwoReplyTimeInMinutes() { return senderTwoReplyTimeInMinutes; }
    public static List<String> getSenderList() {
        return uniqueSenderList;
    }
    public static float getSenderOneAverageReplyTimingInHours() { return senderOneAverageReplyTimingInHours; }
    public static float getSenderTwoAverageReplyTimingInHours() { return senderTwoAverageReplyTimingInHours; }
    public static int getSenderOneTotalMessages() {
        return senderOneTotalMessages;
    }
    public static int getSenderTwoTotalMessages() {
        return senderTwoTotalMessages;
    }
    public static int getSenderOneTotalWords() {
        return senderOneTotalWords;
    }
    public static int getSenderTwoTotalWords() {
        return senderTwoTotalWords;
    }
    public static int getSenderOneTotalMedia() { return senderOneTotalMedia; }
    public static int getSenderTwoTotalMedia() { return senderTwoTotalMedia; }

    public static void setInitialized(boolean initialized) { Metrics.initialized = initialized; }

    // LOGIC METHODS
    public static void senderListToSet() {
        for (String sender : FileProcessing.getSender()) {
            if (uniqueSenderList.size() == 2) break;
            else if (!uniqueSenderList.contains(sender)) uniqueSenderList.add(sender);
        }
    }

    public static void analyzeReplyTimings() {
        // EDGE CASE : FOR < 2 MESSAGES SENT
        if (FileProcessing.getSender().size() < 2) {
            return;
        }
        Metrics.reset();
        Metrics.senderListToSet();
        Metrics.calculateMetrics();
        Metrics.setInitialized(true);
    }

    public static void debugReplyTiming() {
        Log.d(TAG, "senderOneTimeStamp.size() : " + senderOneTimeStamp.size());
        Log.d(TAG, "senderOneTotalMessages : " + senderOneTotalMessages);
        Log.d(TAG, "senderOneTotalWords : " + senderOneTotalWords);
        Log.d(TAG, "senderOneTotalMedia : " + senderOneTotalMedia);
        Log.d(TAG, "senderOneAverageReplyTimingInHours : " + senderOneAverageReplyTimingInHours);
        Log.d(TAG, "senderOneReplyTimeInMinutes.size() : " + senderOneReplyTimeInMinutes.size());

        Log.d(TAG, "\n");

        Log.d(TAG, "senderTwoTimeStamp.size() : " + senderTwoTimeStamp.size());
        Log.d(TAG, "senderTwoTotalWords : " + senderTwoTotalWords);
        Log.d(TAG, "senderTwoTotalMedia : " + senderTwoTotalMedia);
        Log.d(TAG, "senderTwoAverageReplyTimingInHours : " + senderTwoAverageReplyTimingInHours);
        Log.d(TAG, "senderTwoReplyTimeInMinutes.size() : " + senderTwoReplyTimeInMinutes.size());

        Log.d(TAG, "\n");

        Log.d(TAG, "uniqueSenderList : " + uniqueSenderList.toString());
    }

    public static void reset() {
        initialized = false;
        senderOneTimeStamp.clear();
        senderOneReplyTimeInMinutes.clear();
        senderTwoTimeStamp.clear();
        senderTwoReplyTimeInMinutes.clear();
        uniqueSenderList.clear();
        senderOneAverageReplyTimingInHours = 0;
        senderTwoAverageReplyTimingInHours = 0;
        senderOneTotalMessages = 0;
        senderTwoTotalMessages = 0;
        senderOneTotalWords = 0;
        senderTwoTotalWords = 0;
        senderOneTotalMedia = 0;
        senderTwoTotalMedia = 0;
    }

    public static void calculateMetrics() {

        int i = 0;
        Message startMsg = FileProcessing.getConversationHistory().get(0);
        final int MIN_REPLY_TIME_DAYS = 0;
        final int MAX_REPLY_TIME_DAYS = 7;
        final String MEDIA_TEXT = "<Media omitted>";

        for (Message m : FileProcessing.getConversationHistory()) {
            
            // senderOne
            if (m.getSender().equals(getSenderList().get(0))) {

                // Total Texts
                senderOneTotalMessages++;

                // Total Words
                senderOneTotalWords += m.getMessageText().split(" ").length;

                // Total Media
                if (m.getMessageText().equals(MEDIA_TEXT)) {
                    senderOneTotalMedia++;
                }
            } 
            
            // senderTwo
            else {
                // Total Texts
                senderTwoTotalMessages++;

                // Total Words
                senderTwoTotalWords += m.getMessageText().split(" ").length;

                // Total Media
                if (m.getMessageText().equals(MEDIA_TEXT)) {
                    senderTwoTotalMedia++;
                }
            }

            // Reply Timings Array
            if (i > 0) {

                // Analyze reply timings iff DIFFERENT senders
                if (!m.getSender().equals(startMsg.getSender())) {

                    // Calculate the reply timing
                    long earlierDate = startMsg.getMessageDate().getTime();
                    long laterDate = m.getMessageDate().getTime();
                    float replyTimingInMilliseconds = laterDate - earlierDate;
                    float replyTimingInMinutes = replyTimingInMilliseconds / (60 * 1000);
                    float replyTimingInDays = replyTimingInMilliseconds / (24 * 60 * 60 * 1000);

                    // Proceed iff replyTimingInDays within acceptable range
                    if (replyTimingInDays >= MIN_REPLY_TIME_DAYS && replyTimingInDays <= MAX_REPLY_TIME_DAYS) {

                        // Add to senderOne's RT
                        if (m.getSender().equals(getSenderList().get(0))) {
                            senderOneReplyTimeInMinutes.add(replyTimingInMinutes);
                            senderOneTotalReplyTimingInMinutes += replyTimingInMinutes;
                            senderOneTimeStamp.add(startMsg.getMessageDate());
                        }

                        // Add to senderTwo's RT
                        else {
                            senderTwoReplyTimeInMinutes.add(replyTimingInMinutes);
                            senderTwoTotalReplyTimingInMinutes += replyTimingInMinutes;
                            senderTwoTimeStamp.add(startMsg.getMessageDate());
                        }
                    }

                    // Advance startMsg
                    startMsg = m;
                }
            }

            // Average Reply Timings (performed when we've reached the end of our chat)
            if (i == FileProcessing.getConversationHistory().size() - 1) {
                senderOneAverageReplyTimingInHours =
                        senderOneTotalReplyTimingInMinutes / (senderTwoReplyTimeInMinutes.size() * 60f);
                senderTwoAverageReplyTimingInHours =
                        senderTwoTotalReplyTimingInMinutes / (senderTwoReplyTimeInMinutes.size() * 60f);
            }

            // Advance index pointer
            i++;
        }
    }
}
