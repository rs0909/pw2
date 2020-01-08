package com.example.tab_widget;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ResourceBundle;

public class FaceActivity extends Activity {

    Context mContext;
    Bitmap bitmap;
    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_face);
        mContext = this;



            ImageView imageview = findViewById(R.id.ImageView_main);
            //메인 액티비티에서 사진 정보 받아오기
            Intent intent = getIntent();
            //Uri uri = getIntent().getParcelableExtra("uri");
            Uri uri = Uri.parse(intent.getExtras().getString("uri"));
            Log.d("asdf", uri.toString());


            final RelativeLayout RelativeLayout_main = findViewById(R.id.RelativeLayout_main);

            FirebaseVisionFaceDetectorOptions options =
                    new FirebaseVisionFaceDetectorOptions.Builder()
                            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                            .build();

            //이미지를 bitmap으로 옮겨오기
            InputStream inputStream = null;
            try {
                inputStream = (InputStream) getContentResolver().openInputStream(uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            bitmap = BitmapFactory.decodeStream(inputStream);
            //final Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0 , byteArray.length);
            //이미지를 화면에 보여주기
            imageview.setImageBitmap(bitmap);

            if (bitmap != null) {
                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
                FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
                        .getVisionFaceDetector(options);

                Task<List<FirebaseVisionFace>> result =
                        detector.detectInImage(image)
                                .addOnSuccessListener(
                                        new OnSuccessListener<List<FirebaseVisionFace>>() {
                                            @Override
                                            public void onSuccess(List<FirebaseVisionFace> faces) {
                                                // Task completed successfully
                                                // ...

                                                Log.d("FACES", faces.toString());
                                                Point p = new Point();
                                                Display display = getWindowManager().getDefaultDisplay();
                                                display.getSize(p);

                                                // p.x; p.y;
                                                //1:10=10:x


                                                for (FirebaseVisionFace face : faces) {
                                                    Rect bounds = face.getBoundingBox();
                                                    float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
                                                    float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees

                                                    // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                                                    // nose available):
                                                    FirebaseVisionFaceLandmark leftEye = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE);
                                                    float lex = leftEye.getPosition().getX();
                                                    float ley = leftEye.getPosition().getY();

                                                    FirebaseVisionFaceLandmark leftCheek = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_CHEEK);
                                                    float lcx = leftCheek.getPosition().getX();
                                                    float lcy = leftCheek.getPosition().getY();

                                                    FirebaseVisionFaceLandmark rightCheek = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_CHEEK);
                                                    float rcx = rightCheek.getPosition().getX();
                                                    float rcy = rightCheek.getPosition().getY();

                                                    FirebaseVisionFaceLandmark leftEar = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR);
                                                    float leax = leftEar.getPosition().getX();
                                                    float leay = leftEar.getPosition().getY();

                                                    FirebaseVisionFaceLandmark rightEar = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EAR);
                                                    float reax = rightEar.getPosition().getX();
                                                    float reay = rightEar.getPosition().getY();


                                                    ImageView imageLE = new ImageView(mContext);
                                                    imageLE.setImageResource(R.mipmap.sunglass);
                                                    imageLE.setX(p.x * lex / bitmap.getWidth());
                                                    imageLE.setY(p.y * ley / bitmap.getHeight());
                                                    imageLE.setLayoutParams(new RelativeLayout.LayoutParams(200, 200));
                                                    RelativeLayout_main.addView(imageLE);

                                                    ImageView imageRC = new ImageView(mContext);
                                                    imageRC.setImageResource(R.mipmap.right_whiskers);
                                                    imageRC.setX(p.x * rcx / bitmap.getWidth() - 100);
                                                    imageRC.setY(p.y * rcy / bitmap.getHeight() - 100);
                                                    imageRC.setLayoutParams(new RelativeLayout.LayoutParams(200, 200));
                                                    RelativeLayout_main.addView(imageRC);

                                                    ImageView imageLC = new ImageView(mContext);
                                                    imageLC.setImageResource(R.mipmap.left_whiskers);
                                                    imageLC.setX(p.x * lcx / bitmap.getWidth() - 100);
                                                    imageLC.setY(p.y * lcy / bitmap.getHeight() - 100);
                                                    imageLC.setLayoutParams(new RelativeLayout.LayoutParams(200, 200));
                                                    RelativeLayout_main.addView(imageLC);

                                                    ImageView imageLEA = new ImageView(mContext);
                                                    imageLEA.setImageResource(R.mipmap.leftear);
                                                    imageLEA.setX(p.x * leax / bitmap.getWidth() - 100);
                                                    imageLEA.setY(p.y * leay / bitmap.getHeight() - 100);
                                                    imageLEA.setLayoutParams(new RelativeLayout.LayoutParams(200, 200));
                                                    RelativeLayout_main.addView(imageLEA);
                                                    ImageView imageREA = new ImageView(mContext);
                                                    imageREA.setImageResource(R.mipmap.rightear);
                                                    imageREA.setX(p.x * reax / bitmap.getWidth() - 100);
                                                    imageREA.setY(p.y * reay / bitmap.getHeight() - 100);
                                                    imageREA.setLayoutParams(new RelativeLayout.LayoutParams(200, 200));
                                                    RelativeLayout_main.addView(imageREA);

                                                }
                                            }
                                        })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Task failed with an exception
                                                // ...
                                            }
                                        });
            }

        }



}

