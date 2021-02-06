package Tasks;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Task implements Serializable, Parcelable {
    private boolean isDone;
    private String taskTime;
    private String task;
    public Task()
    {

    }

    protected Task(Parcel in) {
        isDone = in.readByte() != 0;
        taskTime = in.readString();
        task = in.readString();
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    public String getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(String taskTime) {
        this.taskTime = taskTime;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getTask()
    {
        return task;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isDone ? 1 : 0));
        dest.writeString(taskTime);
        dest.writeString(task);
    }
}
