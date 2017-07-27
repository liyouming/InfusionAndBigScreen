package com.fugao.bigscreen.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.Iterator;
import java.util.Stack;

/** app管理类
 * Created by li on 2015/9/18.
 */
public class AppManager {
    private static Stack<Activity> activityStack;
    private static AppManager instance;

    private AppManager() {
    }

    public static AppManager getInstance() {
        if(instance == null) {
            instance = new AppManager();
        }

        return instance;
    }

    public int getCount() {
        return activityStack.size();
    }

    public void addActivity(Activity activity) {
        if(activityStack == null) {
            activityStack = new Stack();
        }

        activityStack.add(activity);
    }

    public Activity getTopActivity() {
        if(activityStack == null) {
            throw new NullPointerException("Activity stack is Null");
        } else {
            return activityStack.isEmpty()?null:(Activity)activityStack.lastElement();
        }
    }

    public Activity getActivity(Class<?> cls) {
        Iterator var2 = activityStack.iterator();

        Activity aty;
        do {
            if(!var2.hasNext()) {
                return null;
            }

            aty = (Activity)var2.next();
        } while(!aty.getClass().equals(cls));

        return aty;
    }

    public Activity getActivity(String name) {
        Iterator var2 = activityStack.iterator();

        Activity ac;
        do {
            if(!var2.hasNext()) {
                return null;
            }

            ac = (Activity)var2.next();
        } while(!ac.getClass().getName().contains(name));

        return ac;
    }

    public Stack<Activity> getActivityStack() {
        return activityStack;
    }

    public void removeActivity() {
        this.finishActivity((Activity)activityStack.lastElement());
    }

    public void finishActivity(Activity activity) {
        if(activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }

    }

    public void finishActivity(Class<?> cls) {
        Iterator var2 = activityStack.iterator();

        while(var2.hasNext()) {
            Activity activity = (Activity)var2.next();
            if(activity.getClass().equals(cls)) {
                this.finishActivity(activity);
            }
        }

    }

    public void finishAllActivity() {
        int i = 0;

        for(int size = activityStack.size(); i < size; ++i) {
            if(null != activityStack.get(i)) {
                ((Activity)activityStack.get(i)).finish();
            }
        }

        activityStack.clear();
    }

    public void appExit(Context context) {
        try {
            this.finishAllActivity();
            ActivityManager e = (ActivityManager)context.getSystemService("activity");
            e.killBackgroundProcesses(context.getPackageName());
            System.exit(0);
        } catch (Exception var3) {
            ;
        }

    }
}
