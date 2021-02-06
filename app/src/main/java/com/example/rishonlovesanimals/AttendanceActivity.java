package com.example.rishonlovesanimals;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
//Displays all the users arriving status
public class AttendanceActivity extends ListActivity {
    private  ArrayList<String>arriving;

    private DatabaseReference databaseReference1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_layout);

        arriving = new ArrayList<>();
        DownloadInformation();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.list_cell_layout,R.id.list_cell_layout_Tv,arriving);
        setListAdapter(adapter);
    }

    private void DownloadInformation()
    {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arriving.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    String value = dataSnapshot.getKey();
                  databaseReference1 = FirebaseDatabase.getInstance().getReference("users/" + value + "/userData");
                    databaseReference1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            StringBuilder string = new StringBuilder();
                            for(DataSnapshot ds: snapshot.getChildren())
                            {
                                String b = ds.getKey();
                                Object a = ds.getValue();
                                assert b != null;
                                if(b.equals("name")) {
                                   string.append(a);
                                   string.append(" ");
                                }
                                if(b.equals("arriving"))
                                {
                                    assert a != null;
                                    if(a.equals("true"))
                                        string.append(getResources().getString(R.string.arriving));
                                    else
                                        string.append(getResources().getString(R.string.not_arriving));
                                    string.append(" ");
                                }
                            }
                            arriving.add(string.toString());
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(AttendanceActivity.this,R.layout.list_cell_layout,R.id.list_cell_layout_Tv,arriving);
                            setListAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
               ArrayAdapter<String> adapter = new ArrayAdapter<String>(AttendanceActivity.this,R.layout.list_cell_layout,R.id.list_cell_layout_Tv,arriving);
                setListAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
