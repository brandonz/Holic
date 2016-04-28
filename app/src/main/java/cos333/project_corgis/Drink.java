package cos333.project_corgis;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Type used for Stats that represents a drink in a session.
 */
public class Drink implements Parcelable {
    double amount;
    long time;
    double bac;

    // Required CREATOR field for Parcelable interface.
    // Generates instances of a Drink from a Parcel.
    public static final Parcelable.Creator<Drink> CREATOR
            = new Parcelable.Creator<Drink>() {
        public Drink createFromParcel(Parcel in) {
            return new Drink(in);
        }

        public Drink[] newArray(int size) {
            return new Drink[size];
        }
    };

    // Normal constructor.
    public Drink(double amount, long time, double bac) {
        this.amount = amount;
        this.time = time;
        this.bac = bac;
    }

    // Constructor from a Parcel. Assume order of read/write.
    public Drink(Parcel in) {
        amount = in.readDouble();
        time = in.readLong();
        bac = in.readDouble();
    }

    @Override
    // This method is stupid. You return 0 unless the object is a FileDescriptor ("special").
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(amount);
        dest.writeLong(time);
        dest.writeDouble(bac);
    }
}
