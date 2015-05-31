package com.ylsg365.pai.web.dic;

public enum EnumParameter {
    PHONE("phone"),
    NEWPHONE("newPhone"),
    PWD("pwd"),
    VALIDATECODE("validateCode"),
    TOKEN("token"),
    OLDPWD("oldPwd"),
    NICKNAME("nickName"),
    SEX("sex"),
    AREA("area"),
    HEADIMG("headImg"),
    TYPEID("typeid"),
    PAGE("page"),
    ROWS("rows"),
    SINGERID("singerid"),
    SONGNAME("songName"),
    ATTENTIONID("attentionId"),
    TYPE("type"),
    FILE("File"),
    USERID("userId"),
    TEXT("text"),
    IMAGES("images"),
    FORWARDUSERIDS("forwardUserIds"),
    FORWARDUSERID("forwardUserId"),
    NEWINFOID("newInfoId"),
    FORWARDTEXT("forwardText"),
    NTEXT("ntext"),
    RECEIVEUSERID("receiveUserId"),
    RECORDURL("recordUrl"),
    SONGID("songId"),
    SCORE("score"),
    RECORDTIME("recordTime"),
    NDESC("ndesc"),
    PRICE("price"),
    RECORDID("recordId"),
    MVID("mvId"),
    NID("nid"),
    NNAME("nname"),
    GAMECENTERID("gameCenterId"),
    CONTACT("contact"),
    ACCESSAUTH("accessAuth"),
    SINGAUTH("singAuth"),
    IMGURL("imgUrl"),
    HOUSENO("houseNo"),
    NOTICE("notice"),
    ZHUBOID("zhuboId"),
    MANAGEIDS("manageIds"),
    AUTOQIEMAI("autoQiemai"),
    HOUSEID("houseId"),
    GIFTID("giftId"),
    GCOUNT("gCount"),
    BANKCARD("bankCard"),
    IDNUMBER("idNumber"),
    BANKID("bankId"),
    BANKBRANCHNAME("bankBranchName"),
    BANKPHONE("bankPhone"),
    MONEY("money"),
    MEALID("mealId"),
    ntype("ntype"),
    NTYPE("ntype"),
    typeId("typeId"),
    singerId("singerId")
    ;

    private String desc;

    private EnumParameter(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

}
