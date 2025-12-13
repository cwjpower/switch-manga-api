package com.switchmanga.api.util;

/**
 * API 공통 응답 클래스
 */
public class ApiResponse<T> {
    
    private int code;
    private String msg;
    private T data;

    // ==================== Constructors ====================
    
    public ApiResponse() {}

    public ApiResponse(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // ==================== Static Factory Methods ====================

    /**
     * 성공 응답
     */
    public static <T> ApiResponse<T> success(String msg, T data) {
        return new ApiResponse<>(0, msg, data);
    }

    /**
     * 성공 응답 (메시지만)
     */
    public static <T> ApiResponse<T> success(String msg) {
        return new ApiResponse<>(0, msg, null);
    }

    /**
     * 에러 응답
     */
    public static <T> ApiResponse<T> error(String msg) {
        return new ApiResponse<>(-1, msg, null);
    }

    /**
     * 에러 응답 (코드 지정)
     */
    public static <T> ApiResponse<T> error(int code, String msg) {
        return new ApiResponse<>(code, msg, null);
    }

    // ==================== Getters & Setters ====================

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
