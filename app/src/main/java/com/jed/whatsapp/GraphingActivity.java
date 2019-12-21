//package com.jed.whatsapp;
//
//import android.graphics.Color;
//import android.os.Bundle;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.github.mikephil.charting.charts.LineChart;
//import com.github.mikephil.charting.components.XAxis;
//import com.github.mikephil.charting.data.Entry;
//import com.github.mikephil.charting.data.LineData;
//import com.github.mikephil.charting.data.LineDataSet;
//import com.github.mikephil.charting.formatter.ValueFormatter;
//import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
//
//import java.util.ArrayList;
//
//public class GraphingActivity extends AppCompatActivity {
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_graphing);
//        try {
//            getSupportActionBar().hide();
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//        }
//
//        // CREATE GRAPH REFERENCES
//        LineChart replyTimingChart = findViewById(R.id.replyTimingChart);
//
////        replyTimingChart.setDragEnabled(true);
////        replyTimingChart.setScaleEnabled(true);
//
//        // CREATE DUMMY DATA
//        ArrayList<Entry> replyTimings = new ArrayList<>();
//        replyTimings.add(new Entry(0f, 10.0f));
//        replyTimings.add(new Entry(1f, 10.1f));
//        replyTimings.add(new Entry(2f, 10.2f));
//
////        long referenceTimestamp = 1451660400;
////        HourAxisValueFormatter xAxisFormatter = new HourAxisValueFormatter(referenceTimestamp);
////        XAxis xAxis = replyTimingChart.getXAxis();
////        xAxis.setValueFormatter(xAxisFormatter);
//
//
//        LineDataSet set1 = new LineDataSet(replyTimings, "replyTimings (Hours)");
//        set1.setFillAlpha(110);
//        set1.setColor(Color.GREEN);
//        set1.setLineWidth(3);
//
//        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
//        dataSets.add(set1);
//
//        LineData data = new LineData(dataSets);
//        replyTimingChart.setData(data);
//        replyTimingChart.invalidate();
//
//        replyTimingChart.setViewPortOffsets(60, 0, 50, 60);
//
//    }
//}
