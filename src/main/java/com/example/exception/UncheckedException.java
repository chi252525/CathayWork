package com.example.exception;

import com.example.errormessage.BaseErrMsg;

public class UncheckedException extends RuntimeException {
    private static final long serialVersionUID = -1258755665478203525L;

    private BaseErrMsg errMsg;

    public UncheckedException(BaseErrMsg errMsg) {
        super(errMsg.toJsonString());
        this.errMsg = errMsg;
    }

    public UncheckedException(BaseErrMsg errMsg, Exception exception) {
        super(errMsg.toJsonString(), exception);
        this.errMsg = errMsg;
    }

    public BaseErrMsg getErrMsg() {
        return errMsg;
    }

}
