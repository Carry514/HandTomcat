package org.gao.page02;

import java.util.HashMap;

/**
 * @BelongsProject: HandTomcat
 * @BelongsPackage: org.gao.page02
 * @Author: 高
 * @CreateTime: 2025-06-21 17:21
 * @Description: 初步解析请求消息
 * @Version: 1.0
 */
public class MyHttpRequest {

    /*
     * 属性:
     * 请求方法  get post
     * URL      127.0.0.1:9090/login.html
     *          127.0.0.1:9090/333.jpg
     *          127.0.0.1:9090/login?account=zhangsan&password=123456
     * 请求模块
     * 协议版本
     * 参数列表 --> HashMap
     * 请求头列表 --> HashMap
     * 请求体
     *
     * 三个方法  解析: 请求行 请求头 请求体
     */
    // 请求行
    private String requestLine;
    // 请求消息
    private String requestMSG;
    // 请求方法
    private String requestMethod;
    // 请求URL
    private String requestURL;
    // 请求模块
    private String requestModel;
    // 协议版本
    private String protocol;
    // 请求体
    private String requestBody;
    // 请求参数
    private HashMap<String , String> requestParams = new HashMap<>();
    // 请求头参数
    private HashMap<String , String> requestHeadParams = new HashMap<>();




    public MyHttpRequest(String requestMSG) {
        this.requestMSG = requestMSG;

        // 解析消息
        praseRequsetMSG();
    }


    /**
     * @Description: 解析消息
     * @Param:
     * @return:
     */
    public void praseRequsetMSG(){

        // 1.通过回车换行 切割消息
        String[] splitRequestMSGArray = requestMSG.split("\r\n");
        // 2.给请求行赋值
        requestLine = splitRequestMSGArray[0];
        //切割请求行
        praseRequestLine();
//        System.out.println(splitRequestMSGArray[splitRequestMSGArray.length - 1]);
        // 给请求体赋值
        if(requestMethod.equals("POST")){
            // 请求体
            requestBody = splitRequestMSGArray[splitRequestMSGArray.length - 1];
            // 解析请求体
            praseRequestBody();
        }
        // 解析请求头
        praseRequestHead(splitRequestMSGArray);

    }

    // 返回值? 参数列表?
    /**
    * @Description: 解析请求行
    * @Param:
    * @return:
    */
    public void praseRequestLine(){

        // 通过空格切割请求行 获取请求方法 URL 协议版本
        String[] splitRequestLineArray = requestLine.split(" ");
        if (splitRequestLineArray.length < 3) {
            return;
        }
        // 请求方法
        requestMethod = splitRequestLineArray[0];
        // RUL
        requestURL = splitRequestLineArray[1];
        //协议版本
        protocol = splitRequestLineArray[2];

//        login?account=lisi&password=123456
        /**
         * 1.判断有没有问号
         * 2.先判断方法
         * 3.
         */
        // 判断是否为GET请求
        if (requestMethod.equals("GET")){
            //判断有没有问号
            if(requestURL.contains("?")){
                // 按照第一个问号来切割
                String[] splitRequestURLArray = requestURL.split("\\?",2);
                // 请求模块
                requestModel = splitRequestURLArray[0];
                // 按照&切割参数
                String[] split = splitRequestURLArray[1].split("&");

                for (String item : split) {
                    String[] value = item.split("=");
                    // 防止越界报错
                    if(value.length>1){
                        requestParams.put(value[0],value[1]);
                    }
                }

//                System.out.println(requestParams);
//                System.out.println(requestParams.get("account"));
            }
        }

    }
    /**
     * @Description: 解析请求头
     * @Param:
     * @return:
     */
    public void praseRequestHead(String[] splitRequestMSGArray){
//        System.out.println("-------------------------------");
        for (int i = 1; i < splitRequestMSGArray.length; i++) {
            String[] splitRequestHead = splitRequestMSGArray[i].split(": ");
            // 有头 没有值 会报错
            if(splitRequestHead.length>1){
                requestHeadParams.put(splitRequestHead[0],splitRequestHead[1]);
            }
        }
//        System.out.print(requestHeadParams);

    }

    /**
     * @Description: 解析请求体
     * @Param:
     * @return:
     */
    public void praseRequestBody(){
        String[] split = requestBody.split("&");

        for (String item : split) {
            String[] value = item.split("=");
            // 防止越界报错
            if(value.length>1){
                requestParams.put(value[0],value[1]);
            }
        }

        System.out.println(requestParams);
    }

    public String getProtocol() {
        return protocol;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public String getRequestModel() {
        return requestModel;
    }

    public String getRequestURL() {
        return requestURL;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getRequestMSG() {
        return requestMSG;
    }

    public String getRequestLine() {
        return requestLine;
    }

    public void setRequestHeadParams(HashMap<String, String> requestHeadParams) {
        this.requestHeadParams = requestHeadParams;
    }
}
