package hcmus.selab.tvhung;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hcmus.selab.tvhung.models.Product;
import hcmus.selab.tvhung.models.ProductBuilder;
import io.fotoapparat.Fotoapparat;
import io.fotoapparat.error.CameraErrorListener;
import io.fotoapparat.exception.camera.CameraException;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.preview.Frame;
import io.fotoapparat.preview.FrameProcessor;
import io.fotoapparat.view.CameraView;

import static hcmus.selab.tvhung.MainActivity.mProductAdapter;
import static io.fotoapparat.log.LoggersKt.fileLogger;
import static io.fotoapparat.log.LoggersKt.logcat;
import static io.fotoapparat.log.LoggersKt.loggers;
import static io.fotoapparat.selector.AspectRatioSelectorsKt.standardRatio;
import static io.fotoapparat.selector.LensPositionSelectorsKt.back;
import static io.fotoapparat.selector.ResolutionSelectorsKt.highestResolution;

public class ScanQRActivity extends AppCompatActivity {

    private static final String TAG = ScanQRActivity.class.toString();
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;

    Fotoapparat fotoapparat;
    FirebaseVisionBarcodeDetector detector;
    long currentTime, prevTime;
    CameraView cameraView;
    String productID;
    Intent intent;
    ImageButton btn_back;
    LottieAnimationView lottieAnimationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        init();
        fotoapparat = createFotoapparat();
        checkPermission();
        intent = new Intent(ScanQRActivity.this, ProductActivity.class);
    }

    private void init() {
        cameraView = findViewById(R.id.camera_view);
        lottieAnimationView = findViewById(R.id.lottieAnimationView);
        btn_back = (ImageButton) findViewById(R.id.buttonBackScan);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        FirebaseVisionBarcodeDetectorOptions options =
                new FirebaseVisionBarcodeDetectorOptions.Builder()
                        .setBarcodeFormats(
                                FirebaseVisionBarcode.FORMAT_QR_CODE,
                                FirebaseVisionBarcode.FORMAT_AZTEC)
                        .build();
        detector = FirebaseVision.getInstance()
                .getVisionBarcodeDetector(options);
    }

    private Fotoapparat createFotoapparat() {
        return Fotoapparat
                .with(this)
                .into(cameraView)
                .previewScaleType(ScaleType.CenterCrop)
                .frameProcessor(new FrameProcessor() {
                    @Override
                    public void process(@NotNull Frame frame) {
                        currentTime = System.currentTimeMillis();
                        if (currentTime - 500 > prevTime) {
                            scanBarcode(frame);
                            prevTime = currentTime;
                        }
                    }
                })
                .lensPosition(back())
                .logger(loggers(
                        logcat(),
                        fileLogger(this)
                ))
                .cameraErrorCallback(new CameraErrorListener() {
                    @Override
                    public void onError(@NotNull CameraException e) {
                    }
                })
                .photoResolution(standardRatio(
                        highestResolution()
                ))
                .build();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        } else {
            fotoapparat.start();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fotoapparat.start();
        } else {
            finish();
        }
    }

    private void scanBarcode(Frame frame) {
        Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(getVisionImageFromFrame(frame))
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
                        for (FirebaseVisionBarcode barcode : firebaseVisionBarcodes) {

                            // getRawValue return the string encoded in the barcode
                            productID = barcode.getRawValue();
                            Log.wtf("ScanQRActivity - productID from barcode", productID);
                            onScanned();
                        }
                    }
                });
    }

    private void onScanned() {

        // Query to database to get details of productID
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        Query query = database.child("products").child(productID);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();

                if(data == null){
                    Log.d(TAG, "Not found product " + productID);
                    return;
                }

                Log.d(TAG, productID + " found");
                ProductBuilder builder = new ProductBuilder();
                builder = builder.setId(productID)
                        .setValue(data);


                if(data.containsKey("avatar_url")){
//                    Log.d(TAG, "data contains avatar url");
                    String avatarUrl = (String)data.get("avatar_url");
                    String downloadedPath = downloadAvatar(avatarUrl, productID);

                    Log.d(TAG, downloadedPath);
                    if(downloadedPath != null) {
                        builder.setAvatarPath(downloadedPath);
                    }

                }

                Product product = builder.createProduct();
                intent.putExtra("product_scanned", product);
//                finish();
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String downloadAvatar(String url, final String id){
        FirebaseStorage mStorage = FirebaseStorage.getInstance();
        StorageReference reference = mStorage.getReferenceFromUrl(url);

        final File file = new File(getFilesDir() + "/" + id + ".jpeg");

//        Log.d("Download avatar: ", getFilesDir() + "/" + id + ".jpeg");

        // Avatar already downloaded
        if(file.exists()){

            Log.d("Download avatar: ", "File already exists");
            return file.toString();
        }

        reference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.d("Download avatar: ", "Get file successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                Log.e(TAG, "Cannot download " + file.toString());
                e.printStackTrace();
            }
        });

        Log.d("File link: ", file.toString());
        return file.toString();
    }

    private FirebaseVisionImage getVisionImageFromFrame(Frame frame) {
        byte[] bytes = frame.getImage();
        FirebaseVisionImageMetadata metadata = new FirebaseVisionImageMetadata.Builder()
                .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                .setRotation(FirebaseVisionImageMetadata.ROTATION_270)
                .setHeight(frame.getSize().height)
                .setWidth(frame.getSize().width)
                .build();

        FirebaseVisionImage image = FirebaseVisionImage.fromByteArray(bytes, metadata);
        return image;
    }

}
