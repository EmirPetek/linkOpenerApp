package com.linkopener.linkopener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class websiteCardAdapter extends RecyclerView.Adapter<websiteCardAdapter.CardViewObjHolder> {

    private List<String> id = new ArrayList<>();
    private List<String> userKey = new ArrayList<>();
    private List<String> URL = new ArrayList<>();
    private List<String> URL_nickname = new ArrayList<>();
    public Context mContext;
    private EditText editTextUrlAddress,editTextUrlNickname;
    private TextView textView2, textView3;

    public websiteCardAdapter(Context mContext, List<String> id, List<String> userKey, List<String> URL, List<String> URL_nickname) {
        this.mContext = mContext;
        this.id = id;
        this.userKey = userKey;
        this.URL = URL;
        this.URL_nickname = URL_nickname;
    }

    public websiteCardAdapter() {
    }

    public class CardViewObjHolder extends RecyclerView.ViewHolder{

        public TextView textViewUrlNickname;
        public ImageView imageViewMore;
        public CardView card_website;

        public CardViewObjHolder(@NonNull View itemView) {
            super(itemView);
            textViewUrlNickname = itemView.findViewById(R.id.textViewUrlNickname);
            imageViewMore = itemView.findViewById(R.id.imageViewMore);
            card_website = itemView.findViewById(R.id.card_website);
        }
    }

    @NonNull
    @Override
    public websiteCardAdapter.CardViewObjHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.website_card,parent,false);
        return new CardViewObjHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull websiteCardAdapter.CardViewObjHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.textViewUrlNickname.setText(URL_nickname.get(position));
        holder.textViewUrlNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("size of url list" , String.valueOf(getItemCount()));
                Intent intent = new Intent(mContext,webviewActivity.class);
                intent.putExtra("webURL",URL.get(position));
                mContext.startActivity(intent);
            }
        });

        holder.imageViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mContext, holder.imageViewMore);
                popupMenu.getMenuInflater().inflate(R.menu.more_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_sil:
                                Snackbar.make(view, "Website Silinsin mi ?"
                                        , Snackbar.LENGTH_SHORT).setAction("Evet", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        new MainActivity().deleteItem(mContext,id.get(position));
                                    }
                                }).show();
                                return true;
                            case R.id.action_guncelle:
                                alertUpdateName(position);
                                notifyDataSetChanged();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });
    }
    @Override
    public int getItemCount() {
        return URL.size();
    }

    public void alertUpdateName(Integer position){
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.alert_add_website, null);
        AlertDialog.Builder ad = new AlertDialog.Builder(mContext);

        ad.setTitle("İsim Güncelleme");
        ad.setView(view);

        editTextUrlAddress = view.findViewById(R.id.editTextUrlAddress);
        editTextUrlNickname = view.findViewById(R.id.editTextUrlNickname);
        textView2 = view.findViewById(R.id.textView2);
        textView3 = view.findViewById(R.id.textView3);

        textView2.setText("Değiştirmek istediğiniz website URL'sini giriniz:",TextView.BufferType.NORMAL);
        textView3.setText("Değişecek olan yeni takma adı giriniz:",TextView.BufferType.NORMAL);

        boolean isDark = checkDarkMode();
        if (isDark){
            textView2.setTextColor(Color.WHITE);
            textView3.setTextColor(Color.WHITE);
        }else {
            textView2.setTextColor(Color.BLACK);
            textView3.setTextColor(Color.BLACK);
        }

        editTextUrlAddress.setText(URL.get(position));
        editTextUrlNickname.setText(URL_nickname.get(position));

        ad.setPositiveButton("Güncelle", new DialogInterface.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String newURL = editTextUrlAddress.getText().toString();
                String newURL_nickname = editTextUrlNickname.getText().toString();

                new MainActivity().updateItemName(mContext,id.get(position),newURL,newURL_nickname);
            }
        });

        ad.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(mContext, "İşlem iptal edildi!", Toast.LENGTH_SHORT).show();
            }
        });
        ad.create().show();
    }
    public boolean checkDarkMode() {
        int currentNightMode = mContext.getApplicationContext().getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;

        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Uygulama karanlık modda değil, gündüz teması kullanılıyor
                return false;
            case Configuration.UI_MODE_NIGHT_YES:
                // Uygulama karanlık modda, gece teması kullanılıyor
                return true;
            default:
                return false;
        }
    }
}