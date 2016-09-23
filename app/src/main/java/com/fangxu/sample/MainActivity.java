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
        installButton90to90();
        installButton90to180();
    }

    private void installButton90to90() {
        final AllAngleExpandableButton button = (AllAngleExpandableButton) findViewById(R.id.button_expandable_90_90);
        final List<ButtonData> buttonDatas = new ArrayList<>();
        int[] drawable = {R.drawable.plus, R.drawable.mark, R.drawable.settings, R.drawable.heart};
        int[] color = {R.color.blue, R.color.red, R.color.green, R.color.yellow};
        for (int i = 0; i < 4; i++) {
            ButtonData buttonData;
            if (i == 0) {
                buttonData = ButtonData.buildIconButton(this, drawable[i], 20);
            } else {
                buttonData = ButtonData.buildIconButton(this, drawable[i], 0);
            }
            buttonData.setBackgroundColorId(this, color[i]);
            buttonDatas.add(buttonData);
        }
        button.setButtonDatas(buttonDatas);
        button.setButtonEventListener(new ButtonEventListener() {
            @Override
            public void onButtonClicked(int index) {

            }

            @Override
            public void onExpand() {

            }

            @Override
            public void onCollapse() {

            }
        });
    }

    private void installButton90to180() {
        final AllAngleExpandableButton button = (AllAngleExpandableButton) findViewById(R.id.button_expandable_90_180);
        final List<ButtonData> buttonDatas = new ArrayList<>();
        int[] drawable = {R.drawable.plus, R.drawable.mark, R.drawable.settings, R.drawable.heart};
        int[] color = {R.color.blue, R.color.red, R.color.green, R.color.yellow};
        for (int i = 0; i < 4; i++) {
            ButtonData buttonData;
            if (i == 0) {
                buttonData = ButtonData.buildIconButton(this, drawable[i], 20);
            } else {
                buttonData = ButtonData.buildIconButton(this, drawable[i], 0);
            }
            buttonData.setBackgroundColorId(this, color[i]);
            buttonDatas.add(buttonData);
        }
        button.setButtonDatas(buttonDatas);
        button.setButtonEventListener(new ButtonEventListener() {
            @Override
            public void onButtonClicked(int index) {

            }

            @Override
            public void onExpand() {

            }

            @Override
            public void onCollapse() {

            }
        });
    }
}
