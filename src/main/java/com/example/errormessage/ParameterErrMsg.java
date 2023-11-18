package com.example.errormessage;

public class ParameterErrMsg extends BaseErrMsg {

    public enum ErrEnum {
        REQUIRED(1, "[%s] 為必填"),
        NOT_FOUND(2, "系統找不到[%s]"),
        NOT_FOUND_LIST(2, "找不到[%s], 清單:[%s]"),
        DUPLICATE_KEY(3, "[%s] 不可重複"),
        DUPLICATE_KEY_LIST(3, "[%s]不可重複, 重複的清單:[%s]"),
        NO_PARAM(4, "不可沒有參數"),
        NOT_SUPPORTED(5, "有不支援的參數"),
        AT_LEAST_ONE_PARAMETER_REQUIRED(6, "[%s] 或 [%s] 為必填"),
        AT_LEAST_ONE_PARAMETER_REQUIRED_LIST(7, "以下欄位：[%s] 至少其一為必填"),

        INVALID(100, "[%s] 無效"),
        INVALID_VALUE(101, "[%s] 為無效的值"),
        INVALID_STATUS(111, "[%s] 為無效的狀態"),
        INVALID_TYPE(121, "[%s] 為無效的資料型態"),
        INVALID_FORMAT(131, "[%s] 為無效的格式"),
        INVALID_FORMAT_WITH_SAMPLE(131, "[%s] 為無效的格式。 正確格式:[%s]"),
        INVALID_FORMAT_LIST(131, "[%s] 為無效的格式, 無效的清單:[%s]"),
        INVALID_TIME_FORMAT(132, "[%s] 必須為有效的時間格式，或是符合規定的時間格式"),
        INVALID_TIME_FORMAT_WITH_SAMPLE(132, "[%s] 必須為有效的時間格式。 正確格式:[%s]"),
        INVALID_EMAIL(133, "[%s] 必須為有效的Email格式"),
        INVALID_IMAGE(134, "無效的影像檔案。請上傳 PNG、JPG、JPEG 影像檔案。"),
        INVALID_RAKUTEN_URL(135, "請勿加入外部連結!"),

        OUT_OF_BOUND(200, "[%s] 超出範圍"),
        MUST_EQ(201, "必須 [%s] == [%s]"),
        MUST_GT(202, "%s 需大於 %s"),
        MUST_GE(203, "%s 需大於等於 %s"),
        MUST_LT(204, "%s 需小於 %s"),
        MUST_LE(205, "%s 需小於等於 %s"),
        MUST_STARTS_WITH(206, "[%s] , 必須為 [%s] 開頭"),
        ILLEGAL_PAGINATION(211, "頁數索引必須 >= 0, 每頁資料數必須 >= 1"),
        DATE_DURATION_OUT_OF_BOUND(221, "%s 區間不可超過 %d 天"),
        TIME_DURATION_OUT_OF_BOUND(222, "%s 區間不可超過 %f %s"),

        NOT_A_NUMBER(300, "[%s] 必須是數字"),
        NOT_A_INTEGER(301, "[%s] 必須是整數");

        private int code;
        private String message;

        private ErrEnum(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return this.code;
        }

        public String getMessage() {
            return this.message;
        }
    }

    public ParameterErrMsg(ErrEnum errorEnum, Object... args) {
        super(ErrTypeEnum.PARAMETER, errorEnum.getCode(), formatMsg(errorEnum.getMessage(), args));
    }

}
