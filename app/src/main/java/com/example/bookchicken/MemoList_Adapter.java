package com.example.bookchicken;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MemoList_Adapter extends RecyclerView.Adapter<MemoList_Adapter.CustomViewHolder> implements MyItemClickListener{

    static ArrayList<MemoList_Data> arrayDataList;
    private MyItemClickListener listener;

    //Adapter에 들어갈 List
    public MemoList_Adapter(ArrayList<MemoList_Data> arrayList){
        this.arrayDataList = arrayList;
    }

    //onCreateViewHolder? 뷰홀더를 생성(레이아웃 생성)
    //ViewHolder를 새로 만들어야 할 때 호출되는 메서드, 이 메서드를 통해 각 아이템을 위한 XML 레이아웃을 이용한 뷰 객체를 생성하고 뷰 홀더에 담아 리턴한다.
    //이때는 뷰의 콘텐츠를 채우지 않는다. 왜냐하면 아직 뷰 홀더가 특정 데이터에 바인딩된 상태가 아니기 때문이다.
    @Override
    public MemoList_Adapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater를 이용하여 XML파일을 inflate시킨다.
        //inflate? XML에 표기된 레이아웃들을 메모리에 객체화시키는 행동(즉, XML 코드들을 객체화해서 사용하기 위함)
        //return 인자는 Viewholder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_memo_list,parent,false);
        CustomViewHolder customViewHolder = new CustomViewHolder(view);

        return customViewHolder;
    }

    //onBindViewHolder? 뷰홀더가 재활용될 때 실행되는 메서드, View Holder를 데이터와 연결할 때 호출되는 메서드, 이를 통해 뷰 홀더의 레이아웃을 채우게 된다.
    //Item을 하나, 하나 보여주는(bind 되는) 함수
    @Override
    public void onBindViewHolder(MemoList_Adapter.CustomViewHolder holder, int position) {
        holder.memodate.setText(arrayDataList.get(position).getMemodate());
        holder.booktitle.setText(arrayDataList.get(position).getBooktitle());
        holder.bookmemo.setText(arrayDataList.get(position).getBookmemo());
        Log.d("바인드 뷰 홀더","데이터 들어가고 있니?");
    }

    //getItemCount? 아이템 갯수 조회(리사이클러뷰의 총 갯수), 데이터 갯수를 반환
    //.size()? ArrayList가 포함하는 요소의 개수 반환
    @Override
    public int getItemCount() {
        Log.d("아이템 카운트", "진행 중");
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
    public void setOnItemLongClickListener(MyItemClickListener listener){this.listener = listener;}

    @Override
    public void onItemClick(View view, int position) {
    }

    @Override
    public void onItemLongClick(View view, int position) {
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView item_Element;

        protected TextView memodate;
        protected TextView booktitle;
        protected TextView bookmemo;

        public CustomViewHolder(View itemView) {
            super(itemView);
            this.memodate = (TextView) itemView.findViewById(R.id.memodate);
            this.booktitle = (TextView) itemView.findViewById(R.id.booktitle);
            this.bookmemo = (TextView) itemView.findViewById(R.id.bookmemo);

            item_Element = itemView.findViewById(R.id.recycler_memolist);

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

    public static MemoList_Data getItem(int position){
        return arrayDataList.get(position);
    }
    public void setItem(int position, MemoList_Data item){
        arrayDataList.set(position, item);
    }

}
