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

import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by Administrator on 2017/10/31.
 */

public class LineColumnChartFragment extends Fragment {
    private LineChartView mChartTop;
    private ColumnChartView mChartBottom;
    int [] colorUtil={ Color.parseColor("#2a7ac2"), Color.parseColor("#c09237")};
    private LineChartData lineData;
    private ColumnChartData columnData;
    private static final String[] HOUR_OF_DAY={"0:00","1:00","2:00","3:00","4:00","5:00","6:00",
    "7:00","8:00","9:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00",
    "19:00","20:00","21:00","22:00","23:00"};

    private static final String [] MIN_OF_HOUR={":00",":05",":10",":15",":20",":25",":30",":35",":40",
    ":45",":50",":55"};

    int [][] mDataDay = new int[2][24];
    int [][] mDataHour = new int [2][12];

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_line_column_chart,container,false);
        mChartTop = v.findViewById(R.id.lcc_chart);
        mChartBottom = v.findViewById(R.id.lcc_column_chart);

        generateInitialLineData();
        generateColumnData();
        return v;
    }

    private void generateInitialLineData(){
        int numValues= 12;//一小时12个点
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        List<PointValue> valuesIn = new ArrayList<PointValue>();
        List<PointValue> valuesOut = new ArrayList<PointValue>();
        for (int i = 0; i < numValues; ++i) {
            valuesIn.add(new PointValue(i, 0));
            valuesOut.add(new PointValue(i, 0));
            axisValues.add(new AxisValue(i).setLabel(""));
        }
        Line lineIn = new Line(valuesIn);
        lineIn.setColor(colorUtil[0])
                .setCubic(true)
                .setFilled(true)
                .setHasLabels(true)
                .setHasLabelsOnlyForSelected(true)
                .setPointRadius(4)
                .setStrokeWidth(1);

        Line lineOut = new Line(valuesOut);
        lineOut.setColor(colorUtil[1])
                .setCubic(true)
                .setFilled(true)
                .setHasLabels(true)
                .setHasLabelsOnlyForSelected(true)
                .setPointRadius(4)
                .setStrokeWidth(1);

        List<Line> lines = new ArrayList<Line>();
        lines.add(lineIn);
        lines.add(lineOut);

        lineData = new LineChartData(lines);
        lineData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
        lineData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(3).setName("人数"));

        mChartTop.setLineChartData(lineData);
        mChartTop.setViewportCalculationEnabled(false);
        mChartTop.setValueSelectionEnabled(true);

        Viewport v = new Viewport(0, 110, 11, 0);
        mChartTop.setMaximumViewport(v);
        mChartTop.setCurrentViewport(v);
    }

    private void generateColumnData(){
        getDataDay();
        int numSubcolumns = 2;
        int numColumns = 24;//24小时

        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;

        for(int i=0;i<numColumns;i++){
            values=new ArrayList<>();
            for (int j=0;j<numSubcolumns;j++){
                values.add(new SubcolumnValue(mDataDay[j][i],colorUtil[j]));
            }
            axisValues.add(new AxisValue(i).setLabel(HOUR_OF_DAY[i]));
            columns.add(new Column(values).setHasLabelsOnlyForSelected(true));
        }
        columnData = new ColumnChartData(columns);
        columnData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
        columnData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(2).setName("人数"));

        mChartBottom.setColumnChartData(columnData);

        mChartBottom.setOnValueTouchListener(new ValueTouchListener());
        mChartBottom.setValueSelectionEnabled(true);
        mChartBottom.setZoomEnabled(false);
    }

    private void getDataDay(){
        for(int i=0;i<2;i++){
            for(int j=0;j<24;j++){
                mDataDay[i][j] = (int)(Math.random()*100);
            }
        }
    }

    private void getDataHour(int hour){
        for(int i=0;i<2;i++){
            for (int j=0;j<12;j++){
                mDataHour[i][j]=(int)(Math.random()*100);
            }
        }



    }

    private void generateLineData(int hour,boolean isInit){//当前小时数据
        int flag = isInit?0:1;
        getDataHour(hour);
        mChartTop.cancelDataAnimation();
        Line lineIn = lineData.getLines().get(0);
        lineIn.setColor(colorUtil[0]);
        List<PointValue> valuesIn =  lineIn.getValues();
        for(int i=0;i<12;i++){
            PointValue value = valuesIn.get(i);
            value.setTarget(value.getX(), mDataHour[0][i]*flag);
            value.setLabel(isInit?"": ("进入："+mDataHour[0][i]));
        }

        Line lineOut = lineData.getLines().get(1);
        lineOut.setColor(colorUtil[1]);
        List<PointValue> valuesOut =  lineOut.getValues();
//        mDataHour[1][i]*flag
        for(int i=0;i<12;i++){
            PointValue value = valuesOut.get(i);
            value.setTarget(value.getX(), mDataHour[1][i]*flag);
            value.setLabel(isInit?"": ("出去："+mDataHour[1][i]));
        }
        Viewport v = new Viewport(0, 110, 11, 0);
        mChartTop.setMaximumViewport(v);
        mChartTop.setCurrentViewport(v);
        mChartTop.setZoomEnabled(false);
        List<AxisValue> axisValues =  lineData.getAxisXBottom().getValues();
        for(int i=0;i<12;i++){
            AxisValue axisValue = axisValues.get(i);
            String label=String.format("%02d",hour)+  MIN_OF_HOUR[i];
            axisValue.setLabel(isInit?"":label);
        }
        mChartTop.startDataAnimation(300);
    }

    private class ValueTouchListener implements ColumnChartOnValueSelectListener {

        @Override
        public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
//            Log.i("123","columnIndex="+columnIndex+" subcolumnIndex="+subcolumnIndex+" value="+value);
            generateLineData(columnIndex, false);
        }

        @Override
        public void onValueDeselected() {
            generateLineData(0, true);
        }
    }




}
