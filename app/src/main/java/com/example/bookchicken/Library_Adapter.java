package com.example.bookchicken;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Library_Adapter extends RecyclerView.Adapter<Library_Adapter.CustomViewHolder> implements MyItemClickListener {

    private static ArrayList<Library_Data> arrayList;
    private MyItemClickListener listener;

    public Library_Adapter(ArrayList<Library_Data> arrayList) {this.arrayList = arrayList; }

    public void setOnItemClickListener(MyItemClickListener listener) {
        this.listener = listener;
    }

    public Library_Adapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_library_list,parent,false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Library_Adapter.CustomViewHolder holder, int position) {
        holder.libraryname.setText(arrayList.get(position).getLibraryname());
        holder.codevalue.setText(arrayList.get(position).getcodevalue());
        holder.fdrm_close_date.setText(arrayList.get(position).getfdrm_close_date());
        holder.tel_no.setText(arrayList.get(position).gettel_no());

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

        protected TextView libraryname, codevalue, fdrm_close_date, tel_no;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            libraryname = (TextView) itemView.findViewById(R.id.LBRRY_NM);
            codevalue = (TextView) itemView.findViewById(R.id.BOOK_CO);
            fdrm_close_date = (TextView) itemView.findViewById(R.id.fdrm_close_date);
            tel_no = (TextView) itemView.findViewById(R.id.tel_no);

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
