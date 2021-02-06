package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rishonlovesanimals.R;

import java.util.ArrayList;

public class UserProfileAdapter extends BaseAdapter {

    private ArrayList<String>userInfo;

    public UserProfileAdapter()
    {
    }
    public void setUserInfo(ArrayList<String>userInfo){this.userInfo = userInfo;}

    @Override
    public int getCount() {
        return userInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return userInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(layoutInflater!=null) {
            convertView = layoutInflater.inflate(R.layout.animal_info_cell, parent, false);
            TextView text = convertView.findViewById(R.id.animal_info_cell_txt);
            text.setText(userInfo.get(position));
            return convertView;
        }
        Toast.makeText(parent.getContext(), "Error in UserProfileAdapter", Toast.LENGTH_SHORT).show();
        return null;
    }
}
