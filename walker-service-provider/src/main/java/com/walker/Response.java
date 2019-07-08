package com.walker;

import io.swagger.annotations.*;
import lombok.Data;

import java.io.Serializable;


@ApiModel(value="基础返回类value",description="基础返回类description")
@Data
public class Response<T> implements Serializable {

    public static final String INFO_TRUE = "操作成功";
    public static final String INFO_FALSE = "操作失败";


    @ApiModelProperty(example="true")
    Boolean flag;

    @ApiModelProperty(example="操作成功")
    String info;

    @ApiModelProperty(example="128")
    Long costTime;

    public static String getInfoTrue() {
        return INFO_TRUE;
    }

    public static String getInfoFalse() {
        return INFO_FALSE;
    }

    public Boolean getFlag() {
        return flag;
    }

    public Response<T> setFlag(Boolean flag) {
        this.flag = flag;
        return this;
    }

    public String getInfo() {
        return info;
    }

    public Response<T> setInfo(String info) {
        this.info = info;
        return this;
    }

    public Long getCostTime() {
        return costTime;
    }

    public Response<T> setCostTime(Long costTime) {
        this.costTime = costTime;
        return this;
    }

    public T getData() {
        return data;
    }

    public Response<T> setData(T data) {
        this.data = data;
        return this;
    }

    @ApiModelProperty(example="{id:1, name:2}")
    T data;


    Response(Boolean flag, String info, T data){
        this.flag = flag;
        this.info = info;
//        this.costTime = costTime;
        this.data = data;
    }




    public static <T> Response<T> make(Boolean sta, String info, T data){
        return new Response<T>(sta, info, data);
    }

    public static <T> Response<T> makeTrue(String info){
        return make(true, info, null);
    }
    public static <T> Response<T> makeTrue(String info, T data){
        return make(true, info, data);
    }
    public static <T> Response<T> makeFalse(String info){
        return make(false, info, null);
    }
    public static <T> Response<T> makeFalse(String info, T data){
        return make(false, info, data);
    }

}
