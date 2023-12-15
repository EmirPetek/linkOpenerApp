package com.linkopener.linkopener;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private TextView textViewInternetConnection;
    private Button buttonTekrarDene, buttonCikisYap;
    private FloatingActionButton floatingActionButtonAddWebsite;
    private EditText editTextUrlAddress, editTextUrlNickname;
    private TextView textView2, textView3;
    public RecyclerView recyclerView;
    private SharedPreferences sp;
    private SharedPreferences.Editor e;
    private ArrayList < String > userKey = new ArrayList < > ();
    private ArrayList < String > URL = new ArrayList < > ();
    private ArrayList < String > URL_nickname = new ArrayList < > ();
    private ArrayList < String > id = new ArrayList < > ();
    public static websiteCardAdapter adapter;
    public String URL_update, URL_nickname_update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startApp();
    }

    @SuppressLint({
            "ResourceAsColor",
            "SetTextI18n"
    })
    public void startApp() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();

        if (isConnected) { // internet bağlantısı var
            mainActivity();
        } else { //internet bağlantısı yok
            mainActivity2();
        }
    }

    public void mainActivity() {

        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Link Opener'a Hoşgeldiniz!"); // anasayfada toolbar adı değiştiren kısım
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7757E6"))); // toolbar renk değişimi

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.actionBarColor)); // R.color.your_color, kendi renk kaynağınıza göre değiştirir
        }
        Log.e("if", "bloğu");
        floatingActionButtonAddWebsite = findViewById(R.id.floatingActionButtonAddWebsite);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        floatingActionButtonAddWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertAddWebsite();
            }
        });

        sp = getSharedPreferences("userInfo", MODE_PRIVATE);
        e = sp.edit();

        String registerState = sp.getString("registerState", "0");
        String userKeyData = sp.getString("userKey", "0");

        if (registerState.equals("0")) {
            registerUser();
        }

        // if the user registered before, items will come directly.
        // if not register, user will be registered with userKey.

        cardLister();
        adapter = new websiteCardAdapter(this, id, userKey, URL, URL_nickname);
        recyclerView.setAdapter(adapter);
    }

    public String userKeyGetter() {
        String userKeyData = sp.getString("userKey", "0");
        return userKeyData;
    }

    public void alertAddWebsite() {
        View view = getLayoutInflater().inflate(R.layout.alert_add_website, null);
        AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);

        ad.setTitle("URL Ekle");
        ad.setView(view);

        textView2 = view.findViewById(R.id.textView2);
        textView3 = view.findViewById(R.id.textView3);
        boolean isDark = checkDarkMode();
        if (isDark){
            textView2.setTextColor(Color.WHITE);
            textView3.setTextColor(Color.WHITE);
        }else {
            textView2.setTextColor(Color.BLACK);
            textView3.setTextColor(Color.BLACK);
        }

        ad.setPositiveButton("Ekle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                sp = getSharedPreferences("userInfo", MODE_PRIVATE);
                e = sp.edit();

                editTextUrlAddress = view.findViewById(R.id.editTextUrlAddress);
                editTextUrlNickname = view.findViewById(R.id.editTextUrlNickname);


                textView2.setText("Eklemek istediğiniz website URL'sini giriniz:");
                textView3.setText("Eklemek istediğiniz websiteye takma ad giriniz:");



                String URL = editTextUrlAddress.getText().toString();
                String URL_nickname = editTextUrlNickname.getText().toString();
                String registerState = sp.getString("registerState", "0");
                String userKeyData = sp.getString("userKey", "0");

                addWebsiteToDB(userKeyData, URL, URL_nickname);
                finishActivity(adapter.mContext);
            }
        });

        ad.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MainActivity.this, "İşlem iptal edildi!", Toast.LENGTH_SHORT).show();
            }
        });
        ad.create().show();
    }

    public void deleteItem(Context mContext, String id) {

        String url = "";// delete item php link.

        StringRequest requestAdd = new StringRequest(Request.Method.POST, url, new Response.Listener < String > () {
            @Override
            public void onResponse(String response) {
                Toast.makeText(mContext, "Website kısaltıcı başarıyla silindi!", Toast.LENGTH_SHORT).show();
                finishActivity(mContext);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Nullable
            @Override
            protected Map < String, String > getParams() throws AuthFailureError {
                Map < String, String > params = new HashMap < > ();
                params.put("id", id);
                return params;
            }
        };
        Volley.newRequestQueue(mContext).add(requestAdd);
    }

    public void finishActivity(Context mContext) {
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
        finish();
    }

    public void updateItemName(Context mContext, String id, String URL, String URL_nickname) {
        String url = "";// update item php link
        StringRequest requestAdd = new StringRequest(Request.Method.POST, url, new Response.Listener < String > () {
            @Override
            public void onResponse(String response) {
                Log.e("update içi onresponse: ", String.valueOf(adapter));
                finishActivity(mContext);
                Toast.makeText(mContext, "Eleman bilgileri başarıyla güncellendi!", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Nullable
            @Override
            protected Map < String, String > getParams() throws AuthFailureError {
                Map < String, String > params = new HashMap < > ();
                Log.e("url ve nickname set", URL_update + URL_nickname_update);
                params.put("id", String.valueOf(id));
                params.put("URL", URL);
                params.put("URL_nickname", URL_nickname);
                return params;
            }
        };
        Volley.newRequestQueue(mContext).add(requestAdd);
    }

    public void addWebsiteToDB(String userKey, String URL, String URL_nickname) {

        String url = ""; // insert website php link

        StringRequest requestAdd = new StringRequest(Request.Method.POST, url, new Response.Listener < String > () {
            @Override
            public void onResponse(String response) {
                Toast.makeText(adapter.mContext, "Yeni website kısaltıcısı eklendi!", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Nullable
            @Override
            protected Map < String, String > getParams() throws AuthFailureError {
                Map < String, String > params = new HashMap < > ();
                params.put("userKey", userKey);
                params.put("URL", URL);
                params.put("URL_nickname", URL_nickname);
                Log.e("key url nickname", userKey + URL + URL_nickname);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(requestAdd);
    }

    public void cardLister() {
        String url = ""; // select * from table_name php link. take all websites
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener < String > () {
            @Override
            public void onResponse(String response) {
                Log.e("cevap", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray websiteList = jsonObject.getJSONArray("website");
                    for (int i = 0; i < websiteList.length(); i++) {
                        JSONObject k = websiteList.getJSONObject(i);
                        int id_data = k.getInt("id");
                        String userKeyData = k.getString("userKey");
                        String URL_data = k.getString("URL");
                        String URL_nickname_data = k.getString("URL_nickname");

                        if (userKeyGetter().equals(userKeyData)) {

                            id.add(String.valueOf(id_data));
                            userKey.add(userKeyData);
                            URL.add(URL_data);
                            URL_nickname.add(URL_nickname_data);
                        }

                    }
                    adapter.notifyDataSetChanged();
                    Log.e("list adapteri: ", String.valueOf(adapter));
                    Log.e("userkey size: ", String.valueOf(userKey.size()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error of volley", String.valueOf(error));
            }
        });
        Volley.newRequestQueue(this).add(request);
    }


    public boolean checkDarkMode() {
        int currentNightMode = getResources().getConfiguration().uiMode
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

    public void registerUser() {
        SharedPreferences sp = getSharedPreferences("userInfo", MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        String userKey = generateRandomKey();
        e.putString("userKey", userKey);
        e.putString("registerState", "1");
        e.commit();
    }

    public static String generateRandomKey() {
        // Rastgele karakterlerin kullanılacağı karakter kümesi
        String characterSet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        // Rastgele bir karakter kümesi oluşturmak için SecureRandom kullan
        SecureRandom secureRandom = new SecureRandom();

        // 32 karakterlik rastgele bir anahtar oluştur
        StringBuilder randomKeyBuilder = new StringBuilder();
        for (int i = 0; i < 64; i++) {
            int randomIndex = secureRandom.nextInt(characterSet.length());
            char randomChar = characterSet.charAt(randomIndex);
            randomKeyBuilder.append(randomChar);
        }

        return randomKeyBuilder.toString();
    }

    @SuppressLint("ResourceAsColor")
    public void mainActivity2() {
        setContentView(R.layout.activity_main_2);
        Log.e("else", "bloğu");
        getSupportActionBar().hide();
        textViewInternetConnection = findViewById(R.id.textViewInternetConnection);
        textViewInternetConnection.setText("İnternet bağlantısı yok. Lütfen tekrar deneyiniz.");
        textViewInternetConnection.setTextColor(R.color.black);

        buttonCikisYap = findViewById(R.id.buttonCikisYap);
        buttonTekrarDene = findViewById(R.id.buttonTekrarDene);

        buttonTekrarDene.setBackgroundColor(R.color.butonColor1);
        buttonCikisYap.setBackgroundColor(R.color.butonColor1);
        buttonTekrarDene.setTextColor(Color.BLACK);
        buttonCikisYap.setTextColor(Color.BLACK);

        buttonCikisYap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });

        buttonTekrarDene.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}