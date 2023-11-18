package com.example.errormessage;

public class BaseErrMsg {
    protected enum ErrTypeEnum {
        UNEXPECTED(1),
        AUTH(2),
        PARAMETER(3),
        BUSINESS_LOGIC(4),
        EXTERNAL_FAULT(5),
        DB_ERROR(6),
        DATA_NOT_FOUND(7),
        TOO_MUCH_DATA(8);

        private int typeCode;

        private ErrTypeEnum(int typeCode) {
            this.typeCode = typeCode;
        }

        public int getTypeCode() {
            return this.typeCode;
        }
    }

    // ===============================================================================================================================

    private int type;
    private String typeName;
    private int code;
    private String message;

    protected BaseErrMsg(ErrTypeEnum typeEnum, int code, String message) {
        this.type = typeEnum.getTypeCode();
        this.typeName = typeEnum.name();
        this.code = code;
        this.message = message;
    }

    protected BaseErrMsg(int type, String typeName, int code, String message) {
        this.type = type;
        this.typeName = typeName;
        this.code = code;
        this.message = message;
    }

    public int getType() {
        return type;
    }

    protected void setType(int type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    protected void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getCode() {
        return code;
    }

    protected void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    protected void setMessage(String message) {
        this.message = message;
    }

    public String toJsonString() {
        return new Gson().toJson(this);
    }

    @Override
    public String toString() {
        return toJsonString();
    }

    protected static String formatMsg(String msg, Object... args) {
        try {
            return String.format(msg, args);
        } catch (Exception e) {
            e.printStackTrace();
            return msg;
        }
    }
}