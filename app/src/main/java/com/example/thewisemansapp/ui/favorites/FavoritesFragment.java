package com.example.thewisemansapp.ui.favorites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.thewisemansapp.data.DbManager;
import com.example.thewisemansapp.databinding.FragmentFavoritesBinding;
import com.example.thewisemansapp.model.Advice;

public class FavoritesFragment extends Fragment {
    private FragmentFavoritesBinding binding;

    private DbManager dbManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dbManager = new DbManager(this.getActivity());
        dbManager.open();

        FavoritesViewModel favoritesViewModel =
                new ViewModelProvider(this).get(FavoritesViewModel.class);

        binding = FragmentFavoritesBinding.inflate(inflater, container, false);

        ArrayAdapter<Advice> adviceArrayAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, dbManager.findAll());

        binding.advices.setAdapter(adviceArrayAdapter);

        binding.advices.setOnItemClickListener((adapterView, view, i, l) -> {
            Advice advice = (Advice) adapterView.getAdapter().getItem(i);
            dbManager.delete(advice.getId());
            adviceArrayAdapter.remove(advice);
            adviceArrayAdapter.notifyDataSetChanged();
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}