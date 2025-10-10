package com.example.databank.Utils;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QRCodeGenerator {
    
    public static Bitmap generateQRCode(String data, int width, int height) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, width, height);
            
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static String generateConnectionData(String parentPhone, String parentName) {
        // Формат: "PARENT_CONNECT:phone:name"
        return "PARENT_CONNECT:" + parentPhone + ":" + parentName;
    }
    
    public static boolean isValidConnectionData(String data) {
        return data != null && data.startsWith("PARENT_CONNECT:");
    }
    
    public static String[] parseConnectionData(String data) {
        if (!isValidConnectionData(data)) {
            return null;
        }
        
        String[] parts = data.split(":");
        if (parts.length >= 3) {
            return new String[]{parts[1], parts[2]}; // [phone, name]
        }
        return null;
    }
}
