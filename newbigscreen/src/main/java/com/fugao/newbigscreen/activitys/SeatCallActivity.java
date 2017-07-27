package com.fugao.newbigscreen.activitys;

import android.widget.TextView;

import com.fugao.newbigscreen.R;
import com.fugao.newbigscreen.adapter.SeatAdapter;
import com.fugao.newbigscreen.adapter.SeatGridviewAdapter;
import com.fugao.newbigscreen.utils.StandardGridView;
import com.jasonchen.base.utils.DateUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/** 座位呼叫
 * Created by li on 2016/6/22.
 */
public class SeatCallActivity extends BaseTempleActivity{
    private TextView seat_nowTime;
    private TextView seatcall_seat;
    private StandardGridView seatcall_gridview;
    private StandardGridView seatcall_gridview2;
    private SeatGridviewAdapter seatGridviewAdapter;
    private SeatAdapter seatAdapter;
    private String currentDate;
    private String currentWeek;
    private String stringtext;
    private List<String> seats;
    private List<String> seatStrings;
    private String[] seat_strings;
    @Override
    public void setContentView() {
        setContentView(R.layout.seatcall_layout);
    }

    @Override
    public void initView() {
        seat_nowTime= (TextView) findViewById(R.id.seat_nowTime);
        seatcall_seat= (TextView) findViewById(R.id.seatcall_seat);
        seatcall_gridview= (StandardGridView) findViewById(R.id.seatcall_gridview);
        seatcall_gridview2= (StandardGridView) findViewById(R.id.seatcall_gridview2);
    }

    @Override
    public void initData() {
        showDate();
        seat_strings=getResources().getStringArray(R.array.seats_string);
        seatStrings= Arrays.asList(seat_strings);
        seats=new ArrayList<>();
        for(int i=1;i<55;i++){
            String seat;
            if(i<10){
                seat="00"+i+"座";
            }else{
                seat="0"+i+"座";
            }
            seats.add(seat);
        }
//        seatStrings=new ArrayList<>();
        seatGridviewAdapter=new SeatGridviewAdapter(SeatCallActivity.this,seatStrings);
        seatcall_gridview.setAdapter(seatGridviewAdapter);
        seatAdapter=new SeatAdapter(SeatCallActivity.this,seats);
        seatcall_gridview2.setAdapter(seatAdapter);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initIntent() {

    }
    /**
     * 显示日期
     */
    private void showDate(){
        currentDate= DateUtils.getCurrentDate("yyyyMMdd");
        currentWeek=getCurrentWeek();
        stringtext=currentDate+"  "+currentWeek;
        seat_nowTime.setText(stringtext);
    }
    /**
     * 得到当前星期几
     * @return
     */
    private String getCurrentWeek(){
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }
}
