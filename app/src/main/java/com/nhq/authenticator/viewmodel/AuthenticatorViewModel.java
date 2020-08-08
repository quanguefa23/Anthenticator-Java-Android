package com.nhq.authenticator.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.nhq.authenticator.R;
import com.nhq.authenticator.appcomponent.AuthenticatorApp;
import com.nhq.authenticator.data.entity.AuthCode;
import com.nhq.authenticator.data.repository.Repository;
import com.nhq.authenticator.data.repository.remote.RemoteDataSource;
import com.nhq.authenticator.util.FormatStringUtil;
import com.nhq.authenticator.util.calculator.CalTaskByHandlerThread;
import com.nhq.authenticator.util.calculator.CalculationTask;
import com.nhq.authenticator.util.ui.LoadingDialog;
import com.nhq.authenticator.view.activities.AuthenticatorActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static android.os.Looper.getMainLooper;

public class AuthenticatorViewModel extends ViewModel {

    private static final String SHOW_CODE_PREFERENCES_KEY = "isShowing";
    private static final String PREFERENCES_STATE_NAME = "AuthenticatorState";

    private CalculationTask calculationTask;
    private MutableLiveData<Boolean> showCodesFlag = new MutableLiveData<>(true);
    private MutableLiveData<Boolean> signInFlag = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> triggerUpdateTime = new MutableLiveData<>();
    private MutableLiveData<Boolean> triggerUpdateCode = new MutableLiveData<>();

    private final Repository repository;
    private LiveData<List<AuthCode>> listCodes;

    public AuthenticatorViewModel(Repository repository) {
        this.repository = repository;
        listCodes = this.repository.getLocalDataSource().getListCodesLocal();
    }

    public LiveData<List<AuthCode>> getListCodes() {
        return listCodes;
    }

    public MutableLiveData<Boolean> getTriggerUpdateTime() {
        return triggerUpdateTime;
    }

    public MutableLiveData<Boolean> getTriggerUpdateCode() {
        return triggerUpdateCode;
    }

    public MutableLiveData<Boolean> getShowCodesFlag() {
        return showCodesFlag;
    }

    public MutableLiveData<Boolean> getSignInFlag() {
        return signInFlag;
    }

    public void updateDataCodeAllRows(List<AuthCode> listCodes) {
        if (listCodes.isEmpty()) return;

        CalculationTask.OnUpdateTimeRemaining updateTimeCallback = time -> {
            for (AuthCode item : listCodes)
                item.reTime = time;
            triggerUpdateTime.postValue(true);
        };

        CalculationTask.OnUpdateCode updateCodeCallback = codes -> {
            for (int i = 0; i < listCodes.size(); i++)
                listCodes.get(i).currentCode = FormatStringUtil.formatCodeToStringWithSpace(codes.get(i));
            triggerUpdateCode.postValue(true);
        };

        List<String> keys = new ArrayList<>();
        for (int i = 0; i < listCodes.size(); i++)
            keys.add(listCodes.get(i).key);

        calculationTask = new CalTaskByHandlerThread(keys, updateTimeCallback, updateCodeCallback);
        calculationTask.startCalculate();
    }

    public void stopCalculateCode() {
        if (calculationTask != null)
            calculationTask.stopCalculate();
    }

    public void setTitleShowHideItem(MenuItem showHide) {
        if (showCodesFlag.getValue())
            showHide.setTitle(R.string.hide_codes);
        else
            showHide.setTitle(R.string.show_codes);
    }

