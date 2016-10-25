package com.fangxu.allangleexpandablebutton;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;

/**
 * Created by Administrator on 2016/10/21.
 */
public class Blur {
    private static final float SCALE = 0.4F;

    public static Bitmap getBlurBitmap(Context context, Bitmap inBitmap, float radius) {
        int width = Math.round(inBitmap.getWidth() * SCALE);
        int height = Math.round(inBitmap.getHeight() * SCALE);

        Bitmap in = Bitmap.createScaledBitmap(inBitmap, width, height, false);
        Bitmap out = Bitmap.createBitmap(in);

        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        Allocation allocationIn = Allocation.createFromBitmap(rs, in);
        Allocation allocationOut = Allocation.createFromBitmap(rs, out);

        blurScript.setRadius(radius);
        blurScript.setInput(allocationIn);
        blurScript.forEach(allocationOut);
        allocationOut.copyTo(out);

        allocationIn.destroy();
        allocationOut.destroy();
        blurScript.destroy();
        rs.destroy();

        return out;
    }

}
