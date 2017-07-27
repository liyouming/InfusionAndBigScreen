package com.fugao.newbigscreen.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

/** 语音播放基类
 * Created by li on 2016/6/21.
 */
public class TextSpeaker {
    private Context context;
    private TextToSpeech tts;
    public TextSpeaker(final Context context) {
        // TODO Auto-generated constructor stub
        this.context = context;
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // TODO Auto-generated method stub
                if (status == TextToSpeech.SUCCESS)
                {
                    int result = tts.setLanguage(Locale.CHINA);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED)
                    {
                        Toast.makeText(context, "Language is not available.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void speak(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}
