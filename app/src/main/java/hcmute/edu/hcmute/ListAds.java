package hcmute.edu.hcmute;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import Adapter.AdsAdapter;
import Models.Ads;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static Models.CODE.KEY_ADS;
import static Models.CODE.REQUEST_CODE_ADS;

public class ListAds extends AppCompatActivity {

    RecyclerView rvListAds;
    FloatingActionButton fabCreate;
    ImageView ivLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_ads);

        mapping();

        registerActivity();

        loadAdvertisment();
    }


    private void registerActivity() {
        fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListAds.this, AdsActivity.class);
                Ads ad = new Ads();
                Bundle bundle = new Bundle();
                bundle.putSerializable(KEY_ADS, ad);
                intent.putExtras(bundle);
                startActivityForResult(intent, REQUEST_CODE_ADS);
            }
        });

    }

    private void loadAdvertisment() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ivLoading.setVisibility(View.VISIBLE);
            }
        });
        // Khởi tạo OkHttpClient để lấy dữ liệu.
        OkHttpClient client = new OkHttpClient();

        // Khởi tạo Moshi adapter để biến đổi json sang model java (ở đây là User)
        Moshi moshi = new Moshi.Builder().build();
        Type adsType = Types.newParameterizedType(List.class, Ads.class);
        final JsonAdapter<List<Ads>> jsonAdapter = moshi.adapter(adsType);

        // Tạo request lên server.
        Request request = new Request.Builder()
                .url("https://thawing-falls-33830.herokuapp.com/ads/")
                .build();

        // Thực thi request.
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Error", "Network Error");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                // Lấy thông tin JSON trả về. Bạn có thể log lại biến json này để xem nó như thế nào.
                String json = response.body().string();
                final List<Ads> lstAds = jsonAdapter.fromJson(json);

                // Cho hiển thị lên RecyclerView.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ivLoading.setVisibility(View.INVISIBLE);
                        rvListAds.setAdapter(new AdsAdapter(lstAds, ListAds.this));
                    }
                });
            }
        });
    }

    private void mapping() {
        rvListAds = (RecyclerView) findViewById(R.id.rv_ads);
        rvListAds.setLayoutManager(new LinearLayoutManager(this));
        fabCreate = (FloatingActionButton) findViewById(R.id.fab_Create);
        ivLoading = (ImageView) findViewById(R.id.iv_loading_list);
        Glide.with(this).load(R.drawable.loading).into(ivLoading);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADS) {
            if (resultCode == Activity.RESULT_OK) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadAdvertisment();
                    }
                });
            }
        }
    }
}