package com.slow.ruijitakeout.controller;

import com.slow.ruijitakeout.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;


/**
 * 文件上传与下载的Controller
 *
 * @author: Slow
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {
    @Value("${slow-ruiji-takeout.baseContextPath}")
    public String basePath ;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        log.info("开始文件上传... :{}",file.toString());
//        将临时图片路径写入磁盘
        String originalFilename = file.getOriginalFilename();
        log.info("原始文件名:{}",originalFilename);

        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        log.info("文件后缀名:{}",suffix);
//      动态生成文件名
        String fileName = UUID.randomUUID().toString().replace("-","")+suffix;
//     创建目录
        File dir = new File(basePath);
        if (!dir.exists()){
            dir.mkdirs();
        }
        try {
//            将文件存到指定目录
            file.transferTo(new File(basePath+fileName));
            log.info("文件上传成功:{}",basePath+fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(fileName);
    }

    /**
     * 文件下载
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response ){
        log.info("开始文件下载...");
        try {
//        获取文件输入流
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
//        响应文件输出流在浏览器展示
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");
//            构造byte数组进行循环读取
            int len =0;
            byte[] bytes = new byte[1024];
//            输入流的内容读到byte数组中
           while ((len = fileInputStream.read(bytes))!=-1){
//               输出流写出
               outputStream.write(bytes,0,len);
               outputStream.flush();
           }
           outputStream.close();
           fileInputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
