package rustelefonen.no.drikkevett_android.information;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.List;

import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.InformationCategory;
import rustelefonen.no.drikkevett_android.db.InformationCategoryDao;
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;

/**
 * Created by simenfonnes on 15.07.2016.
 */

public class InformationCategoryActivity extends AppCompatActivity {
    private static final int SPAN_COUNT = 2;

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    protected LayoutManagerType mCurrentLayoutManagerType;
    protected RecyclerView mRecyclerView;
    protected InformationCategoryAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    private List<InformationCategory> informationCategories;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_category_layout);

        insertInformationCategory();
        initDataset();


        mRecyclerView = (RecyclerView) findViewById(R.id.information_category_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        mAdapter = new InformationCategoryAdapter(informationCategories);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        if (layoutManagerType == LayoutManagerType.GRID_LAYOUT_MANAGER) {
            mLayoutManager = new GridLayoutManager(this, SPAN_COUNT);
            mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
        } else {
            mLayoutManager = new LinearLayoutManager(this);
            mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    private void insertInformationCategory() {
        SuperDao superDao = new SuperDao(this);

        InformationCategoryDao informationCategoryDao = superDao.getInformationCategoryDao();
        InformationCategory informationCategory = new InformationCategory();
        informationCategory.setName("Trening");
        informationCategoryDao.insert(informationCategory);

        superDao.close();
    }

    private void initDataset() {
        SuperDao superDao = new SuperDao(this);
        InformationCategoryDao informationCategoryDao = superDao.getInformationCategoryDao();
        informationCategories = informationCategoryDao.queryBuilder().list();
        superDao.close();
    }
}
