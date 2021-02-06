package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rishonlovesanimals.Profile;
import com.example.rishonlovesanimals.R;

import java.util.List;

public class UserListAdapter extends BaseAdapter {
    private List<Profile> users;

    public UserListAdapter(List<Profile> users)
    {
        this.users = users;
    }

    public void setUsers(List<Profile>users)
    {
        this.users = users;
    }
    public interface UsersInterface
    {
        void onUserPressed(int position);
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(layoutInflater!=null) {
            convertView = layoutInflater.inflate(R.layout.user_cell, parent, false);
            TextView usersName = convertView.findViewById(R.id.user_cell_TV);
            ImageView usersImage = convertView.findViewById(R.id.user_cell_image);
            usersName.setText(users.get(position).getProfile_name());
            //usersImage.setImageURI();
        }
        return convertView;
    }
}
