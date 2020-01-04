package com.jed.whatsapp;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MessageStatisticsActivity extends AppCompatActivity {

    TextView leftSenderName;
    TextView rightSenderName;
    TextView leftCircleOne;
    TextView leftCircleTwo;
    TextView leftCircleThree;
    TextView leftCircleFour;
    TextView leftCircleFive;

    TextView rightCircleOne;
    TextView rightCircleTwo;
    TextView rightCircleThree;
    TextView rightCircleFour;
    TextView rightCircleFive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_statistics);

        // SET THE VALUES FOR USER DISPLAY
        leftSenderName = findViewById(R.id.leftSenderName);
        rightSenderName = findViewById(R.id.rightSenderName);

        leftCircleOne = findViewById(R.id.leftCircleOne);
        leftCircleTwo = findViewById(R.id.leftCircleTwo);
        leftCircleThree = findViewById(R.id.leftCircleThree);
        leftCircleFour = findViewById(R.id.leftCircleFour);
        leftCircleFive = findViewById(R.id.leftCircleFive);

        rightCircleOne = findViewById(R.id.rightCircleOne);
        rightCircleTwo = findViewById(R.id.rightCircleTwo);
        rightCircleThree = findViewById(R.id.rightCircleThree);
        rightCircleFour = findViewById(R.id.rightCircleFour);
        rightCircleFive = findViewById(R.id.rightCircleFive);

        // RETRIEVE AND DISPLAY ALL RELEVANT FIELDS
        try {
            int senderOneTotalMessages = ReplyTiming.getSenderOneTotalMessages();
            int senderOneTotalMedia = ReplyTiming.getSenderOneTotalMedia();
            int senderOneTotalWords = ReplyTiming.getSenderOneTotalWords();
            int senderOneWPM = ReplyTiming.getSenderOneTotalWords() / ReplyTiming.getSenderOneTotalMessages();
            double senderOneAvgReplyTiming = ReplyTiming.getSenderOneAverageReplyTimingInHours();

            int senderTwoTotalMessages = ReplyTiming.getSenderTwoTotalMessages();
            int senderTwoTotalMedia = ReplyTiming.getSenderTwoTotalMedia();
            long senderTwoTotalWords = ReplyTiming.getSenderTwoTotalWords();
            long senderTwoWPM = ReplyTiming.getSenderTwoTotalWords() / ReplyTiming.getSenderTwoTotalMessages();
            double senderTwoAvgReplyTiming = ReplyTiming.getSenderTwoAverageReplyTimingInHours();

            leftSenderName.setText(ReplyTiming.getSenderList().get(0));
            rightSenderName.setText(ReplyTiming.getSenderList().get(1));

            leftCircleOne.setText("" + senderOneTotalMessages);
            leftCircleTwo.setText("" + senderOneTotalMedia);
            leftCircleThree.setText("" + senderOneTotalWords);
            leftCircleFour.setText("" + senderOneWPM);
            leftCircleFive.setText("" + String.format("%.1f", senderOneAvgReplyTiming));

            rightCircleOne.setText("" + senderTwoTotalMessages);
            rightCircleTwo.setText("" + senderTwoTotalMedia);
            rightCircleThree.setText("" + senderTwoTotalWords);
            rightCircleFour.setText("" + senderTwoWPM);
            rightCircleFive.setText("" + String.format("%.1f", senderTwoAvgReplyTiming));
        } catch (ArithmeticException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Umm... could you maybe upload the " +
                    "text file for me first?", Toast.LENGTH_SHORT).show();
        }
    }
}

