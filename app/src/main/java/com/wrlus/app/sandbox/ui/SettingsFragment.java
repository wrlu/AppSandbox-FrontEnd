package com.wrlus.app.sandbox.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Process;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.wrlus.app.sandbox.R;
import com.wrlus.app.sandbox.config.PropertyManager;
import com.wrlus.app.sandbox.databinding.FragmentSettingsBinding;
import com.wrlus.app.sandbox.utils.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    private boolean isShowSysApp;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        SharedPreferences sharedPref = requireActivity().getSharedPreferences(
                Constant.SHARED_PREF_MAIN, Context.MODE_PRIVATE);
        isShowSysApp = sharedPref.getBoolean(Constant.SP_KEY_SHOW_SYS_APP, false);

        binding.buttonClearAll.setOnClickListener(v ->
                clearAllWatchedUid());
        binding.buttonModifyDex.setOnClickListener(v ->
                showSelectPackageDialog(Constant.FEATURE_DEX));
        binding.buttonModifyArtMethod.setOnClickListener(v ->
                showSelectPackageDialog(Constant.FEATURE_ART_METHOD));
        binding.buttonModifyBinder.setOnClickListener(v ->
                showSelectPackageDialog(Constant.FEATURE_BINDER));

        binding.switchShowSysApp.setChecked(isShowSysApp);
        binding.switchShowSysApp.setOnCheckedChangeListener(
                (buttonView, isChecked) -> setShowSysAppChecked(isChecked));
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWatchedUid();
    }

    private void updateWatchedUid() {
        if (binding != null) {
            int dexWatchedUid = PropertyManager.getWatchedUid(Constant.FEATURE_DEX);
            int artMethodWatchedUid = PropertyManager.getWatchedUid(Constant.FEATURE_ART_METHOD);
            int binderWatchedUid = PropertyManager.getWatchedUid(Constant.FEATURE_BINDER);

            binding.textDexWatchedUidValue
                    .setText(getString(R.string.watched_uid_value,
                            getNameForUid(dexWatchedUid), dexWatchedUid));
            binding.textArtMethodWatchedUidValue
                    .setText(getString(R.string.watched_uid_value,
                            getNameForUid(artMethodWatchedUid), artMethodWatchedUid));
            binding.textBinderWatchedUidValue
                    .setText(getString(R.string.watched_uid_value,
                            getNameForUid(binderWatchedUid), binderWatchedUid));
        }
    }

    public String getNameForUid(int uid) {
        if (uid == Process.INVALID_UID) {
            return "not set";
        } else {
            PackageManager pm = requireContext().getPackageManager();
            return pm.getNameForUid(uid);
        }
    }

    private void showSelectPackageDialog(String forWhat) {
        List<ApplicationInfo> allAppInfo = requireContext().getPackageManager()
                .getInstalledApplications(0);
        List<Integer> allAppUidList = new ArrayList<>();
        List<String> allAppInfoStringList = new ArrayList<>();
        String appStringFormat = "%s (%d)";

        for (ApplicationInfo applicationInfo : allAppInfo) {
            if (applicationInfo.packageName.equals(requireContext().getPackageName())) {
                continue;
            }
            if (!isShowSysApp && (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                continue;
            }
            allAppUidList.add(applicationInfo.uid);
            allAppInfoStringList.add(String.format(Locale.getDefault(), appStringFormat,
                    applicationInfo.packageName, applicationInfo.uid));
        }
        if (allAppUidList.isEmpty() && allAppInfoStringList.isEmpty()) {
            if (!isShowSysApp) {
                Toast.makeText(requireActivity(),
                        R.string.no_third_party_app, Toast.LENGTH_SHORT).show();
                return;
            } else {
                throw new IllegalStateException("Cannot find any app including system app.");
            }
        }

        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(requireActivity());
        listDialog.setTitle(getString(R.string.app_select));
        listDialog.setItems(allAppInfoStringList.toArray(new String[0]), (dialog, which) -> {
            PropertyManager.setWatchedUid(forWhat, allAppUidList.get(which));
            updateWatchedUid();
        });
        listDialog.show();
    }

    private void setShowSysAppChecked(boolean isChecked) {
        isShowSysApp = isChecked;
        SharedPreferences sharedPref = requireActivity().getSharedPreferences(
                Constant.SHARED_PREF_MAIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPref.edit();
        spEditor.putBoolean(Constant.SP_KEY_SHOW_SYS_APP, isChecked);
        spEditor.apply();
    }

    private void clearAllWatchedUid() {
        PropertyManager.setWatchedUid(Constant.FEATURE_DEX, -1);
        PropertyManager.setWatchedUid(Constant.FEATURE_ART_METHOD, -1);
        PropertyManager.setWatchedUid(Constant.FEATURE_BINDER, -1);
        updateWatchedUid();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}