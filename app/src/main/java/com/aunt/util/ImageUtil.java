package com.aunt.util;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public final class ImageUtil {
	public static void savePhotoToSDCard(Bitmap photoBitmap, String path, String photoName) {
		String endingpath = path + "/com.foerstzhu";
		File dir = new File(endingpath);
		if (!dir.exists()) {
			dir.mkdirs();//创建文件夹！！
		}
		File photoFile = new File(endingpath, photoName + ".png");//在parent的文件夹下创立文件！
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(photoFile);
			if (photoBitmap != null) {
				if (photoBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)) {
					fileOutputStream.flush();
					fileOutputStream.close();
				}
			}
		} catch (FileNotFoundException e) {
			photoFile.delete();
			e.printStackTrace();
		} catch (IOException e) {
			photoFile.delete();
			e.printStackTrace();
		} finally {
			try {
				fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
