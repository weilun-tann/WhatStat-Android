package com.jed.whatsapp;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class ReplyTiming {
    // ATTRIBUTES
    private static final String TAG = "ReplyTiming";
    private static boolean initialized = false;
    private static List<Date> senderOneTimeStamp = new ArrayList<>();
    private static List<Float> senderOneReplyTimeInMinutes = new ArrayList<>();
    private static List<Date> senderTwoTimeStamp = new ArrayList<>();
    private static List<Float> senderTwoReplyTimeInMinutes = new ArrayList<>();
    private static List<String> uniqueSenderList = new ArrayList<>();
    private static float senderOneAverageReplyTiming = 0;
    private static float senderTwoAverageReplyTiming = 0;
    private static int senderOneTotalMessages = 0;
    private static int senderTwoTotalMessages = 0;
    private static int senderOneTotalWords = 0;
    private static int senderTwoTotalWords = 0;

    // ACCESSOR/MUTATOR METHODS
    public static boolean isInitialized() { return initialized; }
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
    public static float getSenderOneAverageReplyTiming() {
        return senderOneAverageReplyTiming;
    }
    public static float getSenderTwoAverageReplyTiming() {
        return senderTwoAverageReplyTiming;
    }
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
    public static void setInitialized(boolean initialized) { ReplyTiming.initialized = initialized; }

    // LOGIC METHODS
    public static void changeSenderList() {
        uniqueSenderList = new ArrayList<>(new HashSet<>(FileProcessing.getSender()));
    }

    public static void analyzeReplyTimings() {
        // EDGE CASE : FOR < 2 MESSAGES SENT
        if (FileProcessing.getSender().size() < 2) { return; }
        ReplyTiming.reset();
        ReplyTiming.changeSenderList();
        ReplyTiming.processReplyTimings();
        ReplyTiming.countMessages();
        for (float replyTime : senderOneReplyTimeInMinutes) {
            senderOneAverageReplyTiming += replyTime;
        }
        for (float replyTime : senderTwoReplyTimeInMinutes) {
            senderTwoAverageReplyTiming += replyTime;
        }
        senderOneAverageReplyTiming /= senderOneReplyTimeInMinutes.size();
        senderTwoAverageReplyTiming /= senderTwoReplyTimeInMinutes.size();
        ReplyTiming.setInitialized(true);
    }

    public static void debugReplyTiming() {
        Log.d(TAG, "senderOneTimeStamp.size() : " + senderOneTimeStamp.size());
        Log.d(TAG, "senderOneTotalWords : " + senderOneTotalWords);
        Log.d(TAG, "senderOneAverageReplyTiming : " + senderOneAverageReplyTiming);
        Log.d(TAG, "senderOneReplyTimeInMinutes.size() : " + senderOneReplyTimeInMinutes.size());

        Log.d(TAG, "senderTwoTimeStamp.size() : " + senderTwoTimeStamp.size());
        Log.d(TAG, "senderTwoTotalWords : " + senderTwoTotalWords);
        Log.d(TAG, "senderTwoAverageReplyTiming : " + senderTwoAverageReplyTiming);
        Log.d(TAG, "senderTwoReplyTimeInMinutes.size() : " + senderTwoReplyTimeInMinutes.size());
    }

    public static void reset() {
        initialized = false;
        senderOneTimeStamp.clear();
        senderOneReplyTimeInMinutes.clear();
        senderTwoTimeStamp.clear();
        senderTwoReplyTimeInMinutes.clear();
        uniqueSenderList.clear();
        senderOneAverageReplyTiming = 0;
        senderTwoAverageReplyTiming = 0;
        senderOneTotalMessages = 0;
        senderTwoTotalMessages = 0;
        senderOneTotalWords = 0;
        senderTwoTotalWords = 0;
    }

    public static void countMessages() {
        int i = 0;
        for (String s : FileProcessing.getSender()) {
            if (s.equals(getSenderList().get(0))) {
                senderOneTotalMessages++;
                senderOneTotalWords += FileProcessing.getMessageBody().get(i).split(" ").length;
            } else {
                senderTwoTotalMessages++;
                senderTwoTotalWords += FileProcessing.getMessageBody().get(i).split(" ").length;
            }
            i++;
        }
    }

    public static void processReplyTimings() {
        for (int i = 1; i < FileProcessing.getSender().size(); i++) {
            // PROCESS IFF DIFFERENT SENDERS
            if (!FileProcessing.getSender().get(i).equals(FileProcessing.getSender().get(i - 1))) {
                float replyTimingInMilliseconds =
                        FileProcessing.getMessageTimeStamp().get(i).getTime() - (FileProcessing.getMessageTimeStamp().get(i - 1).getTime());
                float replyTimingInMinutes = replyTimingInMilliseconds / (60 * 1000) % 60;
                float replyTimingInDays = replyTimingInMilliseconds / (24 * 60 * 60 * 1000);

                if (!FileProcessing.getSender().get(i).equals(getSenderList().get(0))) {
                    // TODO : CREATE USER-DEFINED THRESHOLD TO FILTER ANOMALIES
                    if ((replyTimingInDays >= 0) & (replyTimingInDays <= 10)) {
                        senderOneTimeStamp.add(FileProcessing.getMessageTimeStamp().get(i));
                        senderOneReplyTimeInMinutes.add(replyTimingInMinutes);
                    }
                } else {
                    if ((replyTimingInDays >= 0) & (replyTimingInDays <= 10)) {
                        senderTwoTimeStamp.add(FileProcessing.getMessageTimeStamp().get(i));
                        senderTwoReplyTimeInMinutes.add(replyTimingInMinutes);
                    }
                }
            }
        }
    }
}
