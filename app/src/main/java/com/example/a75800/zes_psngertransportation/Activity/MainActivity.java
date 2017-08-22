package com.example.a75800.zes_psngertransportation.Activity;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.example.a75800.zes_psngertransportation.Adapter.TicketCheckAdapter;
import com.example.a75800.zes_psngertransportation.Adapter.TrainTimetableAdapter;
import com.example.a75800.zes_psngertransportation.Model.TrainModel;
import com.example.a75800.zes_psngertransportation.R;
import com.example.a75800.zes_psngertransportation.Util.LocalStore;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //创建一个adapter会返回该activity三个fragment中的任意一个主要section
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }


    //右上角小按钮
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    public static class PlaceholderFragment extends Fragment implements View.OnClickListener {
        /**
         * fragment类的下面这个参数表示这个fragment的section number
         * 使用方法：将下面这个参数作为key，每一个section传回的sectionNumber作为value传入bundle内，将bundle作为fragment的参数，后调用时，使用getArguments().getInt(ARG_SECTION_NUMBER)
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        final private List<TrainModel> trainData = new ArrayList<TrainModel>();
        private static View viewOfTicketCheck;
        private static View viewOfTrainTimetable;

        private TextView mtv_nextTrainNum;
        private TextView mtv_arrivingTime;
        private TextView mtv_station;
        private TextView mtv_chosenStation;

        private TextView mtv_passedTrainCount;
        private TextView mtv_trainRemains;

        private static ListView mlv_ticketCheck;
        private static ListView mlv_train_timetable;

        private int passedTrain_All = 0;
        private int passedTrain_WithoutToTheEndTrain = 0;

        private Button naviToNow;
        private Button bt_selectStation;

        private String[] stations = new String[]{"A1", "A2 A3", "B2 B3", "A4 A5", "B4 B5", "A6 A7", "B6 B7", "A8 A9", "B8 B9"
                , "A12 A13", "B12 B13", "A14 A15", "B14 B15", "A16 A17", "B16 B17", "A18 A19", "B18 B19", "A20 A21", "B20 B21"
                , "A22 A23","B22 B23", "A24 A25", "B24 B25", "A26 A27", "B26 B27", "A28 A29", "B28 B29", "A30 A31", "B30 B31", "B32"};
        private boolean[] selectedStations = new boolean[] { false, false, false, false, false,
                false, false, false, false,false,
                false, false, false, false,false,
                false, false, false, false,false,
                false, false, false, false,false,
                false, false, false, false,false};
        private String selectedStationsString = "";
        Timer timer = new Timer();

        //加载动画
        private RelativeLayout ani_ticketCheck;
        private RelativeLayout ani_trainTimeTable;

        public PlaceholderFragment() {
        }

        /**
         * 返回一个你给予Section编号的Fragment
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public void refreshTicketCheck(String selectedStation) {
            //刷新班计划内容
            ani_ticketCheck = (RelativeLayout)viewOfTicketCheck.findViewById(R.id.ani_ticketCheck);
            mtv_chosenStation = (TextView)viewOfTicketCheck.findViewById(R.id.tv_chosenStation);
            mlv_ticketCheck = (ListView)viewOfTicketCheck.findViewById(R.id.lv_ticketCheck);
            TicketCheckAdapter ticketCheckAdapter = new TicketCheckAdapter(getActivity(), trainData,selectedStation);
            mlv_ticketCheck.setDivider(null);
            mlv_ticketCheck.setAdapter(ticketCheckAdapter);
            //检票班计划
            mtv_nextTrainNum = (TextView)viewOfTicketCheck.findViewById(R.id.tv_nextTrainNum);
            mtv_arrivingTime = (TextView)viewOfTicketCheck.findViewById(R.id.tv_leavingTime);
            mtv_station = (TextView)viewOfTicketCheck.findViewById(R.id.tv_station);
            TrainModel model = ticketCheckAdapter.getCurrentCheckTrain();
            mtv_nextTrainNum.setText(model.trainNum);
            mtv_arrivingTime.setText(model.leavingTime);
            mtv_station.setText(model.station);
            if (selectedStation.equals("")){
                mtv_chosenStation.setText("已选择的检票口：全部");
            }else {
                mtv_chosenStation.setText("已选择的检票口："+selectedStation);
            }

            bt_selectStation = (Button)viewOfTicketCheck.findViewById(R.id.bt_selectStation);
            bt_selectStation.setOnClickListener(this);
            passedTrain_WithoutToTheEndTrain = ticketCheckAdapter.getPassedTrain(selectedStation);
                if (passedTrain_WithoutToTheEndTrain <= ticketCheckAdapter.removeToTheEndTrain_sortByTime(trainData).size()){
                    mlv_ticketCheck.setSelection(passedTrain_WithoutToTheEndTrain);
                }
            ani_ticketCheck.setVisibility(View.GONE);
        }

        public void refreshTrainTime(boolean onlyRefreshTitle){
            //刷新时刻表内容
            ani_trainTimeTable = (RelativeLayout)viewOfTrainTimetable.findViewById(R.id.ani_trainTimeTable);
            //列表
            mlv_train_timetable = (ListView)viewOfTrainTimetable.findViewById(R.id.lv_train_timetable);
            TrainTimetableAdapter trainTimetableAdapter = new TrainTimetableAdapter(getActivity(),trainData);
            mlv_train_timetable.setDivider(null);
            //列车时刻表
            mtv_passedTrainCount = (TextView)viewOfTrainTimetable.findViewById(R.id.tv_alreadyPassingTrainCount);
            mtv_trainRemains = (TextView)viewOfTrainTimetable.findViewById(R.id.tv_dayLeft);
            passedTrain_All = trainTimetableAdapter.getPassedTrain();
            mtv_passedTrainCount.setText(passedTrain_All+"趟");
            mtv_trainRemains.setText((trainData.size()-passedTrain_All)+"趟");

            if (!onlyRefreshTitle){
                mlv_train_timetable.setAdapter(trainTimetableAdapter);
                mlv_train_timetable.setSelection(passedTrain_All);
            }

            naviToNow = (Button)viewOfTrainTimetable.findViewById(R.id.bt_naviToNow);
            naviToNow.setOnClickListener(this);
            TextView mtv_date = (TextView)viewOfTrainTimetable.findViewById(R.id.tv_date);
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");//可以方便地修改日期格式
            mtv_date.setText(dateFormat.format(date));
            ani_trainTimeTable.setVisibility(View.GONE);
        }

        //定时刷新数据
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        update();
                        break;
                }
                super.handleMessage(msg);
            }
            void update() {
                refreshTicketCheck(selectedStationsString);
                //刷新时刻表
                refreshTrainTime(true);
                Toast.makeText(getActivity(), "刷新完成", Toast.LENGTH_SHORT).show();
            }
        };
        TimerTask task = new TimerTask() {
            public void run() {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        };


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView;
            switch (getArguments().getInt(ARG_SECTION_NUMBER)){
                case 1:
                    rootView = inflater.inflate(R.layout.fragment_ticketcheck_daywork, container, false);
                    this.viewOfTicketCheck = rootView;
                    break;
                case 2:
                    rootView = inflater.inflate(R.layout.fragment_train_timetable,container,false);
                    this.viewOfTrainTimetable = rootView;
                    initData();
                    break;
                default:
                    rootView = inflater.inflate(R.layout.fragment_main,container,false);
                    break;
            }
            return rootView;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_naviToNow:
                    TrainTimetableAdapter trainTimetableAdapter = new TrainTimetableAdapter(getActivity(),trainData);
                    passedTrain_All = trainTimetableAdapter.getPassedTrain();
                    mtv_passedTrainCount.setText(passedTrain_All+"趟");
                    mtv_trainRemains.setText((trainData.size()-passedTrain_All)+"趟");
                    mlv_train_timetable.setSelection(passedTrain_All);
                    break;
                case R.id.bt_selectStation:
                    //选择检票口
                    this.showDialog("选择检票口", R.drawable.ic_passing, stations);
                    break;
            }
        }

        private void initData(){
            //从leanCloud获取数据
            AVQuery<AVObject> query = new AVQuery<>("Train");
            query.limit(500);
            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    //ArrayList<AVObject> allTrainFromDB = (ArrayList<AVObject>) list;
                    if (e == null){
                        for (AVObject trainFromDB : list) {
                            TrainModel trainModel = new TrainModel();
                            trainModel.trainNum = trainFromDB.getString("TrainNum");
                            trainModel.startDestination = trainFromDB.getString("Start_Destinition");
                            trainModel.arrivingTime = trainFromDB.getString("ArrivingTime");
                            trainModel.leavingTime = trainFromDB.getString("LeavingTime");
                            trainModel.station = trainFromDB.getString("Station");
                            trainData.add(trainModel);
                        }
                        //获取存储的检票口
                        selectedStations = LocalStore.getApkEnableArray(getActivity(),30);
                        selectedStationsString = transStationsToString();
                        refreshTicketCheck(selectedStationsString);
                        //刷新时刻表
                        refreshTrainTime(false);
                        timer.schedule(task, 1000 * 60, 1000 * 60); //启动timer
                    }else {
                        //String error = "错误" + e.toString().split("error")[1].replaceAll("\"", "").replaceAll("\\}", "");
                        Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }

        //复选框
        private void showDialog(String title, int RID, final String[] items)
        {
            AlertDialog builder = new AlertDialog.Builder(getActivity())
                    .setTitle(title)
                    .setIcon(RID)
                    .setMultiChoiceItems(items,selectedStations, new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    selectedStations[which] = isChecked;
                                }
                            })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            selectedStationsString = transStationsToString();
                            LocalStore.saveApkEnalbleArray(getActivity(),selectedStations);
                            refreshTicketCheck(selectedStationsString);
                        }
                    }).setNegativeButton("取消", null).create();
            builder.show();
        }

        //将之前选出的站转换为字符串
        private String transStationsToString(){
            String stationString = "";
            for (int counter = 0;counter<stations.length;counter++) {
                if (selectedStations[counter]){
                    stationString = stationString + stations[counter] + " ";
                }
            }
            return stationString;
        }


    }



    /**
     * FragmentPagerAdapter返回对应其中某一个section/pages/tabs的fragment
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "检票";
                case 1:
                    return "列车时刻";
            }
            return null;
        }
    }
}
