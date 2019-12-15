package com.jed.whatsapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MessageStatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_statistics);

        TextView leftCircleOne = (TextView) findViewById(R.id.leftCircleOne);
        TextView leftCircleTwo = (TextView) findViewById(R.id.leftCircleTwo);
        TextView leftCircleThree = (TextView) findViewById(R.id.leftCircleThree);
        TextView leftCircleFour = (TextView) findViewById(R.id.leftCircleFour);

        TextView rightCircleOne = (TextView) findViewById(R.id.rightCircleOne);
        TextView rightCircleTwo = (TextView) findViewById(R.id.rightCircleTwo);
        TextView rightCircleThree = (TextView) findViewById(R.id.rightCircleThree);
        TextView rightCircleFour = (TextView) findViewById(R.id.rightCircleFour);

//        // RETRIEVE AND DISPLAY ALL RELEVANT FIELDS
        ReplyTiming.analyzeReplyTimings();
        int senderOneTotalMessages = ReplyTiming.getSenderOneTotalMessages();
        long senderOneTotalWords = ReplyTiming.getSenderOneTotalWords();
        long senderOneWPM = ReplyTiming.getSenderOneTotalWords() / ReplyTiming.getSenderOneTotalMessages();
        double senderOneAvgReplyTiming = ReplyTiming.getSenderOneAverageReplyTiming();

        int senderTwoTotalMessages = ReplyTiming.getSenderTwoTotalMessages();
        long senderTwoTotalWords = ReplyTiming.getSenderTwoTotalWords();
        long senderTwoWPM = ReplyTiming.getSenderTwoTotalWords() / ReplyTiming.getSenderTwoTotalMessages();
        double senderTwoAvgReplyTiming = ReplyTiming.getSenderTwoAverageReplyTiming();

        leftCircleOne.setText("" + senderOneTotalMessages);
        leftCircleTwo.setText("" + senderOneTotalWords);
        leftCircleThree.setText("" + senderOneWPM);
        leftCircleFour.setText("" + senderOneAvgReplyTiming);

        rightCircleOne.setText("" + senderTwoTotalMessages);
        rightCircleTwo.setText("" + senderTwoTotalWords);
        rightCircleThree.setText("" + senderTwoWPM);
        rightCircleFour.setText("" + senderTwoAvgReplyTiming);
    }
}

