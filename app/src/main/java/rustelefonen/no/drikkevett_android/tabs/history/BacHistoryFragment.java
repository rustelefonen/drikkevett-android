package rustelefonen.no.drikkevett_android.tabs.history;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import rustelefonen.no.drikkevett_android.MainActivity;
import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.NewHistory;
import rustelefonen.no.drikkevett_android.db.NewHistoryDao;
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;
import rustelefonen.no.drikkevett_android.util.NavigationUtil;

public class BacHistoryFragment extends Fragment {
    private static final String TAG = "RecyclerViewFragment";
    private static final int SPAN_COUNT = 2;

    public CardView defaultCard;

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    protected LayoutManagerType mCurrentLayoutManagerType;
    protected RecyclerView mRecyclerView;
    protected HistoryAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    private List<NewHistory> historyList;

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
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom));
                builder.setTitle("Slett all historikk")
                        .setMessage("Er du sikker på at du vil slette all historikk? Handlingen kan ikke angres.")
                        .setPositiveButton("JA", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deleteAllHistories();
                                refreshFragment();
                            }
                        })
                        .setNegativeButton("AVBRYT", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
                return false;
            case R.id.action_contact:
                NavigationUtil.navigateToContactInformation(getContext());
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.bac_history_frag, container, false);

        rootView.setTag(TAG);

        defaultCard = (CardView) rootView.findViewById(R.id.history_list_default_card);
        if (historyList.size() <= 0) defaultCard.setVisibility(View.VISIBLE);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        double goalBac = ((MainActivity)getActivity()).getUser().getGoalBAC();
        mAdapter = new HistoryAdapter(historyList, getContext(), goalBac);
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        displayHiddenCard();
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
        NewHistoryDao historyDao = superDao.getNewHistoryDao();

        List<NewHistory> allHistoriesSorted = historyDao.queryBuilder().orderDesc(NewHistoryDao.Properties.BeginDate).list();
        List<NewHistory> tempList = new ArrayList<>();

        for (NewHistory newHistory : allHistoriesSorted) {
            if (newHistory.getEndDate() != null) tempList.add(newHistory);
        }
        historyList = tempList;
        superDao.close();
    }

    private void deleteAllHistories() {
        /*SuperDao superDao = new SuperDao(getContext());
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
        superDao.close();*/
    }

    private void refreshFragment() {
        initDataset();
        double goalBac = ((MainActivity)getActivity()).getUser().getGoalBAC();
        mAdapter = new HistoryAdapter(historyList, getContext(), goalBac);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void displayHiddenCard() {
        if (historyList.size() <= 0) {
            defaultCard.setVisibility(View.VISIBLE);
        } else {
            defaultCard.setVisibility(View.GONE);
        }
    }
}