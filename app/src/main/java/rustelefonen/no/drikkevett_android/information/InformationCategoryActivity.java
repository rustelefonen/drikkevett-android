package rustelefonen.no.drikkevett_android.information;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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

    private List<InformationCategory> informationCategories;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_category_layout);

        insertInformationCategory();
        initDataset();


        GridView gridview = (GridView) findViewById(R.id.information_category_grid_view);
        gridview.setAdapter(new InformationCategoryAdapter(this, informationCategories));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Toast.makeText(v.getContext(), "" + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(v.getContext(), InformationListActivity.class);
                intent.putExtra(InformationListActivity.ID, informationCategories.get(position));
                startActivity(intent);
            }
        });
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
