package com.video.maker.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.TextView;

import com.video.maker.R;
import com.video.maker.activities.VideoPreviewActivity;


public class ExitDialog extends Dialog {
    Activity context;
    TextView tvDiscard;
    TextView tvKeep;

    public ExitDialog(Activity activity) {
        super(activity);
        this.context = activity;
        setContentView(R.layout.dialog_exit);
        addControls();
        addEvents();
    }

    private void addEvents() {
        this.tvDiscard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ExitDialog.this.context.finish();
                if (ExitDialog.this.context instanceof VideoPreviewActivity) {
                    ExitDialog.this.dismiss();
                    ((VideoPreviewActivity) ExitDialog.this.context).destroyMovie();

                }
            }
        });
        this.tvKeep.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ExitDialog.this.dismiss();
            }
        });
    }

    private void addControls() {
        this.tvDiscard = (TextView) findViewById(R.id.tv_discard);
        this.tvKeep = (TextView) findViewById(R.id.tv_keep);
        setCanceledOnTouchOutside(true);
    }
}
