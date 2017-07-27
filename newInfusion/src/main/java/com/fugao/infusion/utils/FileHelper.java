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

package com.fugao.infusion.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.jasonchen.base.utils.SDCardTools;
import com.jasonchen.base.utils.StringUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 文件操作相关
 *
 * @author chenliang
 * @date 2012-11-30
 */
public class FileHelper {

  public static String SDCARD_PATH = SDCardTools.getSDPath();
  public static String APP_NAME = "";
  /**
   * mnt/sdcard/fugleapp/mobile_infusion
   */
  public static String appSDPath = SDCARD_PATH + "/" + "fugaoapps" + "/" +"mobile_infusion"+ "/" + APP_NAME;

  /**
   * 设置项目的sdcard 根目录
   *
   * @Title: setAppSDPath
   * @Description: TODO
   * @return: String
   */
  public static String setAppSDPath(String rootName) {
    appSDPath += rootName;
    return appSDPath;
  }

  public static void creatAPPSROOT(String dirName) {
    File appSDPathFile = new File(appSDPath);
    if (!appSDPathFile.exists()) {
      appSDPathFile.mkdirs();
    }
    File destFileDir = new File(appSDPathFile + "/" + dirName);
    if (!destFileDir.exists()) {
      destFileDir.mkdir();
    }
  }

  /**
   * 创建sd卡的目录
   */
  public static File creatSDDir(String dir) {
    File appSDPathFile = new File(appSDPath);
    if (!appSDPathFile.exists()) {
      appSDPathFile.mkdirs();
    }
    File destFileDir = new File(appSDPathFile + "/" + dir);
    if (!destFileDir.exists()) {
      destFileDir.mkdirs();
    }

    return destFileDir;
  }

