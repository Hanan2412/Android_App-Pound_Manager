package Animals;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Random;

public class Dog implements Animals, Serializable, Parcelable {

    private String name;
    private String age;
    private String history ="";
    private String medical="";
    private String type;
    private String kind;
    private String notes="";
    private String Uri;
    private String id;
    private String sysTimeId;
    private String fireBasePath;
    public Dog(String name, String kind) {
        this.name = name;
        this.kind = kind;
    }

    protected Dog(Parcel in) {
        name = in.readString();
        age = in.readString();
        history = in.readString();
        medical = in.readString();
        type = in.readString();
        kind = in.readString();
        notes = in.readString();
        Uri = in.readString();
        id = in.readString();
    }

    public static final Creator<Dog> CREATOR = new Creator<Dog>() {
        @Override
        public Dog createFromParcel(Parcel in) {
            return new Dog(in);
        }

        @Override
        public Dog[] newArray(int size) {
            return new Dog[size];
        }
    };

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setAge(String age) {
        this.age = age;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAge() {
        return age;
    }

    @Override
    public void setHistory(String history) {
        this.history = this.history + " " + history;
    }

    @Override
    public String getHistory() {
        return history;
    }

    @Override
    public void setMedical(String medical) {
        this.medical = this.medical + " " + medical;
    }

    @Override
    public String getMedical() {
        return medical;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setKind(String kind) {
        this.kind = kind;
    }

    @Override
    public String getKind() {
        return kind;
    }

    @Override
    public void additionalNotes(String note) {
        notes = notes + " " + note;
    }

    @Override
    public String getNotes() {
        return notes;
    }

    @Override
    public String getImageUri() {
        return Uri;
    }

    @Override
    public void setImageUri(String Uri) {
        this.Uri = Uri;
    }

    @Override
    public String getAnimalId() {
        sysTimeId = System.currentTimeMillis()*1000 + "";
        id = name + age + kind + " " + sysTimeId;
        return id;
    }

    public String getSystemTimeInId()
    {
        return sysTimeId;
    }

    public String getFireBasePath()
    {
        return fireBasePath;
    }

    public void setFireBasePath(String fireBasePath)
    {
        this.fireBasePath = fireBasePath;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(age);
        dest.writeString(history);
        dest.writeString(medical);
        dest.writeString(type);
        dest.writeString(kind);
        dest.writeString(notes);
        dest.writeString(Uri);
        dest.writeString(id);
    }
}
