AllAngleExpandableButton
=============

An button menu that can expand from any angle to any angle as you like.  
It support two type of button, text button and icon button.You can define the button style as you like, such as button size, button background color, text size,button shadow and so on.

###Add to your project
```xml

dependencies {
  compile 'com.fangxu:AllAngleExpandableButton:1.0.0'
}

```

###Usage
Declare an AllAngleExpandableButton inside your XML file as show below, but note that the layout_width and layout_height is useless, the size of AllAngleExpandableButton is desided by aebMainButtonSizeDp and aebButtonElevation together at last. 
```xml
<com.fangxu.allangleexpandablebutton.AllAngleExpandableButton
    android:id="@+id/button_expandable"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_alignParentBottom="true"
    android:layout_centerHorizontal="true"
    android:layout_marginBottom="100dp"
    app:aebAnimDurationMillis="175"
    app:aebButtonElevation="4dp"
    app:aebButtonGapDp="25dp"
    app:aebEndAngleDegree="90"
    app:aebIsSelectionMode="false"
    app:aebMainButtonRotateAnimDurationMillis="250"
    app:aebMainButtonRotateDegree="-135"
    app:aebMainButtonSizeDp="56dp"
    app:aebMainButtonTextColor="#ffff5656"
    app:aebMainButtonTextSizeSp="20dp"
    app:aebMaskBackgroundColor="@color/blue"
    app:aebRippleColor="@color/red"
    app:aebRippleEffect="true"
    app:aebStartAngleDegree="90"
    app:aebSubButtonSizeDp="56dp"
    app:aebSubButtonTextColor="#ff0000ff"
    app:aebSubButtonTextSizeSp="18dp"/>
```
use AllAngleExpandableButton in java code like this:  
* step1: define an ArrayList to store button infos and set the list to AllAngleExpandableButton
```java
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
```
* step2: add listener to AllAngleExpandableButton
```java
    AllAngleExpandableButton button;
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
```

###Attributes
all of the attributes are listed below:  

|attribute|value type|defalut value| description|
|---| ---| ---|---|
|aebStartAngleDegree|integer|90|the start angle of the expand buttons|
|aebEndAngleDegree|integer|180|the end angle of the expand buttons|
|aebMaskBackgroundColor|color|Color.TRANSPARENT|the fullscreen background color when the buttons are expanded|
|aebIsSelectionMode|boolean|true|if true,when a sub button is selected,the main button is setted as the selected sub button|
|aebAnimDurationMillis|integer|225|expand and collapse animator duration in time milliseconds.|
|aebMainButtonRotateAnimDurationMillis|integer|175|the main button rotate animator duration in time milliseconds|
|aebMainButtonRotateDegree|integer|0|main button rotate degree while expanding|
|aebButtonElevation|dimen|4dp|used for draw the button shadow.|
|aebRippleEffect|boolean|true|ripple effect on main button when it's touched|
|aebRippleColor|color|depends on button background|ripple effect color, default is the light color of the button background|
|aebMainButtonSizeDp|dimen|60|the size of the main button|
|aebMainButtonTextSizeSp|dimen|20|the size of the main button text|
|aebMainButtonTextColor|color|Color.BLACK|the color of the main button text|
|aebSubButtonSizeDp|dimen|60|the size of the sub button|
|aebSubButtonTextSizeSp|dimen|20|the size of the sub button text|
|aebSubButtonTextColor|color|Color.BLACK|the color of the sub button text|
|aebButtonGapDp|dimen|50dp|the distance of main button and sub button.|

###License
```
Copyright (c) 2016 uin3566 <xufang2@foxmail.com>

Licensed under the Apache License, Version 2.0 (the "License”);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
   
   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
