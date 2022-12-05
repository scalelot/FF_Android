package com.example.friendfield.TagView;

import android.os.Parcel;
import android.os.Parcelable;

public class Person implements Parcelable {
    private String name;

    public Person(String n) {
        name = n;
    }

    public static Person[] samplePeople() {
        return new Person[]{
                new Person("Playing"),
                new Person("Running"),
                new Person("Reading"),
                new Person("Learnning"),
                new Person("Singing")
        };
    }

    public String getName() { return name; }

    @Override
    public String toString() { return name; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
    }

    private Person(Parcel in) {
        this.name = in.readString();
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel source) {
            return new Person(source);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };
}