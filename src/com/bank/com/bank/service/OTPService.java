package com.bank.service;

public class OTPService {

    public static int generateOTP() {
        return (int)(Math.random() * 9000) + 1000; // 4 digit OTP
    }
}