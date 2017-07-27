package com.fugao.infusion.seat;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fugao.infusion.R;
import com.fugao.infusion.base.BaseFragmentV4;

import butterknife.InjectView;

/**
 * A simple {@link android.app.Fragment} subclass.
 */
public class SeatFragment extends BaseFragmentV4 {

    @InjectView(R.id.categroy_num)
    TextView mCategroyNum;
    private String category;

    static SeatFragment newInstance(String category) {
        SeatFragment f = new SeatFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("category", category);
        f.setArguments(args);

        return f;
    }

    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        category = getArguments().getString("category");
        return inflater.inflate(R.layout.fragment_seat, container, false);
    }

    @Override
    public void initView(View currentView) {

    }

    @Override
    public void initData() {
        mCategroyNum.setText(category);

    }

    @Override
    public void initListener() {

    }

    /**
     * 刷新数据
     */
    public void getData() {
        //   setRefreshing(true);
//        InfoApi.getEmptySeats(fatherActivity, catalog, new RestListener<List<String>>() {
//            @Override
//            public void onSuccess(List<String> emptySeatAll) {
//                setRefreshing(true);
//                emptySeats.clear();
//                emptySeats.addAll(emptySeatAll);
//                areaEmptySeatAdapter.notifyDataSetChanged();
//                setRefreshing(false);
//            }
//
//            @Override
//            public void onFailed(AppException error, String msg) {
//                setRefreshing(false);
//                error.makeToast(fatherActivity);
//            }
//        });
    }
}
