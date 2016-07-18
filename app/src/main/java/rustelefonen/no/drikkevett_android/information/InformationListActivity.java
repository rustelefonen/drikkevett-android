package rustelefonen.no.drikkevett_android.information;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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

        insertInformation();
        initDataset();

        mRecyclerView = (RecyclerView) findViewById(R.id.information_list_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        mAdapter = new InformationListAdapter(informationList);
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

    private void initDataset() {
        SuperDao superDao = new SuperDao(this);
        InformationDao informationDao = superDao.getInformationDao();
        informationList = informationDao.queryBuilder().where
                (InformationDao.Properties.CategoryId.eq(informationCategory.getId())).list();
        superDao.close();
    }

    private void insertInformation() {
        SuperDao superDao = new SuperDao(this);
        InformationDao informationDao = superDao.getInformationDao();
        Information information = new Information();
        information.setName("Treningstittel elns");
        information.setContent("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis egestas diam sed sem placerat, id euismod libero cursus. Proin gravida gravida tellus ac sodales. Fusce bibendum eu ligula non tristique. Phasellus a libero est. Vivamus posuere id sem a ultrices. Sed elementum pulvinar nibh, vitae porta turpis aliquet pulvinar. Nulla congue malesuada dui et sollicitudin. Suspendisse rhoncus, turpis non fringilla semper, urna lorem condimentum ligula, et sollicitudin est tellus in tellus. Vivamus est massa, hendrerit facilisis interdum ac, scelerisque sit amet elit. Vivamus tincidunt, sapien ac rutrum sagittis, libero nisi pharetra tortor, ac euismod dui nibh eu sapien. Proin eros elit, gravida nec erat a, pellentesque finibus sem. Aliquam ullamcorper, augue id facilisis dignissim, nunc felis scelerisque est, a pharetra est sapien nec ex. Nunc at enim ultricies, hendrerit nisl quis, tempus leo. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras lectus ligula, tincidunt eget vestibulum ac, egestas eu nunc.\n" +
                "\n");
        information.setCategoryId(informationCategory.getId());
        informationDao.insert(information);
        superDao.close();
    }
}
