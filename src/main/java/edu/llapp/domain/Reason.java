package edu.llapp.domain;

/**
 * reason for recommending a course
 * explain why recommending this course
 */
public class Reason {
    private String code;      // reason code，eg "FILLS_GAP", "FREE", "SHORT"
    private String message;   // reason description

    public Reason(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Reason{" + code + ": " + message + "}";
    }
}