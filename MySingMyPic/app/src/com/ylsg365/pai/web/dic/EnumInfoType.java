package com.ylsg365.pai.web.dic;

public enum EnumInfoType {
    RECORD("record", 0),
    CAPPELLA("cappella", 1),
    VIDEO("video", 2),
    MV("mv", 3),
    NEWS("news", 4),
    ;

    private String desc;
    private int type;

    private EnumInfoType(String desc, int type) {
        this.desc = desc;
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public int getType(){
        return type;
    }

    public static EnumInfoType getEnumInfoType(int type){
        EnumInfoType[] status = EnumInfoType.values();
        for(int i = 0; i < status.length; i++) {
            if(status[i].getType() == type) {
                return status[i];
            }
        }

        return null;
    }

}
