package com.fangxu.sample;

import android.app.Activity;
import android.os.Bundle;

import com.fangxu.allangleexpandablebutton.AllAngleExpandableButton;
import com.fangxu.allangleexpandablebutton.ButtonData;

import java.util.ArrayList;
import java.util.List;

import fangxu.com.allangleexpandablebutton.R;

/**
 * Created by dear33 on 2016/8/20.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createButton();
    }

    private void createButton() {
        AllAngleExpandableButton button = (AllAngleExpandableButton) findViewById(R.id.button_expandable);
        List<ButtonData> buttonDatas = new ArrayList<>();
        String[] str = {"A", "B", "C", "D"};
        for (int i = 0; i < 4; i++) {
            ButtonData buttonData = new ButtonData(false);
            buttonData.setText(str[i]).setPaddingDp(2).setTextSizeSp(20).setBackgroundColor(0xffFFFFFF);
            buttonDatas.add(buttonData);
        }
        button.setButtonDatas(buttonDatas);
    }
}
