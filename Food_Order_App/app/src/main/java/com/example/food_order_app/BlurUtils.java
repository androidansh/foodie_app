package com.example.food_order_app;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

public class BlurUtils {

    public static Bitmap blur(Context context, Bitmap image, float blurRadius) {
        // Create a RenderScript context
        RenderScript rs = RenderScript.create(context);

        // Create an allocation from the bitmap
        Allocation input = Allocation.createFromBitmap(rs, image, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);

        // Create an allocation for output
        Allocation output = Allocation.createTyped(rs, input.getType());

        // Create a blur script
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(blurRadius);

        // Perform the blur operation
        script.setInput(input);
        script.forEach(output);

        // Copy the output to a bitmap
        Bitmap blurredBitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
        output.copyTo(blurredBitmap);

        // Release resources
        rs.destroy();

        return blurredBitmap;
    }
}
