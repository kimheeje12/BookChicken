package com.example.bookchicken;

import android.media.Rating;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ReadingRecord_Adapter extends RecyclerView.Adapter<ReadingRecord_Adapter.CustomViewHolder> implements MyItemClickListener {

    private static ArrayList<ReadingRecord_Data> arrayDataList;
    private MyItemClickListener listener;

    public ReadingRecord_Adapter(ArrayList<ReadingRecord_Data> arrayList) {
        this.arrayDataList = arrayList;
    }

    @Override
    public ReadingRecord_Adapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_readingrecord_list,parent,false);
        ReadingRecord_Adapter.CustomViewHolder customViewHolder = new ReadingRecord_Adapter.CustomViewHolder(view);

        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(ReadingRecord_Adapter.CustomViewHolder holder, int position) {

        //책 제목, 작가, 서평 셋팅
        holder.booktitle.setText(arrayDataList.get(position).getBooktitle());
        holder.author.setText(arrayDataList.get(position).getAuthor());

        //ratingBar 셋팅
//        holder.ratingBar.setRating(Float.parseFloat(arrayDataList.get(position).getRatingBar()));
        holder.ratingBar.setRating(arrayDataList.get(position).getRatingBar());

        //이미지 셋팅
        try{
            Uri img_uri = Uri.parse(ReadingRecord_Activity.arrayDataList.get(position).getBookimage());
            Glide.with(holder.bookimage).load(img_uri).into(holder.bookimage);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return (null != arrayDataList ? arrayDataList.size() : 0);
    }

    public static void remove(int position){
        try{
            arrayDataList.remove(position);

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

        ImageView item_Element;
        TextView item_Element2;
        RatingBar item_Element3;

        protected ImageView bookimage;
        protected TextView booktitle;
        protected TextView author;
        protected RatingBar ratingBar;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.bookimage = (ImageView) itemView.findViewById(R.id.bookimage);
            this.booktitle = (TextView) itemView.findViewById(R.id.booktitle);
            this.author = (TextView) itemView.findViewById(R.id.author);
            this.ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);

            item_Element = itemView.findViewById(R.id.recycler_readingrecord);
            item_Element2 = itemView.findViewById(R.id.recycler_readingrecord);
            item_Element3 = itemView.findViewById(R.id.recycler_readingrecord);

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
}
