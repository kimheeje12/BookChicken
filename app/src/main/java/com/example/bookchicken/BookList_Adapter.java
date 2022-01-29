package com.example.bookchicken;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class BookList_Adapter extends RecyclerView.Adapter<BookList_Adapter.CustomViewHolder> implements MyItemClickListener{

    private static ArrayList<BookList_Data> arrayList;
    private MyItemClickListener listener;

    //Adapter에 들어갈 List
    public BookList_Adapter(ArrayList<BookList_Data> arrayList){
        this.arrayList = arrayList;
    }

    public BookList_Adapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater를 이용하여 XML파일을 inflate시킨다.
        //inflate? XML에 표기된 레이아웃들을 메모리에 객체화시키는 행동(즉, XML 코드들을 객체화해서 사용하기 위함)
        //return 인자는 Viewholder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_list,parent,false);
        CustomViewHolder customViewHolder = new CustomViewHolder(view);

        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(BookList_Adapter.CustomViewHolder holder, int position) {
//        holder.booktitle.setText(arrayList.get(position).getBooktitle());
//        holder.author.setText(arrayList.get(position).getAuthor());
//        holder.publisher.setText(arrayList.get(position).getPublisher());
//        holder.bookpage.setText(arrayList.get(position).getBookpage());

        //이미지 셋팅
        try{
            Uri img_uri = Uri.parse(BookList_Activity.arrayDataList.get(position).getBookimage());
            Glide.with(holder.bookimage).load(img_uri).into(holder.bookimage);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }

    public void remove(int position){
        try{
            arrayList.remove(position);
        }catch(IndexOutOfBoundsException ex){
            ex.printStackTrace();
        }
    }

    public void setOnItemClickListener(MyItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

//        TextView item_Element;
//
//        protected TextView booktitle;
//        protected TextView author;
//        protected TextView publisher;
//        protected TextView bookpage;
        protected ImageView bookimage;

        ImageView item_Element;

        public CustomViewHolder(View itemView) {
            super(itemView);
//            this.booktitle = (TextView) itemView.findViewById(R.id.booktitle);
//            this.author = (TextView) itemView.findViewById(R.id.author);
//            this.publisher = (TextView) itemView.findViewById(R.id.publisher);
              this.bookimage = (ImageView) itemView.findViewById(R.id.bookimage);

            item_Element = itemView.findViewById(R.id.recycler_booklist);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(listener != null){
                        listener.onItemClick(view, position);
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int position = getAdapterPosition();
                    if(listener != null){
                        listener.onItemLongClick(view, position);
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    public static BookList_Data getItem(int position){
        return arrayList.get(position);
    }
    public void setItem(int position, BookList_Data item){ arrayList.set(position, item); }
}
