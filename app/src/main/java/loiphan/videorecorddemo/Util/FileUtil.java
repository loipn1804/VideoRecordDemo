package loiphan.videorecorddemo.Util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import java.io.File;

@SuppressWarnings("unused")
public class FileUtil {

    public static String FILENAME = "video_record";
    public static String EXTENSION = "avi";
    public static String FOLDER = "aaaa";
    public static String FULL_NAME = "video";

    public static Uri createTempFile(Context context) {
        deleteTempFile(context);
        String root = getRootFolder(context);
        File myDir = new File(root + "/" + FOLDER);

        if (!myDir.exists()) {
            myDir.mkdirs();
        }

        File file = new File(myDir, FULL_NAME);
        Uri uri = Uri.fromFile(file);

        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));

        return uri;
    }

    public static void deleteTempFile(Context context) {
        String root = getRootFolder(context);
        File myDir = new File(root + "/" + FOLDER);

        if (!myDir.exists()) {
            myDir.mkdirs();
        }

        File file = new File(myDir, FULL_NAME);
        file.delete();

        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
    }

    public static Uri getTempFile(Context context) {
        String root = getRootFolder(context);
        File myDir = new File(root + "/" + FOLDER);

        File file = new File(myDir, FULL_NAME);
        Uri uri = Uri.fromFile(file);

        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

        return uri;
    }

    public static String getRootFolder(Context context) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            return Environment.getExternalStorageDirectory().toString();
        } else {
            return context.getExternalFilesDir(null).toString();
        }
    }
}
