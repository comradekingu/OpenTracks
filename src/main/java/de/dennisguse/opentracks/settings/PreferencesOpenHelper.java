package de.dennisguse.opentracks.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

import de.dennisguse.opentracks.R;
import de.dennisguse.opentracks.util.CsvConstants;

class PreferencesOpenHelper {

    private final int version;

    private PreferencesOpenHelper(int version) {
        this.version = version;
    }

    static PreferencesOpenHelper newInstance(int version) {
        return new PreferencesOpenHelper(version);
    }

    void check() {
        int lastVersion = PreferencesUtils.getInt(R.string.prefs_last_version_key, 0);
        if (version > lastVersion) {
            onUpgrade();
        } else if (version < lastVersion) {
            onDowngrade();
        }
    }

    private void onUpgrade() {
        PreferencesUtils.setInt(R.string.prefs_last_version_key, version);
        for (int i = 1; i <= version; i++) {
            switch (i) {
                case 1:
                    upgradeFrom0to1();
                    break;
                case 2:
                    upgradeFrom1to2();
                    break;
            }
        }
    }

    private void upgradeFrom0to1() {
        String preferenceValue = PreferencesUtils.getString(R.string.stats_custom_layouts_key, "");
        if (preferenceValue.isEmpty()) {
            PreferencesUtils.setString(R.string.stats_custom_layouts_key, PreferencesUtils.buildDefaultLayout());
        }
    }

    private void upgradeFrom1to2() {
        String csvVersion1CustomLayout = PreferencesUtils.getString(R.string.stats_custom_layouts_key, PreferencesUtils.buildDefaultLayout());
        ArrayList<String> parts = new ArrayList<>();
        Collections.addAll(parts, csvVersion1CustomLayout.split(CsvConstants.ITEM_SEPARATOR));
        if (!parts.get(1).matches("\\d+")) {
            parts.add(1, String.valueOf(PreferencesUtils.getLayoutColumnsByDefault()));
        }
        PreferencesUtils.setString(R.string.stats_custom_layouts_key, parts.stream().collect(Collectors.joining(CsvConstants.ITEM_SEPARATOR)));
    }

    private void onDowngrade() {
        PreferencesUtils.setString(R.string.stats_custom_layouts_key, PreferencesUtils.buildDefaultLayout());
    }
}
