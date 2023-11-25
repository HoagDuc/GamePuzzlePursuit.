package com.example.game_puzzle_pursuit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText inputUserName, inputEmail, inputPassword, inputConformPassword;
    private Button btnRegister;
    private TextView haveAccount;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputUserName = findViewById(R.id.inputUsername);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConformPassword = findViewById(R.id.inputConformPassword);
        haveAccount = findViewById(R.id.haveAccount);
        btnRegister = findViewById(R.id.btnRegister);

        database = FirebaseDatabase.getInstance().getReferenceFromUrl("https://game-puzzle-pursuit-a6c54-default-rtdb.asia-southeast1.firebasedatabase.app/");

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCrendedentiatls();
            }
        });

        haveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                finish();
            }
        });
    }

    private void registerUser(String userName, String email, String password, int totalScore){
        FirebaseAuth auth = FirebaseAuth.getInstance();

        //create user
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            //Luu vao db
                            Account accounts = new Account(userName, email, password, totalScore);
                            database = FirebaseDatabase.getInstance().getReference("User");
                            database.child(firebaseUser.getUid()).setValue(accounts).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isComplete()) {
                                        Toast.makeText(RegisterActivity.this, "Bạn đã đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                        finish();
                                    } else
                                        Toast.makeText(RegisterActivity.this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else {
                            Toast.makeText(RegisterActivity.this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void handleCrendedentiatls() {
        String userName = inputUserName.getText().toString();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String conformPassword = inputConformPassword.getText().toString();
        int totalScore = 0;

        if(userName.isEmpty() || userName.length()<6){
            showError(inputUserName,"Tên người dùng không hợp lệ!");
        }
        if(email.isEmpty() || !email.contains("@")){
            showError(inputEmail,"Email không hợp lệ!");
        }
        if(password.isEmpty() || password.length()<6){
            showError(inputPassword,"Mật khẩu ít nhất 6 ký tự!");
        }
        if(conformPassword.isEmpty() || !conformPassword.equals(password)){
            showError(inputConformPassword,"Nhập lại mật khẩu không đúng!");
        }
        else {
            registerUser(userName, email, password, totalScore);
        }
    }

    private void showError(EditText input,String s) {
        input.setError(s);
        input.requestFocus();
    }
}