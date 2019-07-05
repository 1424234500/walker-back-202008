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
    @ApiModelProperty(example="{id:1, name:2}")
    T data;


    Response(Boolean flag, String info, Long costTime){
        this.flag = flag;
        this.info = info;
        this.costTime = costTime;
    }




    public static <T> Response<T> getTrue(String info){
        return new Response<T>(true, info, 1L);
    }

}
