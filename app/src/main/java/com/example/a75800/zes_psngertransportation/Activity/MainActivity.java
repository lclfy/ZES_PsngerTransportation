package com.example.a75800.zes_psngertransportation.Activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
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

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

        public void refreshDataInView(){
            //两个列表
            mlv_ticketCheck = (ListView)viewOfTicketCheck.findViewById(R.id.lv_ticketCheck);
            mlv_train_timetable = (ListView)viewOfTrainTimetable.findViewById(R.id.lv_train_timetable);
            TicketCheckAdapter ticketCheckAdapter = new TicketCheckAdapter(getActivity(), trainData);
            TrainTimetableAdapter trainTimetableAdapter = new TrainTimetableAdapter(getActivity(),trainData);
            mlv_ticketCheck.setDivider(null);
            mlv_train_timetable.setDivider(null);
            mlv_ticketCheck.setAdapter(ticketCheckAdapter);
            mlv_train_timetable.setAdapter(trainTimetableAdapter);
            //检票班计划
            mtv_nextTrainNum = (TextView)viewOfTicketCheck.findViewById(R.id.tv_nextTrainNum);
            mtv_arrivingTime = (TextView)viewOfTicketCheck.findViewById(R.id.tv_leavingTime);
            mtv_station = (TextView)viewOfTicketCheck.findViewById(R.id.tv_station);
            TrainModel model = ticketCheckAdapter.getCurrentCheckTrain();
            mtv_nextTrainNum.setText(model.trainNum);
            mtv_arrivingTime.setText(model.leavingTime);
            mtv_station.setText(model.station);
            bt_selectStation = (Button)viewOfTicketCheck.findViewById(R.id.bt_selectStation);
            bt_selectStation.setOnClickListener(this);

            passedTrain_WithoutToTheEndTrain = ticketCheckAdapter.getPassedTrain();
            mlv_ticketCheck.setSelection(passedTrain_WithoutToTheEndTrain);
            //列车时刻表
            mtv_passedTrainCount = (TextView)viewOfTrainTimetable.findViewById(R.id.tv_alreadyPassingTrainCount);
            mtv_trainRemains = (TextView)viewOfTrainTimetable.findViewById(R.id.tv_dayLeft);
            passedTrain_All = trainTimetableAdapter.getPassedTrain();
            mtv_passedTrainCount.setText(passedTrain_All+"趟");
            mtv_trainRemains.setText((trainData.size()-passedTrain_All)+"趟");
            mlv_train_timetable.setSelection(passedTrain_All);
            naviToNow = (Button)viewOfTrainTimetable.findViewById(R.id.bt_naviToNow);
            naviToNow.setOnClickListener(this);
            TextView mtv_date = (TextView)viewOfTrainTimetable.findViewById(R.id.tv_date);
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");//可以方便地修改日期格式
            mtv_date.setText(dateFormat.format(date));
//            mlv_train_timetable.smoothScrollToPositionFromTop(passedTrain, 0);

//            this.setListViewHeightBasedOnChildren(mlv_train_timetable,passedTrain);
        }

//        public void setListViewHeightBasedOnChildren(ListView listView,int itemCount) {
//            //获取listview的适配器
//            ListAdapter listAdapter = mlv_train_timetable.getAdapter(); //item的高度
//            if (listAdapter == null) {
//                return;
//            }
//            int totalHeight = 0;
//            View listItem = mlv_train_timetable.getAdapter().getView(1, null, listView);
//            listItem.measure(0, 0); //计算子项View 的宽高 //统计所有子项的总高度
//            totalHeight = itemCount*(listItem.getMeasuredHeight()+listView.getDividerHeight());
//            ViewGroup.LayoutParams params = listView.getLayoutParams();
//            params.height = totalHeight;
//            listView.setLayoutParams(params);
//        }

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
//                    mlv_train_timetable.smoothScrollToPosition(passedTrain, 3);
                    mlv_train_timetable.setSelection(passedTrain_All);
                    break;
                case R.id.bt_selectStation:
                    //选择检票口
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
                        refreshDataInView();
                    }else {
                        //String error = "错误" + e.toString().split("error")[1].replaceAll("\"", "").replaceAll("\\}", "");
                        Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show();
                    }

                }
            });

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
