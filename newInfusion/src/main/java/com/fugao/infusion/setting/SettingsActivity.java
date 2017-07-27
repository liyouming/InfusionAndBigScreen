/*
 * Copyright (C) 2014 loQua.Xee <loquaciouser@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fugao.infusion.setting;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import com.fugao.infusion.R;
import com.fugao.infusion.bluetooth.bluetooth.ChooseBluetoothDeviceActivity;
import com.fugao.infusion.utils.XmlDB;
import com.jasonchen.base.utils.ChangeLogDialog;
import com.jasonchen.base.utils.RestClient;

/**
 *  TODO 设置界面
 */
public class SettingsActivity extends PreferenceActivity {
    private static final boolean ALWAYS_SIMPLE_PREFS = false;
    private static String address = "BE:03:00:1F:00:17";//蓝牙打印机地址
    private Preference confirmErrorSetting;
    private String comfirmErrorMessage;
    private Preference newVersion;
    private Preference aboutSoftware;
    private Preference connectionBluetooth;
    private Preference chooseBluetoothMode;
    private Preference isUseEmptySeat;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initActionBar();
        setupSimplePreferencesScreen();
    }

    /**
     * 设置ActionBar
     */
    private void initActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setTitle("设置");
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.ab_solid_custom_blue_inverse_holo));
//        actionBar.setHomeButtonEnabled(true);
    }

    @SuppressWarnings("deprecation")
    private void setupSimplePreferencesScreen() {
        addPreferencesFromResource(R.xml.pref_general);
        innitPreference();
    }

    /**
     * 找到控件并进行监听
     */
    private void innitPreference() {
        bindPreferenceSummaryToValue(findPreference("ip"));
        bindPreferenceSummaryToValue(findPreference("port"));
        bindPreferenceSummaryToValue(findPreference("syncTimes"));
        bindPreferenceSummaryToValue(findPreference("deptID"));
        newVersion = findPreference("newVersion");
        aboutSoftware = findPreference("aboutSoftware");
        confirmErrorSetting = findPreference("confrimErrorSetting");
        connectionBluetooth = findPreference("connectionBluetooth");
        chooseBluetoothMode = findPreference("chooseBluetoothMode");
        isUseEmptySeat = findPreference("isUseEmptySeat");


        /**
         * 连接蓝牙打印机
         */
        connectionBluetooth.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent();
                intent.setClass(SettingsActivity.this, ChooseBluetoothDeviceActivity.class);
                startActivity(intent);
                return false;
            }
        });

        /**
         * 关于软件
         */
        aboutSoftware.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(final Preference preference) {
                ChangeLogDialog _ChangelogDialog =
                        new ChangeLogDialog(SettingsActivity.this, R.xml.changelog, R.string.app_name,
                                R.string.alert_close);
                _ChangelogDialog.show();
                return false;
            }
        });
        /**
         * 新版本
         */
        newVersion.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                return false;
            }
        });

        /**
         * 核对错误提醒设置
         */
        confirmErrorSetting.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent();
                intent.setClass(SettingsActivity.this,ScanConfirmSettingActivity.class);
                startActivity(intent);
                return false;
            }
        });

        /**
         * 选择打印模式
         */
        chooseBluetoothMode.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent();
                intent.setClass(SettingsActivity.this,PrintModeSettingActivity.class);
                startActivity(intent);
                return false;
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    private static boolean isSimplePreferences(Context context) {
        return ALWAYS_SIMPLE_PREFS
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                || !isXLargeTablet(context);
    }

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener =
            new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object value) {
                    String stringValue = value.toString();

                    if (preference instanceof EditTextPreference) {
                        preference.setSummary(stringValue);
                    }
                    return true;
                }
            };

    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), "")
        );

        /**
         * 设置点击每个文输入框弹出的dialog在点击外部时dialog不被取消
         */
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Dialog dialog = ((EditTextPreference) preference).getDialog();
                if (dialog != null) {
                    dialog.setCanceledOnTouchOutside(false);
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        comfirmErrorMessage = XmlDB.getInstance(SettingsActivity.this).getKeyString("userSetting", "打开震动和声音");
        confirmErrorSetting.setSummary(comfirmErrorMessage);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        String ip = XmlDB.getInstance(this).getKeyString("ip", "");
        String port = XmlDB.getInstance(this).getKeyString("port", "");
        RestClient.BASE_URL = "http://" + ip + ":"+port  + "/";
    }
}
