package com.example.databank.bottomnav.chats;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.databank.databinding.FragmentChatsBinding;

public class ChatsFragment extends Fragment {
    public FragmentChatsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatsBinding.inflate(inflater, container, false);
        FragmentChatsBinding.inflate(inflater, container, false);
        binding.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(ChatsFragment.this.getActivity(), ChatActivity.class);
                startActivity(homeIntent);
            }
        });



        return binding.getRoot();
    }
}
