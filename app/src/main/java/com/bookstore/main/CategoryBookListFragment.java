package com.bookstore.main;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.bookstore.booklist.CategoryBookGridViewAdapter;
import com.bookstore.bookparser.BookCategory;

/**
 * Created by Administrator on 2016/4/6.
 */
public class CategoryBookListFragment extends Fragment {
    public static final String ARGS_CATEGORY_CODE = "category_code";
    private Activity mActivity;
    private int mCategoryCode = 0;
    private View category_fragment = null;
    private CategoryBookGridViewAdapter gridViewAdapter = null;

    public static CategoryBookListFragment newInstance(int category_code) {
        CategoryBookListFragment fragment = new CategoryBookListFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_CATEGORY_CODE, category_code);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mCategoryCode = getArguments().getInt(ARGS_CATEGORY_CODE, 0);
        Log.i("BookStore", "read books by category code " + mCategoryCode);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        category_fragment = inflater.inflate(R.layout.category_list_fragment, null);
        TextView title = (TextView) category_fragment.findViewById(R.id.book_category_name);
        BookCategory bookCategory = new BookCategory();
        title.setText(bookCategory.getCategoryName(mCategoryCode));

        GridView gridView = (GridView) category_fragment.findViewById(R.id.category_book_gridview);
        gridViewAdapter = new CategoryBookGridViewAdapter(mActivity);
        gridView.setAdapter(gridViewAdapter);
        return category_fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}