  /**
   * 把string 保存在文件中
   *
   * @param filename 文件名称
   * @param path 文件路径
   * @param content 要写的内容
   * @throws java.io.IOException
   */
  public static void saveString2File(String path, String filename, String content) {
    File filePath = new File(appSDPath + "/" + path);

    if (!filePath.exists()) {
      creatSDDir(path);
    }
    File fileAbsolutely = new File(filePath + "/" + filename);
    if (!fileAbsolutely.exists()) {
      try {
        fileAbsolutely.createNewFile();

        FileOutputStream fos = new FileOutputStream(fileAbsolutely);

        fos.write(content.getBytes());
        fos.flush();
        fos.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } else {
      fileAbsolutely.delete();
      try {
        fileAbsolutely.createNewFile();

        FileOutputStream fos = new FileOutputStream(fileAbsolutely);

        fos.write(content.getBytes());
        fos.flush();
        fos.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  /**
   * 删除一个目录下的所有文件
   */
  public static void deleteDirFile(String dirPath) {
    File dirFile = new File(appSDPath + "/" + dirPath);
    if (dirFile.exists()) {
      File[] files = dirFile.listFiles();
      for (int i = 0; i < files.length; i++) {
        File f = files[i];
        f.delete();
      }
    }
  }

  /**
   * 删除文件
   *
   * @param filePath 文件路径(不包含根文件夹路径)
   */
  public static void deleteFile(String filePath) {
    if (!StringUtils.StringIsEmpty(filePath)) {
      String path = appSDPath + "/" + filePath;
      File file = new File(path);
      if (file.exists()) {
        file.delete();
      }
    }
  }

  /**
   * 判断文件是否存在
   */
  public static boolean fileIsExists(String filename, String path) {

    File dirFile = new File(appSDPath + "/" + path);
    if (dirFile.exists()) {
      File[] files = dirFile.listFiles();
      boolean isExistflag = false;
      for (int i = 0; i < files.length && !isExistflag; i++) {
        File f = files[i];

        if (filename.equals(f.getName())) {
          isExistflag = true;
          break;
        }
      }
      if (isExistflag) {
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  /**
   * 判断文件是否存在
   *
   * @param filePath 文件路径(不包含根文件夹路径)
   * @return true 存在; false 不存在
   */
  public static boolean fileIsExists(String filePath) {
    boolean isExistflag = false;
    if (!StringUtils.StringIsEmpty(filePath)) {
      String path = appSDPath + "/" + filePath;
      File readFile = new File(path);
      isExistflag = readFile.exists();
    }
    return isExistflag;
  }

  /**
   * 读出指定位置的文件的内容
   *
   * @param filename 文件名称
   * @param path 路径
   */
  public static String readString4File(String filename, String path) {
    String temp = "";
    FileInputStream fis = null;
    InputStreamReader isreader = null;
    BufferedReader bufferReader = null;
    try {
      if (fileIsExists(filename, path)) {
        File destFile = new File(appSDPath + "/" + path + "/" + filename);
        fis = new FileInputStream(destFile);
        isreader = new InputStreamReader(fis, "utf-8");
        bufferReader = new BufferedReader(isreader);
        StringBuffer infos = new StringBuffer();
        String info;
        while ((info = bufferReader.readLine()) != null) {
          infos.append(info);
        }
        temp = infos.toString();
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      if (bufferReader != null) {
        try {
          bufferReader.close();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      if (isreader != null) {
        try {
          isreader.close();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      if (fis != null) {
        try {
          fis.close();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
    return temp;
  }

  /**
   * 保存图片
   *
   * @param picturePath 图片文件所处文件夹路径
   * @param pictureName 图片文件名称
   * @param bitmap 要保存的图片
   * @return 图片文件的路径
   */
  public static String saveBitmap(String picturePath, String pictureName, Bitmap bitmap) {
    String path = "";
    FileOutputStream fos = null;
    BufferedOutputStream bos = null;
    ByteArrayOutputStream baos = null; // 字节数组输出流
    try {
      if (StringUtils.StringIsEmpty(picturePath)) {
        path = pictureName;
      } else {
        path = picturePath + "/" + pictureName;
      }
      if (fileIsExists(path)) {
        deleteDirFile(path);
      }
      baos = new ByteArrayOutputStream();
      File file = creatSDDir(picturePath);
      File fileAbsolutely = new File(file, pictureName);
      fos = new FileOutputStream(fileAbsolutely);
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
      byte[] byteArray = baos.toByteArray();// 字节数组输出流转换成字节数组
      bos = new BufferedOutputStream(fos);
      bos.write(byteArray);
      bos.flush();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      try {
        if (baos != null) {
          baos.close();
        }
        if (bos != null) {
          bos.close();
        }
        if (fos != null) {
          fos.close();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return path;
  }

  /**
   * 得到assets 文件中的内容
   */
  public static String getFromAssets(String fileName, Context context) {
    try {
      InputStreamReader inputReader = new InputStreamReader(
          context.getApplicationContext().getResources().getAssets().open(fileName));

      BufferedReader bufReader = new BufferedReader(inputReader);
      String line = "";
      String Result = "";
      while ((line = bufReader.readLine()) != null) Result += line;
      return Result;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return fileName;
  }

  /**
   * 从assert文件中得到输入流
   *
   * @param fileName 文件名称
   * @Title: getInputStreamFromAssets
   * @Description: TODO
   * @return: InputStream
   */
  public static InputStream getInputStreamFromAssets(String fileName, Context context) {
    InputStream is = null;
    try {
      is = context.getApplicationContext().getResources().getAssets().open(fileName);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return is;
  }

  /**
   * 判断文件的类型
   *
   * @return 1图片，2音频，3视频
   */
  public static int getAttachType(String pathName) {
    int type = 0;
    /* 取得扩展名 */
    String extension =
        pathName.substring(pathName.lastIndexOf(".") + 1, pathName.length()).toLowerCase();
		/* 按扩展名的类型决定MimeType */
    if (extension.equals("m4a")
        || extension.equals("mp3")
        || extension.equals("mid")
        || extension.equals("xmf")
        || extension.equals("ogg")
        || extension.equals("avi")
        || extension.equals("3gpp")
        || extension.equals("wav")) {
      type = 2;
    } else if (extension.equals("3gp") || extension.equals("mp4")) {
      type = 3;
    } else if (extension.equals("jpg")
        || extension.equals("gif")
        || extension.equals("png")
        || extension.equals("jpeg")
        || extension.equals("bmp")) {
      type = 1;
    } else if (extension.equals("doc") || extension.equals("docx")) {
      type = 4;
    } else if (extension.equals("xls")) {
      type = 5;
    } else if (extension.equals("ppt")
        || extension.equals("pptx")
        || extension.equals("pps")
        || extension.equals("dps")) {
      type = 6;
    } else {
      type = 0;
    }
    return type;
  }
}
