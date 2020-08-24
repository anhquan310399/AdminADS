package hcmute.edu.hcmute;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

import Adapter.AdsAdapter;
import Models.LoginModel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    TextInputEditText edtUserName, edtPassword;
    Button btnLogin;
    ImageView ivLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapping();

        registerActivity();
    }

    private void registerActivity() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginModel model = checkEmptyLogin();
                if (model != null) {
                    btnLogin.setEnabled(false);
                    ivLoading.setVisibility(View.VISIBLE);
                    // Khởi tạo OkHttpClient để lấy dữ liệu.
                    OkHttpClient client = new OkHttpClient();

                    // Khởi tạo Moshi adapter để biến đổi json sang model java (ở đây là User)
                    Moshi moshi = new Moshi.Builder().build();
                    final JsonAdapter<LoginModel> jsonAdapter = moshi.adapter(LoginModel.class);


                    // Tạo request lên server.
                    String url = "https://thawing-falls-33830.herokuapp.com/login/" + model.getUsername() + "/" + model.getPassword();

                    Request request = new Request.Builder()
                            .url(url)
                            .build();
                    // Thực thi request.
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("Error", "Network Error");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String json = response.body().string();
                            Log.e("Json login", json);
                            // Cho hiển thị lên RecyclerView.
                            final LoginModel user = jsonAdapter.fromJson(json);
                            if (user.getUsername() != null) {
                                Log.e("Username", user.getUsername());
                                Intent intent = new Intent(MainActivity.this, ListAds.class);
                                startActivity(intent);
                                finish();
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ivLoading.setVisibility(View.INVISIBLE);
                                        btnLogin.setEnabled(true);
                                        Toast.makeText(MainActivity.this, "Username and password is incorrect", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    private LoginModel checkEmptyLogin() {
        String username = edtUserName.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        if (username.equals("")) {
            edtUserName.setError("The username field is empty");
            return null;
        } else if (password.equals("")) {
            edtPassword.setError("The password field is empty");
            return null;
        }
        return new LoginModel(username, password);
    }

    private void mapping() {
        edtUserName = (TextInputEditText) findViewById(R.id.edt_username);
        edtPassword = (TextInputEditText) findViewById(R.id.edt_password);
        btnLogin = (Button) findViewById(R.id.btn_Login);
        ivLoading = (ImageView) findViewById(R.id.iv_loading);
        Glide.with(this).load(R.drawable.loading).into(ivLoading);
    }
}