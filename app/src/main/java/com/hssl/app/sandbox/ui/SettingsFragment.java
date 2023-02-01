package com.hssl.app.sandbox.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Process;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.hssl.app.sandbox.R;
import com.hssl.app.sandbox.config.PropertyManager;
import com.hssl.app.sandbox.databinding.FragmentSettingsBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SettingsFragment extends Fragment {
    private static final String SHARED_PREF_MAIN = "main";
    private static final String SP_KEY_SHOW_SYS_APP = "show_system_app";
    private FragmentSettingsBinding binding;
    private boolean isShowSysApp;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        SharedPreferences sharedPref = requireActivity().getSharedPreferences(
                SHARED_PREF_MAIN, Context.MODE_PRIVATE);
        isShowSysApp = sharedPref.getBoolean(SP_KEY_SHOW_SYS_APP, false);
        binding.buttonModify.setOnClickListener(v -> showSelectPackageDialog());
        binding.switchShowSysApp.setChecked(isShowSysApp);
        binding.switchShowSysApp.setOnCheckedChangeListener(
                (buttonView, isChecked) -> setShowSysAppChecked(isChecked));
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateBinderWatchedUid();
    }

    private void updateBinderWatchedUid() {
        if (binding != null) {
            int watchedUid = PropertyManager.getBinderWatchedUid();
            binding.textBinderWatchedUidValue
                    .setText(getString(R.string.binder_watched_uid_value,
                            getNameForUid(watchedUid), watchedUid));
        }
    }

    public String getNameForUid(int uid) {
        if (Process.isApplicationUid(uid)) {
            String[] packages = requireContext().getPackageManager().getPackagesForUid(uid);
            return packages[0];
        } else if (uid == Process.INVALID_UID) {
            return "not set";
        } else if (uid == Process.ROOT_UID) {
            return "root";
        } else {
            return "system";
        }
    }

    private void showSelectPackageDialog() {
        List<ApplicationInfo> allAppInfos = requireContext().getPackageManager()
                .getInstalledApplications(0);
        List<Integer> allAppUidList = new ArrayList<>();
        List<String> allAppInfoStringList = new ArrayList<>();
        String appStringFormat = "%s (%d)";
        for (ApplicationInfo applicationInfo : allAppInfos) {
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
        if (allAppUidList.size() == 0 && allAppInfoStringList.size() == 0) {
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
            setBinderWatchedUid(allAppUidList.get(which));
            updateBinderWatchedUid();
        });
        listDialog.show();
    }

    private void setBinderWatchedUid(int uid) {
        PropertyManager.setBinderWatchedUid(uid);
    }

    private void setShowSysAppChecked(boolean isChecked) {
        isShowSysApp = isChecked;
        SharedPreferences sharedPref = requireActivity().getSharedPreferences(
                SHARED_PREF_MAIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPref.edit();
        spEditor.putBoolean(SP_KEY_SHOW_SYS_APP, isChecked);
        spEditor.apply();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}