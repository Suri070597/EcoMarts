/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Timestamp;

public class Token {

    private int tokenId;
    private int accountId;
    private String token;
    private String status;
    private Timestamp timeAdd;
    private Timestamp timeExp;

    public Token() {
    }

    public Token(int tokenId, int accountId, String token, String status, Timestamp timeAdd, Timestamp timeExp) {
        this.tokenId = tokenId;
        this.accountId = accountId;
        this.token = token;
        this.status = status;
        this.timeAdd = timeAdd;
        this.timeExp = timeExp;
    }

    public int getTokenId() {
        return tokenId;
    }

    public void setTokenId(int tokenId) {
        this.tokenId = tokenId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getTimeAdd() {
        return timeAdd;
    }

    public void setTimeAdd(Timestamp timeAdd) {
        this.timeAdd = timeAdd;
    }

    public Timestamp getTimeExp() {
        return timeExp;
    }

    public void setTimeExp(Timestamp timeExp) {
        this.timeExp = timeExp;
    }

    @Override
    public String toString() {
        return "Token{" + "tokenId=" + tokenId + ", accountId=" + accountId + ", token=" + token + ", status=" + status + ", timeAdd=" + timeAdd + ", timeExp=" + timeExp + '}';
    }

}