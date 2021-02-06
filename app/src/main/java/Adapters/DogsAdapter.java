package Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rishonlovesanimals.R;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

import Animals.Dog;

public class DogsAdapter extends RecyclerView.Adapter<DogsAdapter.DogsViewHolder> {

    private DogListener listener;
    private ArrayList<Dog> dogsList;

    public DogsAdapter(ArrayList<Dog> dogsList){this.dogsList = dogsList;}
    public DogsAdapter() {
        dogsList = new ArrayList<>();
        //populateDogs();
    }


    public interface DogListener {
        void onDogClicked(int position,View view);
        void onDogLongClicked(int position,View view);

    }
    public void setListener(DogListener listener){
        this.listener = listener;
    }
    public void setDogsList(ArrayList<Dog> dogsList){
        this.dogsList = dogsList;
    }
    public class DogsViewHolder extends RecyclerView.ViewHolder {
        TextView name,age,type;
        ImageView dogPic;

        public DogsViewHolder(View itemView){
            super(itemView);
            //here goes the reference to the corresponding values in cardView
            name = itemView.findViewById(R.id.cardAnimalName);
            age = itemView.findViewById(R.id.cardAnimalAge);
            type = itemView.findViewById(R.id.cardAnimalType);
            dogPic = itemView.findViewById(R.id.cardAnimalPicture);
            //------------------------------------------------//
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null)
                         listener.onDogClicked(getAdapterPosition(),v);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(listener!=null)
                         listener.onDogLongClicked(getAdapterPosition(),v);
                    return false;
                }
            });
        }
    }
    @NonNull
    @Override
    public DogsAdapter.DogsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout,parent,false);
        return new DogsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DogsAdapter.DogsViewHolder holder, int position) {
        //updates the values in cardView
        Dog dog = dogsList.get(position);
        holder.name.setText(dog.getName());
        holder.age.setText(dog.getAge());
        holder.type.setText(dog.getKind());
        ///////////////////////////////////////
        //here should be a server download of all the data
        System.out.println("this is path: " + dog.getImageUri());
        Picasso.get().load(dog.getImageUri()).into(holder.dogPic);
        //holder.dogPic.setRotation(90);
        holder.dogPic.setScaleType(ImageView.ScaleType.FIT_XY);

        //holder.dogPic.setImageURI(Uri.parse(dog.getImageUri()));
    }

    @Override
    public int getItemCount() {
        return dogsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }




}
