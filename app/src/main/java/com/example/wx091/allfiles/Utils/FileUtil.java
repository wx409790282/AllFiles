package com.example.wx091.allfiles.Utils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build;

import com.example.wx091.allfiles.MainActivity;
import com.example.wx091.allfiles.beans.File_Item;
import com.example.wx091.allfiles.beans.IConstant;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileUtil {



    /**
     * 删除某个文件目录
     *
     * @param file 需要删除的文件
     */
    public static void deleteFile(File file){
        if(file != null && file.exists())
            file.delete();
    }

    /**
     * @param path 文件存储的路径
     */
    public static boolean deleteFile(String path){
        File file = new File(path);
        if(file != null && file.exists()){
            return file.delete();
        }
        return false;
    }


    public static File makeDir(String path){
        File file = new File(path);
        if(!file.exists())
            file.mkdirs();
        return file;
    }
    /**
     * 获取当前目录信息
     *
     * @param path 根目录地址
     * @return
     */
    public static List<String> getDirFile(String path){
        File file = makeDir(path);
        File[] files = file.listFiles();
        List<String> list = new ArrayList<String>();
        if(files != null && files.length != 0){
            for(int i=0;i<files.length;i++){
                list.add(files[i].getName());
            }
        }
        return list;
    }

    /**
     * 判断文件是否存在
     *
     * @param path
     * @return
     */
    public static boolean fileIsExist(String path){
        File file = new File(path);
        if(file.exists()){
            return true;
        }
        return false;
    }

    public static double getFileSpace(File f){
        double s=f.length()/1048576.0;
        s=Math.round(s*100);
        s=s/100.0;
        return s;
    }

    //获取文件后缀名，video为视频，photo为照片，其他不显示
    public static String getFileType(String fileName) {
        if (fileName != null) {
            int typeIndex = fileName.lastIndexOf(".");
            if (typeIndex != -1) {
                String fileType = fileName.substring(typeIndex + 1)
                        .toLowerCase();
                if (fileType != null
                        && (fileType.equals("jpg") || fileType.equals("gif")
                        || fileType.equals("png") || fileType.equals("jpeg")
                        || fileType.equals("bmp") || fileType.equals("wbmp")
                        || fileType.equals("ico") || fileType.equals("jpe"))) {
                    return "photo";
                }else if(fileType != null
                        && (fileType.equals("3gp") || fileType.equals("asf")
                        || fileType.equals("avi") || fileType.equals("m4u")
                        || fileType.equals("m4v") || fileType.equals("mp4")
                        || fileType.equals("mov") || fileType.equals("mpe")
                        || fileType.equals("mpeg") || fileType.equals("mpg")
                        || fileType.equals("mpg4") )) {
                    return "video";
                }
            }
        }
        return "";
    }

    public static String getVideoLength(File f){
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try{
//            if (Build.VERSION.SDK_INT >= 14)
//                mmr.setDataSource(f.getAbsolutePath(), new HashMap<String, String>());
//            else
                mmr.setDataSource(f.getAbsolutePath());
            //mmr.setDataSource(f.getAbsolutePath());
            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            return duration;
        }catch (Exception e){
            return "0";
        }
    }

    public static boolean renameFile(File_Item f, String newname){
        if(f.type.equals("photo")){
            newname= IConstant.FoldPath[1]+newname;
        }else if(f.type.equals("video")){
            newname=IConstant.FoldPath[0]+newname;
        }
        try{
            File oldfile=new File(f.path);
            return oldfile.renameTo(new File(newname));
        }catch (Exception e){

        }
        return false;
    }



}