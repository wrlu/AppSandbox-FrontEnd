package com.hssl.app.sandbox.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;

import com.hssl.app.sandbox.MainApplication;
import com.hssl.app.sandbox.R;
import com.hssl.app.sandbox.databinding.FragmentHomeBinding;
import com.hssl.app.sandbox.preference.Debug;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.buttonClear.setOnClickListener(v -> onClearData());
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        new Thread(this::updateDataCount).start();
    }

    private void updateDataCount() {
        MainApplication mainApp = (MainApplication) requireActivity().getApplication();
        long binderDataCount = mainApp.getBinderDataCount();
        long httpDataCount = mainApp.getHttpDataCount();
        long dexDataCount = mainApp.getDexDataCount();
        long intentDataCount = mainApp.getIntentDataCount();
        requireActivity().runOnUiThread(()->{
            if (binding == null) {
                Debug.w(TAG, "Fragment binding is null, ignore");
                return;
            }
            binding.textBinder.setText(
                    String.format(getString(R.string.collected_binder_data), binderDataCount));
            binding.textHttp.setText(
                    String.format(getString(R.string.collected_http_data), httpDataCount));
            binding.textDex.setText(
                    String.format(getString(R.string.collected_dex_data), dexDataCount));
            binding.textIntent.setText(
                    String.format(getString(R.string.collected_intent_data), intentDataCount));
        });
    }

    private void onClearData() {
        MainApplication mainApp = (MainApplication) requireActivity().getApplication();
        mainApp.clearData();
        new Thread(this::updateDataCount).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        synchronized (FragmentHomeBinding.class) {
            binding = null;
        }
    }
}