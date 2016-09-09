package fangxu.com.allangleexpandablebutton;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fangxu.com.library.refactor.AllAngleExpandableButton;
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
        createButton();
    }

    private void createButton() {
        AllAngleExpandableButton button = (AllAngleExpandableButton) findViewById(R.id.button_expandable);
        button.setButtonDrawableResId(R.drawable.black_num_0).setButtonDrawableResId(R.drawable.black_num_1).setButtonDrawableResId(R.drawable.black_num_2);
        button.invalidate();
    }
}
