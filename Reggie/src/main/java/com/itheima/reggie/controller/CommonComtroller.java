package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
// import javax.xml.ws.Action;
import java.io.*;
import java.util.UUID;
/**
 * 文件上传和下载
 */
@RestController
@RequestMapping("/common")
public class CommonComtroller {

    @Value("${reggie.path}")
    private String basePath;

    @Autowired
    private HttpServletResponse response;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R upload(MultipartFile file){

        //获取文件后缀名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用UUID重新生成文件名，防止文件名称重复造成文件覆盖
        String fileName = UUID.randomUUID().toString() + suffix;

        //创建一个目录对象
        File dir = new File(basePath);
        //判断当前目录是否存在
        if(!dir.exists()){
            //目录不存在，需要创建
            dir.mkdirs();
        }

        try {
            //将临时文件转存到指定位置
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }


    /**
     * 文件下载
     * @param name
     *
     */
    @GetMapping("/download")
    public void download(String name) {
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(basePath + name));

            ServletOutputStream os = response.getOutputStream();

            byte[] bytes = new byte[1024];

            while (true){
                int read = bis.read(bytes);
                if (read == -1){
                    break;
                }
                os.write(bytes,0,read);
            }

            os.flush();
            os.close();
            bis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
