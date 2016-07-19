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
        history.implementsSerializable();

        Property historyId = graphHistory.addLongProperty("historyId").notNull().getProperty();
        ToMany historyToGraphHistory = history.addToMany(graphHistory, historyId);
        historyToGraphHistory.setName("graphHistories");

        Entity userData = schema.addEntity("User");
        userData.addIdProperty();
        userData.addIntProperty("age");
        userData.addIntProperty("beerPrice");
        userData.addIntProperty("winePrice");
        userData.addIntProperty("drinkPrice");
        userData.addIntProperty("shotPrice");
        userData.addStringProperty("gender");
        userData.addDateProperty("goalDate");
        userData.addDoubleProperty("goalBAC");
        userData.addStringProperty("nickname");
        userData.addDoubleProperty("weight");
        userData.implementsSerializable();

        Entity planParty = schema.addEntity("PlanPartyElements");
        planParty.addIdProperty();
        planParty.addStringProperty("status");
        planParty.addIntProperty("plannedBeer");
        planParty.addIntProperty("plannedWine");
        planParty.addIntProperty("plannedDrink");
        planParty.addIntProperty("plannedShot");
        planParty.addDateProperty("firstUnitAddedDate");
        planParty.addDateProperty("startTimeStamp");
        planParty.addDateProperty("endTimeStamp");

        Entity informationCategory = schema.addEntity("InformationCategory");
        informationCategory.addIdProperty();
        informationCategory.addStringProperty("name");
        informationCategory.addByteArrayProperty("image");
        informationCategory.implementsSerializable();

        Entity information = schema.addEntity("Information");
        information.addIdProperty();
        information.addStringProperty("name");
        information.addStringProperty("content");
        information.addByteArrayProperty("image");
        information.implementsSerializable();

        Property informationCategoryId = information.addLongProperty("categoryId").notNull().getProperty();
        ToMany informationCategoryToInformation = informationCategory.addToMany(information, informationCategoryId);
        informationCategoryToInformation.setName("informationList");

        DaoGenerator dg = new DaoGenerator();
        dg.generateAll(schema, "./app/src/main/java");
    }
}