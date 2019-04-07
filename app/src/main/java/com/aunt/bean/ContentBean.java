package com.aunt.bean;

/**
 * Created by Administrator on 2017/9/21.
 */

public class ContentBean {
  public String message;
    public int id;
    public String getMessage(){return message;}
    public int getId(){return id;}
    public void setId(int id){
        this.id=id;
    }
    public void setMessage(String message){
        this.message=message;
    }
    @Override
    public  String toString(){
        return "Content [message="+message+"]";
    }
}
