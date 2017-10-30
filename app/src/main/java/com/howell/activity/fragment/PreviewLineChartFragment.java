package com.howell.activity.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.howell.webcam.R;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PreviewLineChartView;

/**
 * Created by Administrator on 2017/10/30.
 */

public class PreviewLineChartFragment extends Fragment {
    private LineChartView mChart;
    private PreviewLineChartView mPreviewChart;
    private LineChartData data;
    private LineChartData previewData;
    int [][] manAllDay;
    private static final int DATA_LEN = 12*12;
    int [] colorUtil={ Color.parseColor("#2a7ac2"), Color.parseColor("#c09237")};
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_preview_line_charts,container,false);
        mChart = v.findViewById(R.id.plc_chart);
        mPreviewChart = v.findViewById(R.id.plc_preview_charts);
        generateData();
        return v;
    }


    private void generateData(){
        if (manAllDay==null)manAllDay=new int[2][DATA_LEN];
        for (int i=0;i<2;i++){
            for (int j=0;j<DATA_LEN;j++){
                manAllDay[i][j] =  (int)(Math.random()*100);
            }
        }
        onData();
    }

    private Line getLine(int index){
        List<PointValue> values = new ArrayList<PointValue>();
        for (int i=0;i<DATA_LEN;i++){
            values.add(new PointValue(i,manAllDay[index][i]));
        }
        Line line = new Line(values)
                .setColor(colorUtil[index])
                .setHasPoints(false)
                .setStrokeWidth(1)
                .setCubic(true)
                .setHasLines(true);

       return line;
    }

    private void onData(){
       List<Line> lines = new ArrayList<>();
       lines.add(getLine(0));
       lines.add(getLine(1));
       data = new LineChartData(lines);
       data.setAxisXBottom(new Axis());
       data.setAxisYLeft(new Axis().setHasLines(true));
       previewData = new LineChartData(data);


       mChart.setLineChartData(data);
       mChart.setZoomEnabled(false);
       mChart.setScrollEnabled(false);

       mPreviewChart.setLineChartData(previewData);
       mPreviewChart.setViewportChangeListener(new ViewportListener());

       previewX(true);
    }







    private void previewX(boolean animate) {
        Viewport tempViewport = new Viewport(mChart.getMaximumViewport());
        int dx =(int) (tempViewport.width() / 2);
        Log.i("123","dx="+dx);
        tempViewport.inset(dx, 0);
        if (animate) {
            mPreviewChart.setCurrentViewportWithAnimation(tempViewport);
        } else {
            mPreviewChart.setCurrentViewport(tempViewport);
        }
        mPreviewChart.setZoomType(ZoomType.HORIZONTAL);
        mPreviewChart.setZoomEnabled(false);
    }



    private class ViewportListener implements ViewportChangeListener {

        @Override
        public void onViewportChanged(Viewport newViewport) {
            // don't use animation, it is unnecessary when using preview chart.
            mChart.setCurrentViewport(newViewport);
        }
    }

}
