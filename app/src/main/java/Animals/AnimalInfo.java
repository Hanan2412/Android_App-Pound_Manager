package Animals;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rishonlovesanimals.R;
import com.squareup.picasso.Picasso;

public class AnimalInfo extends AppCompatActivity {
    private ImageView animalImage;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animal_view);
        TextView animalName,animalAge,animalType,animalHistory,animalMedical;
        animalName = findViewById(R.id.animal_name);
        animalAge = findViewById(R.id.animal_age);
        animalType = findViewById(R.id.animal_type);
        animalHistory = findViewById(R.id.animal_history);
        animalMedical = findViewById(R.id.animal_medical);
        animalImage = findViewById(R.id.animalViewPic);
        animalName.setText(getIntent().getStringExtra("animalName"));
        animalAge.setText(getIntent().getStringExtra("animalAge"));
        animalType.setText(getIntent().getStringExtra("animalKind"));
        animalHistory.setText(getIntent().getStringExtra("animalHistory"));
        animalMedical.setText(getIntent().getStringExtra("animalMedical"));
        loadImage();
    }

    private void loadImage()//accesses the server to download the correct image
    {
        Picasso.get().load(getIntent().getStringExtra("pictureLink")).into(animalImage);
        animalImage.setRotation(90);
    }

}
