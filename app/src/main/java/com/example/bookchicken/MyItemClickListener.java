package com.example.bookchicken;

import android.content.Intent;
import android.view.View;

public interface MyItemClickListener {
    void onItemClick(View view, int position);
    void onItemLongClick(View view, int position);
}
