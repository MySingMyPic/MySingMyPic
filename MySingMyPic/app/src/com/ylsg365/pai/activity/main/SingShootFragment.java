package com.ylsg365.pai.activity.main;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ylsg365.pai.R;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.event.NavEvent;
import com.ylsg365.pai.util.StringUtil;

import de.greenrobot.event.EventBus;

public class SingShootFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    //搜索
    private EditText searchInput;
    private ImageView searchIcon;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SingShootFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SingShootFragment newInstance(String param1, String param2) {
        SingShootFragment fragment = new SingShootFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SingShootFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sing_shoot, container, false);
        TextView titileTextView = (TextView)rootView.findViewById(R.id.toolbar_title);
        titileTextView.setText("我唱我拍");
        rootView.findViewById(R.id.text_toolbar_left).setVisibility(View.GONE);
        rootView.findViewById(R.id.text_right).setVisibility(View.VISIBLE);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_fresh);
        recyclerView.setHasFixedSize(true);

        View header = LayoutInflater.from(getActivity()).inflate(R.layout.layout_category, null);

        final SingShootAdapter singShootAdapter = new SingShootAdapter(header, R.layout.item_home, 30);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.setAdapter(singShootAdapter);

        searchInput=(EditText)rootView.findViewById(R.id.edit_search);
        searchInput.setHint("输入歌曲名");
        searchIcon=(ImageView)rootView.findViewById(R.id.img_search);
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = searchInput.getText().toString();
                if (StringUtil.isNull(keyword)) {
                    Toast.makeText(getActivity(), "请输入歌曲名进行搜索.", Toast.LENGTH_LONG).show();
                } else {
                    NavHelper.toSongActivity(getActivity(),keyword);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    // This method will be called when a SomeOtherEvent is posted
    public void onEvent(NavEvent navEvent){
//        Toast.makeText(getActivity(), "" + navEvent.id, Toast.LENGTH_SHORT).show();

        switch (navEvent.id){
            case R.id.layout_category_1:
                NavHelper.toNewsInfoSendPage(getActivity());
                break;
            case R.id.layout_category_2:
                NavHelper.toOriginalBaseListPage(getActivity(), NavHelper.REQUEST_ALL_ORIGINAL);
                break;
            case R.id.layout_category_3:
                NavHelper.toKaraokePage(getActivity());
                break;
            case R.id.layout_category_5:
                NavHelper.toCappellaRecordPage(getActivity());
                break;
            case R.id.layout_category_6:
                NavHelper.toSingerPage(getActivity());
                break;
            case R.id.layout_category_7:
                NavHelper.toSongCategoryPage(getActivity());
                break;
            case R.id.layout_category_8:
                NavHelper.toGameCenterPage(getActivity());
                break;
        }
    }
}
