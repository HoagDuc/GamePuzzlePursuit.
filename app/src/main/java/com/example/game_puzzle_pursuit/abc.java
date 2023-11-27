import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class abc {

//    private LocationManager locationManager;
//    private LocationListener locationListener;
//    private TextView locationTextView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        locationTextView = findViewById(R.id.locationTextView);
//
//        // Kiểm tra quyền truy cập vị trí
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//        } else {
//            startLocationUpdates();
//        }
//    }
//
//    private void startLocationUpdates() {
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        locationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//                // Xử lý sự thay đổi vị trí
//                double latitude = location.getLatitude();
//                double longitude = location.getLongitude();
//
//                // Hiển thị thông tin vị trí trên TextView
//                String locationInfo = "Latitude: " + latitude + "\nLongitude: " + longitude;
//                locationTextView.setText(locationInfo);
//            }
//
//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {}
//
//            @Override
//            public void onProviderEnabled(String provider) {}
//
//            @Override
//            public void onProviderDisabled(String provider) {}
//        };
//
//        // Lắng nghe sự thay đổi vị trí
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if (requestCode == 1) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                startLocationUpdates();
//            } else {
//                // Xử lý khi người dùng không cấp quyền truy cập vị trí
//            }
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        // Dừng lắng nghe khi hoạt động bị hủy
//        if (locationManager != null && locationListener != null) {
//            locationManager.removeUpdates(locationListener);
//        }
//    }
}
