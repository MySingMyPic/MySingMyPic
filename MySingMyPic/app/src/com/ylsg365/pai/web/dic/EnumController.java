package com.ylsg365.pai.web.dic;

public enum EnumController {
    MEMBER("memberController"),
    SONGTYPE("songTypeController"),
    SINGERTYPE("singerTypeController"),
    SINGER("singerController"),
    SONG("songController"),
    ATTENTION("attentionController"),
    FILE("fileController"),
    NEWINFO("newInfoController"),
    PRIVATEMSG("privateMsgController"),
    RECORD("recordController"),
    GAMECENTER("gameCenterController"),
    HOUSE("houseController"),
    GIFT("giftController"),
    CASH("cashController"),
    ;

    private String desc;

    private EnumController(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

}
