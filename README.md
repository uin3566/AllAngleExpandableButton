AllAngleExpandableButton
=============
[![](https://jitpack.io/v/uin3566/AllAngleExpandableButton.svg)](https://jitpack.io/#uin3566/AllAngleExpandableButton)  

An button menu that can expand from any angle to any angle as you like.  
It support two type of button, text button and icon button.You can define the button style as you like, such as button size, button background color, text size, button shadow and so on.  

![screenshot](/screenshot/demo.gif)

###Add to your project
* step1:Add it in your root build.gradle at the end of repositories:
```xml
    allprojects {
        repositories {
            ...
	        maven { url "https://jitpack.io" }
        }
    }
```
* step2:Add the dependency:
```
    dependencies {
        compile 'com.github.uin3566:AllAngleExpandableButton:v1.1.0'
    }
```

###Usage
Declare an AllAngleExpandableButton inside your XML file as show below, but note that the layout_width and layout_height is useless, the size of AllAngleExpandableButton is decided by aebMainButtonSizeDp and aebButtonElevation together at last. 
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
    app:aebMaskBackgroundColor="@color/transparent"
    app:aebRippleColor="@color/red"
    app:aebRippleEffect="true"
    app:aebStartAngleDegree="90"
    app:aebSubButtonSizeDp="56dp"
    app:aebSubButtonTextColor="#ff0000ff"
    app:aebSubButtonTextSizeSp="18dp"
    app:aebBlurBackground="true"
    app:aebBlurRadius=10/>
```
use AllAngleExpandableButton in java code like this:  
* step1: define an ArrayList to store button infos and set the list to AllAngleExpandableButton
```java
    AllAngleExpandableButton button = (AllAngleExpandableButton)findViewById(R.id.button_expandable);
    final List<ButtonData> buttonDatas = new ArrayList<>();
    int[] drawable = {R.drawable.plus, R.drawable.mark, R.drawable.settings, R.drawable.heart};
    for (int i = 0; i < drawable.length; i++) {
        ButtonData buttonData = ButtonData.buildIconButton(context, drawable[i], 0);
        buttonDatas.add(buttonData);
    }
    button.setButtonDatas(buttonDatas);
```
* step2: add listener to AllAngleExpandableButton
```java
    button.setButtonEventListener(new ButtonEventListener() {
        @Override
        public void onButtonClicked(int index) {
            //do whatever you want,the param index is counted from startAngle to endAngle,  
	        //the value is from 1 to buttonCount - 1(buttonCount if aebIsSelectionMode=true)
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
|aebEndAngleDegree|integer|90|the end angle of the expand buttons|
|aebMaskBackgroundColor|color|Color.TRANSPARENT|the fullscreen background color when the buttons are expanded|
|aebIsSelectionMode|boolean|false|if true,when a sub button is selected,the main button is setted as the selected sub button|
|aebAnimDurationMillis|integer|225|expand and collapse animator duration in time milliseconds.|
|aebMainButtonRotateAnimDurationMillis|integer|300|the main button rotate animator duration in time milliseconds|
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
|aebBlurBackground|boolean|false|if true, show blur background when expanded|
|aebBlurRadius|float|10|blur radius|

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
