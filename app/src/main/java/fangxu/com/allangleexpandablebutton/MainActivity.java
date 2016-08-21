package fangxu.com.allangleexpandablebutton;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fangxu.com.library.AllAngleExpandableButton;
import fangxu.com.library.ButtonBean;
import fangxu.com.library.ButtonClickListener;

/**
 * Created by dear33 on 2016/8/20.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createAllAngleExpandableButton();
    }

    private void createAllAngleExpandableButton() {
        AllAngleExpandableButton expandableButton = (AllAngleExpandableButton)findViewById(R.id.button_expandable);
        ButtonBean beanMain = new ButtonBean("送到", 0);
        ButtonBean bean1 = new ButtonBean("99", 1);
        ButtonBean bean2 = new ButtonBean("520", 2);
        ButtonBean bean3 = new ButtonBean("1314", 3);
        List<ButtonBean> beanList = new ArrayList<>(4);
        beanList.add(beanMain);
        beanList.add(bean1);
        beanList.add(bean2);
        beanList.add(bean3);
        expandableButton.setItems(beanList);
        expandableButton.setClickListener(new ButtonClickListener() {
            @Override
            public void onMainButtonClick() {

            }

            @Override
            public void onSubButtonClick(int position) {
                Toast.makeText(MainActivity.this.getApplicationContext(), "click position:" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
