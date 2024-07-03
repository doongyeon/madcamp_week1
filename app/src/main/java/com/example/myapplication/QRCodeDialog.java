package com.example.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

public class QRCodeDialog extends Dialog {

    private Bitmap qrCode;

    public QRCodeDialog(Context context, Bitmap qrCode) {
        super(context);
        this.qrCode = qrCode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_qr_code);

        ImageView qrCodeImageView = findViewById(R.id.qrCodeImageView);
        qrCodeImageView.setImageBitmap(qrCode);
    }
}

