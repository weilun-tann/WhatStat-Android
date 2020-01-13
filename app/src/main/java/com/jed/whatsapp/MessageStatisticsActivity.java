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
            int senderOneTotalMessages = Metrics.getSenderOneTotalMessages();
            int senderOneTotalMedia = Metrics.getSenderOneTotalMedia();
            int senderOneTotalWords = Metrics.getSenderOneTotalWords();
            int senderOneWPM = Metrics.getSenderOneTotalWords() / Metrics.getSenderOneTotalMessages();
            double senderOneAvgReplyTiming = Metrics.getSenderOneAverageReplyTimingInHours();

            int senderTwoTotalMessages = Metrics.getSenderTwoTotalMessages();
            int senderTwoTotalMedia = Metrics.getSenderTwoTotalMedia();
            long senderTwoTotalWords = Metrics.getSenderTwoTotalWords();
            long senderTwoWPM = Metrics.getSenderTwoTotalWords() / Metrics.getSenderTwoTotalMessages();
            double senderTwoAvgReplyTiming = Metrics.getSenderTwoAverageReplyTimingInHours();

            leftSenderName.setText(Metrics.getSenderList().get(0));
            rightSenderName.setText(Metrics.getSenderList().get(1));

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

