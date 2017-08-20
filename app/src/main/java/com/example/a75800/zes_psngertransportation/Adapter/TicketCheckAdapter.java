package com.example.a75800.zes_psngertransportation.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a75800.zes_psngertransportation.Model.TrainModel;
import com.example.a75800.zes_psngertransportation.R;

import java.util.List;

/**
 * Created by 75800 on 2017/8/20.
 */

public class TicketCheckAdapter extends BaseAdapter {

    private Context context;
    private List<TrainModel> list;

    public TicketCheckAdapter(Context context, List<TrainModel> list) {
        super();
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        } else {
            return list.size();
        }
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
        tvHeader.setVisibility(View.GONE);

        TextView tv_FirstLine = (TextView) view.findViewById(R.id.tv_FirstLine);
        TextView tv_SecondLine = (TextView) view.findViewById(R.id.tv_SecondLine);
        TextView tv_Right = (TextView) view.findViewById(R.id.tv_Right);
        ImageView MainImage = (ImageView) view.findViewById(R.id.MainImage);

        TrainModel model = list.get(position);
        //设置第二行是否显示到/发
        int trainState = 0;
        if (model.arrivingTime.substring(0,1).equals("-")){
            MainImage.setBackgroundResource(R.drawable.ic_originating);
            trainState = 1;
        }else if (model.leavingTime.substring(0,1).equals("-")){
            MainImage.setBackgroundResource(R.drawable.ic_totheend);
            trainState = 2;
        }else {
            MainImage.setBackgroundResource(R.drawable.ic_passing);
        }
        tv_FirstLine.setText(model.trainNum+" "+model.startDestination);
        if (trainState == 0){
            tv_SecondLine.setText(model.arrivingTime+"到,"+model.leavingTime+"发");
        }else if (trainState == 1){
            tv_SecondLine.setText(model.leavingTime+"发");
        }else{
            tv_SecondLine.setText(model.arrivingTime+"到");
        }
        tv_Right.setText(model.station);

        return view;
    }
}
