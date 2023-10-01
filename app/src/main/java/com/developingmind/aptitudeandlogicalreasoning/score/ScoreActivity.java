package com.developingmind.aptitudeandlogicalreasoning.score;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.utils.widget.ImageFilterView;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;

import com.developingmind.aptitudeandlogicalreasoning.R;
import com.developingmind.aptitudeandlogicalreasoning.ScoreEnum;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;

public class ScoreActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    private ImageFilterView scoreCardIcon;
    private TextView total,correct,attempted,wrong,skipped;
    private int totalQuestions,correctQuestions,attemptedQuestions,wrongQuestions,skippedQuestions = 0;

    private PieChart pieChart;

    String[] parties = new String[] {
            "Correct", "Attempted", "Wrong", "Skipped"
    };
    private ArrayList<Integer> s = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        total = findViewById(R.id.score_total);
        correct = findViewById(R.id.score_correct);
        attempted = findViewById(R.id.score_attempted);
        wrong = findViewById(R.id.score_wrong);
        skipped = findViewById(R.id.score_skip);
        pieChart = findViewById(R.id.pieChart);

        totalQuestions = getIntent().getIntExtra(ScoreEnum.totalQuestions.toString(),0);
        correctQuestions = getIntent().getIntExtra(ScoreEnum.correctQuestions.toString(),0);
        attemptedQuestions = getIntent().getIntExtra(ScoreEnum.totalAttempted.toString(),0);
        wrongQuestions = getIntent().getIntExtra(ScoreEnum.totalWrong.toString(),0);
        skippedQuestions = getIntent().getIntExtra(ScoreEnum.totalSkipped.toString(),0);

        s.add(correctQuestions);
        s.add(attemptedQuestions);
        s.add(wrongQuestions);
        s.add(skippedQuestions);


        // Setting Values to Text View
        setScoreText(total,totalQuestions);
        setScoreText(correct,correctQuestions);
        setScoreText(attempted,attemptedQuestions);
        setScoreText(wrong,wrongQuestions);
        setScoreText(skipped,skippedQuestions);

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

//        pieChart.setCenterTextTypeface(tfLight);
        pieChart.setCenterText(generateCenterSpannableText());

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);

        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);

        pieChart.setHoleRadius(50f);
        pieChart.setTransparentCircleRadius(50f);

        pieChart.setDrawCenterText(true);

        pieChart.setRotationAngle(0);

        pieChart.setOnChartValueSelectedListener(this);

        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.animateY(1400, Easing.EaseInOutQuad);

        // entry label styling
        pieChart.setEntryLabelColor(Color.WHITE);
//        pieChart.setEntryLabelTypeface(tfRegular);
        pieChart.setEntryLabelTextSize(12f);

        pieChart.getLegend().setTextColor(getColor(R.color.black));
        pieChart.getLegend().setTextSize(13);

        setData(4,10);

    }

    private void setData(int count, float range) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (int i = 0; i < count ; i++) {
            if(s.get(i)!=0)
                entries.add(new PieEntry(s.get(i),
                        parties[i % parties.length],
                        getResources().getDrawable(R.drawable.ic_tip)));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();

//        for (int c : ColorTemplate.VORDIPLOM_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.JOYFUL_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.COLORFUL_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.LIBERTY_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.PASTEL_COLORS)
//            colors.add(c);

        if(correctQuestions!=0){
            colors.add(Color.GREEN);
        }
        if(attemptedQuestions!=0){
            colors.add(Color.BLUE);
        }
        if(wrongQuestions!=0){
            colors.add(Color.RED);
        }
        if(skippedQuestions!=0){
            colors.add(Color.MAGENTA);
        }

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
//        data.setValueTypeface(tfLight);
        pieChart.setData(data);

        // undo all highlights
        pieChart.highlightValues(null);

        pieChart.invalidate();
    }

    private void setScoreText(TextView t,int toAdd){
        t.setText(t.getText().toString()+toAdd);
    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("Statistics");
        s.setSpan(new RelativeSizeSpan(1.7f), 0, 10, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), 0, 10, 0);
        return s;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}