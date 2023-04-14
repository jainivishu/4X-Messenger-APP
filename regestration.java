package com.example.fourxmessenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class regestration extends AppCompatActivity {
    TextView loginbut;
    EditText rg_username,rg_email,rg_password,rg_repassword;
    Button rg_signup;
    CircleImageView rg_profileimg;
    FirebaseAuth auth;
    Uri imageURI;
    String imageuri;
    String emailPattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    FirebaseDatabase database;
    FirebaseStorage storage;
    ProgressDialog progressDialog;




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regestration);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Creating account");
        progressDialog.setCancelable(false);
        getSupportActionBar().hide();
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();
        auth=FirebaseAuth.getInstance();
        loginbut=findViewById(R.id.loginbut);
        rg_username=findViewById(R.id.rgusername);
        rg_email=findViewById(R.id.rgemail);
        rg_password=findViewById(R.id.rgpassword);
        rg_repassword=findViewById(R.id.rgrepassword);
        rg_profileimg =findViewById(R.id.profilerg0);
        rg_signup=findViewById(R.id.signupbutton);


        loginbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(regestration.this,login.class);
                startActivity(intent);
                finish();
            }
        });

        rg_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String namee=rg_username.getText().toString();
                String emaill=rg_email.getText().toString();
                String Password=rg_password.getText().toString();
                String cPassword=rg_repassword.getText().toString();
                String status="Hey I'm using this application";

                if (TextUtils.isEmpty(namee)|| TextUtils.isEmpty(emaill)|| TextUtils.isEmpty(Password)|| TextUtils.isEmpty(cPassword)){
                    progressDialog.dismiss();
                    Toast.makeText(regestration.this, "Please enter valid information", Toast.LENGTH_SHORT).show();
                }else if (!emaill.matches(emailPattern)){
                    progressDialog.dismiss();
                    rg_email.setError("Enter a valid email id");
                } else if (Password.length()<6) {
                    progressDialog.dismiss();
                    rg_password.setError("Password must be atleast 6 characters");
                } else if (!Password.equals(cPassword)) {
                    progressDialog.dismiss();
                    rg_password.setError("Password Doesn't Match");
                }else {
                    auth.createUserWithEmailAndPassword(emaill,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                String id=task.getResult().getUser().getUid();
                                DatabaseReference reference=database.getReference().child("user").child(id);
                                StorageReference storageReference=storage.getReference().child("upload").child(id);

                                if(imageURI!=null){
                                    storageReference.putFile(imageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if(task.isSuccessful()){
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imageuri=uri.toString();
                                                        Users users=new Users(id,namee,Password,cPassword,imageuri,status);
                                                        reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    progressDialog.show();
                                                                    Intent intent=new Intent(regestration.this,MainActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }else{
                                                                    Toast.makeText(regestration.this, "Error in creating the user", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }else {
                                    String status = "Hey I'm using this application";
                                    imageuri="https://firebasestorage.googleapis.com/v0/b/fourx-messenger.appspot.com/o/man.png?alt=media&token=c7311183-2afb-434a-8402-3d99534bdf94";
                                    Users users=new Users(id,namee,emaill,Password,imageuri,status);
                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                progressDialog.show();
                                                Intent intent=new Intent(regestration.this,MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }else{
                                                Toast.makeText(regestration.this, "Error in creating the user", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }else {
                                Toast.makeText(regestration.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });




        rg_profileimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),10);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==10){
            if(data!=null){
                imageURI=data.getData();
                rg_profileimg.setImageURI(imageURI);

            }
        }
    }
}