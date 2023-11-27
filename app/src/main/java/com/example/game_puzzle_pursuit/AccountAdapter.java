package com.example.game_puzzle_pursuit;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.MyViewHoder> {
    List<Account> accountList;

    public AccountAdapter(List<Account> accountList){
        this.accountList = accountList;
    }

    @NonNull
    @Override
    public AccountAdapter.MyViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_user, parent, false);
        return new MyViewHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountAdapter.MyViewHoder holder, int position) {
        Account account = accountList.get(position);

        Log.d("AccountAdapter", "UserName: " + account.getUserName());
        holder.userNameTextView.setText(account.getUserName());
        holder.scoreTextView.setText(String.valueOf(account.getTotalScore()));
    }

    @Override
    public int getItemCount() {
        return accountList.size();
    }

    public class MyViewHoder extends RecyclerView.ViewHolder {
        public TextView userNameTextView;
        public TextView scoreTextView;

        public MyViewHoder(View view) {
            super(view);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            scoreTextView = itemView.findViewById(R.id.scoreTextView);
        }
    }
}
