package rustelefonen.no.drikkevett_android.information;

import android.content.Context;

import rustelefonen.no.drikkevett_android.db.Information;
import rustelefonen.no.drikkevett_android.db.InformationCategory;
import rustelefonen.no.drikkevett_android.db.InformationCategoryDao;
import rustelefonen.no.drikkevett_android.db.InformationDao;
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;

/**
 * Created by simenfonnes on 19.07.2016.
 */

public class DBSeeder {

    public static void seed(Context context) {
        SuperDao superDao = new SuperDao(context);
        InformationCategoryDao informationCategoryDao = superDao.getInformationCategoryDao();
        InformationDao informationDao = superDao.getInformationDao();

        InformationCategory ic1 = new InformationCategory();
        ic1.setName("Hvordan påvirker alkohol treningen din?");

        informationCategoryDao.insert(ic1);

        Information i1c1 = new Information();
        i1c1.setName("Dårligere treningseffekt");
        i1c1.setContent("Alkohol kan virke negativt inn på treningsmålet ditt. Hvorfor? Belastningen under trening bryter muskulaturen ned, og hvileperioden etter treningen bruker musklene påå forberede seg på liknende påkjenninger senere («restitusjon»). Resultatet er at du blir litt sterkere hver gang du trener. Når du drikker alkohol kan dette hemme nydannelse av glykogen og glykosefrigjøring fra leveren, en prosess som er viktig for å oppnå optimal restitusjon. Hvis du starter med ny trening uten en god restitusjon, vil ikke muskulaturen ha klart å gjenoppbygge seg i mellomtiden. Alkohol kan derfor gi dårligere treningseffekt både på kort og lang sikt.");
        i1c1.setCategoryId(1);

        informationDao.insert(i1c1);

        Information i2c1 = new Information();
        i2c1.setName("Redusert prestasjonsevne");
        i2c1.setContent("Drikker du alkohol etter trening, tar det lengre tid å erstatte væsketapet. Hvis kroppen ikke klarer å gjenopprette væskebalanse før du trener på nytt, vil det øke risikoen for dehydrering. Væsketap gir redusert prestasjonsevne blant annet grunnet dårligere blodgjennomstrømning til musklene og økt kroppstemperatur.");
        i2c1.setCategoryId(1);

        informationDao.insert(i2c1);

        Information i3c1 = new Information();
        i3c1.setName("Påvirkning av hormoner");
        i3c1.setContent("Hvis du drikker relativt mye og jevnlig kan produksjonen av mannlig kjønnshormon, testosteron synke. Samtidig kan konsentrasjonen av stresshormonet kortisol øke. Denne kombinasjonen kan over tid medføre nedbrytning av muskelmasse og/eller forhindre at muskelmasse utvikles. Andre stresshormoner kan også øke; et symptom på dette er uregelmessig hjerterytme.");
        i3c1.setCategoryId(1);

        informationDao.insert(i3c1);


        superDao.close();
    }
}
