package rustelefonen.no.drikkevett_android.tabs.history;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.GraphHistory;
import rustelefonen.no.drikkevett_android.db.GraphHistoryDao;
import rustelefonen.no.drikkevett_android.db.History;
import rustelefonen.no.drikkevett_android.db.HistoryDao;
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;

public class BacHistoryFragment extends Fragment {
    private static final String TAG = "RecyclerViewFragment";
    private static final int SPAN_COUNT = 2;

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    protected LayoutManagerType mCurrentLayoutManagerType;
    protected RecyclerView mRecyclerView;
    protected HistoryAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    private List<History> historyList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        initDataset();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.history_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteAllHistories();
                Toast.makeText(getContext(), "Slettet alt as", Toast.LENGTH_SHORT).show();
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.bac_history_frag, container, false);
        rootView.setTag(TAG);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        mAdapter = new HistoryAdapter(historyList);
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    private void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        if (layoutManagerType == LayoutManagerType.GRID_LAYOUT_MANAGER) {
            mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
            mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
        } else {
            mLayoutManager = new LinearLayoutManager(getActivity());
            mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    private void initDataset() {
        SuperDao superDao = new SuperDao(getContext());
        HistoryDao historyDao = superDao.getHistoryDao();
        historyList = historyDao.queryBuilder().list();
        superDao.close();
    }

    private void deleteAllHistories() {
        SuperDao superDao = new SuperDao(getContext());
        HistoryDao historyDao = superDao.getHistoryDao();
        GraphHistoryDao graphHistoryDao = superDao.getGraphHistoryDao();

        List<GraphHistory> graphHistoryList = graphHistoryDao.queryBuilder().list();
        List<History> historyList = historyDao.queryBuilder().list();

        for (GraphHistory graphHistory : graphHistoryList) {
            graphHistoryDao.delete(graphHistory);
        }

        for (History history : historyList) {
            historyDao.delete(history);
        }

        /*for (History history : historyList) {
            for (GraphHistory graphHistory : graphHistoryList) {
                if (graphHistory.getHistoryId() == history.getId()) {
                    history.getGraphHistories().remove(graphHistory);
                    graphHistoryDao.delete(graphHistory);
                }
            }
            historyDao.delete(history);
        }*/
        superDao.close();
    }
}
