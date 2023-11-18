package com.example.errormessage;

public class ApiErrMsgCenter {
    public static class CoinDeskApi {
        public static final BaseErrMsg ERROR_CURRENCY_ID_IS_REQUIRED = new ParameterErrMsg(ParameterErrMsg.ErrEnum.REQUIRED, "currencyId");
        public static final BaseErrMsg ERROR_CURRENCY_ID_NOT_FOUND = new ParameterErrMsg(ParameterErrMsg.ErrEnum.NOT_FOUND, "currencyId");
    }
}
