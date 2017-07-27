package com.fugao.infusion.model;



public class CounterModel {

  /**
   * 分类名称
   */
  public String name;

  /**
   * 统计次数
   */
  public int count;

  public CounterModel(String name, int count) {
    this.name = name;
    this.count = count;
  }
}
