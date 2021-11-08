package de.dennisguse.opentracks.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import de.dennisguse.opentracks.R;
import de.dennisguse.opentracks.content.data.Layout;

@RunWith(AndroidJUnit4.class)
public class PreferencesOpenHelperTest {

    private final Context context = ApplicationProvider.getApplicationContext();

    @Test
    public void test_upgradeFrom0To1_withoutStatsCustomLayouts() {
        // given the version 0
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(context.getString(R.string.stats_custom_layouts_key));
        editor.putInt(context.getString(R.string.prefs_last_version_key), 0);
        editor.commit();

        // when update to version 1
        PreferencesOpenHelper.newInstance(1).checkForUpgrade();

        // then there should be one layout with old custom layout that has the new CSV value.
        List<Layout> layouts = PreferencesUtils.getAllCustomLayouts();
        assertNotNull(layouts);
        assertEquals(layouts.size(), 1);
        assertEquals(layouts.get(0).toCsv(), PreferencesUtils.getCustomLayout().toCsv());
    }

    @Test
    public void test_upgradeFrom1To2_withOldVersion() {
        // given the version 1
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String oldCustomLayoutCsv = context.getString(R.string.activity_type_unknown) + ";distance,1,1,0;speed,1,1,0;";
        editor.putString(context.getString(R.string.stats_custom_layouts_key), oldCustomLayoutCsv);
        editor.putInt(context.getString(R.string.prefs_last_version_key), 1);

        editor.commit();

        // when update to version 2
        PreferencesOpenHelper.newInstance(2).checkForUpgrade();

        String updatedOldCustomLayoutCsv = context.getString(R.string.activity_type_unknown) + ";"
                + PreferencesUtils.getLayoutColumnsByDefault() + ";distance,1,1,0;speed,1,1,0;";

        // then there should be one layout with old custom layout that has the new CSV value.
        List<Layout> layouts = PreferencesUtils.getAllCustomLayouts();
        assertNotNull(layouts);
        assertEquals(layouts.size(), 1);
        assertEquals(layouts.get(0).toCsv(), updatedOldCustomLayoutCsv);
    }

    @Test
    public void test_upgradeFrom1To2_withNewVersion() {
        // given the version 1
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String oldCustomLayoutCsv = "whatever;3;distance,1,1,0;speed,1,1,0;";
        editor.putString(context.getString(R.string.stats_custom_layouts_key), oldCustomLayoutCsv);
        editor.putInt(context.getString(R.string.prefs_last_version_key), 1);

        editor.commit();

        // when update to version 2
        PreferencesOpenHelper.newInstance(2).checkForUpgrade();

        String updatedOldCustomLayoutCsv = "whatever;3;distance,1,1,0;speed,1,1,0;";

        // then there should be one layout with old custom layout that has the new CSV value.
        List<Layout> layouts = PreferencesUtils.getAllCustomLayouts();
        assertNotNull(layouts);
        assertEquals(layouts.size(), 1);
        assertEquals(layouts.get(0).toCsv(), updatedOldCustomLayoutCsv);
    }
}
