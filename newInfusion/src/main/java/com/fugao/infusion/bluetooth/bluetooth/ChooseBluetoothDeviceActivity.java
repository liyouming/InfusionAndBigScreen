package com.fugao.infusion.bluetooth.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.fugao.infusion.R;
import com.fugao.infusion.base.BaseTempleActivity;
import com.fugao.infusion.bluetooth.service.BlueToothService;
import com.fugao.infusion.bluetooth.service.BluetoothPrintService;
import com.fugao.infusion.constant.Constant;
import com.fugao.infusion.utils.XmlDB;
import com.jasonchen.base.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.InjectView;

/**
 *
 * @Prject: FastApps
 * @Location: com.android.fastinfusion.utils.bluetooths.bluetooth.chooseBluetoothDeviceActivityTest
 * @Description: TODO 获取蓝牙设备列表
 * @author: 胡乐    hule@fugao.com
 * @date: 2014/10/16 10:34
 * @version: V1.0
 */

public class ChooseBluetoothDeviceActivity extends BaseTempleActivity implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "Fugao-chooseBluetoothDeviceActivityTest";
    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    private ArrayAdapter<String> bondedArrayAdapter;
    private ListView bluetooth_device_list;
    private Set<BluetoothDevice> devices;
    private ArrayList<String> bondedAddress = new ArrayList<String>();
    private BlueToothService mbtService = null;
    private SimpleAdapter simpleAdapter = null;
    private List<Map<String, Object>> deviceList;
//    private FinishReceiver receiver;
    private TextView title_text_view;
    @InjectView(R.id.un_complete_refresh_layout)
    SwipeRefreshLayout mUnCompleteRefreshLayout;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_chose_bule_tooth_device);
    }

    @Override
    public void initView() {
        mUnCompleteRefreshLayout.setOnRefreshListener(this);
        mUnCompleteRefreshLayout.setColorScheme(android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light);
        Intent intent = new Intent();
        intent.setClass(ChooseBluetoothDeviceActivity.this, BluetoothPrintService.class);
        startService(intent);
        bluetooth_device_list = (ListView) findViewById(R.id.bluetooth_device_list);
        title_text_view = (TextView) findViewById(R.id.title_text_view);
        bluetooth_device_list.setOnItemClickListener(this);
        mbtService = new BlueToothService(context);
        deviceList = new ArrayList<Map<String, Object>>();

    }

    @Override
    public void initData() {
        simpleAdapter = new SimpleAdapter(context, deviceList, R.layout.activity_chose_bule_tooth_device_item,
                new String[]{"deviceName", "deviceAddress"}, new int[]{R.id.work_name, R.id.address});
        bluetooth_device_list.setAdapter(simpleAdapter);
        getBondedDevice();

    }

    @Override
    public void initListener() {
        title_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    public void initIntent() {

    }

    /**
     * 获得已经配对的蓝牙
     */
    public void getBondedDevice() {
        if (adapter != null) {
            if (!adapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //请求开启蓝牙设备
                startActivity(intent);
            }
            mUnCompleteRefreshLayout.setRefreshing(true);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (adapter.getBondedDevices().size() > 0) {
                        for (BluetoothDevice bluetoothDevice : adapter.getBondedDevices()) {
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("deviceName", bluetoothDevice.getName());
                            map.put("deviceAddress", bluetoothDevice.getAddress());
                            deviceList.add(map);
                            bondedAddress.add(bluetoothDevice.getAddress());
                            simpleAdapter.notifyDataSetChanged();
                        }
                    }
                }
            },2000);
            mUnCompleteRefreshLayout.setRefreshing(false);
        } else {
            Toast.makeText(this, "当前无蓝牙设备", Toast.LENGTH_SHORT).show();
        }
    }

    //        bondedArrayAdapter = new ArrayAdapter<String>(this,
    //                R.layout.activity_chose_bule_tooth_device_item);
    //        if(adapter!=null){
    //            if(!adapter.isEnabled()){
    //                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    //                //请求开启蓝牙设备
    //                startActivity(intent);
    //            }
    //            //获得已配对的远程蓝牙设备的集合
    //            devices = adapter.getBondedDevices();
    //            if (devices.size() > 0) {
    //
    //                for (BluetoothDevice device : devices) {
    //                    bondedArrayAdapter.add(device.getName() + "\n"
    //                            + device.getAddress());
    //                    bondedAddress.add(device.getAddress());
    //                }
    //                bluetooth_device_list.setAdapter(bondedArrayAdapter);
    //            }
    //
    //        }else{
    //            Toast.makeText(this, "当前无蓝牙设备", Toast.LENGTH_SHORT).show();
    //        }
    //    }

    @Override
    public void onItemClick(AdapterView<?> parent   , View view, int position, long id) {
        String adviceAddress = bondedAddress.get(position);
        Constant.BlueToothAddress = StringUtils.getString(adviceAddress);
        XmlDB.getInstance(ChooseBluetoothDeviceActivity.this).saveKey("blutoothAddress", adviceAddress);
//        InfoUtils.checkBlueTooth(context);
        sendCmd(BluetoothUtils.CMD_CONNTECT_BLUETOOTH, "", adviceAddress);
    }

    public void sendCmd(int command, String value, String address) {
        Intent intent = new Intent();//创建Intent对象
        intent.setAction("android.intent.action.cmd");
        intent.putExtra("command", command);
        intent.putExtra("value", value);
        intent.putExtra("address", address);
        sendBroadcast(intent);//发送广播
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.bluetooth_refresh, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_refresh:
//                scanBlueToothDevice();
//                break;
//            case android.R.id.home:
//                finish();
//                break;
//            default:
//                break;
//        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 扫描蓝牙设备
     */
    private void scanBlueToothDevice() {

        mUnCompleteRefreshLayout.setRefreshing(true);
        deviceList.clear(); //刷新前先清除列表
        for (BluetoothDevice bluetoothDevice : adapter.getBondedDevices()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("deviceName", bluetoothDevice.getName());
            map.put("deviceAddress", bluetoothDevice.getAddress());
            deviceList.add(map);
            bondedAddress.add(bluetoothDevice.getAddress());
        }
        if(simpleAdapter !=null)simpleAdapter.notifyDataSetChanged();
        mUnCompleteRefreshLayout.setRefreshing(false);

    }

    @Override
    public void onRefresh() {
        scanBlueToothDevice();
    }

    //    /**
//     * 当连接蓝牙成功结束当前Activity
//     */
//    private  class  FinishReceiver extends BroadcastReceiver{
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if(intent.getAction().equals("android.intent.action.finish")){
//                ChooseBluetoothDeviceActivity.this.finish();
//            }
//        }
//    }
//
}
