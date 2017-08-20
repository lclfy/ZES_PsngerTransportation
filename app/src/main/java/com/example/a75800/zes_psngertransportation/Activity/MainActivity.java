package com.example.a75800.zes_psngertransportation.Activity;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.example.a75800.zes_psngertransportation.Adapter.TicketCheckAdapter;
import com.example.a75800.zes_psngertransportation.Adapter.TrainTimetableAdapter;
import com.example.a75800.zes_psngertransportation.Model.TrainModel;
import com.example.a75800.zes_psngertransportation.R;

import java.util.ArrayList;
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

    public static class PlaceholderFragment extends Fragment {
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
        private static ListView mlv_ticketCheck;
        private static ListView mlv_train_timetable;

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
            //检票班计划
            mtv_nextTrainNum = (TextView)viewOfTicketCheck.findViewById(R.id.tv_nextTrainNum);
            mtv_arrivingTime = (TextView)viewOfTicketCheck.findViewById(R.id.tv_arrivingTime);
            mtv_station = (TextView)viewOfTicketCheck.findViewById(R.id.tv_station);

            mtv_nextTrainNum.setText(trainData.get(0).trainNum);
            mtv_arrivingTime.setText(trainData.get(0).arrivingTime);
            mtv_station.setText(trainData.get(0).station);
            //两个列表
            TicketCheckAdapter ticketCheckAdapter = new TicketCheckAdapter(getActivity(), trainData);
            TrainTimetableAdapter trainTimetableAdapter = new TrainTimetableAdapter(getActivity(),trainData);
            mlv_ticketCheck.setDivider(null);
            mlv_train_timetable.setDivider(null);
            mlv_ticketCheck.setAdapter(ticketCheckAdapter);
            mlv_train_timetable.setAdapter(trainTimetableAdapter);

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView;
            switch (getArguments().getInt(ARG_SECTION_NUMBER)){
                case 1:
                    rootView = inflater.inflate(R.layout.fragment_ticketcheck_daywork, container, false);
                    this.viewOfTicketCheck = rootView;
                    mlv_ticketCheck = (ListView)rootView.findViewById(R.id.lv_ticketCheck);
                    break;
                case 2:
                    rootView = inflater.inflate(R.layout.fragment_train_timetable,container,false);
                    this.viewOfTrainTimetable = rootView;
                    mlv_train_timetable = (ListView)rootView.findViewById(R.id.lv_train_timetable);
                    initData();
                    break;
                default:
                    rootView = inflater.inflate(R.layout.fragment_main,container,false);
                    break;
            }
            return rootView;
        }

        private void initData(){
            //从leanCloud获取数据
            AVQuery<AVObject> query = new AVQuery<>("Train");
            query.limit(500);
            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    //ArrayList<AVObject> allTrainFromDB = (ArrayList<AVObject>) list;
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
