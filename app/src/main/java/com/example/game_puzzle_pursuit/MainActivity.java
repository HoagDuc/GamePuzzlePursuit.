package com.example.game_puzzle_pursuit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class MainActivity extends AppCompatActivity  implements OnMapReadyCallback {
    TextView userNameTextView, totalScoreTextView;
    private GoogleMap gMap;
    private ImageButton btnLogout, btnTable;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker marker;
    private DatabaseReference databaseReference;
    private AlertDialog alertDialog;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogout = findViewById(R.id.btnquiz);
        btnTable = findViewById(R.id.btnTable);
        userNameTextView = findViewById(R.id.userNameTextView);
        totalScoreTextView = findViewById(R.id.totalScoreTextView);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        auth = FirebaseAuth.getInstance();

        //enable map
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("questions");

        loadUserData();
        btnTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTableDiaLog();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogout();
            }
        });
    }

    //active map
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;

        // Kiểm tra quyền truy cập vị trí
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            showCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }

    private void showCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location location = task.getResult();
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            LatLng currentLocation = new LatLng(latitude, longitude);

                            addRandomMarkers(currentLocation);

                            // Thêm marker mới với biểu tượng tùy chỉnh
                            gMap.addMarker(new MarkerOptions().position(currentLocation).title("Your Location"));
                            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

                            // Set up marker click event
                            gMap.setOnMarkerClickListener(marker -> {
                                String questionId = (String) marker.getTitle();
                                if (questionId != null) {
                                    showQuestionDialog(questionId);
                                }
                                return false;
                            });
                        }
                    });
        }
    }

    private void showQuestionDialog(String questionId) {
        databaseReference.child(questionId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Lấy dữ liệu từ Firebase
                    String content = dataSnapshot.child("content").getValue(String.class);
                    DataSnapshot answersSnapshot = dataSnapshot.child("answers");

                    // Tạo danh sách câu trả lời
                    List<Answer> answers = new ArrayList<>();
                    for (DataSnapshot answerSnapshot : answersSnapshot.getChildren()) {
                        Answer answer = answerSnapshot.getValue(Answer.class);
                        answers.add(answer);
                    }

                    // Hiển thị dialog
                    showQuestionAlertDialog(content, answers);
                } else {
                    Toast.makeText(MainActivity.this, "Question not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi có lỗi
                Toast.makeText(MainActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showQuestionAlertDialog(String questionContent, List<Answer> answers) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_question, null);
        builder.setView(dialogView);

        TextView questionTextView = dialogView.findViewById(R.id.questionTextView);
        questionTextView.setText(questionContent);

        RadioGroup answersRadioGroup = dialogView.findViewById(R.id.answersRadioGroup);

        // Tạo các RadioButton dựa trên danh sách đáp án
        for (int i = 0; i < answers.size(); i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 50);
            RadioButton radioButton = new RadioButton(MainActivity.this);
            radioButton.setText(answers.get(i).getContent());
            radioButton.setTextColor(getResources().getColor(R.color.black));
            radioButton.setGravity(Gravity.CENTER);
            radioButton.setButtonDrawable(android.R.color.transparent);
            radioButton.setBackgroundResource(R.drawable.custom_radio_button);
            radioButton.setWidth(1000);
            radioButton.setHeight(100);
            radioButton.setLayoutParams(params);
            answersRadioGroup.addView(radioButton);
        }

        alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button submit_btn = dialogView.findViewById(R.id.submit_btn);

                submit_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Xử lý khi người dùng nhấn nút Submit
                        int selectedId = answersRadioGroup.getCheckedRadioButtonId();
                        if (selectedId != -1) {
                            RadioButton selectedRadioButton = dialogView.findViewById(selectedId);
                            String selectedAnswer = selectedRadioButton.getText().toString();
                            // Xử lý kết quả
                            for (Answer answer : answers) {
                                if (answer.getContent().equals(selectedAnswer)) {
                                    boolean isCorrect = answer.isCorrect();
                                    if (isCorrect){
                                        FirebaseUser currentUser = auth.getCurrentUser();

                                        if (currentUser != null) {
                                            String userId = currentUser.getUid();
                                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User").child(userId);

                                            // Lấy thông tin người dùng hiện tại từ Realtime Database
                                            userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DataSnapshot userData = task.getResult();
                                                        if (userData.exists()) {
                                                            // Lấy điểm số hiện tại của người dùng
                                                            int currentScore = userData.child("totalScore").getValue(int.class);

                                                            // Cộng thêm 10 vào điểm số hiện tại
                                                            int newScore = currentScore + 10;

                                                            // Cập nhật điểm số mới vào Realtime Database
                                                            userRef.child("totalScore").setValue(newScore);
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    }
                                    String resultMessage = isCorrect ? "Đúng, +10 Point!" : "Sai rồi bạn ơi!";
                                    Toast.makeText(MainActivity.this, resultMessage, Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Please select an answer", Toast.LENGTH_SHORT).show();
                        }

                        alertDialog.dismiss();
                    }
                });
            }
        });

        alertDialog.show();
    }

    private void checkAnswer(String selectedAnswer, List<Answer> answers) {
        for (Answer answer : answers) {
            if (answer.getContent().equals(selectedAnswer)) {
                boolean isCorrect = answer.isCorrect();
                String resultMessage = isCorrect ? "Đúng, +10 Point!" : "Sai rồi bạn ơi!";
                Toast.makeText(MainActivity.this, resultMessage, Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    //random marker
    private void addRandomMarkers(LatLng currentLocation) {
        // Kiểm tra xem bản đồ đã sẵn sàng chưa
        if (gMap != null) {
            double numberOfMarkers = 3; // Số lượng marker ngẫu nhiên

            double radius = 2000; // Đơn vị: độ (tùy chỉnh bán kính theo nhu cầu của bạn)
            Random random = new Random();

            for (int i = 0; i < numberOfMarkers; i++) {
                double randomDistance = random.nextDouble() * radius;
                double randomBearing = random.nextDouble() * 360;

                LatLng newLocation = calculateNewLocation(currentLocation, randomDistance, randomBearing);

                // Tạo BitmapDescriptor từ hình ảnh marker trong thư mục drawable
                // Lấy Bitmap từ hình ảnh nguồn
                Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hopcauhoi1);

                // Tạo một biểu tượng bitmap lớn hơn
                Bitmap largeBitmap = Bitmap.createScaledBitmap(originalBitmap, 60, 60, false);

                // Thêm marker mới với biểu tượng tùy chỉnh
                gMap.addMarker(new MarkerOptions().position(newLocation).title("question" + (i + 1)).icon(BitmapDescriptorFactory.fromBitmap(largeBitmap)));
            }
        }
    }

    private LatLng calculateNewLocation(LatLng currentLocation, double distance, double bearing) {
        double earthRadius = 6371000; // Bán kính trái đất, đơn vị: mét

        double lat1 = Math.toRadians(currentLocation.latitude);
        double lon1 = Math.toRadians(currentLocation.longitude);
        double angularDistance = distance / earthRadius;
        double trueCourse = Math.toRadians(bearing);

        double lat2 = Math.asin(Math.sin(lat1) * Math.cos(angularDistance) +
                Math.cos(lat1) * Math.sin(angularDistance) * Math.cos(trueCourse));

        double lon2 = lon1 + Math.atan2(Math.sin(trueCourse) * Math.sin(angularDistance) * Math.cos(lat1),
                Math.cos(angularDistance) - Math.sin(lat1) * Math.sin(lat2));

        lon2 = (lon2 + 3 * Math.PI) % (2 * Math.PI) - Math.PI;  // Chuyển về khoảng -180 đến +180 độ

        return new LatLng(Math.toDegrees(lat2), Math.toDegrees(lon2));
    }

    //doi anh marker
    private BitmapDescriptor getMarkerIconFromDrawable(int resId) {
        // Tạo Bitmap từ hình ảnh drawable
        BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(resId);
        Bitmap bitmap = bitmapDrawable.getBitmap();

        // Chuyển đổi Bitmap thành BitmapDescriptor
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void showTableDiaLog() {
        databaseReference = FirebaseDatabase.getInstance().getReference("User");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Account> accountList = new ArrayList<>();
                List<Account> accountList1 = new ArrayList<>();

                for (DataSnapshot item : snapshot.getChildren()) {
                    Account account = new Account();
                    account.setUserName(item.child("userName").getValue(String.class));
                    account.setTotalScore(item.child("totalScore").getValue(Integer.class));
                    accountList.add(account);
                }

                Collections.sort(accountList, new Comparator<Account>() {
                    @Override
                    public int compare(Account account1, Account account2) {
                        // So sánh theo totalScore giảm dần
                        return Integer.compare(account2.getTotalScore(), account1.getTotalScore());
                    }
                });

                showAlertDialog("Bảng xếp hạng", accountList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra
            }
        });
    }

    private void showAlertDialog(String title, List<Account> accountList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_leaderboard, null);

        // Tạo RecyclerView và thiết lập LayoutManager
        RecyclerView recyclerView = dialogView.findViewById(R.id.rvUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        // Tạo Adapter và thiết lập dữ liệu
        AccountAdapter adapter = new AccountAdapter(accountList);
        recyclerView.setAdapter(adapter);

        // Đặt RecyclerView vào AlertDialog
        builder.setView(dialogView);

        Button btnClose = dialogView.findViewById(R.id.btnClose);

        AlertDialog dialog = builder.create();
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void loadUserData() {
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User").child(userId);

            // Lấy thông tin người dùng hiện tại từ Realtime Database
            userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        DataSnapshot userData = task.getResult();
                        if (userData.exists()) {
                            // Lấy điểm số hiện tại của người dùng
                            String userName = userData.child("userName").getValue(String.class);
                            int currentScore = userData.child("totalScore").getValue(int.class);

                            userNameTextView.setText("Username: " + userName);
                            totalScoreTextView.setText("Total Score: " + currentScore);

                        }
                    }
                }
            });
        }
    }

    private void handleLogout(){
        FirebaseAuth.getInstance().signOut();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getBaseContext(),googleSignInOptions);
        googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(getBaseContext(),LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showCurrentLocation();
            } else {
                // Quyền truy cập vị trí bị từ chối, xử lý theo nhu cầu của bạn
            }
        }
    }
}