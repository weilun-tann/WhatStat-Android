package com.jed.whatsapp;

import java.lang.String;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Date;
import java.util.List;

public class ReplyTiming {
    // ATTRIBUTES
    private static List<Date> senderOneTimeStamp = new ArrayList<>();
    private static List<Long> senderOneReplyTimeInMinutes = new ArrayList<>();
    private static List<Date> senderTwoTimeStamp = new ArrayList<>();
    private static List<Long> senderTwoReplyTimeInMinutes = new ArrayList<>();
    private static List<String> uniqueSenderList = new ArrayList<>();
    private static long senderOneAverageReplyTiming = 0;
    private static long senderTwoAverageReplyTiming = 0;
    private static int senderOneTotalMessages = 0;
    private static int senderTwoTotalMessages = 0;

    // ACCESSOR METHODS
    public static List<Date> getSenderOneTimeStamp() {
        return senderOneTimeStamp;
    }

    public static List<Long> getSenderOneReplyTimeInMinutes() {
        return senderOneReplyTimeInMinutes;
    }

    public static List<Date> getSenderTwoTimeStamp() {
        return senderTwoTimeStamp;
    }

    public static List<Long> getSenderTwoReplyTimeInMinutes() {
        return senderTwoReplyTimeInMinutes;
    }

    public static List<String> getSenderList() {
        return uniqueSenderList;
    }

    public static double getSenderOneAverageReplyTiming() {
        return senderOneAverageReplyTiming;
    }

    public static double getSenderTwoAverageReplyTiming() {
        return senderTwoAverageReplyTiming;
    }

    public static int getSenderOneTotalMessages() {
        return senderOneTotalMessages;
    }

    public static int getSenderTwoTotalMessages() {
        return senderTwoTotalMessages;
    }

    // LOGIC METHODS
    static void addSenderOneTimeStamp(Date myTimeStamp) {
        senderOneTimeStamp.add(myTimeStamp);
    }

    static void addSenderOneReplyTime(long myReplyTime) {
        senderOneReplyTimeInMinutes.add(myReplyTime);
    }

    static void addSenderTwoTimeStamp(Date myTimeStamp) {
        senderTwoTimeStamp.add(myTimeStamp);
    }

    static void addSenderTwoReplyTime(long myReplyTime) {
        senderTwoReplyTimeInMinutes.add(myReplyTime);
    }

    static void changeSenderList() {
        uniqueSenderList = new ArrayList<>(new HashSet<>(FileProcessing.getSender()));
    }

    static void analyzeReplyTimings() {
        // EDGE CASE : FOR < 2 MESSAGES SENT
        if (FileProcessing.getSender().size() < 2) {
            return;
        }
        // CHANGE THE SENDER LIST TO MATCH THE CHAT
        changeSenderList();
        // PROCESS THE DATAFRAME
        processWhatsAppDF();
        // COUNT THE NUMBER OF MESSAGES
        countMessages();
        // SUM BOTH SENDERS' REPLY TIMINGS
        for (long replyTime : senderOneReplyTimeInMinutes) {
            senderOneAverageReplyTiming += replyTime;
        }
        for (long replyTime : senderTwoReplyTimeInMinutes) {
            senderTwoAverageReplyTiming += replyTime;
        }
        senderOneAverageReplyTiming /= senderOneReplyTimeInMinutes.size();
        senderTwoAverageReplyTiming /= senderTwoReplyTimeInMinutes.size();

        System.out.println("senderOneTotalMessages " + senderOneTotalMessages);
        System.out.println("senderTwoTotalMessages " + senderTwoTotalMessages);
        System.out.println("Average Reply Timing of " + uniqueSenderList.get(0) +
                " " + senderOneAverageReplyTiming + "hours");
        System.out.println("Average Reply Timing of " + uniqueSenderList.get(0) +
                " " + senderTwoAverageReplyTiming + " hours");

        // RESET ALL ATTRIBUTES
        wipeInternalState();
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
    }

    static void countMessages() {
        for (String s : FileProcessing.getSender()) {
            if (s == uniqueSenderList.get(0)) {
                senderOneTotalMessages++;
            } else {
                senderTwoTotalMessages++;
            }
        }
    }

    static void processWhatsAppDF() {
        for (int i = 1; i < FileProcessing.getSender().size(); i++) {
            // PROCESS IFF DIFFERENT SENDERS
            if (!FileProcessing.getSender().get(i).equals(FileProcessing.getSender().get(i - 1))) {
                long replyTimingInMilliseconds =
                        FileProcessing.getMessageTimeStamp().get(i).getTime() - (FileProcessing.getMessageTimeStamp().get(i - 1).getTime());
                long replyTimingInMinutes = replyTimingInMilliseconds / (60 * 1000) % 60;
                long replyTimingInDays = replyTimingInMilliseconds / (24 * 60 * 60 * 1000);

                if (FileProcessing.getSender().get(i).equals(getSenderList().get(i))) {
                    // TODO : CREATE USER-DEFINED THRESHOLD TO FILTER ANOMALIES
                    if ((replyTimingInDays >= 0) & (replyTimingInDays <= 10)) {
                        senderOneTimeStamp.add(FileProcessing.getMessageTimeStamp().get(i));
                        senderOneReplyTimeInMinutes.add(replyTimingInMinutes);
                    }
                } else {
                    if ((replyTimingInDays > 0) & (replyTimingInDays < 8)) {
                        senderTwoTimeStamp.add(FileProcessing.getMessageTimeStamp().get(i));
                        senderTwoReplyTimeInMinutes.add(replyTimingInMinutes);
                    }
                }
            }
        }
    }
}
