package androidtest.example.com.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;

public class ProgressDialog extends Dialog {

    private ProgressBar progress;

    public ProgressDialog(Context context) {
        super(context);
    }

    public ProgressDialog(Context context, int themeResId) {
        super(context, R.style.DialogStyle);
        View view = getLayoutInflater().inflate(R.layout.progress_dialog_view, null);
        ImageView imageView = view.findViewById(R.id.ivLoading);
        Glide.with(context).load(R.drawable.refresh_hipai).into(imageView);
        setContentView(view);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
