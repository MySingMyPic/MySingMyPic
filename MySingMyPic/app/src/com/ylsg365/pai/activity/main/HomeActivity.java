package com.ylsg365.pai.activity.main;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.HomeAdapter;
import com.ylsg365.pai.activity.base.BaseActivity;

public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home, container, false);
            TextView titileTextView = (TextView)rootView.findViewById(R.id.toolbar_title);
            titileTextView.setText("首页");
            rootView.findViewById(R.id.text_toolbar_left).setVisibility(View.GONE);
            rootView.findViewById(R.id.text_right).setVisibility(View.VISIBLE);

            RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_song_category);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

                Paint paint = new Paint();

                @Override
                public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                    super.onDraw(c, parent, state);
                }

                @Override
                public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
                    super.onDrawOver(c, parent, state);
                    paint.setColor(getResources().getColor(R.color.line_radio));
                    for (int i = 0, size = parent.getChildCount(); i < size; i++) {
                        View child = parent.getChildAt(i);
                        c.drawLine(child.getLeft() + 20, child.getBottom(), child.getRight(), child.getBottom(), paint);
                    }
                }

                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    super.getItemOffsets(outRect, view, parent, state);
                }
            });
            recyclerView.setAdapter(new HomeAdapter(R.layout.item_home, 30));
            return rootView;
        }
    }
}