    public Intent createFeedBackIntent() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"nhqnhq1@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, AuthenticatorApp.getInstance().
                getString(R.string.feedback_for_za_authenticator));
        return intent;
    }

    public void configShowHideOption(MenuItem item) {
        showCodesFlag.setValue(!showCodesFlag.getValue());
        new Handler(getMainLooper()).postDelayed(() -> setTitleShowHideItem(item), 350);

        //remember selection
        SharedPreferences sharedPreferences = AuthenticatorApp.getInstance().getSharedPreferences(
                PREFERENCES_STATE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SHOW_CODE_PREFERENCES_KEY, showCodesFlag.getValue());
        editor.apply();
    }

    public void handleSignInResult(Intent result, Context context, int requestCode) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener(googleAccount -> {
                    Log.d(AuthenticatorApp.APP_TAG, "Signed in as " + googleAccount.getEmail());

                    // Use the authenticated account to sign in to the Drive service.
                    GoogleAccountCredential credential = GoogleAccountCredential
                            .usingOAuth2(context, Collections.singleton(DriveScopes.DRIVE_APPDATA));
                    credential.setSelectedAccount(googleAccount.getAccount());
                    Drive ggDrive = new Drive.Builder(
                            AndroidHttp.newCompatibleTransport(),
                            new GsonFactory(), credential)
                            .setApplicationName("Drive API Migration")
                            .build();

                    repository.getRemoteDataSource().createGoogleDriveService(ggDrive);
                    signInFlag.setValue(true);

                    // notify to user
                    if (requestCode == AuthenticatorActivity.REQUEST_CODE_SIGN_IN)
                        Toast.makeText(AuthenticatorApp.getInstance(), R.string.sign_in_success,
                                Toast.LENGTH_SHORT).show();

                    switch (requestCode) {
                        case AuthenticatorActivity.REQUEST_CODE_SIGN_IN_BACKUP: {
                            backupToGgDrive(context);
                            break;
                        }
                        case AuthenticatorActivity.REQUEST_CODE_SIGN_IN_RESTORE: {
                            restoreDataFromGgDrive(context);
                            break;
                        }
                    }
                })
                .addOnFailureListener(exception ->
                        Log.e(AuthenticatorApp.APP_TAG, "Unable to sign in", exception));
    }

    public void restoreDataFromGgDrive(Context context) {
        Toast.makeText(AuthenticatorApp.getInstance(), R.string.start_restore, Toast.LENGTH_SHORT).show();
        LoadingDialog loadingDialog = new LoadingDialog(context);
        loadingDialog.start();

        repository.getRemoteDataSource().getListDataFile(new RemoteDataSource.GetListFileCallBack() {
            @Override
            public void onReceive(FileList fileList) {
                List<File> listFile = fileList.getFiles();
                if (listFile.isEmpty()) {
                    handleEmptyDataResult();
                    return;
                }

                String id = getIdBackupFile(listFile);
                if (id.equals(""))
                    handleEmptyDataResult();
                else
                    getCodesViaFileId(id, loadingDialog);
            }

            @Override
            public void onFailure() {
                handleEmptyDataResult();
            }

            private void handleEmptyDataResult() {
                loadingDialog.stop();
                Toast.makeText(AuthenticatorApp.getInstance(), R.string.data_empty,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getCodesViaFileId(String id, LoadingDialog loadingDialog) {
        repository.getRemoteDataSource().getCodesViaFileId(id, new RemoteDataSource.GetCodesCallBack() {
            @Override
            public void onReceive(List<AuthCode> codes) {
                List<AuthCode> newCodes = new ArrayList<>();
                List<AuthCode> oldCodes = listCodes.getValue();
                for (AuthCode code : codes) {
                    if (!isContains(oldCodes, code))
                        newCodes.add(code);
                }
                repository.getLocalDataSource().insertCodes(newCodes);
                loadingDialog.stop();
                Toast.makeText(AuthenticatorApp.getInstance(), R.string.restore_success,
                        Toast.LENGTH_LONG).show();
            }

            private boolean isContains(List<AuthCode> oldCodes, AuthCode code) {
                if (oldCodes == null) return false;
                for (AuthCode oldCode : oldCodes) {
                    if (code.equalInContent(oldCode))
                        return true;
                }
                return false;
            }

            @Override
            public void onFailure() {

            }
        });
    }

    private String getIdBackupFile(List<File> listFile) {
        int maxTime = 0;
        File maxTimeFile = null;
        for (File file : listFile) {
            try {
                String[] arr = file.getName().split("-");
                int time = Integer.parseInt(arr[1]);
                if (time > maxTime) {
                    maxTime = time;
                    maxTimeFile = file;
                }
            }
            catch (Exception e) {
                Log.e(AuthenticatorApp.APP_TAG, e.getMessage());
            }
        }

        if (maxTimeFile == null)
            return "";
        return maxTimeFile.getId();
    }

    public void backupToGgDrive(Context context) {
        Toast.makeText(AuthenticatorApp.getInstance(), R.string.start_backup, Toast.LENGTH_SHORT).show();
        LoadingDialog loadingDialog = new LoadingDialog(context);
        loadingDialog.start();
        
        List<AuthCode> codes = listCodes.getValue();
        if (codes == null || codes.isEmpty()) {
            loadingDialog.stop();
            Toast.makeText(AuthenticatorApp.getInstance(), R.string.must_have_one_code,
                    Toast.LENGTH_LONG).show();
            return;
        }

        String exportString = exportCodesDataToText(codes);
        repository.getRemoteDataSource().createDataFile(exportString, new RemoteDataSource.CreateFileCallBack() {
            @Override
            public void onSuccess() {
                loadingDialog.stop();
                Toast.makeText(AuthenticatorApp.getInstance(), R.string.backup_success,
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure() {
                loadingDialog.stop();
                Toast.makeText(AuthenticatorApp.getInstance(), R.string.fail_connect_server,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private String exportCodesDataToText(List<AuthCode> codes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (AuthCode code : codes)
            stringBuilder.append(code.toString()).append("\n");
        return stringBuilder.toString();
    }

    public boolean updateShowCodesFlagFromPreferences() {
        SharedPreferences sharedPreferences = AuthenticatorApp.getInstance().getSharedPreferences(
                PREFERENCES_STATE_NAME, MODE_PRIVATE);
        boolean res = sharedPreferences.getBoolean(SHOW_CODE_PREFERENCES_KEY, true);
        showCodesFlag.setValue(res);
        return res;
    }

    public void syncTime(Context context) {
        LoadingDialog loadingDialog = new LoadingDialog(context);
        repository.getRemoteDataSource().
                getTime(new RemoteDataSource.GetTimeCallBack() {
            @Override
            public void onResponse(int unixTime) {
                AuthenticatorApp.DIFF_TIME_SECOND =
                        (int) (unixTime - System.currentTimeMillis() / 1000);

                stopCalculateCode();
                updateDataCodeAllRows(listCodes.getValue());
                loadingDialog.stop();
                Toast.makeText(AuthenticatorApp.getInstance(), R.string.sync_time_success,
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure() {
                loadingDialog.stop();
                Toast.makeText(AuthenticatorApp.getInstance(), R.string.server_time_error,
                        Toast.LENGTH_LONG).show();
            }
        });
        loadingDialog.start();
    }
}
