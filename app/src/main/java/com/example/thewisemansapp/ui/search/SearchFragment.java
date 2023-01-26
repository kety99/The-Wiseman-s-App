package com.example.thewisemansapp.ui.search;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.thewisemansapp.data.DbManager;
import com.example.thewisemansapp.databinding.FragmentSearchBinding;
import com.example.thewisemansapp.model.Advice;
import com.example.thewisemansapp.util.HttpCallback;
import com.example.thewisemansapp.util.HttpUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;

    private DbManager dbManager;

    private final Handler handler = new Handler(Looper.getMainLooper());

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SearchViewModel searchViewModel =
                new ViewModelProvider(this).get(SearchViewModel.class);

        dbManager = new DbManager(this.getActivity());
        dbManager.open();

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ArrayAdapter<Advice> adviceArrayAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1);

        binding.advices.setAdapter(adviceArrayAdapter);

        EditText searchField = binding.search;
        searchField.setOnEditorActionListener((textView, i, keyEvent) -> {
            HttpUtils.searchAdvice(searchField.getText().toString(), new HttpCallback<List<Advice>>() {
                @Override
                public void onResponse(List<Advice> response) {
                    handler.post(() -> {
                        adviceArrayAdapter.clear();
                        adviceArrayAdapter.addAll(response);
                        adviceArrayAdapter.notifyDataSetChanged();
                        searchField.clearFocus();
                        hideKeyboard();
                    });
                }

                @Override
                public void onError(Exception e) {
                    handler.post(() -> {
                        adviceArrayAdapter.clear();
                        adviceArrayAdapter.add(new Advice("Not Found"));
                        searchField.clearFocus();
                        hideKeyboard();
                    });
                }
            });
            return true;
        });

        binding.advices.setOnItemClickListener((adapterView, view, i, l) -> {
            Advice advice = (Advice) adapterView.getAdapter().getItem(i);
            if (dbManager.findById(advice.getId()) == null) {
                dbManager.insert(advice);
            } else {
                dbManager.delete(advice.getId());
            }
        });
        return root;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getActivity().getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(getActivity());
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}