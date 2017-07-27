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
package com.fugao.infusion.comonPage;

import com.fugao.infusion.R;
import com.fugao.infusion.base.BaseFragmentV4;
import com.fugao.infusion.chuaici.ChuanCiPeopleFragment;
import com.fugao.infusion.paiyao.PaiYaoPeopleFragment;
import com.fugao.infusion.peiye.NewPeiYePeopleFragment;
import com.fugao.infusion.peiye.PeiYePeopleFragment;
import com.fugao.infusion.peiye.XinhuaPeiyeFragment;
import com.fugao.infusion.setting.SettingFragment;
import com.fugao.infusion.xinhua.XHXunshiPeopleFragment;
import com.fugao.infusion.xinhua.XinHuaChuanCiPeopleFragment;
import com.fugao.infusion.xunshi.ArroundFragment;
import com.fugao.infusion.xunshi.XunshiPeopleFragment;
import com.fugao.infusion.zhongji.ZhongJIArroundFragment;
import com.fugao.infusion.zhongji.ZhongJiChuanCiPeopleFragment;
import com.fugao.infusion.zhongji.ZhongJiXunshiPeopleFragment;

/**
 * Do one thing at a time, and do well!
 *
 * @Prject: FastAndroid
 * @Location: com.android.fastinfusion.ui.activity.MainActivity
 * @Description: TODO
 * @Author: LoQua.Xee    loquaciouser@gmail.com
 * @Create-date: 2014/10/9 13:39
 * @Modify-date: 修改日期
 * @Modify-description: TODO 修改说明
 * @Modify-author: 修改人
 * @version: V1.0
 */
public class TabHostFactory {

    public static BaseFragmentV4 getFragmentByPaiYao(String tabName) {
        BaseFragmentV4 fragment = null;
        if ("列表".equals(tabName)) {
            fragment = new PaiYaoPeopleFragment();
        }else if ("统计".equals(tabName)) {
            fragment = new WorkloadFragment();
        }else if ("更多".equals(tabName)) {
            fragment = new SettingFragment();
        }
        return fragment;
    }

    public static int getTabIconByPaiYao(String tabName) {
        int tabIcon = 0;
        if ("列表".equals(tabName)) {
            tabIcon = R.drawable.tab_index_selector;
        } else if ("统计".equals(tabName)) {
            tabIcon = R.drawable.tab_signs_selector;
        } else if ("更多".equals(tabName)) {
            tabIcon = R.drawable.tab_more_selector;
        }
        return tabIcon;
    }
    public static BaseFragmentV4 getFragmentByPeiYe(String tabName) {
        BaseFragmentV4 fragment = null;
        if ("列表".equals(tabName)) {
//            fragment = new PeiYePeopleFragment();
//            fragment=new NewPeiYePeopleFragment();
            fragment=new XinhuaPeiyeFragment();
        }else if ("统计".equals(tabName)) {
            fragment = new WorkloadFragment();
        }else if ("更多".equals(tabName)) {
            fragment = new SettingFragment();
        }
        return fragment;
    }

    public static int getTabIconByPeiYe(String tabName) {
        int tabIcon = 0;
        if ("列表".equals(tabName)) {
            tabIcon = R.drawable.tab_index_selector;
        } else if ("统计".equals(tabName)) {
            tabIcon = R.drawable.tab_signs_selector;
        } else if ("更多".equals(tabName)) {
            tabIcon = R.drawable.tab_more_selector;
        }
        return tabIcon;
    }
    public static BaseFragmentV4 getFragmentByChuanCi(String tabName) {
        BaseFragmentV4 fragment = null;
        if ("列表".equals(tabName)) {
            fragment = new XinHuaChuanCiPeopleFragment();
        }else if ("统计".equals(tabName)) {
            fragment = new WorkloadFragment();
        }else if ("更多".equals(tabName)) {
            fragment = new SettingFragment();
        }
        return fragment;
    }

    public static int getTabIconByChuanCi(String tabName) {
        int tabIcon = 0;
        if ("列表".equals(tabName)) {
            tabIcon = R.drawable.tab_index_selector;
        } else if ("统计".equals(tabName)) {
            tabIcon = R.drawable.tab_signs_selector;
        }  else if ("更多".equals(tabName)) {
            tabIcon = R.drawable.tab_more_selector;
        }
        return tabIcon;
    }
    public static BaseFragmentV4 getFragmentByXunShi(String tabName) {
        BaseFragmentV4 fragment = null;
        if ("列表".equals(tabName)) {
//            fragment = new XunshiPeopleFragment();
            fragment = new XHXunshiPeopleFragment();
        }else if ("统计".equals(tabName)) {
            fragment = new WorkloadFragment();
        }else if("座位".equals(tabName)){
            fragment = new RealeseSeatFragment();
        }else if ("巡视".equals(tabName)) {
            fragment = new ArroundFragment();
        }else if ("更多".equals(tabName)) {
            fragment = new SettingFragment();
        }
        return fragment;
    }

    public static int getTabIconByXunShi(String tabName) {
        int tabIcon = 0;
        if ("列表".equals(tabName)) {
            tabIcon = R.drawable.tab_index_selector;
        }else if("座位".equals(tabName)){
            tabIcon = R.drawable.tab_tools_selector;
        }  else if ("巡视".equals(tabName)) {
            tabIcon = R.drawable.tab_icon_people_list_selector;
        } else if ("统计".equals(tabName)) {
            tabIcon = R.drawable.tab_signs_selector;
        }else if ("更多".equals(tabName)) {
            tabIcon = R.drawable.tab_more_selector;
        }
        return tabIcon;
    }

    public static BaseFragmentV4 getFragmentByZhongJiXunShi(String tabName) {
        BaseFragmentV4 fragment = null;
        if ("输液巡视".equals(tabName)) {
            fragment = new ZhongJiXunshiPeopleFragment();
        }else if ("穿刺".equals(tabName)) {
            fragment = new ZhongJiChuanCiPeopleFragment();
        }else if("座位".equals(tabName)){
            fragment = new RealeseSeatFragment();
        }else if ("巡视".equals(tabName)) {
            fragment = new ZhongJIArroundFragment();
        }else if ("更多".equals(tabName)) {
            fragment = new SettingFragment();
        }
        return fragment;
    }

    public static int getTabIconByZhongJiXunShi(String tabName) {
        int tabIcon = 0;
        if ("输液巡视".equals(tabName)) {
            tabIcon = R.drawable.tab_index_selector;
        }else if("座位".equals(tabName)){
            tabIcon = R.drawable.tab_tools_selector;
        }  else if ("巡视".equals(tabName)) {
            tabIcon = R.drawable.tab_icon_people_list_selector;
        } else if ("穿刺".equals(tabName)) {
            tabIcon = R.drawable.tab_signs_selector;
        }else if ("更多".equals(tabName)) {
            tabIcon = R.drawable.tab_more_selector;
        }
        return tabIcon;
    }
}
