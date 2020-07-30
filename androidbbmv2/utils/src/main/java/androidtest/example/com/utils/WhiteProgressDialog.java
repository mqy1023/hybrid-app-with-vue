package androidtest.example.com.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

public class WhiteProgressDialog extends Dialog {

    private ProgressBar progress;

    public WhiteProgressDialog(Context context) {
        super(context);
    }

    public WhiteProgressDialog(Context context, int themeResId) {
        super(context, R.style.DialogStyle);
        View view = getLayoutInflater().inflate(R.layout.white_progress_dialog_view, null);
        setContentView(view);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
