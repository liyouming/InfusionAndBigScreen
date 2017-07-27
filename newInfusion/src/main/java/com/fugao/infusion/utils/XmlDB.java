package com.fugao.infusion.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 存放本地配置的相关内容
 */
public class XmlDB {

  private Context context;
  private SharedPreferences mSharePrefs = null;
  private SharedPreferences.Editor editor;
  /**
   * Preferences Name that we use.
   */
  public static String Pref_Name = "com.fugao.infusion_preferences";

  /**
   * Holds the single instance that is shared by the process.
   */
  private static XmlDB sInstance;

  /**
   * Return the single SharedPreferences instance.
   */
  public static XmlDB getInstance(Context context) {
    if (sInstance == null) {
      sInstance = new XmlDB(context);
      sInstance.open();
    }
    return sInstance;
  }

  public XmlDB(Context context) {
    this.context = context;
  }

  /**
   * Get a sharePreference instance.
   */
  public void open() {
    mSharePrefs = context.getSharedPreferences(Pref_Name, 4);
  }

  public void close() {
    sInstance = null;
    mSharePrefs = null;
  }

  /**
   * Save the String-String key-values in sharePreference file.
   */
  public void saveKey(String mKey, String mValue) {
    if (mSharePrefs != null) {
      editor = mSharePrefs.edit();
      editor.putString(mKey, mValue);
      editor.commit();
    }
  }

  public String getKeyString(String mKey, String mDefValue) {
    String mStr = null;
    if (mSharePrefs != null) {
      mStr = mSharePrefs.getString(mKey, mDefValue);
    }
    return mStr;
  }

  public String getKeyStringValue(String mKey, String mDefValue) {
    String mStr = null;
    if (mSharePrefs != null) {
      mStr = mSharePrefs.getString(mKey, mDefValue);
    }
    return mStr;
  }

  public int getKeyIntValue(String mKey, int mDefValue) {
    int mInt = 0;
    if (mSharePrefs != null) {
      mInt = mSharePrefs.getInt(mKey, mDefValue);
    }
    return mInt;
  }

  public boolean getKeyBooleanValue(String mKey, boolean mDefValue) {
    boolean mBool = false;
    if (mSharePrefs != null) {
      mBool = mSharePrefs.getBoolean(mKey, mDefValue);
    }
    return mBool;
  }

  public Float getKeyFloatValue(String mKey, int mDefValue) {
    Float mFloat = null;
    if (mSharePrefs != null) {
      mFloat = mSharePrefs.getFloat(mKey, mDefValue);
    }
    return mFloat;
  }

  public long getKeyLongValue(String mKey, long mDefValue) {
    long mFloat = 0L;
    if (mSharePrefs != null) {
      mFloat = mSharePrefs.getLong(mKey, mDefValue);
    }
    return mFloat;
  }

  /** 保存整型的键值对到配置文件当中. */
  public void saveKey(String mKey, int mValue) {
    if (mSharePrefs != null) {
      editor = mSharePrefs.edit();
      editor.putInt(mKey, mValue);
      editor.commit();
    }
  }

  /** 保存boolean类型的键值对到配置文件当中. */
  public void saveKey(String mKey, boolean mValue) {
    if (mSharePrefs != null) {
      editor = mSharePrefs.edit();
      editor.putBoolean(mKey, mValue);
      editor.commit();
    }
  }

  /** 保存float类型的键值对到配置文件当中. */
  public void saveKey(String mKey, Float mValue) {
    if (mSharePrefs != null) {
      editor = mSharePrefs.edit();
      editor.putFloat(mKey, mValue);
      editor.commit();
    }
  }

  /** 保存long类型的键值对到配置文件当中 */
  public void saveKey(String mKey, long mValue) {
    if (mSharePrefs != null) {
      editor = mSharePrefs.edit();
      editor.putLong(mKey, mValue);
      editor.commit();
    }
  }

  public void removeKey(String key) {
    if (mSharePrefs != null) {
      editor = mSharePrefs.edit();
      editor.remove(key);
      editor.commit();
    }
  }

  @Override
  protected void finalize() throws Throwable {
    sInstance.close();
    super.finalize();
  }

  /**
   * 清除 sf 文件的所有数据， 注意  在ondestory 方法中使用，不要再同一个页面中使用
   * 在同一个页面中不能销毁所有的数据
   *
   * @Title: clearALlData
   * @Description: TODO
   * @return: void
   */
  public void clearALlData() {
    if (mSharePrefs != null) {
      mSharePrefs.edit().clear().commit();
    }
  }
}