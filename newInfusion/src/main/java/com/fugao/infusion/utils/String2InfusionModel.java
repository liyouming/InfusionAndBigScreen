package com.fugao.infusion.utils;

import com.fugao.infusion.model.BottleModel;
import com.fugao.infusion.model.InfusionAreaBean;
import com.fugao.infusion.model.InfusionEventModel;
import com.fugao.infusion.model.InfusioningModel;
import com.fugao.infusion.model.NurseAccountModel;
import com.fugao.infusion.model.PatrolModel;
import com.fugao.infusion.model.QueueModel;
import com.fugao.infusion.model.RealseSeatModel;
import com.fugao.infusion.model.WorkloadModel;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 字符串转模型或者模型集合 string2NotesBeans
 *
 * @author findchen TODO 2013-6-7下午6:51:49
 */
public class String2InfusionModel {
    public static String ITEMS = "Items";
    public static String COUNTS = "Count";
    public static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper
                .configure(
                        DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,
                        false);

        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }

        public static NurseAccountModel string2NurseAccount(String string) {
            NurseAccountModel nurseAccountModel = new NurseAccountModel();

        try {
            nurseAccountModel = objectMapper.readValue(string, NurseAccountModel.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return nurseAccountModel;
    }
    public static QueueModel string2QueueModel(String string) {
        QueueModel queueModel = new QueueModel();

        try {
            queueModel = objectMapper.readValue(string, QueueModel.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return queueModel;
    }

    	public static ArrayList<InfusionAreaBean> string2InfusionAreaBeans(String string) {
		ArrayList<InfusionAreaBean> infusionAreaBeans = new ArrayList<InfusionAreaBean>();

		try {
            infusionAreaBeans = objectMapper.readValue(string,
					new TypeReference<List<InfusionAreaBean>>() {
					});
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return infusionAreaBeans;
	}

	public static ArrayList<BottleModel> string2BottleModels(String string) {
		ArrayList<BottleModel> bottleModels = new ArrayList<BottleModel>();
		//String content = getContents(string);
		try {

            bottleModels = objectMapper.readValue(string,
					new TypeReference<List<BottleModel>>() {
					});
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bottleModels;

	}

    /**
     *座位转换
     * @param string
     * @return
     */
    public static ArrayList<RealseSeatModel> string2RealseSeatModel(String string) {
        ArrayList<RealseSeatModel> bottleModels = new ArrayList<RealseSeatModel>();
        //String content = getContents(string);
        try {
            bottleModels = objectMapper.readValue(string,
                    new TypeReference<List<RealseSeatModel>>() {
					});
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bottleModels;

	}

    public static BottleModel string2BottleModel(String string) {
        BottleModel bottleModel = new BottleModel();
        //String content = getContents(string);
        try {
            bottleModel = objectMapper.readValue(string, BottleModel.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bottleModel;

    }

    /**
     * 工作量
     * @param string
     * @return
     */
    public static WorkloadModel string2WorkloadModel(String string) {
        WorkloadModel workloadModel = new WorkloadModel();
        //String content = getContents(string);
        try {
            workloadModel = objectMapper.readValue(string, WorkloadModel.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workloadModel;
    }

    /**
     * 瓶贴集合转换成字符串
     * @param bottleModels
     * @return
     */
    public static String bottleModles2String(ArrayList<BottleModel> bottleModels){
        String s = "";
        try {
            s = objectMapper
                    .writeValueAsString(bottleModels);
        } catch (JsonGenerationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return s;
    }

    /**
     * 巡视记录json转换为字符串
     * @param patrolModels
     * @return
     */
    public static String patrolModels2String(ArrayList<PatrolModel> patrolModels){
        String s = "";
        try {
            s = objectMapper
                    .writeValueAsString(patrolModels);
        } catch (JsonGenerationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return s;
    }


    public static String bottleModel2String(
            BottleModel bottleModel) {
        String s = "";
        try {
            s = objectMapper
                    .writeValueAsString(bottleModel);
        } catch (JsonGenerationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return s;
    }

    /**
     * 队列model
     *
     * @param queueModel
     * @return
     */
    public static String quequeModel2String(
            QueueModel queueModel) {
        String s = "";
        try {
            s = objectMapper
                    .writeValueAsString(queueModel);
        } catch (JsonGenerationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return s;
    }

    public static String getContents(String string) {
        String resultString = "";
        JSONObject jsonObject = null;
        JSONArray jsonArray;
        try {
            jsonObject = new JSONObject(string);
            jsonArray = jsonObject.getJSONArray(ITEMS);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            jsonArray = new JSONArray();
        }
        resultString = jsonArray.toString();
        return resultString;
    }

    public static String getListContents(String string) {
        String resultString = "";
        JSONObject jsonObject = null;
        JSONArray jsonArray;
        try {
            jsonObject = new JSONObject(string);
            jsonArray = jsonObject.getJSONArray(ITEMS);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            jsonArray = new JSONArray();
        }
        resultString = jsonArray.toString();
        return resultString;
    }

    /**
     * 输液接瓶的时候判断有几组正在执行
     *
     * @param string
     * @return
     */
    public static InfusioningModel string2InfusioningModel(String string) {
        InfusioningModel infusioningModel = new InfusioningModel();

        try {
            infusioningModel = objectMapper.readValue(string, InfusioningModel.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return infusioningModel;
    }

    /**
     * 扫描病人信息和平贴核对不正确的时候记录内容
     * @param infusionEventModels
     * @return
     */
    public static String InfusionEventModel2infusionEventList(ArrayList<InfusionEventModel> infusionEventModels){
        String s = "";
        try {
            s = objectMapper
                    .writeValueAsString(infusionEventModels);
        } catch (JsonGenerationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return s;
    }
}
