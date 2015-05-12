package com.ylsg365.pai.face;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ylsg365.pai.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class FaceView {
	public FaceListener listener;

	public List<Integer> data = new ArrayList<Integer>();

    GridView gridview;
    FaceGridViewAdapter adapter;
    public View faceView(Activity context,final FaceListener listener)
    {

        initData();

    	View root=LayoutInflater.from(context).inflate(R.layout.face_layout, null);

        gridview = (GridView) root
                .findViewById(R.id.gridview);
        adapter=new FaceGridViewAdapter(context,data);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.selectFace(data.get(position));
            }
        });
    	return root;
    }


    public void initData()
    {
        for(int i = 0; i < 25; i++){
            try {
                if(i<10){
                    Field field = R.drawable.class.getDeclaredField("emoji_1f60" + i);
                    int resourceId = Integer.parseInt(field.get(null).toString());
                    data.add( resourceId);
                }else{
                    Field field = R.drawable.class.getDeclaredField("emoji_1f6" + i);
                    int resourceId = Integer.parseInt(field.get(null).toString());
                    data.add( resourceId);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
