package com.example.a75800.zes_psngertransportation.Adapter;

import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.a75800.zes_psngertransportation.Model.TrainModel;
import com.example.a75800.zes_psngertransportation.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Created by 75800 on 2017/8/20.
 */

public class TicketCheckAdapter extends BaseAdapter {

    private Context context;
    private List<TrainModel> list;
    private int passedTrain;
//    private String selectedStation;

    public TicketCheckAdapter(Context context, List<TrainModel> list,String selectedStation) {
        super();
        this.context = context;
        //去除终到的车+按照时间排序
        this.list = removeToTheEndTrain_sortByTime(list);
//        this.selectedStation = selectedStation;
        getPassedTrain(selectedStation);
        if (!selectedStation.equals("")){
            //仅显示已经选中的站台
            this.list = selectTargetStations(selectedStation,this.list);
        }
    }

    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        } else {
            return list.size();
        }
    }

    public int getPassedTrain(String station){
        passedTrain = 0;
        for (int i = 0;i<list.size();i++){
            if (trainPassingState(list.get(i))==1){
                if (trainCurrentState(list.get(i).arrivingTime,false)==0){
                    passedTrain++;
                }
            }else {
                if (trainCurrentState(list.get(i).leavingTime,false)==0){
                    passedTrain++;
                }
            }
        }
        return passedTrain;
    }

    public TrainModel getCurrentCheckTrain(){
        TrainModel model = new TrainModel();
        if (getPassedTrain("") < list.size()){
            model = list.get(getPassedTrain(""));
        }else {
            if (list.size()>0){
                model = list.get(0);
            }else {
                model.trainNum = "无";
                model.leavingTime = "无";
                model.station = "无";
            }
        }
        return model;
    }

    public List<TrainModel> removeToTheEndTrain_sortByTime(List<TrainModel> list){
        //去掉终到车
        List<TrainModel> tempModels = new ArrayList<TrainModel>();
        for (int i = 0;i<list.size();i++){
            TrainModel model = list.get(i);
            if (trainPassingState(model) != 1){
                tempModels.add(model);
            }
        }
        return sortByTime(tempModels);
    }

    public List<TrainModel> sortByTime(List<TrainModel> list){
        List<TrainModel> temp_sortByTimeModels = new ArrayList<>();
        List<TrainModel> final_sortByTimeModels = new ArrayList<>();
        for (TrainModel model:list) {
            String leavingTime;
            if (model.arrivingTime.split(":").length == 2){
                leavingTime = model.leavingTime.split(":")[0]+model.leavingTime.split(":")[1];
            }else {
                leavingTime = model.leavingTime;
            }

            TrainModel tempModel = new TrainModel();
            tempModel.trainNum = model.trainNum;
            tempModel.arrivingTime = model.arrivingTime;
            tempModel.leavingTime = leavingTime;
            tempModel.startDestination = model.startDestination;
            tempModel.station = model.station;
            temp_sortByTimeModels.add(tempModel);
        }
        Collections.sort(temp_sortByTimeModels);
        for (TrainModel model:temp_sortByTimeModels){
            String leavingTime;
            if (Integer.parseInt(model.leavingTime)/100 < 10){
                leavingTime = "0"+Integer.parseInt(model.leavingTime)/100+":";
            }else {
                leavingTime = Integer.parseInt(model.leavingTime)/100+":";
            }

            if (Integer.parseInt(model.leavingTime)%100 < 10){
                leavingTime = leavingTime + "0" + Integer.parseInt(model.leavingTime)%100;
            }else {
                leavingTime += Integer.parseInt(model.leavingTime)%100;
            }
            TrainModel tempModel = new TrainModel();
            tempModel.trainNum = model.trainNum;
            tempModel.arrivingTime = model.arrivingTime;
            tempModel.leavingTime = leavingTime;
            tempModel.startDestination = model.startDestination;
            tempModel.station = model.station;
            final_sortByTimeModels.add(tempModel);
        }
        return final_sortByTimeModels;
    }

    public List<TrainModel> selectTargetStations(String selectedStation,List<TrainModel> list){
        //匹配一下选出的检票口…
        List<TrainModel> tempModel = new ArrayList<TrainModel>();
        String[] everyStations = selectedStation.split(" ");
        for (int i = 0;i<everyStations.length;i++){
            for (TrainModel model:list) {
                if (model!=null && model.station!=null){
                    if (model.station.contains(",")){
                        String[] tempStation = model.station.split(",");
                        if (tempStation[0].equals(everyStations[i])||
                                tempStation[1].equals(everyStations[i])){
                            tempModel.add(model);
                        }
                    }else {
                        if (model.station.equals(everyStations[i])){
                            tempModel.add(model);
                        }
                    }
                }
            }
        }
        List<TrainModel> modelWithoutDup = new ArrayList<TrainModel>(new HashSet<TrainModel>(tempModel));
        modelWithoutDup = sortByTime(modelWithoutDup);
        return modelWithoutDup;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(context, R.layout.select_item_ticket_check, null);
        //把header隐藏
        TextView tvHeader = (TextView) view.findViewById(R.id.tv_Header);
        LinearLayout layout = (LinearLayout)view.findViewById(R.id.ticketCheck_layout);
        tvHeader.setVisibility(View.GONE);
        TextView tv_FirstLine = (TextView) view.findViewById(R.id.tv_FirstLine);
        TextView tv_SecondLine = (TextView) view.findViewById(R.id.tv_SecondLine);
        TextView tv_Right = (TextView) view.findViewById(R.id.tv_Right);
        ImageView MainImage = (ImageView) view.findViewById(R.id.MainImage);
        //默认显示当前时间以后的车
        TrainModel model = new TrainModel();
        model = list.get(position);
        //判断发-到-通过->0，1，2
        int trainPassingState = 0;
        //判断当前状态 已开走 正在检票 停止检票
        int trainCurrentState;
        if (model.arrivingTime != null ||
                model.leavingTime != null){
            if (model.arrivingTime.length() != 0 ||
                    model.leavingTime.length() != 0) {
                trainPassingState = trainPassingState(model);
                //判断时间 0已开走或者已到达 1正在检票 2停止检票 其他为剩余时间
                if (trainPassingState == 0){
                    //始发
                    trainCurrentState = trainCurrentState(model.leavingTime,true);
                    MainImage.setBackgroundResource(R.drawable.ic_originating);
                    if (trainCurrentState == 0){
                        tv_Right.setText("已发车");
                        tv_Right.setTextColor(this.context.getResources().getColor(R.color.colorOriginating));
                        layout.setBackgroundColor(this.context.getResources().getColor(R.color.Grey));
                    }else if (trainCurrentState == 1){
                        tv_Right.setText("正在检票");
                        tv_Right.setTextColor(this.context.getResources().getColor(R.color.colorPassing));
                    }else if (trainCurrentState == 2){
                        tv_Right.setText("停止检票");
                        tv_Right.setTextColor(this.context.getResources().getColor(R.color.colorToTheEnd));
                    }else {
                        String trainCurrentStateString;
                        String minutes;
                        if (trainCurrentState%100 <10){
                            minutes = "0"+trainCurrentState%100;
                        }else {
                            minutes = ""+trainCurrentState%100;
                        }
                        trainCurrentStateString = trainCurrentState/100 +":"+ minutes;
                        tv_Right.setText(trainCurrentStateString + "后开车");
                    }
                }else if (trainPassingState == 1){
                    //终到
                    trainCurrentState = trainCurrentState(model.arrivingTime,false);
                    MainImage.setBackgroundResource(R.drawable.ic_totheend);
                    if (trainCurrentState == 0){
                        tv_Right.setText("已到达");
                        tv_Right.setTextColor(this.context.getResources().getColor(R.color.colorOriginating));
                        layout.setBackgroundColor(this.context.getResources().getColor(R.color.Grey));
                    } else if (trainCurrentState == 1 ||
                            trainCurrentState == 2 ){
                        tv_Right.setText("即将终到");
                        tv_Right.setTextColor(this.context.getResources().getColor(R.color.colorToTheEnd));
                    }else {
                        String trainCurrentStateString;
                        String minutes;
                        if (trainCurrentState%100 <10){
                            minutes = "0"+trainCurrentState%100;
                        }else {
                            minutes = ""+trainCurrentState%100;
                        }
                        trainCurrentStateString = trainCurrentState/100 +":"+ minutes;
                        tv_Right.setText(trainCurrentStateString + "后终到");
                    }
                }else {
                    //路过
                    trainCurrentState = trainCurrentState(model.leavingTime,false);
                    MainImage.setBackgroundResource(R.drawable.ic_passing);
                    if (trainCurrentState == 0){
                        tv_Right.setText("已发车");
                        tv_Right.setTextColor(this.context.getResources().getColor(R.color.colorOriginating));
                        layout.setBackgroundColor(this.context.getResources().getColor(R.color.Grey));
                    }else if (trainCurrentState == 1){
                        tv_Right.setText("正在检票");
                        tv_Right.setTextColor(this.context.getResources().getColor(R.color.colorPassing));
                    }else if (trainCurrentState == 2){
                        tv_Right.setText("停止检票");
                        tv_Right.setTextColor(this.context.getResources().getColor(R.color.colorToTheEnd));
                    }else {
                        String trainCurrentStateString;
                        String minutes;
                        if (trainCurrentState%100 <10){
                            minutes = "0"+trainCurrentState%100;
                        }else {
                            minutes = ""+trainCurrentState%100;
                        }
                        trainCurrentStateString = trainCurrentState/100 +":"+ minutes;
                        tv_Right.setText(trainCurrentStateString + "后开车");
                    }
                }

            }
        }
        tv_FirstLine.setText(model.trainNum+" "+model.startDestination);
        if (trainPassingState == 2){
            tv_SecondLine.setText(model.arrivingTime+"到,"+model.leavingTime+"发");
        }else if (trainPassingState == 0){
            tv_SecondLine.setText(model.leavingTime+"发");
        }else{
            tv_SecondLine.setText(model.arrivingTime+"到");
        }

        return view;

    }

    private int trainPassingState(TrainModel model) {
        //0始发，1终到，2通过
        if (model.arrivingTime.substring(0,1).equals("-")){
            return 0;
        }else if (model.leavingTime.substring(0,1).equals("-")){
            return 1;
        }else {
            return 2;
        }
    }

    private int trainCurrentState(String originTime,boolean isOrigin){
        //对比当前时间，默认显示未接发的列车
        //有以下返回值：0->已过开车时间，1->正在检票 2->停止检票 其他->距离开车剩余时间
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HHmm");//可以方便地修改日期格式
        int nowTime = Integer.parseInt(dateFormat.format(date));

        String leavingTimeString = originTime.split(":")[0]+originTime.split(":")[1];
        int leavingTime = Integer.parseInt(leavingTimeString);
        int timeRemains = 0;
        if(nowTime > leavingTime){
            //已过开车时间
            return 0;
        }else if(leavingTime%100 >= nowTime%100){
            timeRemains = leavingTime - nowTime;
        }else {
            //例:当前时间1850，开车时间2024，差值应为134（1小时34分钟）
            timeRemains = ((leavingTime-100)/100 - nowTime/100)*100 + 60-(nowTime%100 - leavingTime%100);
        }
//        if (!isOrigin){
        //去除始发车提前20分钟检票的代码
            if (timeRemains < 15 &&timeRemains > 3){
                //开始检票
                return 1;
            }else if (timeRemains <= 3){
                //停止检票
                return 2;
            }else {
                return timeRemains;
            }
//        }else {
//            if (timeRemains < 20 &&timeRemains > 3){
//                //开始检票
//                return 1;
//            }else if (timeRemains <= 3){
//                //停止检票
//                return 2;
//            }else {
//                return timeRemains;
//            }
//        }

    }

}
