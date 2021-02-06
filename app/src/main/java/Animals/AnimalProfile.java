package Animals;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.rishonlovesanimals.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

import Adapters.AnimalProfileListAdapter;

public class AnimalProfile extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animal_profile);
        ListView listView = findViewById(android.R.id.list);
        AnimalProfileListAdapter adapter = new AnimalProfileListAdapter();
        ArrayList<String> animalProfileInfo = new ArrayList<>();
        animalProfileInfo.add(getResources().getString(R.string.name) + ": " + getIntent().getStringExtra("animalName"));
        animalProfileInfo.add(getResources().getString(R.string.animal_kind) + ": " + getIntent().getStringExtra("animalKind"));
        animalProfileInfo.add(getResources().getString(R.string.animal_age) + ": " +getIntent().getStringExtra("animalAge"));
        animalProfileInfo.add(getResources().getString(R.string.animal_history) + ": " + getIntent().getStringExtra("animalHistory"));
        animalProfileInfo.add(getResources().getString(R.string.animal_medical) + ": " + getIntent().getStringExtra("animalMedical"));
        adapter.setAnimalInfo(animalProfileInfo);
        listView.setAdapter(adapter);
        ImageView animalImage = findViewById(R.id.animal_pic);
        Picasso.get().load(getIntent().getStringExtra("pictureLink")).into(animalImage);
        //animalImage.setRotation(90);
    }
}
