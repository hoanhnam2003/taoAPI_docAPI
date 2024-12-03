package com.namha.taoapi_docapi;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    TextView txtKetQua;
    EditText edtUserName, edtPassword;
    Button btnDangNhap, btnDangKy, btnDocDuLieuTuPHP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtKetQua = findViewById(R.id.txtKetQua);
        edtUserName = findViewById(R.id.edtUserName);
        edtPassword = findViewById(R.id.edtPassword);
        btnDangNhap = findViewById(R.id.btnDangNhap);
        btnDangKy = findViewById(R.id.btnRegister);
        btnDocDuLieuTuPHP = findViewById(R.id.btnDocDuLieuTuPHP);

        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLoginOrRegister("login");
            }
        });

        btnDangKy.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 handleLoginOrRegister("register");
             }
         });
        btnDocDuLieuTuPHP.setOnClickListener(new View.OnClickListener() { @Override
            public void onClick(View v) {
                ReadData(v);
            }
        });
    }

    // Xử lý đăng nhập hoặc đăng ký
    public void handleLoginOrRegister(String action) {
        String username = edtUserName.getText().toString();
        String password = edtPassword.getText().toString();

        if (!username.isEmpty() && !password.isEmpty()) {
            HttpLoginRegister httpLoginRegister = new HttpLoginRegister();
            httpLoginRegister.execute(username, password, action);
        } else {
            txtKetQua.setText("Vui lòng điền đầy đủ thông tin.");
        }
    }

    // Lớp AsyncTask xử lý đăng nhập và đăng ký
    public class HttpLoginRegister extends AsyncTask<String, Void, String> {
        private static final String SERVER = "http://192.168.1.108/API/readJSONAPI.php"; // Đổi đường dẫn API

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try {
                String username = params[0];
                String password = params[1];
                String action = params[2];

                // Tạo URL cho yêu cầu POST
                String urlString = SERVER;
                URL url = new URL(urlString);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true); // Cho phép gửi dữ liệu
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setReadTimeout(5000);

                // Tạo dữ liệu để gửi
                String data = "username=" + username + "&password=" + password + "&action=" + action;

                // Gửi dữ liệu
                httpURLConnection.getOutputStream().write(data.getBytes("UTF-8"));
                httpURLConnection.connect();

                // Kiểm tra mã phản hồi HTTP
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    result = stringBuilder.toString();
                    bufferedReader.close();
                } else {
                    result = "Error: " + httpURLConnection.getResponseCode();
                }
            } catch (Exception e) {
                result = "Exception: " + e.getMessage();
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Hiển thị kết quả từ server (dữ liệu trả về)
            txtKetQua.setText(result);
        }
    }

    // Hàm đọc dữ liệu từ PHP
    public void ReadData(View view) {
        HttpReadFromPHP httpReadFromPHP = new HttpReadFromPHP();
        httpReadFromPHP.execute();
    }

    // Lớp AsyncTask đọc dữ liệu từ PHP
    public class HttpReadFromPHP extends AsyncTask<Void, Void, String> {
        public static final String SERVER = "http://192.168.1.108/API/readJSONAPI.php";

        @Override
        protected String doInBackground(Void... voids) {
            String result = null;
            try {
                URL url = new URL(SERVER);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.connect();

                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    result = stringBuilder.toString();
                    bufferedReader.close();
                } else {
                    result = "Error: " + httpURLConnection.getResponseCode();
                }
            } catch (Exception e) {
                result = "Exception: " + e.getMessage();
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            txtKetQua.setText(result);
        }
    }
}
