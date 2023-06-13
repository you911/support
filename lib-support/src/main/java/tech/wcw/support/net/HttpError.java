package tech.wcw.support.net;

/**
 * @Author: tech_wcw
 * @Eamil tech_wcw@163.com
 * @Data: 2021/2/5 11:23
 * @Description:
 */
public class HttpError extends IllegalArgumentException {
    int code;
    String msg;

    public HttpError(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public HttpError(String message, Throwable cause, int code, String msg) {
        super(message, cause);
        this.code = code;
        this.msg = msg;
    }

    public HttpError(Throwable cause, int code, String msg) {
        super(cause);
        this.code = code;
        this.msg = msg;
    }
}
