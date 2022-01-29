package com.example.bookchicken;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ViewTarget;

import java.util.ArrayList;

public class BookSearch_Adapter extends RecyclerView.Adapter<BookSearch_Adapter.CustomViewHolder> implements MyItemClickListener{

    private static ArrayList<BookSearch_Data> arrayList;
    public static Context context;
    private MyItemClickListener listener;

    public BookSearch_Adapter(ArrayList<BookSearch_Data> arraylist){
        this.arrayList = arraylist;
    }

    public void setOnItemClickListener(MyItemClickListener listener) {
        this.listener = listener;
    }

    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_search,parent,false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
            holder.author.setText(arrayList.get(position).getAuthor());
            holder.publisher.setText(arrayList.get(position).getPublisher());
            holder.price.setText(arrayList.get(position).getBookprice());

        //이미지 셋팅
        String bookimage = arrayList.get(position).getBookImage();
        Glide.with(holder.bookimage).load(bookimage).into(holder.bookimage);

    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }


    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onItemLongClick(View view, int position) {

    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected ImageView bookimage;

        protected TextView author, publisher, price;

//        ImageView item_Element;
//        TextView item_Element2;

        public CustomViewHolder(View itemView) {
            super(itemView);

            bookimage = (ImageView) itemView.findViewById(R.id.bookimage);

            author = (TextView) itemView.findViewById(R.id.author);
            publisher = (TextView) itemView.findViewById(R.id.publisher);
            price = (TextView) itemView.findViewById(R.id.price);

//            item_Element = itemView.findViewById(R.id.recycler_booksearch);
//            item_Element2 = itemView.findViewById(R.id.recycler_booksearch);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(listener != null){
                        listener.onItemClick(view, position);
                    }
                }
            });
        }
    }
}
