package com.howell.activity.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.howell.webcam.R;
import com.howell.activity.PreviewLineChartActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by Administrator on 2017/10/27.
 */

public class LineChartFragment extends Fragment{

    LineChartView lcHour;
    LineChartView lcDay;
    private LineChartData hourData,dayData;
    int [][] manHour,manDay;


    private static final int CHARTS_LEN = 12;
    int [] colorUtil={ Color.parseColor("#2a7ac2"), Color.parseColor("#c09237")};
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_line_charts,container,false);
        lcHour = v.findViewById(R.id.lc_hour);
        lcDay = v.findViewById(R.id.lc_day);

        initData();
        generateData();
        generateData2();
        lcHour.setViewportCalculationEnabled(false);
        lcDay.setViewportCalculationEnabled(false);
        resetViewport(lcHour);
        resetViewport(lcDay);
        toggleCubic();
        return v;
    }

    private void initData(){
        if (manHour==null)manHour=new int[2][CHARTS_LEN];
        for (int i=0;i<2;i++){
            for (int j=0;j<CHARTS_LEN;j++){
                manHour[i][j]=(int)(Math.random()*100);
            }
        }
        if (manDay==null)manDay=new int[2][CHARTS_LEN];
        for (int i=0;i<2;i++){
            for (int j=0;j<CHARTS_LEN;j++){
                manDay[i][j]=(int)(Math.random()*100);
            }
        }

    }
    private List<AxisValue>getHourX(){
        List<AxisValue> list = new ArrayList<>();
        Date data = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(data);
        c.add(Calendar.HOUR_OF_DAY,-1);
        Log.i("123","c="+c.toString());
        for(int i=0;i<12;i++){
            String str = String.format("%02d:%02d",c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE));
            list.add(new AxisValue(i).setLabel(str));
            c.add(Calendar.MINUTE,5);
        }
        return list;
    }

    private void generateData(){
        List<Line> lines = new ArrayList<Line>();
        for (int i=0;i<2;i++){
            List<PointValue> values = new ArrayList<PointValue>();
            for (int j=0;j<CHARTS_LEN;j++){
                String str="进入："+manHour[0][j]+"\n出去："+manHour[1][j];
                values.add(new PointValue(j,manHour[i][j]).setLabel(str));
            }
            Line line = new Line(values);

            line.setColor(colorUtil[i]);
            line.setShape(ValueShape.CIRCLE);
            line.setStrokeWidth(1);//线粗细
            line.setPointRadius(3);
            line.setCubic(true);
            line.setFilled(true);
            line.setHasLabels(true);
            line.setHasLabelsOnlyForSelected(true);
            line.setHasLines(true);
            line.setHasPoints(true);
            lines.add(line);
        }
        hourData = new LineChartData(lines);
        hourData.setAxisXBottom(new Axis().setName("1小时").setValues(getHourX()));
        hourData.setAxisYLeft(new Axis().setHasLines(true).setName("人数"));
        hourData.setBaseValue(Float.NEGATIVE_INFINITY);
        lcHour.setLineChartData(hourData);
    }

    private void generateData2(){
        List<Line> lines = new ArrayList<Line>();
        for (int i=0;i<2;i++){
            List<PointValue> values = new ArrayList<PointValue>();
            for (int j=0;j<CHARTS_LEN;j++){
                String str="进入："+manDay[0][j]+"\n出去："+manDay[1][j];
                values.add(new PointValue(j,manDay[i][j]).setLabel(str));
            }
            Line line = new Line(values);

            line.setColor(colorUtil[i]);
            line.setShape(ValueShape.CIRCLE);
            line.setStrokeWidth(1);//线粗细
            line.setPointRadius(3);
            line.setCubic(true);
            line.setFilled(true);
            line.setHasLabels(true);
            line.setHasLabelsOnlyForSelected(true);
            line.setHasLines(true);
            line.setHasPoints(true);
            lines.add(line);
        }
        List<AxisValue> list = new ArrayList<>();
        list.add(new AxisValue(0).setLabel("0:00"));
        list.add(new AxisValue(1).setLabel("2:00"));
        list.add(new AxisValue(2).setLabel("4:00"));
        list.add(new AxisValue(3).setLabel("6:00"));
        list.add(new AxisValue(4).setLabel("8:00"));
        list.add(new AxisValue(5).setLabel("10:00"));
        list.add(new AxisValue(6).setLabel("12:00"));
        list.add(new AxisValue(7).setLabel("14:00"));
        list.add(new AxisValue(8).setLabel("16:00"));
        list.add(new AxisValue(9).setLabel("18:00"));
        list.add(new AxisValue(10).setLabel("20:00"));
        list.add(new AxisValue(11).setLabel("22:00"));

        dayData = new LineChartData(lines);
        dayData.setAxisXBottom(new Axis().setName("1天").setValues(list));
        dayData.setAxisYLeft(new Axis().setHasLines(true).setName("人数"));
        dayData.setBaseValue(Float.NEGATIVE_INFINITY);
        lcDay.setLineChartData(dayData);
    }

    private void toggleCubic(){
        final Viewport v = new Viewport(lcHour.getMaximumViewport());
        final Viewport v2 = new Viewport(lcDay.getMaximumViewport());
        lcHour.setCurrentViewportWithAnimation(v);
        lcHour.setZoomEnabled(false);
        lcHour.setValueSelectionEnabled(true);

        lcDay.setCurrentViewportWithAnimation(v2);
        lcDay.setZoomEnabled(false);
        lcDay.setValueSelectionEnabled(true);

    }


    private void resetViewport(LineChartView chart) {
        // Reset viewport height range to (0,100)
        final Viewport v = new Viewport(chart.getMaximumViewport());
        v.bottom = 0;
        v.top = 100;
        v.left = 0;
        v.right = CHARTS_LEN-1;
        chart.setMaximumViewport(v);
        chart.setCurrentViewport(v);
    }
}
