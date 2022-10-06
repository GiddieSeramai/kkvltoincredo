package com.extrainch.kkvl.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class MyPreferences {
    // Sharedpref file name
    private static final String PREF_NAME = "bantu_pref";
    private static final String ACCOUNT_ID = "account_id";
    private static final String PHOTO_ID = "PhotoID";
    private static final String LO = "account_id";
    private static final String LOAN_AMOUNT = "loan_a mount";
    private static final String NOTIFICATION = "notification";
    // All Shared Preferences Keys
    private static final String ODSACCOUNT_ID = "ods_account_id";
    private static final String PHONENUMBER = "phone_number";
    private static final String LOAN_ACCOUNT_ID = "LoanAccountID";
    private static final String NATIONALID = "national_id";
    private static final String ACCOUNT_DETAILS_ID = "accounts_details_id";
    private static final String ACCOUNT_NAME = "account_name";
    private static final String PIN = "pin_number";
    private static final String PHOTO_URL = "PhotoUrl";
    private static final String ACCOUNT_NUMBER = "accountNumber";
    private static final String ACCOUNT_TYPE_ID = "AccountType";
    private static final String MEMBER_ACCOUNT_NAME = "accountName";
    private static final String VERIFICATION_CODE = "verificationCode";
    private static final String CLIENT_ID = "clientId";
    private static final String IS_BLOCKED = "0";
    private static final String MEMBER_ID = "memberId";
    private static final String MESSAGE = "message";
    private static final String STATUS = "status";
    private static final String AcBalance = "AcBalance";
    private static final String CLIENT_NAME = "clientName";
    private static final String ACCOUNT_REMINDER = "AccountReminder";
    private static final String AUTH_TOKEN = "authToken";
    private static final String USER_ID = "UserName";
    private static final String IP_ADDRESS = "IpAddress";
    private static final String FROM_DATE = "FromDate";
    private static final String TO_DATE = "ToDate";
    private static final String MBD_ACCOUNTID = "MemberDepositsAccID";
    private static final String FIREBASE_ID = "FireBaseID";
    private static final String OTP = "OTP";
    private static final String APP_LAUNCH_STATE = "first_time";
    private static final String PROFILE_BASE = "profileBase";
    private static final String LOAN_ID = "LoanID";
    private static final String OUR_BRANCH_ID = "OurBranchID";
    int PRIVATE_MODE = 0;
    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;
    private String TAG = MyPreferences.class.getSimpleName();

    // Constructor
    public MyPreferences(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public String getAppLaunchState() {

        if (pref.getString(APP_LAUNCH_STATE, null) != null)
            return pref.getString(APP_LAUNCH_STATE, null);
        else
            return null;
    }

    public void setAppLaunchState(String clientId) {
        editor.putString(APP_LAUNCH_STATE, clientId);
        editor.commit();

    }

    public void setMBDAccountID(String accountID) {
        editor.putString(MBD_ACCOUNTID, accountID);
        editor.commit();

    }

    public String getClientID() {

        if (pref.getString(CLIENT_ID, null) != null)
            return pref.getString(CLIENT_ID, null);
        else
            return null;
    }

    public void setClientID(String clientId) {
        editor.putString(CLIENT_ID, clientId);
        editor.commit();

    }

    public void setStatus(String status, String message) {
        editor.putString(MESSAGE, message);
        editor.putString(STATUS, status);
        editor.commit();

    }

    public void setODSAccID(String accountID) {
        editor.putString(ODSACCOUNT_ID, accountID);
        editor.commit();

    }

    public String getOdsaccountId() {

        if (pref.getString(ODSACCOUNT_ID, null) != null)
            return pref.getString(ODSACCOUNT_ID, null);
        else
            return null;
    }

    public String getOtp() {

        if (pref.getString(OTP, null) != null)
            return pref.getString(OTP, null);
        else
            return null;
    }

    public void setOtp(String otp) {
        editor.putString(OTP, otp);
        editor.commit();

    }

    public String getPhotoID() {

        if (pref.getString(PHOTO_ID, null) != null)
            return pref.getString(PHOTO_ID, null);
        else
            return null;
    }

    public void setPhotoID(String photoID) {
        editor.putString(PHOTO_ID, photoID);
        editor.commit();

    }

    public String getFirebaseId() {

        if (pref.getString(FIREBASE_ID, null) != null)
            return pref.getString(FIREBASE_ID, null);
        else
            return null;
    }

    public void setFirebaseId(String firebaseId) {
        editor.putString(FIREBASE_ID, firebaseId);
        editor.commit();

    }

    public String getMbdAccountid() {

        if (pref.getString(MBD_ACCOUNTID, null) != null)
            return pref.getString(MBD_ACCOUNTID, null);
        else
            return null;
    }

    public String getNotification() {

        if (pref.getString(NOTIFICATION, null) != null)
            return pref.getString(NOTIFICATION, null);
        else
            return null;
    }

    public void setNotification(String notification) {
        editor.putString(NOTIFICATION, notification);
        editor.commit();

    }

    public String getLoanAmount() {

        if (pref.getString(LOAN_AMOUNT, null) != null)
            return pref.getString(LOAN_AMOUNT, null);
        else
            return null;
    }

    public void setLoanAmount(String loanAmount) {
        editor.putString(LOAN_AMOUNT, loanAmount);
        editor.commit();

    }

    public void setAccountDetailsID(String accountID) {
        editor.putString(ACCOUNT_DETAILS_ID, accountID);
        editor.commit();

    }

    public String getAuthToken() {

        if (pref.getString(AUTH_TOKEN, null) != null)
            return pref.getString(AUTH_TOKEN, null);
        else
            return null;
    }

    public void setAuthToken(String authToken) {
        editor.putString(AUTH_TOKEN, authToken);
        editor.commit();

    }

    public String getAccountTypeId() {

        if (pref.getString(ACCOUNT_TYPE_ID, null) != null)
            return pref.getString(ACCOUNT_TYPE_ID, null);
        else
            return null;
    }

    public void setAccountTypeId(String accountTypeId) {
        editor.putString(ACCOUNT_TYPE_ID, accountTypeId);
        editor.commit();

    }

    public String getPIN() {

        if (pref.getString(PIN, null) != null)
            return pref.getString(PIN, null);
        else
            return null;
    }

    public void setPIN(String pin) {
        editor.putString(PIN, pin);
        editor.commit();

    }

    public String getIPAddress() {

        if (pref.getString(IP_ADDRESS, null) != null)
            return pref.getString(IP_ADDRESS, null);
        else
            return null;
    }

    public void setIPAddress(String ipAddress) {
        editor.putString(IP_ADDRESS, ipAddress);
        editor.commit();
    }

    public void setNationalid(String national_id) {
        editor.putString(NATIONALID, national_id);
        editor.commit();

    }

    public String getProfileBase() {
        if (pref.getString(PROFILE_BASE, null) != null)
            return pref.getString(PROFILE_BASE, null);
        else
            return null;
    }

    public void setProfileBase(String profileBase) {
        editor.putString(PROFILE_BASE, profileBase);
        editor.commit();

    }

    public String getFromDate() {

        if (pref.getString(FROM_DATE, null) != null)
            return pref.getString(FROM_DATE, null);
        else
            return null;
    }

    public void setFromDate(String ipAddress) {
        editor.putString(FROM_DATE, ipAddress);
        editor.commit();

    }

    public String getToDate() {

        if (pref.getString(TO_DATE, null) != null)
            return pref.getString(TO_DATE, null);
        else
            return null;
    }

    public void setToDate(String ipAddress) {
        editor.putString(TO_DATE, ipAddress);
        editor.commit();

    }

    public String getUserID() {

        if (pref.getString(USER_ID, null) != null)
            return pref.getString(USER_ID, null);
        else
            return null;
    }

    public void setUserID(String userID) {
        editor.putString(USER_ID, userID);
        editor.commit();

    }

    public String getAccID() {

        if (pref.getString(ACCOUNT_ID, null) != null)
            return pref.getString(ACCOUNT_ID, null);
        else
            return null;
    }

    public void setAccID(String accountID) {
        editor.putString(ACCOUNT_ID, accountID);
        editor.commit();

    }

    public String getAccountDetailsId() {

        if (pref.getString(ACCOUNT_DETAILS_ID, null) != null)
            return pref.getString(ACCOUNT_DETAILS_ID, null);
        else
            return null;
    }

    public String getPhoneNumber() {

        if (pref.getString(PHONENUMBER, null) != null)
            return pref.getString(PHONENUMBER, null);
        else
            return null;
    }

    public void setPhoneNumber(String phoneNumber) {
        editor.putString(PHONENUMBER, phoneNumber);
        editor.commit();

    }

    public String getLoanAccountID() {

        if (pref.getString(LOAN_ACCOUNT_ID, null) != null)
            return pref.getString(LOAN_ACCOUNT_ID, null);
        else
            return null;
    }

    public void setLoanAccountID(String loanAcountID) {
        editor.putString(LOAN_ACCOUNT_ID, loanAcountID);
        editor.commit();

    }

    public String getNationalID() {

        if (pref.getString(NATIONALID, null) != null)
            return pref.getString(NATIONALID, null);
        else
            return null;
    }

    public String getAccountReminder() {

        if (pref.getString(ACCOUNT_REMINDER, null) != null)
            return pref.getString(ACCOUNT_REMINDER, null);
        else
            return null;
    }

    public void setAccountReminder(String accountReminder) {
        editor.putString(ACCOUNT_REMINDER, accountReminder);
        editor.commit();

    }

    public String getBlockCount() {

        if (pref.getString(IS_BLOCKED, null) != null)
            return pref.getString(IS_BLOCKED, null);
        else
            return null;
    }

    public void setBlockCount(String is_blocked) {
        editor.putString(IS_BLOCKED, is_blocked);
        editor.commit();

    }

    public String getMemberID() {

        if (pref.getString(MEMBER_ID, null) != null)
            return pref.getString(MEMBER_ID, null);
        else
            return null;
    }

    public void setMemberID(String memberID) {
        editor.putString(MEMBER_ID, memberID);
        editor.commit();

    }

    public String getVerificationCode() {

        if (pref.getString(VERIFICATION_CODE, null) != null)
            return pref.getString(VERIFICATION_CODE, null);
        else
            return null;
    }

    public void setVerificationCode(String clientId) {
        editor.putString(VERIFICATION_CODE, clientId);
        editor.commit();

    }

    public String getClientName() {

        if (pref.getString(CLIENT_NAME, null) != null)
            return pref.getString(CLIENT_NAME, null);
        else
            return null;
    }

    public String getMessage() {

        if (pref.getString(MESSAGE, null) != null)
            return pref.getString(MESSAGE, null);
        else
            return null;
    }

    public void setMemberAccount(String accountNumber, String accountName) {
        editor.putString(ACCOUNT_NUMBER, accountNumber);
        editor.putString(MEMBER_ACCOUNT_NAME, accountName);
        editor.commit();
        Log.i(TAG, "" + accountName + " " + accountNumber);

    }

    public String getAccountNumber() {

        if (pref.getString(ACCOUNT_NUMBER, null) != null)
            return pref.getString(ACCOUNT_NUMBER, null);
        else
            return null;
    }

    public String getAcBalance() {

        if (pref.getString(AcBalance, null) != null)
            return pref.getString(AcBalance, null);
        else
            return null;
    }

    public String getMemberAccountName() {

        if (pref.getString(MEMBER_ACCOUNT_NAME, null) != null)
            return pref.getString(MEMBER_ACCOUNT_NAME, null);
        else
            return null;
    }

    public void setAccountDetails(String accountId, String accountName, String acBalance) {
        editor.putString(ACCOUNT_ID, accountId);
        editor.putString(ACCOUNT_NAME, accountName);
        editor.putString(AcBalance, acBalance);
        editor.commit();
        Log.e(TAG, "Account details saved. " + accountId);
    }

    public void clearMemberAccountDetails() {
        editor.remove(ACCOUNT_NUMBER);
        editor.remove(MEMBER_ACCOUNT_NAME);
        editor.commit();
    }

    public void clearClientID() {
        editor.remove(CLIENT_ID);
        editor.commit();
    }

    public void clearCustomerBalance() {
        editor.remove(ACCOUNT_DETAILS_ID);
        editor.commit();
    }

    public String getAccountId() {

        if (pref.getString(ACCOUNT_ID, null) != null)
            return pref.getString(ACCOUNT_ID, null);
        else
            return null;
    }

    public String getAccountName() {

        if (pref.getString(ACCOUNT_NAME, null) != null)
            return pref.getString(ACCOUNT_NAME, null);
        else
            return null;
    }

    public void setAccountName(String accountName) {
        editor.putString(ACCOUNT_NAME, accountName);
        editor.commit();

    }

    public void clearFromDate() {
        editor.remove(TO_DATE);
        editor.remove(MEMBER_ACCOUNT_NAME);
        editor.commit();
    }

    public void clearToDate() {
        editor.remove(FROM_DATE);
        editor.commit();
    }

    public String getOurBranchID() {
        if (pref.getString(OUR_BRANCH_ID, null) != null)
            return pref.getString(OUR_BRANCH_ID, null);
        else
            return null;
    }

    public void setOurBranchID(String OurBranchID) {
        editor.putString(OUR_BRANCH_ID, OurBranchID);
        editor.commit();
    }

    public String getLoanID() {

        if (pref.getString(LOAN_ID, null) != null)
            return pref.getString(LOAN_ID, null);
        else
            return null;
    }

    public void setLoanID(String loanID) {
        editor.putString(LOAN_ID, loanID);
        editor.commit();

    }
}
