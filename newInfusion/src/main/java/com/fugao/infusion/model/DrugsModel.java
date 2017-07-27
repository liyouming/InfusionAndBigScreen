package com.fugao.infusion.model;

import java.util.List;


public class DrugsModel {

  /**
   * 瓶贴号
   */
  public String BottleId;

    /**
     * 门诊号
     */
  public String patId;

  /**
   * 药物明细
   */
  public List<DrugDetailModel> Drugs;
}
