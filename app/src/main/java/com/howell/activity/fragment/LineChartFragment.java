package com.howell.activity.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.howell.webcam.R;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
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
    private LineChartData data;
    int [][] manHour ;
    int [] colorUtil={ Color.parseColor("33B5E5"), Color.parseColor("#FFBB33")};
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_line_charts,container,false);
        lcHour = v.findViewById(R.id.lc_hour);
        lcDay = v.findViewById(R.id.lc_day);
        initData();
        generateData();
        lcHour.setViewportCalculationEnabled(false);
        resetViewport(lcHour);
        return v;
    }

    private void initData(){
        if (manHour==null)manHour=new int[2][12];
        for (int i=0;i<2;i++){
            for (int j=0;j<12;j++){
                manHour[i][j]=(int)Math.random()*10000;
            }
        }
    }

    private void generateData(){
        List<Line> lines = new ArrayList<Line>();
        for (int i=0;i<2;i++){
            List<PointValue> values = new ArrayList<PointValue>();
            for (int j=0;j<12;j++){
                values.add(new PointValue(j,manHour[i][j]).setLabel("当前一小时数据统计"));
            }
            Line line = new Line(values);

            line.setColor(colorUtil[i]);
            line.setShape(ValueShape.CIRCLE);
            line.setCubic(false);
            line.setFilled(true);
            line.setHasLabels(true);
            line.setHasLabelsOnlyForSelected(true);
            line.setHasLines(true);
            line.setHasPoints(true);
            lines.add(line);
        }

        data = new LineChartData(lines);
        data.setAxisXBottom(new Axis().setName("时间"));
        data.setAxisYLeft(new Axis().setHasLines(true).setName("人数"));
        data.setBaseValue(Float.NEGATIVE_INFINITY);
        lcHour.setLineChartData(data);
    }
    private void resetViewport(LineChartView chart) {
        // Reset viewport height range to (0,100)
        final Viewport v = new Viewport(chart.getMaximumViewport());
        v.bottom = 0;
        v.top = 100;
        v.left = 0;
        v.right = 12 - 1;
        chart.setMaximumViewport(v);
        chart.setCurrentViewport(v);
    }
}
