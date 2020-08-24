package hcmute.edu.hcmute;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import Models.Ads;
import Models.LoginModel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static Models.CODE.KEY_ADS;

public class AdsActivity extends AppCompatActivity {
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    ImageView ivBack, ivLoading;
    TextInputEditText edtExpire, edtTitle, edtContent, edtBody, edtUrlImage;
    Button btnSave;
    boolean isUpdate;

    Ads ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads);

        mapping();

        Intent intent = getIntent();

        ad = (Ads) intent.getSerializableExtra(KEY_ADS);
        Log.e("ADS Update", ad.get_id());

        if (ad.get_id().equals("")) {
            isUpdate = false;
        } else {
            isUpdate = true;
        }
        Log.e("ADS Update status ", String.valueOf(isUpdate));

        if (isUpdate) {
            loadInfoAds();
        }
        registerActivity();
    }

    private void loadInfoAds() {
        edtTitle.setText(ad.getTitle());
        edtContent.setText(ad.getContent());
        edtBody.setText(ad.getBody());
        edtUrlImage.setText(ad.getUrlImage());
        edtExpire.setText(ad.getExpires().toString());
    }

    private void registerActivity() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmBack();
            }
        });

        edtExpire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int date = calendar.get(Calendar.DATE);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                DatePickerDialog dialog = new DatePickerDialog(AdsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        edtExpire.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                }, year, month, date);
                dialog.show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = edtTitle.getText().toString().trim();
                String content = edtContent.getText().toString().trim();
                String body = edtBody.getText().toString().trim();
                String urlImage = edtUrlImage.getText().toString().trim();
                String expire = edtExpire.getText().toString().trim();


                Ads adsCurrent = new Ads(ad.get_id(), title, content, body, urlImage, expire);
                if (checkFillInput()) {
                    if (isUpdate) {
                        updateAds(adsCurrent);
                    } else {
                        createAds(adsCurrent);
                    }
                }
            }
        });
    }

    private boolean checkFillInput() {
        boolean res = true;
        if (edtTitle.getText().toString().equals("")) {
            edtTitle.setError("The title field is empty");
            res = false;
        }
        if (edtContent.getText().toString().equals("")) {
            edtContent.setError("The content field is empty");
            res = false;
        }
        if (edtBody.getText().toString().equals("")) {
            edtBody.setError("The body field is empty");
            res = false;
        }
        if (edtUrlImage.getText().toString().equals("")) {
            edtUrlImage.setError("The url image field is empty");
            res = false;
        }
        if (edtExpire.getText().toString().equals("")) {
            edtExpire.setError("The expire day field is empty");
            res = false;
        }

        return res;
    }

    private void createAds(Ads adsCurrent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ivLoading.setVisibility(View.VISIBLE);
                btnSave.setEnabled(false);
            }
        });
        // Khởi tạo OkHttpClient để lấy dữ liệu.
        OkHttpClient client = new OkHttpClient();
        // Tạo request lên server.
        String url = "https://thawing-falls-33830.herokuapp.com/ads/";
        String json = adsCurrent.toString();
        RequestBody body = RequestBody.create(JSON, json);
        Log.e("Json ad", json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        // Thực thi request.
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Error", "Network Error");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AdsActivity.this);

                            builder.setMessage("Create Successfully!")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            setResult(Activity.RESULT_OK);
                                            finish();
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            ivLoading.setVisibility(View.INVISIBLE);
                            dialog.show();
                        }
                    });

                } else {
                    Log.e("Error Code", String.valueOf(response.code()));
                    //display the appropriate message...
                }
            }
        });
    }

    private void updateAds(Ads update) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ivLoading.setVisibility(View.VISIBLE);
                btnSave.setEnabled(false);
            }
        });
        // Khởi tạo OkHttpClient để lấy dữ liệu.
        OkHttpClient client = new OkHttpClient();
        // Tạo request lên server.
        String url = "https://thawing-falls-33830.herokuapp.com/ads/" + update.get_id();
        String json = update.toString();
        RequestBody body = RequestBody.create(JSON, json);
        Log.e("Json ad", json);
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();
        // Thực thi request.
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Error", "Network Error");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AdsActivity.this);

                            builder.setMessage("Update Successfully!")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            setResult(Activity.RESULT_OK);
                                            finish();
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            ivLoading.setVisibility(View.INVISIBLE);
                            dialog.show();
                        }
                    });
                } else {
                    Log.e("Error Code", String.valueOf(response.code()));
                    //display the appropriate message...
                }
            }
        });
    }

    private void confirmBack() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Are you sure to exit without save info?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        confirmBack();
    }

    private void mapping() {
        ivBack = (ImageView) findViewById(R.id.iv_back);
        edtExpire = (TextInputEditText) findViewById(R.id.edt_expire);
        edtTitle = (TextInputEditText) findViewById(R.id.edt_title);
        edtContent = (TextInputEditText) findViewById(R.id.edt_content);
        edtBody = (TextInputEditText) findViewById(R.id.edt_body);
        edtUrlImage = (TextInputEditText) findViewById(R.id.edt_urlImage);
        btnSave = (Button) findViewById(R.id.btn_Save);
        ivLoading = (ImageView) findViewById(R.id.iv_loading_ads);
        Glide.with(this).load(R.drawable.loading).into(ivLoading);
    }
}