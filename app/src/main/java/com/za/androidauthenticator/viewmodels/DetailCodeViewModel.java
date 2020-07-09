package com.za.androidauthenticator.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.za.androidauthenticator.data.contract.SiteIconContract;
import com.za.androidauthenticator.utils.TimeBasedOneTimePasswordUtil;

import java.security.GeneralSecurityException;

public class DetailCodeViewModel extends ViewModel {

    private static final int DEFAULT_TIME_STEP_SECONDS = 30;

    private CalculateCodeThread thread;

    private MutableLiveData<String> timeRemaining;
    private MutableLiveData<String> code;
    private MutableLiveData<String> siteName;
    private MutableLiveData<String> accountName;
    private MutableLiveData<Integer> siteIcon;

    public DetailCodeViewModel() {
        timeRemaining = new MutableLiveData<>();
        code = new MutableLiveData<>();
        siteName = new MutableLiveData<>();
        accountName = new MutableLiveData<>();
        siteIcon = new MutableLiveData<>();
    }

    public MutableLiveData<String> getSiteName() {
        return siteName;
    }

    public MutableLiveData<String> getAccountName() {
        return accountName;
    }

    public MutableLiveData<Integer> getSiteIcon() {
        return siteIcon;
    }

    public MutableLiveData<String> getTimeRemaining() {
        return timeRemaining;
    }

    public MutableLiveData<String> getCode() {
        return code;
    }

    public void updateInfoData(String siteName, String accountName) {
        this.siteName.setValue(siteName);
        this.accountName.setValue(accountName);
        this.siteIcon.setValue(SiteIconContract.getIconId(siteName));
    }

    public void updateCodeData(String key) {
        thread = new CalculateCodeThread(key);
        thread.start();
    }

    public void stopCalculateCode() {
        if (thread != null)
            thread.stopThread();
    }

    class CalculateCodeThread extends Thread {
        private String key;
        private volatile boolean stop = false;

        public CalculateCodeThread(String key) {
            this.key = key;
        }

        public void stopThread() {
            stop = true;
        }

        @Override
        public void run() {
            int tempCode = 0;

            // Calculate remaining time
            long time = DEFAULT_TIME_STEP_SECONDS -
                    ((System.currentTimeMillis() / 1000) % DEFAULT_TIME_STEP_SECONDS);

            // Update UI
            if (!stop)
                timeRemaining.postValue(Long.toString(time));

            // Calculate code via key and system time
            try {
                tempCode = TimeBasedOneTimePasswordUtil.generateNumber(key, System.currentTimeMillis(),
                        DEFAULT_TIME_STEP_SECONDS);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }

            // Update UI
            if (!stop)
                code.postValue(formatToString(tempCode));

            // Repeat count down, but only calculate code one time per a period of 30s
            boolean isCal;
            while (!stop) {
                // Wait 1s
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                time--;
                if (time == 0) {
                    isCal = true;
                    time = DEFAULT_TIME_STEP_SECONDS -
                            ((System.currentTimeMillis() / 1000) % DEFAULT_TIME_STEP_SECONDS);
                }
                else {
                    isCal = false;
                }

                if (!stop)
                    timeRemaining.postValue(Long.toString(time));

                if (isCal) {
                    try {
                        tempCode = TimeBasedOneTimePasswordUtil.generateNumber(key, System.currentTimeMillis(),
                                DEFAULT_TIME_STEP_SECONDS);
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }

                    if (!stop)
                        code.postValue(formatToString(tempCode));
                }
            }
        }

        private String formatToString(int tempCode) {
            String res = Integer.toString(tempCode);
            while (res.length() < 6) {
                res = "0" + res;
            }
            return res.substring(0, 3) + ' ' + res.substring(3);
        }
    }
}