package org.gao.page03;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @BelongsProject: HandTomcat
 * @BelongsPackage: org.gao.page03
 * @Author: 高
 * @CreateTime: 2025-06-24 17:32
 * @Description: 静态资源处理器
 * @Version: 1.0
 */
public class StaticResourceHandler {
    /*
     * 文件路径 --> 字节
     * 媒体类型
     *
     * 属性：
     * 文件路径
     *
     */
    // 文件路径
    private String filePath;
    // 文件的字节数据
    private byte[] fileBytes;
    // 文件的媒体类型
    private String media;

    public StaticResourceHandler(String filePath) {
        this.filePath = filePath;

        getFileByte();
        getFileMedia();
    }

    /**
    * @Description: 获取文件的字节数组
    * @Param:
    * @return:
    */
    private void getFileByte(){
        try (FileInputStream fis = new FileInputStream(filePath)) {
            fileBytes = new byte[fis.available()];
            fis.read(fileBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * @Description: 获取文件的媒体类型
     * @Param:
     * @return:
     */
    private void getFileMedia(){

        try {
            media = Files.probeContentType(Paths.get(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        String[] split = filePath.split("\\.");
//        // 后缀
//        String suffix = split[split.length - 1];
//
//        if(suffix.equals("html")){
//            media = "text/html";
//        }else if(suffix.equals("jpg")){
//            media = "image/jpg";
//        }else if(suffix.equals("png")){
//            media = "image/png";
//        }
    }

    public String getFilePath() {
        return filePath;
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public String getMedia() {
        return media;
    }
}
