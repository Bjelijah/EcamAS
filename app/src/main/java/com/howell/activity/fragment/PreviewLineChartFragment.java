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
import lecho.lib.hellocharts.model.AxisValue;
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
    private String [] mHourOfDay;
    private static final String[] HOUR_OF_DAY={"0:00","1:00","2:00","3:00","4:00","5:00","6:00",
            "7:00","8:00","9:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00",
            "19:00","20:00","21:00","22:00","23:00"};

    private static final String[] HOUR={"0","1","2","3","4","5","6","7","8","9","10","11","12",
            "13","14","15","16","17","18","19","20","21","22","23"};



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_preview_line_charts,container,false);
        mChart = v.findViewById(R.id.plc_chart);
        mPreviewChart = v.findViewById(R.id.plc_preview_charts);
        initLabel();
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


    private void initLabel(){
        if (mHourOfDay==null) mHourOfDay = new String[144];
        int hour=0 , min=0;
        String str="";
        for(int i=0;i<144;i++){
            str = String.format("%02d:%02d",hour,min);
            mHourOfDay[i]=str;
            min+=10;
            if (min==60) {
                min = 0;
                hour++;
            }
        }
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
                .setHasLines(true)
                .setFilled(true);


        return line;
    }

    private void onData(){
        List<Line> lines = new ArrayList<>();
        lines.add(getLine(0));
        lines.add(getLine(1));
        data = new LineChartData(lines);

        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        for (int i=0;i<DATA_LEN;i++){
            axisValues.add(new AxisValue(i).setLabel(mHourOfDay[i]));
        }

        Axis axisX = new Axis(axisValues);
        axisX.setMaxLabelChars(5);
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(new Axis().setHasLines(true).setName("人数"));
        previewData = new LineChartData(data);


        List<Line> datalines = data.getLines();

        for(int i=0;i<datalines.size();i++){
            Line l = datalines.get(i);
            for (PointValue pv:l.getValues()){
                pv.setLabel((i==0?"进入：":"出去：")+pv.getY());

            }

            l.setHasPoints(true);
            l.setPointRadius(4);

            l.setHasLabelsOnlyForSelected(true);


        }
        data.getAxisXBottom().setHasLines(true);
        mChart.setLineChartData(data);
        mChart.setZoomEnabled(false);
        mChart.setScrollEnabled(false);
        mChart.setValueSelectionEnabled(true);

        mPreviewChart.setLineChartData(previewData);
        mPreviewChart.setViewportChangeListener(new ViewportListener());

        previewX(true);
    }







    private void previewX(boolean animate) {
        Viewport tempViewport = new Viewport(mChart.getMaximumViewport());
        int dx =(int) (tempViewport.width() / 12*11);
        int dy =(int) (tempViewport.height()/ 4);
        Log.i("123","dx="+dx+"  dy="+dy);

        float top = tempViewport.top;
        float bottom = tempViewport.bottom;
        float left = tempViewport.left;
        float right = tempViewport.right;

        float newRight = tempViewport.right/12;
        Log.i("123","t="+top+" b="+bottom+" l="+left+" r="+right + " new r="+newRight);
        tempViewport.set(left,top,newRight,bottom);
//        tempViewport.inset(dx, 0);
//        tempViewport.inset(0,dy);
//        tempViewport.offsetTo(dx,0);


        if (animate) {
            mPreviewChart.setCurrentViewportWithAnimation(tempViewport);
        } else {
            mPreviewChart.setCurrentViewport(tempViewport);
        }
//        mPreviewChart.setZoomType(ZoomType.HORIZONTAL);
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
