package com.fangxu.sample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

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
        installButton110to250();
        installButton0to360();
    }

    private void installButton90to90() {
        final AllAngleExpandableButton button = (AllAngleExpandableButton) findViewById(R.id.button_expandable_90_90);
        final List<ButtonData> buttonDatas = new ArrayList<>();
        int[] drawable = {R.drawable.plus, R.drawable.mark, R.drawable.settings, R.drawable.heart};
        int[] color = {R.color.blue, R.color.red, R.color.green, R.color.yellow};
        for (int i = 0; i < 4; i++) {
            ButtonData buttonData;
            if (i == 0) {
                buttonData = ButtonData.buildIconButton(this, drawable[i], 15);
            } else {
                buttonData = ButtonData.buildIconButton(this, drawable[i], 0);
            }
            buttonData.setBackgroundColorId(this, color[i]);
            buttonDatas.add(buttonData);
        }
        button.setButtonDatas(buttonDatas);
        setListener(button);
    }

    private void installButton90to180() {
        final AllAngleExpandableButton button = (AllAngleExpandableButton) findViewById(R.id.button_expandable_90_180);
        final List<ButtonData> buttonDatas = new ArrayList<>();
        int[] drawable = {R.drawable.plus, R.drawable.mark, R.drawable.settings, R.drawable.heart};
        int[] color = {R.color.blue, R.color.red, R.color.green, R.color.yellow};
        for (int i = 0; i < 4; i++) {
            ButtonData buttonData;
            if (i == 0) {
                buttonData = ButtonData.buildIconButton(this, drawable[i], 15);
            } else {
                buttonData = ButtonData.buildIconButton(this, drawable[i], 0);
            }
            buttonData.setBackgroundColorId(this, color[i]);
            buttonDatas.add(buttonData);
        }
        button.setButtonDatas(buttonDatas);
        setListener(button);
    }

    private void installButton110to250() {
        final AllAngleExpandableButton button = (AllAngleExpandableButton) findViewById(R.id.button_expandable_110_250);
        final List<ButtonData> buttonDatas = new ArrayList<>();
        int[] drawable = {R.drawable.edit, R.drawable.refresh, R.drawable.mark, R.drawable.settings, R.drawable.heart, R.drawable.search, R.drawable.copy};
        int[] color = {R.color.black, R.color.orange, R.color.blue, R.color.pink, R.color.yellow, R.color.red, R.color.green};
        for (int i = 0; i < 7; i++) {
            ButtonData buttonData;
            if (i == 0) {
                buttonData = ButtonData.buildIconButton(this, drawable[i], 15);
            } else {
                buttonData = ButtonData.buildIconButton(this, drawable[i], 0);
            }
            buttonData.setBackgroundColorId(this, color[i]);
            buttonDatas.add(buttonData);
        }
        button.setButtonDatas(buttonDatas);
        setListener(button);
    }

    private void installButton0to360() {
        final AllAngleExpandableButton button = (AllAngleExpandableButton) findViewById(R.id.button_expandable_0_360);
        final List<ButtonData> buttonDatas = new ArrayList<>();
        int[] drawable = {R.drawable.edit, R.drawable.refresh, R.drawable.mark, R.drawable.settings, R.drawable.heart, R.drawable.search, R.drawable.copy};
        int[] color = {R.color.black, R.color.orange, R.color.blue, R.color.pink, R.color.yellow, R.color.red, R.color.green};
        for (int i = 0; i < 7; i++) {
            ButtonData buttonData;
            if (i == 0) {
                buttonData = ButtonData.buildIconButton(this, drawable[i], 15);
            } else {
                buttonData = ButtonData.buildIconButton(this, drawable[i], 0);
            }
            buttonData.setBackgroundColorId(this, color[i]);
            buttonDatas.add(buttonData);
        }
        button.setButtonDatas(buttonDatas);
        setListener(button);
    }

    private void setListener(AllAngleExpandableButton button) {
        button.setButtonEventListener(new ButtonEventListener() {
            @Override
            public void onButtonClicked(int index) {
                showToast("clicked index:" + index);
            }

            @Override
            public void onExpand() {
//                showToast("onExpand");
            }

            @Override
            public void onCollapse() {
//                showToast("onCollapse");
            }
        });
    }

    private void showToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }
}
