package com.bookstore.main;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bookstore.booklist.BookListGridListView;
import com.bookstore.booklist.BookListGridListViewAdapter;
import com.bookstore.booklist.BookListViewPagerAdapter;
import com.bookstore.booklist.ListViewListener;
import com.bookstore.bookparser.BookCategory;
import com.bookstore.provider.BookProvider;
import com.bookstore.provider.DB_Column;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/5.
 */
public class MainBookListFragment extends Fragment {
    public DBHandler dbHandler = null;
    public BookCategory mBookCategory = null;
    private Activity mActivity;
    private View booklist_fragment = null;
    private ViewPager bookListViewPager;
    private BookListViewPagerAdapter pagerAdapter;
    private BookListGridListView gridListView = null;
    private BookListGridListViewAdapter mGridListViewAdapter;

    public static MainBookListFragment newInstance() {
        MainBookListFragment fragment = new MainBookListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();

        mBookCategory = new BookCategory();
        if (mActivity instanceof MainActivity) {
            if (((MainActivity) mActivity).isFirstLaunch()) {
                ArrayList<BookCategory.CategoryItem> list = mBookCategory.getDefault_category_list();
                DBHandler.saveBookCategory(mActivity, list);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        booklist_fragment = inflater.inflate(R.layout.booklist_fragment, null);
        List<View> viewList = new ArrayList<View>();
        View booklist_gridview = inflater.inflate(R.layout.booklist_gridview, null);
        View booklist_listview = inflater.inflate(R.layout.booklist_listview, null);
        viewList.add(booklist_gridview);
        viewList.add(booklist_listview);

        bookListViewPager = (ViewPager) booklist_fragment.findViewById(R.id.bookListPager);
        pagerAdapter = new BookListViewPagerAdapter(mActivity, viewList);
        bookListViewPager.setAdapter(pagerAdapter);

        gridListView = (BookListGridListView) booklist_gridview.findViewById(R.id.booklist_grid);
        mGridListViewAdapter = new BookListGridListViewAdapter(mActivity);
        gridListView.setAdapter(mGridListViewAdapter);
        gridListView.setListViewListener(new ListViewListener() {
            @Override
            public void onRefresh() {
                //Execute a syncTask to Refresh
                refreshBookList();
            }

            @Override
            public void onLoadMore() {

            }
        });

        dbHandler = new DBHandler(mGridListViewAdapter);

        return booklist_fragment;
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
        refreshBookList();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopRefreshBookList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    synchronized public void stopRefreshBookList() {
        ArrayList<DBHandler.LoaderItem> loaders = dbHandler.getLoaders();
        for (DBHandler.LoaderItem item : loaders) {
            if (item.loader != null) {
                item.loader.unregisterListener(item.listener);
                item.loader.reset();
                item.listener = null;
            }
        }
        dbHandler.reset();
        mGridListViewAdapter.reset();
    }

    public void refreshBookList() {
        String selection = null;
        stopRefreshBookList();
        ArrayList<BookCategory.CategoryItem> categoryList = mBookCategory.getDefault_category_list();

        for (BookCategory.CategoryItem item : categoryList) {
            if (item.category_code != 'a') {//if category_code is 'a', that means all, it not need selection
                selection = DB_Column.BookInfo.CATEGORY_CODE
                        + "="
                        + item.category_code;
            }
            String[] projection = {DB_Column.BookInfo.IMG_LARGE, DB_Column.BookInfo.CATEGORY_CODE};
            dbHandler.loadBookList(mActivity, BookProvider.BOOKINFO_URI, projection, selection, null, DB_Column.BookInfo.ID + " DESC LIMIT 3");
        }
    }

    public void setListViewVerticalScrollBarEnable(boolean enable) {
        gridListView.setVerticalScrollBarEnabled(enable);
    }
}