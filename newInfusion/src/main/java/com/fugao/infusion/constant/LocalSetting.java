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

package com.fugao.infusion.constant;


import com.fugao.infusion.model.BottleModel;
import com.fugao.infusion.model.GroupBottleModel;
import com.fugao.infusion.model.NurseAccountModel;


public class LocalSetting {
    public static String DepartmentID = "100001";
    public static int RoleIndex = 0;
    public static int CallMaxCount = 3;
    public static NurseAccountModel CurrentAccount = new NurseAccountModel();
    public static NurseAccountModel CurrentCheck = new NurseAccountModel();
    public static boolean BatchScanPaiyao = true;
    public static boolean BatchScanPeiye = false;
    public static boolean AutoEndOthers = true;
    public static boolean CanModifytheSpeed = false;
    public static boolean AutoAllotSeat = false;
    public static BottleModel CurrentBottle = null;
    public static GroupBottleModel CurrentGroupBottle = null;
    public static boolean IsOpenByScan = true;
    public static int MainResult = 999;
    public static String[] Fq = {"1", "2"};
    public static String divZone = "一输液区";
    public static int TodoCount = 0;
    public static int DoingCount = 0;
    public static int type = 1;
    public static int bottletotle=0;
}
