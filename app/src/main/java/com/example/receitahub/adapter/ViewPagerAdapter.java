package com.example.receitahub.adapter; // Ou o seu pacote principal, ex: com.example.receitahub

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.receitahub.CreatedRecipesFragment;
import com.example.receitahub.FavoritedRecipesFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new FavoritedRecipesFragment();
        }
        return new CreatedRecipesFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}