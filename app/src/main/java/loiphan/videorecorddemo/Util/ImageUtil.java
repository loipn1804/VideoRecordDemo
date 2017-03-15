package loiphan.videorecorddemo.Util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Copyright (c) 2016, Stacck Pte Ltd. All rights reserved.
 *
 * @author Lio <lphan@stacck.com>
 * @version 1.0
 * @since September 29, 2016
 */

public class ImageUtil {
    public static String FOLDER = "photo";

    public static Uri createFile(Context context) {
        String root = getDirectory();
        File myDir = new File(root + "/" + FOLDER);

        if (!myDir.exists()) {
            myDir.mkdirs();
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");

        String filename = format.format(new Date()) + "_" + (new Random().nextInt(900) + 100) + ".jpg";

        File file = new File(myDir, filename);
        Uri uri = Uri.fromFile(file);

        return uri;
    }

    public static void deleteFile(File file) {
        if (file.exists()) {
            file.delete();
        }
    }

    public static String getDirectory() {
        return Environment.getExternalStorageDirectory().toString();
    }
}
