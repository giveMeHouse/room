package com.sns.room.post.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonResponse<T> {
    private String message;
    private T data;
    public CommonResponse(String message, T data){
        this.message = message;
        this.data = data;
    }
}

