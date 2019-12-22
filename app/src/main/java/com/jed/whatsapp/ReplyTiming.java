package com.jed.whatsapp;

import java.lang.String;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Date;
import java.util.List;

public class ReplyTiming {
    // ATTRIBUTES
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

    // ACCESSOR METHODS
    public static List<Date> getSenderOneTimeStamp() {
        return senderOneTimeStamp;
    }

    public static List<Float> getSenderOneReplyTimeInMinutes() {
        return senderOneReplyTimeInMinutes;
    }

    public static List<Date> getSenderTwoTimeStamp() {
        return senderTwoTimeStamp;
    }

    public static List<Float> getSenderTwoReplyTimeInMinutes() {
        return senderTwoReplyTimeInMinutes;
    }

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


    // LOGIC METHODS
    static void changeSenderList() {
        uniqueSenderList = new ArrayList<>(new HashSet<>(FileProcessing.getSender()));
    }

    static void analyzeReplyTimings() {
        // EDGE CASE : FOR < 2 MESSAGES SENT
        if (FileProcessing.getSender().size() < 2) {
            return;
        }

        // RESET ALL ATTRIBUTES
        wipeInternalState();

        // CHANGE THE SENDER LIST TO MATCH THE CHAT
        changeSenderList();

        // PROCESS THE DATAFRAME
        processReplyTimings();

        // COUNT THE NUMBER OF MESSAGES
        countMessages();

        // SUM BOTH SENDERS' REPLY TIMINGS
        for (float replyTime : senderOneReplyTimeInMinutes) {
            senderOneAverageReplyTiming += replyTime;
        }
        for (float replyTime : senderTwoReplyTimeInMinutes) {
            senderTwoAverageReplyTiming += replyTime;
        }
        senderOneAverageReplyTiming /= senderOneReplyTimeInMinutes.size();
        senderTwoAverageReplyTiming /= senderTwoReplyTimeInMinutes.size();

        System.out.println("senderOneTotalMessages " + senderOneTotalMessages);
        System.out.println("senderTwoTotalMessages " + senderTwoTotalMessages);
        System.out.println("Average Reply Timing of " + getSenderList().get(0) +
                " " + senderOneAverageReplyTiming + "hours");
        System.out.println("Average Reply Timing of " + getSenderList().get(1) +
                " " + senderTwoAverageReplyTiming + " hours");
    }

    static void wipeInternalState() {
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

    static void countMessages() {
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

    static void processReplyTimings() {
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

//        // @DEBUG
//        System.out.println("senderOneTimeStamp.size() : " + senderOneTimeStamp.size());
//        System.out.println("senderOneTotalWords : " + senderOneTotalWords);
//        System.out.println("senderOneAverageReplyTiming : " + senderOneAverageReplyTiming);
//        System.out.println("senderOneReplyTimeInMinutes.size() : " + senderOneReplyTimeInMinutes.size());
//
//        System.out.println("senderTwoTimeStamp.size() : " + senderTwoTimeStamp.size());
//        System.out.println("senderTwoTotalWords : " + senderTwoTotalWords);
//        System.out.println("senderTwoAverageReplyTiming : " + senderTwoAverageReplyTiming);
//        System.out.println("senderTwoReplyTimeInMinutes.size() : " + senderTwoReplyTimeInMinutes.size());
    }
}
