package com.howell.utils;

import com.howell.entityclass.NodeDetails;

import java.io.File;

/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class TakePhotoUtil {
	//拍照功能 照片存于destDirPath路径下，照片为jpg
	public static void takePhoto(String destDirPath,NodeDetails dev,InviteUtils client){
		File destDir = new File(destDirPath);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		String path = destDirPath+"/"+dev.getDevID()+".jpg";
		client.setCatchPictureFlag(client.getHandle(),path,path.length());
	}
}
