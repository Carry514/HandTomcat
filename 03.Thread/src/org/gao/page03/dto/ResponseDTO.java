package org.gao.page03.dto;

/**
 * @BelongsProject: boot_maven1
 * @BelongsPackage: org.example.dto
 * @Author: 高
 * @CreateTime: 2025-05-23 15:11
 * @Description: 统一格式返回给前端
 * @Version: 1.0
 */

public class ResponseDTO {
    private Integer code;   //状态码   1-成功    -1-失败
    private String msg;     //提示文字
    private Object data;    //数据

    //可以用枚举类
    private static final Integer SUCCESS_CODE = 1;
    private static final Integer ERROR_CODE = -1;

    public ResponseDTO(Integer code, String msg, Object data) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }


    /**
    * @Description: 成功的回调（带数据）
    * @Param:
    * @return:
    */
    public static ResponseDTO success(Object data){ return new ResponseDTO(SUCCESS_CODE,"success",data); }
    //成功    （不带数据）
    public static ResponseDTO success(){
        return new ResponseDTO(SUCCESS_CODE,"success",null);
    }
    //失败
    public static ResponseDTO error(){
        return new ResponseDTO(ERROR_CODE,"error",null);
    }
    //失败    （带自定义消息）
    public static ResponseDTO error(String message){
        return new ResponseDTO(ERROR_CODE,message,null);
    }
    //返回值是影响行数
    public static ResponseDTO effectResult(Integer res){
        return res > 0 ? success(null) : error(null);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
