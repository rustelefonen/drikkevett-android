package rustelefonen.no.drikkevett_android.information;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import rustelefonen.no.drikkevett_android.R;
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
        ic1.setName("ALKOHOL");
        informationCategoryDao.insert(ic1);

        Information i1c1 = new Information();
        i1c1.setName("Hvorfor bruker vi alkohol?");
        i1c1.setContent("Vanligvis brukes alkohol for å oppnå glede, avspenning og lettere sosial omgang. Noen bruker også alkohol for å få sove, dempe angst, depresjon og smerte, fylle et tomrom i livet, eller dempe abstinenser. Denne form for bruk vil virke mot sin hensikt i det lange løp fordi alkohol gir dårlig søvnkvalitet, og kan påvirke psykisk helse negativt. I Norge har toleranse for atferd som følger med ”fylla” bakgrunn helt fra vikingtiden. Å bli ruset later fortsatt til å være den viktigste egenskapen ved alkohol- bruken i Norge.");
        i1c1.setCategoryId(1);
        informationDao.insert(i1c1);

        Information i2c1 = new Information();
        i2c1.setName("Fakta om alkoholbruk");
        i2c1.setContent("Alkoholomsetningen i Norge økte med 40 % fra 1995 til 2009.\n" +
                "\n" +
                "\uF0B7 Det registrerte forbruket i 2012 var anslått til 6,21 liter per voksen innbygger – da\n" +
                "\n" +
                "teller man ikke med smuglervarer og konsum utenfor Norge. Det uregistrerte\n" +
                "\n" +
                "alkoholkonsumet står for ca. 25–30 % av totalforbruket.\n" +
                "\n" +
                "\uF0B7 Økningen skyldes i hovedsak en kraftig vekst i omsetningen av vin.\n" +
                "\n" +
                "\uF0B7 Menn drikker i gjennomsnitt oftere og mer enn kvinner, med unntak av vin.\n" +
                "\n" +
                "\uF0B7 Andelen som drikker alkohol flere ganger i måneden (2 ganger eller mer) har økt i alle\n" +
                "\n" +
                "aldersgrupper, og økningen har vært særlig sterk blant de over 50 år.\n" +
                "\n" +
                "\uF0B7 Til tross for en reduksjon i konsumet blant ungdom de siste årene, er konsumet og\n" +
                "\n" +
                "beruselses-drikkingen fortsatt betydelig.\n" +
                "\n" +
                "\uF0B7 Unge jenter drikker alkohol og blir beruset like ofte, eller mer, enn gutter.");
        i2c1.setCategoryId(1);
        informationDao.insert(i2c1);

        Information i3c1 = new Information();
        i3c1.setName("Hvordan virker alkohol på kroppen");
        i3c1.setContent("\uF0B7 Alkohol tas opp gjennom slimhinnene i tynntarmen og magesekken og går over i\n" +
                "\n" +
                "blodet.\n" +
                "\n" +
                "\uF0B7 Mengden alkohol i blodet blir målt i promille, og en promille betyr ett gram ren\n" +
                "\n" +
                "alkohol pr liter blod.\n" +
                "\n" +
                "\uF0B7 Konsentrasjonen av alkohol i blodet (promille) øker raskt, spesielt hvis du ikke har\n" +
                "\n" +
                "spist på en stund og magesekken er tom. Vanligvis tar det bare noen minutter etter\n" +
                "\n" +
                "inntak før du kan måle alkohol i blodet.\n" +
                "\n" +
                "\uF0B7 Dersom du drikker på tom mage, kan maksimal alkoholkonsentrasjon i blodet\n" +
                "\n" +
                "(promille) oppnås allerede etter 15-30 min, og en stor alkoholmengde kan være\n" +
                "\n" +
                "nærmest fullstendig tatt opp i blodet på mindre enn 1 time.\n" +
                "\n" +
                "\uF0B7 Ved inntak av kullsyreholdige drikker som champagne, rusbrus og drinker med\n" +
                "\n" +
                "kullsyreholdig mineralvann tas alkoholen ekstra raskt opp i blodet.\n" +
                "\n" +
                "\uF0B7 Drikkes alkohol på full mage eller i forbindelse med et måltid, vil det tas langsommere\n" +
                "\n" +
                "(opptil 3 timer) opp i blodet.\n" +
                "\n" +
                "\uF0B7 Den første tiden etter et alkoholinntak vil alkoholkonsentrasjonen i blodet være\n" +
                "\n" +
                "stigende.\n" +
                "\n" +
                "\uF0B7 Når all alkoholen er sugd opp og noenlunde jevnt fordelt i kroppens celler, oppnås det\n" +
                "\n" +
                "høyeste punktet for alkoholkonsentrasjonen i blodet (promille) etter alkoholinntaket.\n" +
                "\n" +
                "Etter dette vil alkoholkonsentrasjonen i blodet synke.\n" +
                "\n" +
                "\uF0B7 95 % av alkoholen som kommer over i kroppen brytes ned i leveren. Ca. 2 % skilles ut\n" +
                "\n" +
                "i urinen, ca. 2 % via utåndingsluft og ca. 1 % via svette.\n" +
                "\n" +
                "\uF0B7 Alkoholforbrenningen foregår med konstant hastighet, slik at alkoholpromillen i\n" +
                "\n" +
                "blodet synker med cirka 0,15 promille per time.\n" +
                "\n" +
                "\uF0B7 Hos personer med et jevnlig, hyppig og stort alkoholforbruk kan forbrenningen gå\n" +
                "\n" +
                "raskere, opp mot 0,30 promille per time, og i sjeldne tilfeller enda raskere.");
        i3c1.setCategoryId(1);
        informationDao.insert(i3c1);

        Information i4c1 = new Information();
        i4c1.setName("Blackout");
        i4c1.setContent("Du kan få blackout om du drikker mye alkohol, fra promille fra 1.4 og oppover. Det er større\n" +
                "\n" +
                "risiko for blackout om du drikker fort, som ved for eksempel shotting. Det som skjer er at\n" +
                "\n" +
                "overføringen fra korttidsminnet/arbeidsminnet til langtidsminnet svekkes, slik at du ikke\n" +
                "\n" +
                "husker noe fra hendelsen senere, men der og da er du klar over hva som skjer. Hvor mye tid\n" +
                "\n" +
                "som «er borte» fra hukommelsen kan variere fra gang til gang, men det er ikke uvanlig at det\n" +
                "\n" +
                "kan dreie seg om timer. Blackout kan være farlig, både fordi høy promille er farlig, men også\n" +
                "\n" +
                "fordi du blir sårbar for ulykker, voldshandlinger og andre farlige situasjoner. Det er dessuten\n" +
                "\n" +
                "ganske frustrerende og ofte litt flaut og ikke huske hva du har gjort og sagt.");
        i4c1.setCategoryId(1);

        Information i5c1 = new Information();



    }
}
