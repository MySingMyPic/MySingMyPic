package com.ylsg365.pai.web.dic;

public enum EnumAction {
    //memberController
    VALIDATECODE("validateCode"),
    REGISTER("register"),
    REGISTER2("register2"),
    LOGIN("login"),
    GETINFO("getInfo"),
    FINDPWDVALIDATECODE("findPwdValidateCode"),
    FINDPWDMEMBER("findPwdMember"),
    RESETPWD("resetPwd"),
    UPDATEPWD("updatePwd"),
    UPDATENICKNAME("updateNickName"),
    UPDATEHEADIMG("updateHeadImg"),
    GETOTHERINFO("getOtherInfo"),
    GETMEALS("getMeals"),
    OPENMEAL("openMeal"),

    //songTypeController
    GETSONGTYPE("getSongType"),

    //singerTypeController
    GETSINGERTYPE("getSingerType"),

    //singerController
    GETSINGERS("getSingers"),

    //songController
    GETSONGS("getSongs"),

    //attentionController
    ATTENTION("attention"),
    CANCELATTENTION("cancelAttention"),
    GETATTENTIONS("getAttentions"),
    GETFANS("getFans"),
    LOOK("look"),
    LOOKLIST("lookList"),

    //fileController
    IMGUPLOAD("imgUpload"),
    VIDEOUPLOAD("videoUpload"),
    AUDIOUPLOAD("audioUpload"),
    CORRECTIONBYFILE("correctionByFile"),
    SNSBYFILE("snsByFile"),

    //newInfoController
    ADDNEWINFO("addNewInfo"),
    NEWINFONICE("newInfoNice"),
    NEWINFOFORWARD("newInfoForward"),
    NEWINFOCOMMENT("newInfoComment"),
    GETNEWINFO("getNewInfo"),
    GETMYNEWINFOS("getMyNewInfos"),
    GETHISNEWINFOS("getHisNewInfos"),
    GETNEWINFOIMG("getNewInfoImg"),
    GETNEWINFOCOMMENTS("getNewInfoComments"),
    GETNEWINFOFORWARDS("getNewInfoForwards"),
    GENEWINFONICES("geNewInfoNices"),
    GETNEWINFOFORWARDSBYFORWARDUSER("getNewInfoForwardsByForwardUser"),
    GETNEWINFOSBYUSERNICE("getNewInfosByUserNice"),
    GETNEWINFOCOMMENTSBYFORWARD("getNewInfoCommentsByForward"),
    GETNEWINFOS("getNewInfos"),

    //privateMsgController
    SENDMSG("sendMsg"),
    GETMSGS("getMsgs"),
    GETSYSMSGS("getSysMsgs"),

    //recordController
    ADDSING("addSing"),
    ADDCLEARSING("addClearSing"),
    ADDVIDEO("addVideo"),
    ADDMV("addMV"),
    RECORDNICE("recordNice"),
    MVNICE("mvNice"),
    RECORDCOMMENT("recordComment"),
    MVCOMMENT("mvComment"),
    NEWINFOCOLLECTION("newInfoCollection"),
    RECORDCOLLECTION("recordCollection"),
    MVCOLLECTION("mvCollection"),
    CANCELRECORDCOLLECTION("cancelRecordCollection"),
    CANCELMVCOLLECTION("cancelMvCollection"),
    GETRECORDNICES("getRecordNices"),
    GETRECORDCOLLECTIONS("getRecordCollections"),
    GETRECORDCOMMENTS("getRecordComments"),
    GETMVNICES("getMvNices"),
    GETMVCOLLECTIONS("getMvCollections"),
    GETMVCOMMENTS("getMvComments"),
    GETMYNICERECORD("getMyNiceRecord"),
    GETMYNICEMV("getMyNiceMv"),
    GETMYCOLLECTION("getMyCollection"),
    GETMYORIGINAL("getMyOriginal"),
    GETORIGINAL("getOriginal"),
    GETHISORIGINAL("getHisOriginal"),
    GETMYWORKS("getMyWorks"),
    GETHISWORKS("getHisWorks"),
    GETMVDETAIL("getMvDetail"),
    GETRECORDDETAIL("getRecordDetail"),
    RECORDPAY("recordPay"),
    GETPAYRECORDS("getPayRecords"),

    //gameCenterController
    GETGAMECENTERS("getGameCenters"),
    GETGAMECENTERDETAIL("getGameCenterDetail"),
    ADDRECORD("addRecord"),
    GETGAMEUSERS("getGameUsers"),
    GETGAMECENTERBYUSERS("getGameCenterByUsers"),

    //houseController
    ADDHOUSE("addHouse"),
    GETHOUSELIST("getHouseList"),
    GETMYHOUSE("getMyHouse"),
    GETHOUSEDETAIL("getHouseDetail"),
    INOUTTHOUSEUSER("inOuttHouseUser"),
    GETHOUSEVIEWS("getHouseViews"),
    HOUSEUPDATE("houseUpdate"),
    HOUSESING("houseSing"),
    GETHOUSESING("getHouseSing"),
    HOUSECHAT("houseChat"),
    GETHOUSECHATS("getHouseChats"),

    //giftController
    GETGIFTLIST("getGiftList"),
    SENDGIFT("sendGift"),
    GETMYGIFTLIST("getMyGiftList"),
    INDEXGIFTLIST("indexGiftList"),

    //cashController
    GETBANKLIST("getBankList"),
    CASH("cash"),
    GETCASHLIST("getCashList"),
    RECHARGE("recharge"),

    GETRECHARGELIST("getRechargeList"),
    SENDINDEXGIFT("sendIndexGift"),
    GETCONTACT("getContact"),
    ;



    private String desc;

    private EnumAction(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

}
