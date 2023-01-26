package com.example.thewisemansapp.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.thewisemansapp.data.DbManager;
import com.example.thewisemansapp.databinding.FragmentHomeBinding;
import com.example.thewisemansapp.model.Advice;
import com.example.thewisemansapp.util.HttpCallback;
import com.example.thewisemansapp.util.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private DbManager dbManager;
    private Advice advice;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dbManager = new DbManager(this.getActivity());
        dbManager.open();
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        HttpUtils.getRandomAdvice(new HttpCallback<Advice>() {
            @Override
            public void onResponse(Advice response) {
                handler.post(()-> {
                    advice = response;
                    binding.textHome.setText(response.getAdvice());
                });
            }

            @Override
            public void onError(Exception e) {

            }
        });

        final Button button = binding.adviceButton;
        button.setOnClickListener(view -> {
            HttpUtils.getRandomAdvice(new HttpCallback<Advice>() {
                @Override
                public void onResponse(Advice response) {
                    handler.post(()-> {
                        advice = response;
                        binding.textHome.setText(response.getAdvice());
                    });
                }

                @Override
                public void onError(Exception e) {

                }
            });
        });

        binding.favoritesButton.setOnClickListener(view -> {
            if (dbManager.findById(advice.getId()) == null) {
                dbManager.insert(advice);
            } else {
                dbManager.delete(advice.getId());
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}