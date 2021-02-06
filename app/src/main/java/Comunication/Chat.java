package Comunication;

import java.util.ArrayList;

public class Chat {

    private String sender,receiver,message,senderName;
    private boolean isSeen;
    private int type;
    private long timeCode;
    private ArrayList<String>readBy;
    public Chat(String sender, String receiver, String message, boolean isSeen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isSeen = isSeen;

    }
    public void setReadBy(ArrayList<String> readBy){
        this.readBy = readBy;
    }
    public void setSenderName(String name)
    {
        senderName = name;
    }
    public String getSenderName()
    {
        return senderName;
    }
    public Chat(){
        readBy = new ArrayList<>();
    }

    public void addReadBy(String userUid)
    {
        readBy.add(userUid);
    }

    public ArrayList<String> getReadBy(){
        return readBy;
    }

    public String getSpecificUserReadBy(int position)
    {
        return readBy.get(position);
    }


    public long getTimeCode()
    {
        return timeCode;
    }
    public void setTimeCode(long timeCode)
    {
        this.timeCode = timeCode;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(boolean isSeen) {
        this.isSeen = isSeen;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }
}
