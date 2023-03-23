package com.example.DeliveryNest;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ReportIssue extends AppCompatActivity {
    Spinner spinner;
    EditText editText;
    Button submitbtn, uploadbtn;

    String issuecategory[] = {"Choose a subject", "Order related", "Payment related", "Agent related","Report a bug"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_issue);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        spinner = (Spinner) findViewById(R.id.categorySpinner);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, issuecategory);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0,false);
        editText = findViewById(R.id.issueDesc);
        submitbtn=findViewById(R.id.BtnSubmit);
        uploadbtn = findViewById(R.id.BtnUpload);
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateIssueDesc();
                validateSpinner();
            }
        });
        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent clickImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(clickImage);
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2){
            data.getExtras();
        }
    }
    public boolean validateIssueDesc(){
        String issuedesc = editText.getText().toString();
        if (issuedesc.isEmpty()){
            editText.setError("Field cannot be empty");
            return false;
        }else{
            editText.setError(null);
            editText.setEnabled(false);
            return true;
        }
    }
    public boolean validateSpinner(){
        String spinnerData = spinner.getAdapter().toString();
        if (spinner.getSelectedItemPosition()==0){
            Toast.makeText(this, "Choose a Subject", Toast.LENGTH_LONG).show();
            return false;
        }else if(spinner.getSelectedItemPosition()>0){

            return true;
        }else{
            return true;
        }
    }

    public void backpressed(View view) {
        super.onBackPressed();
        
    }
}
