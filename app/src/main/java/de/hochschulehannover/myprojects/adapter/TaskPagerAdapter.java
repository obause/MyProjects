package de.hochschulehannover.myprojects.adapter;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.hochschulehannover.myprojects.R;

/**
 * <h2>Adapterklasse TaskPagerAdapter</h2>
 *
 * Adapterklasse, die ein zu einem Tab zugehöriges Fragment zurückgibt
 * Diese erbt von {@link FragmentPagerAdapter}.
 *
 * TODO: FragmentPagerAdapter ist veraltet. Neueren ViewPager2 nutzen und dafür eine FragmentStateAdapter Klasse erstellen.
 *
 * Das Fragment wird mit dem entsprechenden Tab verbunden bzw. zu diesem Tag hinzugefügt.
 *
 *<p>
 * <b>Autor: Constantin</b>
 * </p>
 */
public class TaskPagerAdapter extends FragmentPagerAdapter {

    private final ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    private final ArrayList<String> fragmentTitle = new ArrayList<>();

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3};
    private final Context mContext;

    public TaskPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem wird aufgerufen, um das Fragment für den entsprechenden Tab zu initialisieren.
        // Das Fragment wird hier nicht initialisiert. Stattdessen werden alle drei Fragments
        // an diese Klasse übergeben.
        //return PlaceholderFragment.newInstance(position + 1);
        return fragmentArrayList.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        //return mContext.getResources().getString(TAB_TITLES[position]);
        return fragmentTitle.get(position);
    }

    @Override
    public int getCount() {
        // Anzahl der Tabs entsprechend der Anzahl der Fragments in der ArrayList
        return fragmentArrayList.size();
    }

    // Mit addFragment werden die erstellen Fragment-Objekte aus der Klasse der Activity hier zur
    // ArrayList hinzugefügt.
    public void addFragment(Fragment fragment, String title) {
        fragmentArrayList.add(fragment);
        fragmentTitle.add(title);
    }
}