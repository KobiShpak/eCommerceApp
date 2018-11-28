package com.example.kobishpak.hw01;

public class UserInformation {

    private String m_FullName;
    private String m_Email;
    private String m_PhoneNumber;
    private String m_Password;
    private String m_Gender;
    private String m_Date;
    private String m_ImageUri;


    public UserInformation() {}

    public UserInformation(String i_FullName, String i_Email, String i_PhoneNumber, String i_Password, String i_Gender, String i_Date, String i_ImageUri){
        this.m_FullName = i_FullName;
        this.m_Email = i_Email;
        this.m_PhoneNumber = i_PhoneNumber;
        this.m_Password = i_Password;
        this.m_Gender =i_Gender;
        this.m_Date = i_Date;
        this.m_ImageUri = i_ImageUri;
    }

    public String getM_FullName() {
        return m_FullName;
    }

    public String getM_Email() {
        return m_Email;
    }

    public String getM_PhoneNumber() {
        return m_PhoneNumber;
    }

    public String getM_Password() {
        return m_Password;
    }

    public String getM_Gender() {
        return m_Gender;
    }

    public String getM_Date() {
        return m_Date;
    }

    public String getM_ImageUri() {
        return m_ImageUri;
    }

    public void setM_FullName(String m_FullName) {
        this.m_FullName = m_FullName;
    }

    public void setM_Email(String m_Email) {
        this.m_Email = m_Email;
    }

    public void setM_PhoneNumber(String m_PhoneNumber) {
        this.m_PhoneNumber = m_PhoneNumber;
    }

    public void setM_Password(String m_Password) {
        this.m_Password = m_Password;
    }

    public void setM_Gender(String m_Gender) {
        this.m_Gender = m_Gender;
    }

    public void setM_Date(String m_Date) {
        this.m_Date = m_Date;
    }

    public void setM_ImageUri(String m_ImageUri) {
        this.m_ImageUri = m_ImageUri;
    }
}
