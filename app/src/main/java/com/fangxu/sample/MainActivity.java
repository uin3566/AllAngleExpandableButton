package com.fangxu.sample;

import android.app.Activity;
import android.os.Bundle;

import com.fangxu.allangleexpandablebutton.AllAngleExpandableButton;
import com.fangxu.allangleexpandablebutton.ButtonData;
import com.fangxu.allangleexpandablebutton.ButtonEventListener;

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
        final List<ButtonData> buttonDatas = new ArrayList<>();
        final String[] str = {"A", "B", "C", "D"};
        int[] drawable = {R.drawable.plus, R.drawable.mark, R.drawable.settings, R.drawable.heart};
        int[] color = {R.color.blue, R.color.red, R.color.green, R.color.yellow};
        for (int i = 0; i < 4; i++) {
            ButtonData buttonData = new ButtonData(false);
            if (i == 0) {
                buttonData.configIconButton(drawable[i], 20);
            } else {
                buttonData.configIconButton(drawable[i], 0);
            }
            buttonData.setBackgroundColorId(color[i]);
            buttonDatas.add(buttonData);
        }
        button.setButtonDatas(buttonDatas);
        button.setButtonEventListener(new ButtonEventListener() {
            @Override
            public void onButtonClicked(int index) {
//                if (index == -1) {
//                    Toast.makeText(MainActivity.this, "space", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(MainActivity.this, str[index], Toast.LENGTH_SHORT).show();
//                }
            }

            @Override
            public void onExpand() {
//                ButtonData buttonData = buttonDatas.get(0);
//                buttonData.configIconButton(R.drawable.refresh, 0);
            }

            @Override
            public void onCollapse() {
//                ButtonData buttonData = buttonDatas.get(0);
//                buttonData.configIconButton(R.drawable.plus, 20);
            }
        });
    }
}
