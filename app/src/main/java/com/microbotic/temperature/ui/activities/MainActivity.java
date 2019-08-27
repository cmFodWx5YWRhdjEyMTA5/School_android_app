package com.microbotic.temperature.ui.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.microbotic.temperature.R;
import com.microbotic.temperature.ui.adapters.MenuAdapter;
import com.microbotic.temperature.utils.SchoolDataProvider;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private SchoolDataProvider dataProvider;
    private final String tag = MainActivity.class.getSimpleName();
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;

    private String[] permissionsRequired = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;
        dataProvider = new SchoolDataProvider(context);

        TextView tvTitle = findViewById(R.id.tvSchool);

        tvTitle.setText(dataProvider.getName());

        setupMenu();

        findViewById(R.id.imgLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataProvider.logOut();
                startActivity(new Intent(context, LoginActivity.class));
                MainActivity.this.finish();
            }
        });

        checkPermissions();
    }

    private void checkPermissions() {

        if (ActivityCompat.checkSelfPermission(context, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED) {

            if (dataProvider.isPermissionAsked()) {
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.permission_title);
            builder.setMessage(R.string.permission_message);
            builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    ActivityCompat.requestPermissions(MainActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            boolean allGranted = false;
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    allGranted = true;
                } else {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                Log.e(tag, "all permissions granted!");
            } else {
                Log.e(tag, "permissions denied");
            }

            dataProvider.dontAskPermission();
        }
    }


    private void setupMenu() {
        String[] title = {"Check Temperature", "Student List", "Temperature Records"};
        int[] icons = {R.drawable.img_thr, R.drawable.img_student, R.drawable.img_thr_count};

        RecyclerView rvOptions = findViewById(R.id.rvOptions);
        MenuAdapter adapter = new MenuAdapter(title, icons, pos -> {

            Intent intent = null;

            switch (pos) {
                case 0:
                    intent = new Intent(MainActivity.this, TemperatureActivity.class);
                    break;
                case 1:
                    intent = new Intent(MainActivity.this, StudentsActivity.class);
                    break;
                case 2:
                    intent = new Intent(MainActivity.this, TemperatureHistoryActivity.class);
                    break;
            }

            if (intent != null) {
                startActivity(intent);
            }

        });


        rvOptions.setAdapter(adapter);

    }


}








