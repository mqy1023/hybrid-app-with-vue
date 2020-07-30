package com.shxbd.bbm.utils;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;

public class UploadUtil {

    private static final int TIME_OUT = 30*1000;   //超时时间
    private static final String CHARSET = "utf-8"; //设置编码

    private static final String REQUEST_URL = Constants.APP_HOST + "api/uploadImage"; // 上传文件链接

    /**
     * android上传文件到服务器
     * @param filePath  需要上传的文件的路径   /storage/emulated/0/imagePicker/1568182083362.jpg
     * @return  返回响应的内容
     */
    public static String uploadFile(String filePath) {
        String result = null;
        String BOUNDARY = UUID.randomUUID().toString();  //边界标识   随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data";   //内容类型

        try {
            File file = new File(filePath);

            URL url = new URL(REQUEST_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestProperty("filename", file.getName());//添加头部参数，只有这个key 才可以得到对应的文件
//            conn.setRequestProperty("Authorization", "Bearer AACC4841E28B94249C46FE46E3177D5D");//添加头部参数，只有这个key 才可以得到对应的文件
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true);  //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(false);  //不允许使用缓存
            conn.setRequestMethod("POST");  //请求方式
            conn.setRequestProperty("Charset", CHARSET);  //设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            conn.setRequestProperty("action", "upload");
            conn.connect();
            Log.e("upload", "re==uploadFile=====" + file.getPath() + "......." + file.getName());
            if (file != null) {
                /**
                 * 当文件不为空，把文件包装并且上传
                 */
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                StringBuffer sb = new StringBuffer();

                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意：
                 * name里面的值为服务器端需要key   只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的   比如:abc.png
                 */

                sb.append("Content-Disposition: form-data; name=\"file\"; filename=\""+ URLEncoder.encode(file.getName())+"\""+LINE_END);
                sb.append("Content-Type: application/octet-stream; charset="+CHARSET+LINE_END);
                sb.append(LINE_END);


                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                /**
                 * 获取响应码  200=成功
                 * 当响应成功，获取响应的流
                 */
                int res = conn.getResponseCode();
                if (res == 200) {
                    InputStream input = conn.getInputStream();
//                    StringBuffer sb1= new StringBuffer();
                    int ss;
                    byte[] buffer = new byte[1024];
                    StringBuilder builder = new StringBuilder();
                    while ((ss = input.read(buffer)) != -1) {
//                        sb1.append((char)ss);
                        builder.append(new String(buffer, 0, ss, "UTF-8"));
                    }
                    result = builder.toString();
                    System.out.println(result);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }

}
