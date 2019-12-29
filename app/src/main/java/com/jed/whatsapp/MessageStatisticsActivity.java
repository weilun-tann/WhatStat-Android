package com.jed.whatsapp;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MessageStatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_statistics);

        // SET THE VALUES FOR USER DISPLAY
        TextView leftSenderName = findViewById(R.id.leftSenderName);
        TextView rightSenderName = findViewById(R.id.rightSenderName);

        TextView leftCircleOne = findViewById(R.id.leftCircleOne);
        TextView leftCircleTwo = findViewById(R.id.leftCircleTwo);
        TextView leftCircleThree = findViewById(R.id.leftCircleThree);
        TextView leftCircleFour = findViewById(R.id.leftCircleFour);

        TextView rightCircleOne = findViewById(R.id.rightCircleOne);
        TextView rightCircleTwo = findViewById(R.id.rightCircleTwo);
        TextView rightCircleThree = findViewById(R.id.rightCircleThree);
        TextView rightCircleFour = findViewById(R.id.rightCircleFour);

        // RETRIEVE AND DISPLAY ALL RELEVANT FIELDS
        try {
            int senderOneTotalMessages = ReplyTiming.getSenderOneTotalMessages();
            int senderOneTotalWords = ReplyTiming.getSenderOneTotalWords();
            int senderOneWPM = ReplyTiming.getSenderOneTotalWords() / ReplyTiming.getSenderOneTotalMessages();
            double senderOneAvgReplyTiming = ReplyTiming.getSenderOneAverageReplyTiming();

            int senderTwoTotalMessages = ReplyTiming.getSenderTwoTotalMessages();
            long senderTwoTotalWords = ReplyTiming.getSenderTwoTotalWords();
            long senderTwoWPM = ReplyTiming.getSenderTwoTotalWords() / ReplyTiming.getSenderTwoTotalMessages();
            double senderTwoAvgReplyTiming = ReplyTiming.getSenderTwoAverageReplyTiming();

            leftSenderName.setText(ReplyTiming.getSenderList().get(0));
            rightSenderName.setText(ReplyTiming.getSenderList().get(1));

            leftCircleOne.setText("" + senderOneTotalMessages);
            leftCircleTwo.setText("" + senderOneTotalWords);
            leftCircleThree.setText("" + senderOneWPM);
            leftCircleFour.setText("" + (int)senderOneAvgReplyTiming);

            rightCircleOne.setText("" + senderTwoTotalMessages);
            rightCircleTwo.setText("" + senderTwoTotalWords);
            rightCircleThree.setText("" + senderTwoWPM);
            rightCircleFour.setText("" + (int)senderTwoAvgReplyTiming);
        } catch (ArithmeticException e) {
            Toast.makeText(getApplicationContext(), "Umm... could you maybe upload the " +
                    "text file for me first?", Toast.LENGTH_SHORT).show();
        }
    }
}

