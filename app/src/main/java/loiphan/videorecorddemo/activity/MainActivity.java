package loiphan.videorecorddemo.activity;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import loiphan.videorecorddemo.R;
import loiphan.videorecorddemo.Util.FileUtil;
import loiphan.videorecorddemo.Util.ImageUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public final int CAMERA_REQUEST_VIDEO = 111;
    public final int CAMERA_REQUEST_PICTURE = 112;
    public final int GALLERY_REQUEST_PICTURE = 113;

    @Bind(R.id.btnRecord)
    Button btnRecord;
    @Bind(R.id.btnTakePicture)
    Button btnTakePicture;
    @Bind(R.id.btnLoad)
    Button btnLoad;
    @Bind(R.id.imv)
    ImageView imv;
    @Bind(R.id.videoView)
    VideoView videoView;

    @Bind(R.id.btnValidName)
    Button btnValidName;
    @Bind(R.id.edtName)
    EditText edtName;
    @Bind(R.id.txtLog)
    TextView txtLog;

    private Uri photoUri;

    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions option;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        option = new DisplayImageOptions.Builder().cacheInMemory(true)
                .showImageForEmptyUri(R.color.gray_55)
                .showImageOnLoading(R.color.gray_55)
                .cacheOnDisk(true).build();

        Log.e("LIO", "onCreate");

        initView();
        initData();

        doSomethingMemoryIntensive();

        Log.e("LIO", currentDateAtStart().toString() + " - " + currentDateAtEnd().toString());
    }

    private void initView() {
        ButterKnife.bind(this);

        btnRecord.setOnClickListener(this);
        btnTakePicture.setOnClickListener(this);
        btnLoad.setOnClickListener(this);
        btnValidName.setOnClickListener(this);
    }

    private void initData() {
        grandPermission();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRecord:
                openCamera();
                break;
            case R.id.btnTakePicture:
                openCameraToTakePicture();
                break;
            case R.id.btnLoad:
                getPhoto();
                break;
            case R.id.btnValidName:
//                validName();
                Intent intent = new Intent(this, PhotoActivity.class);
                intent.putExtra("message", "This is message!");
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        Log.e("LIO", "onDestroy");
        Toast.makeText(this, "MainActivity onDestroy", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    private void openCamera() {
//        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileUtil.createFile(this));
//        startActivityForResult(cameraIntent, CAMERA_REQUEST);

        Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileUtil.createTempFile(this));
        startActivityForResult(cameraIntent, CAMERA_REQUEST_VIDEO);
    }

    private void openCameraToTakePicture() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoUri = ImageUtil.createFile(this);
        Log.e("LIO", "photoUri " + photoUri);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_PICTURE);
    }

    private void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
        try {
            startActivityForResult(intent, GALLERY_REQUEST_PICTURE);
        } catch (ActivityNotFoundException e) {
            Log.e("LIO", e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_VIDEO && resultCode == RESULT_OK) {
            Uri uri = FileUtil.getTempFile(this);
            Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show();
            try {
//                Bitmap bmImvMain = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//                imv.setImageBitmap(bmImvMain);
                String root = FileUtil.getRootFolder(this);
                File myDir = new File(root + "/" + FileUtil.FOLDER);

                if (myDir.exists()) {
                    File from = new File(myDir, FileUtil.FULL_NAME);
                    File to = new File(myDir, FileUtil.FULL_NAME + "." + FileUtil.EXTENSION);
                    if (from.exists())
                        from.renameTo(to);
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(to)));
                    MediaController mediaController = new MediaController(this);
                    mediaController.setAnchorView(videoView);
                    mediaController.setMediaPlayer(videoView);
                    videoView.setZOrderOnTop(false);
                    videoView.setMediaController(mediaController);
                    videoView.setVisibility(View.VISIBLE);
                    imv.setVisibility(View.GONE);
                    try {
                        videoView.setVideoURI(Uri.fromFile(to));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                Toast.makeText(this, "Fail " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CAMERA_REQUEST_PICTURE && resultCode == RESULT_OK) {
            try {
                Uri uri = photoUri;
                Bitmap bmImvMain = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                Matrix matrix = new Matrix();
                matrix.postRotate(getCameraPhotoOrientation(this, uri, uri.getPath()));
                bmImvMain = Bitmap.createBitmap(bmImvMain, 0, 0, bmImvMain.getWidth(), bmImvMain.getHeight(), matrix, false);
                imv.setImageBitmap(bmImvMain);
                videoView.setVisibility(View.GONE);
                imv.setVisibility(View.VISIBLE);

                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            } catch (Exception e) {
                Toast.makeText(this, "Fail " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == GALLERY_REQUEST_PICTURE && resultCode == RESULT_OK) {
            Log.e("LIO", data.getData().toString());
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());

                if (inputStream != null) {
                    Log.e("LIO", "inputStream not null");
                } else {
                    Log.e("LIO", "inputStream null");
                }

                saveInputStream(inputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveInputStream(InputStream in) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
        String filename = format.format(new Date()) + "_" + (new Random().nextInt(900) + 100) + ".jpg";
        File file = new File(getFilesDir(), filename);

        try {
            FileOutputStream f = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = in.read(buffer)) > 0) {
                f.write(buffer, 0, len1);
            }
            f.close();

            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            videoView.setVisibility(View.GONE);
            imv.setVisibility(View.VISIBLE);
            imv.setImageBitmap(bitmap);
        } catch (Exception e) {
            Log.e("LIO", e.getMessage());
        }
    }

    private void grandPermission() {
        String permission = "android.permission.CAMERA";
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, 1);
        }
        permission = "android.permission.READ_EXTERNAL_STORAGE";
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, 1);
        }
        permission = "android.permission.WRITE_EXTERNAL_STORAGE";
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, 1);
        }
        permission = "android.permission.STORAGE";
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, 1);
        }
    }

    private String getPathUpload(String filename) {

        return null;
    }

    private void validName() {
        String name = edtName.getText().toString().trim();
        name = name.replaceAll("[^\\w.-]", "_");
        txtLog.setText(name);
    }

    public int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath) {
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Log.e("LIO", "orientation " + orientation);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("LIO", "getCameraPhotoOrientation " + rotate);
        return rotate;
    }

    public void doSomethingMemoryIntensive() {

        // Before doing something that requires a lot of memory,
        // check to see whether the device is in a low memory state.
        ActivityManager.MemoryInfo memoryInfo = getAvailableMemory();

        if (!memoryInfo.lowMemory) {
            // Do memory intensive work ...
            Log.e("LIO", "not lowMemory");
        } else {
            Log.e("LIO", "lowMemory");
        }
    }

    // Get a MemoryInfo object for the device's current memory status.
    private ActivityManager.MemoryInfo getAvailableMemory() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }

    public static Date currentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    public static Date currentDateAtStart() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.add(Calendar.MILLISECOND, 0);

        Date currentDate = cal.getTime();
        return currentDate;
    }

    public static Date currentDateAtEnd() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.SECOND, -1);


        Date currentDate = cal.getTime();
        return currentDate;
    }
}