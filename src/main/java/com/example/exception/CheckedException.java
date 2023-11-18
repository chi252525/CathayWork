package com.example.exception;

import com.example.errormessage.BaseErrMsg;

public class CheckedException extends Exception {
    private static final long serialVersionUID = -3190940007639487112L;

    private BaseErrMsg errMsg;

    public CheckedException(BaseErrMsg errMsg) {
        super(errMsg.toJsonString());
        this.errMsg = errMsg;
    }

    public CheckedException(BaseErrMsg errMsg, Exception exception) {
        super(errMsg.toJsonString(), exception);
        this.errMsg = errMsg;
    }

    public BaseErrMsg getErrMsg() {
        return errMsg;
    }

}
