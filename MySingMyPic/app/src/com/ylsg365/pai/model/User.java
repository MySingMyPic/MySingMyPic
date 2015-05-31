package com.ylsg365.pai.model;

/**
 * Created by ylsg365 on 2015-03-09.
 */
public class User {
    private int userId;
    private String phone;
    private String nickName;
    private int sex;
    private String headImg;
    private double balance;
    private int fansNum;
    private String cTime;
    private String area;
    private boolean attention;
    private double giftMoney;
    private int attentionNUm;
    private int newInfoNum;
    private String token;
    private int isMeal;

    public int getIsMeal()
    {
        return isMeal;
    }
    public void setIsMeal(int isMeal)
    {
        this.isMeal=isMeal;
    }
    
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getFansNum() {
        return fansNum;
    }

    public void setFansNum(int fansNum) {
        this.fansNum = fansNum;
    }

    public String getcTime() {
        return cTime;
    }

    public void setcTime(String cTime) {
        this.cTime = cTime;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isAttention() {
        return attention;
    }

    public void setAttention(boolean attention) {
        this.attention = attention;
    }

    public double getGiftMoney() {
        return giftMoney;
    }

    public void setGiftMoney(double giftMoney) {
        this.giftMoney = giftMoney;
    }

    public int getAttentionNUm() {
        return attentionNUm;
    }

    public void setAttentionNUm(int attentionNUm) {
        this.attentionNUm = attentionNUm;
    }

    public int getNewInfoNum() {
        return newInfoNum;
    }

    public void setNewInfoNum(int newInfoNum) {
        this.newInfoNum = newInfoNum;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
