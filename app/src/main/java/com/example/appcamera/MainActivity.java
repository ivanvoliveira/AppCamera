package com.example.appcamera;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.appcamera.helper.Permissao;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private ImageButton buttonCamera;
    private ImageButton buttonGaleria;

    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private static final int CODE_CAMERA = 100;
    private static final int CODE_GALERIA = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        buttonCamera = findViewById(R.id.button_camera);
        buttonGaleria = findViewById(R.id.button_galeria);

        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent, CODE_CAMERA);
                }
            }
        });

        buttonGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent, CODE_GALERIA);
                }
            }
        });

        Permissao.validarPermissoes(permissoes, this, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap imagem = null;

            try {
                switch (requestCode){
                    case CODE_CAMERA:
                        imagem = (Bitmap) data.getExtras().get("data");
                        break;
                    case CODE_GALERIA:
                        Uri uri = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        break;
                }

                if (imagem != null){
                    imageView.setImageBitmap(imagem);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int resultado : grantResults){
            if (resultado == PackageManager.PERMISSION_DENIED){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Permissões negadas");
                builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
                builder.setCancelable(false);
                builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }
}
