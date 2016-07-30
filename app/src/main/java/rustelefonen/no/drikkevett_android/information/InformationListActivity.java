package rustelefonen.no.drikkevett_android.information;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;

import java.util.List;

import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.Information;
import rustelefonen.no.drikkevett_android.db.InformationCategory;
import rustelefonen.no.drikkevett_android.db.InformationDao;
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;

/**
 * Created by simenfonnes on 18.07.2016.
 */

public class InformationListActivity extends AppCompatActivity{
    public static final String ID = "InformationList";
    private static final int SPAN_COUNT = 2;

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    protected LayoutManagerType mCurrentLayoutManagerType;
    protected RecyclerView mRecyclerView;
    protected InformationListAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    private List<Information> informationList;
    private InformationCategory informationCategory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_list_layout);

        Object tmpInformationCategory = getIntent().getSerializableExtra(ID);

        if (tmpInformationCategory != null && tmpInformationCategory instanceof InformationCategory) {
            informationCategory = (InformationCategory) tmpInformationCategory;
        }

        //insertInformation();
        initDataset();

        mRecyclerView = (RecyclerView) findViewById(R.id.information_list_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        mAdapter = new InformationListAdapter(informationList);
        mRecyclerView.setAdapter(mAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.textColor));

        if (informationCategory != null) {
            String title = informationCategory.getName();
            if (title != null) toolbar.setTitle(title);
        }


        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

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

    private void initDataset() {
        SuperDao superDao = new SuperDao(this);
        InformationDao informationDao = superDao.getInformationDao();
        informationList = informationDao.queryBuilder().where
                (InformationDao.Properties.CategoryId.eq(informationCategory.getId())).list();
        superDao.close();
    }

    private void goBack() {
        super.onBackPressed();
    }
}
