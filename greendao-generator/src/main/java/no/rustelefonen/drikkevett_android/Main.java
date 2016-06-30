package no.rustelefonen.drikkevett_android;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class Main {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "rustelefonen.no.drikkevett_android.db");

        Entity dayAfterBAC = schema.addEntity("DayAfterBAC");
        dayAfterBAC.addIdProperty();
        dayAfterBAC.addDateProperty("timestamp");
        dayAfterBAC.addStringProperty("unit");

        Entity graphHistory = schema.addEntity("GraphHistory");
        graphHistory.addIdProperty();
        graphHistory.addDoubleProperty("currentBAC");
        graphHistory.addDateProperty("timestamp");

        Entity history = schema.addEntity("History");
        history.addIdProperty();
        history.addIntProperty("drinkCount");
        history.addIntProperty("beerCount");
        history.addIntProperty("shotCount");
        history.addIntProperty("wineCount");
        history.addDateProperty("startDate");
        history.addDateProperty("endDate");
        history.addIntProperty("sum");
        history.addDoubleProperty("highestBAC");
        history.addIntProperty("plannedUnitsCount");

        Property historyId = graphHistory.addLongProperty("historyId").notNull().getProperty();
        ToMany historyToGraphHistory = history.addToMany(graphHistory, historyId);
        historyToGraphHistory.setName("graphHistories");

        Entity userData = schema.addEntity("User");
        userData.addIntProperty("age");
        userData.addIntProperty("beerPrice");
        userData.addIntProperty("winePrice");
        userData.addIntProperty("drinkPrice");
        userData.addIntProperty("shotPrice");
        userData.addStringProperty("gender");
        userData.addDateProperty("goalDate");
        userData.addDateProperty("goalBAC");
        userData.addDoubleProperty("height");
        userData.addDoubleProperty("weight");

        DaoGenerator dg = new DaoGenerator();
        dg.generateAll(schema, "./app/src/main/java");
    }
}