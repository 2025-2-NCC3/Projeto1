package com.example.ifoodclone.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ifoodclone.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;

public class PagamentoPixActivity extends AppCompatActivity {

    private ImageView qrCodeImage;
    private TextView valorPix, mensagemPix;
    private Button btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagamento_pix);

        qrCodeImage = findViewById(R.id.qrCodeImage);
        valorPix = findViewById(R.id.valorPix);
        mensagemPix = findViewById(R.id.mensagemPix);
        btnVoltar = findViewById(R.id.btnVoltar);

        double valor = getIntent().getDoubleExtra("valorTotal", 0.0);
        valorPix.setText(String.format("R$ %.2f", valor));

        // Simulação de chave PIX gerada (fixa para testes)
        String chavePix = "chavepix@comedoria.com";
        String conteudoPix = "Pagamento Comedoria - Valor: R$ " + String.format("%.2f", valor) +
                " - Chave: " + chavePix;

        gerarQrCode(conteudoPix);

        mensagemPix.setText("Use o QR Code abaixo para pagar com Pix.");

        btnVoltar.setOnClickListener(v -> finish());
    }

    private void gerarQrCode(String texto) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(texto, BarcodeFormat.QR_CODE, 600, 600);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            qrCodeImage.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
