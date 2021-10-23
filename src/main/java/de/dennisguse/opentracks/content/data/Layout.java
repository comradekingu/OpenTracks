package de.dennisguse.opentracks.content.data;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.dennisguse.opentracks.R;
import de.dennisguse.opentracks.settings.PreferencesUtils;
import de.dennisguse.opentracks.util.CsvConstants;

public class Layout implements Parcelable {
    private final String profile;
    private int columnsPerRow;
    private final List<DataField> dataFields = new ArrayList<>();

    public static Layout fromCsv(String csvLine, Resources resources) {
        List<String> csvParts = Arrays.asList(csvLine.split(CsvConstants.ITEM_SEPARATOR));
        Layout layout = new Layout(csvParts.get(0), Integer.parseInt(csvParts.get(1)));
        for (int i = 2; i < csvParts.size(); i++) {
            String[] fieldParts = csvParts.get(i).split(CsvConstants.PROPERTY_SEPARATOR);
            layout.addField(fieldParts[0], DataField.getTitleByKey(resources, fieldParts[0]), fieldParts[1].equals(DataField.YES_VALUE), fieldParts[2].equals(DataField.YES_VALUE), fieldParts[0].equals(resources.getString(R.string.stats_custom_layout_coordinates_key)));
        }
        return layout;
    }

    public Layout(String profile) {
        this.profile = profile;
        this.columnsPerRow = PreferencesUtils.getLayoutColumnsByDefault();
    }

    public Layout(String profile, int columnsPerRow) {
        this.profile = profile;
        this.columnsPerRow = columnsPerRow;
    }

    protected Layout(Parcel in) {
        profile = in.readString();
        columnsPerRow = in.readInt();
        in.readList(dataFields, DataField.class.getClassLoader());
    }

    public static final Creator<Layout> CREATOR = new Creator<Layout>() {
        @Override
        public Layout createFromParcel(Parcel in) {
            return new Layout(in);
        }

        @Override
        public Layout[] newArray(int size) {
            return new Layout[size];
        }
    };

    public void addField(String key, String title, boolean visible, boolean primary, boolean isWide) {
        dataFields.add(new DataField(key, title, visible, primary, isWide));
    }

    public void addField(DataField dataField) {
        dataFields.add(dataField);
    }

    public void addFields(List<DataField> dataFields) {
        this.dataFields.addAll(dataFields);
    }

    public void removeField(DataField dataField) {
        dataFields.remove(dataField);
    }

    public void replaceAllFields(List<DataField> newFields) {
        dataFields.clear();
        addFields(newFields);
    }

    public List<DataField> getFields() {
        return new ArrayList<>(dataFields);
    }

    public void moveField(int from, int to) {
        DataField dataFieldToMove = dataFields.remove(from);
        dataFields.add(to, dataFieldToMove);
    }

    public String getProfile() {
        return profile;
    }

    public int getColumnsPerRow() {
        return columnsPerRow;
    }

    public void setColumnsPerRow(int columnsPerRow) {
        this.columnsPerRow = columnsPerRow;
    }

    public boolean sameProfile(Layout layout) {
        return this.profile.equalsIgnoreCase(layout.getProfile());
    }

    public boolean sameProfile(String profile) {
        return this.profile.equalsIgnoreCase(profile);
    }

    public String toCsv() {
        List<DataField> fields = getFields();
        if (fields.isEmpty()) {
            return "";
        }

        return getProfile() + CsvConstants.ITEM_SEPARATOR + getColumnsPerRow() + CsvConstants.ITEM_SEPARATOR
                + fields.stream().map(DataField::toCsv).collect(Collectors.joining(CsvConstants.ITEM_SEPARATOR))
                + CsvConstants.ITEM_SEPARATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(profile);
        parcel.writeInt(columnsPerRow);
        parcel.writeList(dataFields);
    }
}
