package com.example.rishonlovesanimals;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class ProfileEditActivity extends AppCompatActivity {
    private boolean access_granted = false;
    private String photoPath;
    private int CAMERA_REQUEST = 3;
    private int WRITE_PERMISSION = 2;
    private ImageView imageView;
    private Uri photoUri;
    private boolean tookPicture = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_edit_layout);
        final TextInputEditText name = findViewById(R.id.profile_edit_name);
        final TextInputEditText age = findViewById(R.id.profile_edit_age);
        Button saveChangesBtn = findViewById(R.id.profile_edit_doneBtn);
        imageView =findViewById(R.id.profile_edit_image);
        saveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser user = auth.getCurrentUser();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                if(user!=null && name.getText()!=null && age.getText()!=null) {
                    DatabaseReference reference = database.getReference("users/" + user.getUid() + "/userData");
                    HashMap<String,Object> userData = new HashMap<>();
                    if(!name.getText().toString().equals(""))
                        userData.put("name",name.getText().toString());
                    if(!age.getText().toString().equals(""))//should be changed to regular expression
                         userData.put("age",age.getText().toString());
                    reference.updateChildren(userData);
                   // reference.setValue(userData);

                    //////////////////////////////////////////////
                    if(tookPicture) {
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference("uploads");
                        final StorageReference pictureReference = storageReference.child(System.currentTimeMillis() + "." + "");
                        StorageTask<UploadTask.TaskSnapshot> uploadTask;
                        uploadTask = pictureReference.putFile(photoUri);
                        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (task.isSuccessful())
                                    Toast.makeText(ProfileEditActivity.this, "Picture was uploaded successfully", Toast.LENGTH_SHORT).show();
                                else {
                                    Toast.makeText(ProfileEditActivity.this, "an Error has accrued during the upload, picture wasn't uploaded", Toast.LENGTH_SHORT).show();
                                    throw Objects.requireNonNull(task.getException());
                                }
                                return pictureReference.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri uri = task.getResult();
                                    if (uri != null) {
                                        String sUri = uri.toString();
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        if (user != null) {
                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid() + "userData");
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("pictureURL", sUri);
                                            databaseReference.updateChildren(hashMap);
                                        }
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfileEditActivity.this, "failed setting picture", Toast.LENGTH_SHORT).show();
                            }
                        });


                    }


                    //////////////////////////////////////////////
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_edit_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.profile_edit_menu_picture)
        {
            if(AskPermission())
            {
                @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "_";
                File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                try {
                    File image = File.createTempFile(imageFileName,".jpg",storageDir);
                    photoPath = image.getAbsolutePath();
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if(takePictureIntent.resolveActivity(getPackageManager())!=null)
                    {
                        File photoFile;
                        photoFile = image;
                        if(photoFile!=null)
                        {
                            Uri photoURI = FileProvider.getUriForFile(ProfileEditActivity.this,
                                    "com.example.rishonlovesanimals.provider",photoFile);
                            this.photoUri = photoURI;
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                            startActivityForResult(takePictureIntent,CAMERA_REQUEST);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("failed creating a file");
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean AskPermission()
    {
        int hasWritePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(hasWritePermission != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},WRITE_PERMISSION);
            tookPicture = false;
        }
        else access_granted = true;
        return access_granted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == WRITE_PERMISSION)
        {
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED)
                Toast.makeText(ProfileEditActivity.this,"permission is required",Toast.LENGTH_SHORT).show();
            else
            {
                @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "_";
                File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                try{
                    File image = File.createTempFile(imageFileName,".jpg",storageDir);
                    photoPath = image.getAbsolutePath();
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if(takePictureIntent.resolveActivity(getPackageManager())!=null)
                    {
                        File photoFile;
                        photoFile = image;
                        if(photoFile!=null)
                        {
                            Uri photoUri = FileProvider.getUriForFile(ProfileEditActivity.this,"com.example.rishonlovesanimals.provider",photoFile);
                            this.photoUri = photoUri;
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                            startActivityForResult(takePictureIntent,CAMERA_REQUEST);
                        }
                    }
                }catch (IOException e){
                    e.printStackTrace();
                    System.out.println("failed creating a file");
                }
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST && resultCode == RESULT_OK)
        {
            tookPicture = true;
            Drawable drawable = Drawable.createFromPath(photoPath);
            assert drawable != null;
            Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
            bitmap = Bitmap.createScaledBitmap(bitmap,120,120,false);
            imageView.setImageBitmap(bitmap);
            //ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            //bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);

        }

    }
}
