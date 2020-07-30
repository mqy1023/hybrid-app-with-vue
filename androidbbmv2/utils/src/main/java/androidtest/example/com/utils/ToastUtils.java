package androidtest.example.com.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ToastUtils {
    private static Toast mToast;

    public static void init(Context context) {
        mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
    }

    public static void show(int resId) {
        if (mToast != null) {
            mToast.setText(resId);
            mToast.show();
        }
    }

    public static void show(CharSequence charSequence) {
        if (mToast != null) {
            mToast.setText(charSequence);
            mToast.show();
        }

    }

    public static void showCustomerText(Context context,String message){
        View toastview= LayoutInflater.from(context).inflate(R.layout.toast_text_layout,null);
        TextView text = (TextView) toastview.findViewById(R.id.tv_message);
        text.setText(message);
        Toast toast=new Toast(context);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastview);
        toast.show();
    }


    public static void showCustomerTextRGB(Context context,String message){
        View toastview= LayoutInflater.from(context).inflate(R.layout.toast_text_rgb_layout,null);
        toastview.setAlpha(0.8f);
        TextView text = (TextView) toastview.findViewById(R.id.tv_message);
        text.setText(message);
        Toast toast=new Toast(context);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastview);
        toast.show();
    }

}
