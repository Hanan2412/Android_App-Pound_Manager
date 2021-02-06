package Adapters;


import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rishonlovesanimals.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import Comunication.Chat;
import Enums.MessagesTypes;
import Enums.MessagingConst;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder> {


    private List<Chat> chat_list;//all the messages in the chat board

    public interface MessageListener
    {
        void onMessageClick(String msg);
    }
    public void setListener(MessageListener listener){this.listener = listener;}
    public void setChatList(List<Chat> chat_list){this.chat_list = chat_list;}
    private MessageListener listener;

    public MessagesAdapter(List<Chat> chat_list)
    {
        this.chat_list = chat_list;
    }
    public MessagesAdapter(){chat_list = new ArrayList<>(); }
    public void addNewMessage(Chat newMessage)
    {
        chat_list.add(newMessage);
    }

    @NonNull
    @Override
    public MessagesAdapter.MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MessagingConst msg = MessagingConst.values()[viewType];
        View view = null;
        switch (msg) {
            case MSG_TYPE_LEFT: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.left_side_message, parent, false);//all the messages received
                break;
            }
            case MSG_TYPE_RIGHT: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.right_side_message, parent, false);//person sending message
                break;
            }
            default:
                Toast.makeText(parent.getContext(), "Error inflating layout", Toast.LENGTH_SHORT).show();
                break;
        }
        return new MessagesAdapter.MessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.MessagesViewHolder holder, int position) {
        final Chat chat = chat_list.get(position);
        String toSubString = "...";
        String text_to_display;
        final MessagesTypes types = MessagesTypes.values()[chat.getType()];
        switch (types){
            case text:{
                    holder.message.setText(chat.getMessage());
                    holder.sender.setText(chat.getSenderName());
                break;
            }
            case file:{
                    if(chat.getMessage().length() > 30) {
                        text_to_display = chat.getMessage().substring(0, 30) + toSubString;
                        holder.message.setText(text_to_display);
                    }else
                        holder.message.setText(chat.getMessage());
                    holder.message.setPaintFlags(holder.message.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                break;
            }
            case image:{
                    holder.message.setVisibility(View.GONE);
                    holder.picture.setVisibility(View.VISIBLE);
                    Picasso.get().load(chat.getMessage()).fit().into(holder.picture);
                break;
            }
            default:
                System.out.println("Error in onBindViewHolder MessageAdapter //////////////////////////////////////");
                break;
        }
        if(position == chat_list.size()-1){//checks for the last message
            if(chat.getIsSeen())
                holder.seenMessage.setText("Seen");
            else holder.seenMessage.setText("Delivered");
        }else
            holder.seenMessage.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (types){
                    case image:case file:{
                        if(listener!=null){
                            listener.onMessageClick(chat.getMessage());
                        }
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return chat_list.size();
    }

    public class MessagesViewHolder extends RecyclerView.ViewHolder{

        TextView message, seenMessage, sender;
        ImageView picture;
        public MessagesViewHolder(@NonNull View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.message_tv);
            seenMessage = itemView.findViewById(R.id.seen_indicator);
            sender = itemView.findViewById(R.id.senderName);
            picture = itemView.findViewById(R.id.message_picture);
        }
    }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null && chat_list.get(position).getSender()!=null) {
            if (chat_list.get(position).getSender().equals(firebaseUser.getUid()))
                return MessagingConst.MSG_TYPE_RIGHT.ordinal();
            else
                return MessagingConst.MSG_TYPE_LEFT.ordinal();
        }
        else
        {
            System.out.println("firebase User in MessageAdapter is null");
            return -1;
        }
    }
}
