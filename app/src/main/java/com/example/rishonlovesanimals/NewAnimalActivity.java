package com.example.rishonlovesanimals;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import Animals.Dog;

public class NewAnimalActivity extends AppCompatActivity {
    private TextInputEditText name,age,history,medical,note,breed;
    private ImageView imageView;
    private ArrayList<Dog> dogs;
    private int SELECT_IMAGE = 1;
    private int WRITE_PERMISSION = 2;
    private int CAMERA_REQUEST = 3;
    private String imageUri = null;
    private boolean access_granted = false;
    private String photoPath;
    private Uri ImageUri;
    private String dogsFireBasePath;
    private String animalKind,animalBreed;
    private Spinner breedSpinner;
    private String uploaderName;
    private String uploaderUId;
    private boolean photoAdded = false;
    private ProgressBar progressBar;


    private ArrayAdapter<CharSequence> breedSpinnerAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_animal_layout);
        progressBar = findViewById(R.id.progressBar1);
        name = findViewById(R.id.new_animal_name_et);
        age = findViewById(R.id.new_animal_age_et);
        history = findViewById(R.id.new_animal_History_et);
        medical = findViewById(R.id.new_animal_medical_et);
        breedSpinner = findViewById(R.id.new_animal_breed_spinner);
        note = findViewById(R.id.new_animal_note_et);
        imageView = findViewById(R.id.newAnimalPicture);
        breed = findViewById(R.id.animal_breed_txt);
        ImageButton mixedBreedBtn = findViewById(R.id.new_animal_imageButton);
        dogs = new ArrayList<>();
        uploaderName = getIntent().getStringExtra("name");
        uploaderUId = getIntent().getStringExtra("userUid");
        Spinner kindSpinner = findViewById(R.id.new_animal_kind_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,R.array.animals_kind,android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        kindSpinner.setAdapter(spinnerAdapter);
        breedSpinnerAdapter = ArrayAdapter.createFromResource(NewAnimalActivity.this,R.array.dogs_breed,android.R.layout.simple_spinner_item);//default value
        kindSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String s = (String) parent.getItemAtPosition(position);
                switch(s){
                    case "Dog":{
                        animalKind = "Dog";
                        breedSpinnerAdapter = ArrayAdapter.createFromResource(NewAnimalActivity.this,R.array.dogs_breed,android.R.layout.simple_spinner_item);
                        breedSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        breedSpinner.setAdapter(breedSpinnerAdapter);
                        break;
                    }
                    case "Cat":{
                        animalKind = "Cat";
                        breedSpinnerAdapter = ArrayAdapter.createFromResource(NewAnimalActivity.this,R.array.cats_breed,android.R.layout.simple_spinner_item);
                        breedSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        breedSpinner.setAdapter(breedSpinnerAdapter);
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        breedSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        breedSpinner.setAdapter(breedSpinnerAdapter);
        breedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                animalBreed = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mixedBreedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout relativeLayout = NewAnimalActivity.this.findViewById(R.id.newAnimalInformation);//needs work
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                Spinner spinner = new Spinner(NewAnimalActivity.this);
                ArrayAdapter<CharSequence> newSpinnerArrayAdapter = ArrayAdapter.createFromResource(NewAnimalActivity.this,R.array.dogs_breed,android.R.layout.simple_spinner_item);
                newSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(newSpinnerArrayAdapter);
                params.addRule(RelativeLayout.BELOW,breedSpinner.getId());
                relativeLayout.addView(spinner,params);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_new_animal_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.take_animal_picture:{
                //opens the camera
                if(AskPermission())
                {
                    @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String imageFileName = "JPEG_" + timeStamp + "_";
                    File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    try {
                        File image = File.createTempFile(imageFileName,".jpg",storageDir);
                        photoPath = image.getAbsolutePath();
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        //Intent takePictureIntent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
                        if(takePictureIntent.resolveActivity(getPackageManager())!=null)
                        {
                            File photoFile;
                            photoFile = image;
                            if(photoFile!=null)
                            {
                                Uri photoURI = FileProvider.getUriForFile(NewAnimalActivity.this,
                                        "com.example.rishonlovesanimals.provider",photoFile);
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);

                                ImageUri = photoURI;
                                startActivityForResult(takePictureIntent,CAMERA_REQUEST);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("failed creating a file");
                    }
                }
                break;
            }
            case R.id.add_new_animal_btn:{
                if(photoAdded) {
                    //saves the data in all the slots
                    if (name.getText() != null)// && kind.getText()!= null)
                    {
                        Dog dog = new Dog((name.getText()).toString(), animalKind);
                        if (age.getText() != null)
                            dog.setAge(age.getText().toString());
                        if (history.getText() != null)
                            dog.setHistory(history.getText().toString());
                        if (medical.getText() != null)
                            dog.setMedical(medical.getText().toString());
                        if(breed.getText()!=null)
                        {
                            animalBreed = breed.getText().toString();
                            dog.setKind(animalBreed);
                        }
                        /*if (animalBreed != null)
                            dog.setKind(animalBreed);*/
                        if (note.getText() != null)
                            dog.additionalNotes(note.getText().toString());
                        if (imageUri != null)
                            dog.setImageUri(imageUri);
                        dogs.add(dog);
                        UpdateInformation();
                       // finish();
                    }
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(NewAnimalActivity.this);
                    builder.setTitle(getResources().getString(R.string.photo_requierd))
                    .setCancelable(true).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setMessage(R.string.photo_must).create();
                }

                break;
            }
            case R.id.animal_picture_from_gallery:{
                //opens the gallery
                Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery,SELECT_IMAGE);

            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST && resultCode == RESULT_OK)
        {
            Drawable drawable = Drawable.createFromPath(photoPath);
            assert drawable != null;
            Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
            bitmap = rotateImage(bitmap);
            photoAdded = true;
            if(bitmap!=null)
            {
                imageView.setImageBitmap(bitmap);
            }

        }
        if(requestCode == SELECT_IMAGE && resultCode == RESULT_OK)
        {
            if(data!=null)
                if(data.getData()!=null) {
                    imageUri = data.getData().toString();
                    imageView.setImageURI(data.getData());
                    photoAdded = true;
                }
        }

    }
    private boolean AskPermission()
    {
        int hasWritePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(hasWritePermission != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},WRITE_PERMISSION);
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
                Toast.makeText(NewAnimalActivity.this,"permission is required",Toast.LENGTH_SHORT).show();
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
                            Uri photoUri = FileProvider.getUriForFile(NewAnimalActivity.this,"com.example.rishonlovesanimals.provider",photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                            ImageUri = photoUri;
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
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("dogs",dogs);
        super.onBackPressed();
    }

    private void UpdateInformation()
    {
        //FirebaseAuth mAuth = FirebaseAuth.getInstance();
        //FirebaseUser user = mAuth.getCurrentUser();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        dogsFireBasePath = dogs.get(dogs.size()-1).getAnimalId();
        System.out.println("this is dogs fire base path " + dogsFireBasePath);
        dogs.get(dogs.size()-1).setFireBasePath(dogsFireBasePath);
        System.out.println("this is dogs fire base pathhhhhhhhhhhhh " + dogs.get(dogs.size()-1).getFireBasePath());
        DatabaseReference reference = database.getReference("animals/" + dogsFireBasePath);
        HashMap<String,String> animal = new HashMap<>();
        if(name.getText()!=null)
            animal.put("name",name.getText().toString());
        else
            animal.put("name","no name");
        if(age.getText()!=null)
            animal.put("age",age.getText().toString());
        else
            animal.put("age","unKnown");
        if(history.getText()!=null)
            animal.put("history",history.getText().toString());
        else
            animal.put("history","unKnown");
        if(medical.getText()!=null)
            animal.put("medical",medical.getText().toString());
        else
            animal.put("medical","unKnown");
        if (animalBreed!=null)
            animal.put("type",animalBreed);
        else
            animal.put("type","unKnown");
        animal.put("path",dogsFireBasePath);
        animal.put("uploaderName",uploaderName);
        animal.put("uploaderUId",uploaderUId);
        reference.setValue(animal);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
       // final StorageReference pictureReference = storageReference.child("animal_pictures/" + System.currentTimeMillis() + "." + "");
        final StorageReference pictureReference = storageReference.child("animal_pictures/" + dogs.get(dogs.size()-1).getSystemTimeInId() + "." + "");
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
        bitmap = Bitmap.createScaledBitmap(bitmap,500,480,false);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,70,out);
        //bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
        StorageTask<UploadTask.TaskSnapshot> uploadTask;
        uploadTask = pictureReference.putStream(new ByteArrayInputStream(out.toByteArray()));
        final int length = out.toByteArray().length;
        //uploadTask = pictureReference.putFile(ImageUri);
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
                long bytesTransferred = snapshot.getBytesTransferred();
                long progress = (bytesTransferred)/(length) * 100;
                progressBar.setProgress((int) progress);
                if(progress == 100)
                    progressBar.setVisibility(View.GONE);

            }
        }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful())
                /*{
                    Toast.makeText(NewAnimalActivity.this,"Started Uploading to server",Toast.LENGTH_SHORT).show();
                }
                else*/
                {
                    Toast.makeText(NewAnimalActivity.this,"There was a problem uploading the data,try again later",Toast.LENGTH_SHORT).show();
                    if(task.getException()!=null)
                         throw task.getException();
                }
                return pictureReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful())
                {
                    Uri uri = task.getResult();
                    if (uri != null) {
                        String sUri = uri.toString();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("animals/" + dogsFireBasePath);
                        HashMap<String,Object> pictureLink = new HashMap<>();
                        pictureLink.put("PictureLink",sUri);
                        databaseReference.updateChildren(pictureLink);

                    }
                    Toast.makeText(NewAnimalActivity.this,"Upload finished successfully",Toast.LENGTH_SHORT).show();
                    clearAll();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NewAnimalActivity.this,"Upload failed",Toast.LENGTH_SHORT).show();
            }
        });


    }
    private Bitmap rotateImage(Bitmap bitmap) {
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(photoPath);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_UNDEFINED);
            Matrix matrix = new Matrix();
            switch (orientation){
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.setRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.setRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.setRotate(270);
                    break;
                default:
                    System.out.println("default orientation");
            }
            return  Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void clearAll()
    {
        age.setText("");
        medical.setText("");
        history.setText("");
        note.setText("");
        name.setText("");
        imageView.setImageResource(R.drawable.ic_launcher_foreground);
        breed.setText("");
    }
}
