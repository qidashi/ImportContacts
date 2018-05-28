package com.xtm.call.importcontacts;

/**
 * Function:
 * Created by TianMing.Xiong on 18-5-25.
 */

public class Contact {
    boolean isCheck = true;
    String name;
    String phone1;
    String phone2;

    public Contact(String name, String phone1, String phone2) {
        this.name = name;
        this.phone1 = phone1;
        this.phone2 = phone2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    @Override
    public int hashCode() {
        return (this.name+this.phone1).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(this==obj){
            return true;
        }
        if(null!=obj && obj.getClass()==Contact.class){
            Contact contact = (Contact) obj;
            if(this.name.equals(contact.name) && this.phone1.equals(contact.name)){
                return true;
            }
        }
        return false;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "name='" + name + '\'' +
                ", phone1='" + phone1 + '\'' +
                ", phone2='" + phone2 + '\'' +
                '}';
    }
}
