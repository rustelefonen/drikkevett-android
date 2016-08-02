package rustelefonen.no.drikkevett_android.information;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import rustelefonen.no.drikkevett_android.db.Information;
import rustelefonen.no.drikkevett_android.db.InformationCategory;
import rustelefonen.no.drikkevett_android.db.InformationCategoryDao;
import rustelefonen.no.drikkevett_android.db.InformationDao;
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;

/**
 * Created by simenfonnes on 19.07.2016.
 */

public class DBSeeder {

    private static byte[] getImage(String strName, Context context) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = getBitmapFromAsset(strName, context);
        if (bitmap == null) return new byte[]{};
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    private static Bitmap getBitmapFromAsset(String strName, Context context) {
        AssetManager assetManager = context.getAssets();
        InputStream istr = null;
        try {
            istr = assetManager.open(strName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(istr);
    }

    public static void seed(Context context) {
        SuperDao superDao = new SuperDao(context);
        InformationCategoryDao informationCategoryDao = superDao.getInformationCategoryDao();
        InformationDao informationDao = superDao.getInformationDao();


        InformationCategory ic1 = new InformationCategory();
        ic1.setName("ALKOHOL");
        ic1.setOrderNumber(0);
        ic1.setVersionNumber(0);
        ic1.setImage(getImage("c1.jpg", context));
        informationCategoryDao.insert(ic1);

        Information i1c1 = new Information();
        i1c1.setName("Hvorfor bruker vi alkohol?");
        i1c1.setContent("Vanligvis brukes alkohol for å oppnå glede, avspenning og lettere sosial omgang. Noen bruker også alkohol for å få sove, dempe angst, depresjon og smerte, fylle et tomrom i livet, eller dempe abstinenser. Denne form for bruk vil virke mot sin hensikt i det lange løp fordi alkohol gir dårlig søvnkvalitet, og kan påvirke psykisk helse negativt. I Norge har toleranse for atferd som følger med ”fylla” bakgrunn helt fra vikingtiden. Å bli ruset later fortsatt til å være den viktigste egenskapen ved alkohol- bruken i Norge.");
        i1c1.setImage(getImage("i1c1.jpg", context));
        i1c1.setCategoryId(1);
        informationDao.insert(i1c1);

        Information i2c1 = new Information();
        i2c1.setName("Fakta om alkoholbruk");
        i2c1.setContent(
                "• Alkoholomsetningen i Norge økte med 40 % fra 1995 til 2009.\n" +
                "• Det registrerte forbruket i 2012 var anslått til 6,21 liter per voksen innbygger – da teller man ikke med smuglervarer og konsum utenfor Norge. Det uregistrerte alkoholkonsumet står for ca. 25–30 % av totalforbruket.\n" +
                "• Økningen skyldes i hovedsak en kraftig vekst i omsetningen av vin.\n" +
                "• Menn drikker i gjennomsnitt oftere og mer enn kvinner, med unntak av vin.\n" +
                "• Andelen som drikker alkohol flere ganger i måneden (2 ganger eller mer) har økt i alle aldersgrupper, og økningen har vært særlig sterk blant de over 50 år.\n" +
                "• Til tross for en reduksjon i konsumet blant ungdom de siste årene, er konsumet og beruselses-drikkingen fortsatt betydelig.\n" +
                "• Unge jenter drikker alkohol og blir beruset like ofte, eller mer, enn gutter.");
        i2c1.setCategoryId(1);
        i2c1.setImage(getImage("i2c1.jpg", context));
        informationDao.insert(i2c1);

        Information i3c1 = new Information();
        i3c1.setName("Hvordan virker alkohol på kroppen");
        i3c1.setContent(
                "• Alkohol tas opp gjennom slimhinnene i tynntarmen og magesekken og går over i blodet.\n" +
                "• Mengden alkohol i blodet blir målt i promille, og en promille betyr ett gram ren alkohol pr liter blod.\n" +
                "• Konsentrasjonen av alkohol i blodet (promille) øker raskt, spesielt hvis du ikke har spist på en stund og magesekken er tom. Vanligvis tar det bare noen minutter etter inntak før du kan måle alkohol i blodet.\n" +
                "• Dersom du drikker på tom mage, kan maksimal alkoholkonsentrasjon i blodet (promille) oppnås allerede etter 15-30 min, og en stor alkoholmengde kan være nærmest fullstendig tatt opp i blodet på mindre enn 1 time.\n" +
                "• Ved inntak av kullsyreholdige drikker som champagne, rusbrus og drinker med kullsyreholdig mineralvann tas alkoholen ekstra raskt opp i blodet.\n" +
                "• Drikkes alkohol på full mage eller i forbindelse med et måltid, vil det tas langsommere\n" +
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
        i3c1.setImage(getImage("i3c1.jpg", context));
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
        i4c1.setImage(getImage("i4c1.jpg", context));
        i4c1.setCategoryId(1);
        informationDao.insert(i4c1);

        Information i5c1 = new Information();
        i5c1.setName("Promille");
        i5c1.setContent("Allerede ved 0,5 promille føler du deg vanligvis lett påvirket.\n" +
                "\n" +
                "\uF0B7 For mange personer vil en ”vanlig festpromille” gjerne være i området opp mot 1\n" +
                "\n" +
                "promille.\n" +
                "\n" +
                "\uF0B7 Grensen for åpenbart påvirket ligger trolig et sted rundt 1 promille.\n" +
                "\n" +
                "\uF0B7 I området 1-2 promille blir de uønskede virkningene gradvis mer uttalte, og du kan bli\n" +
                "\n" +
                "ustø, trøtt, kvalm og sløv\n" +
                "\n" +
                "\uF0B7 De fleste får problemer med hukommelsen (blackout) ved promille på 1.5 og over.\n" +
                "\n" +
                "\uF0B7 Hukommelsesproblemene øker med stigende promille.\n" +
                "\n" +
                "\uF0B7 Du blir ofte kvalm og kaster opp. Mange får også hodepine.\n" +
                "\n" +
                "\uF0B7 Ved svært høy promille kan du bli bevisstløs.\n" +
                "\n" +
                "\uF0B7 Promille på rundt 3 kan være dødelig.\n" +
                "\n" +
                "\uF0B7 Risikoen for død øker hvis du også har brukt medikamenter som har sløvende eller\n" +
                "\n" +
                "dempende virkning.");
        i5c1.setImage(getImage("i5c1.jpg", context));
        i5c1.setCategoryId(1);
        informationDao.insert(i5c1);

        Information i6c1 = new Information();
        i6c1.setName("Toleranseutvikling");
        i6c1.setContent("Hvor lenge rusen varer og om du er vant til å drikke vil påvirke reaksjonene.\n" +
                "\n" +
                "\uF0B7 Er du vant til å drikke til et gitt nivå, kan du utvikle en toleranse, det vil si at\n" +
                "\n" +
                "virkningene blir mindre når denne drikkingen har vært gjentatt svært mange ganger\n" +
                "\n" +
                "med ikke alt for store mellomrom.\n" +
                "\n" +
                "\uF0B7 Også under hver enkelt drikkeepisode vil det utvikle seg toleranse hvis rusen varer\n" +
                "\n" +
                "lengre enn 3-5 timer, slik at du føler seg mindre påvirket selv om promillen er like\n" +
                "\n" +
                "høy.\n" +
                "\n" +
                "\uF0B7 Gjentatte alkoholinntak setter i gang forandringer i nervecellene, som gjør at de\n" +
                "\n" +
                "tilpasser seg alkoholvirkningen. Dette vil gi seg til kjenne ved at den som drikker ofte,\n" +
                "\n" +
                "«tåler mer» alkohol enn tidligere.\n" +
                "\n" +
                "\uF0B7 Slik utvikling av toleranse vil også være forbundet med forekomsten av forskjellige\n" +
                "\n" +
                "abstinensplager når alkoholtilførselen slutter og alkoholkonsentrasjonen i blodet går\n" +
                "\n" +
                "ned mot null.\n" +
                "\n" +
                "\uF0B7 Abstinenssymptomene er ofte de motsatte av det du opplever under\n" +
                "\n" +
                "alkoholpåvirkning:\n" +
                "\n" +
                "Senket stemningsleie, uglede, uro, svetting, søvnforstyrrelser. Kraftig alkoholabstinens\n" +
                "\n" +
                "kan være livsfarlig.");
        i6c1.setCategoryId(1);
        i6c1.setImage(getImage("i6c1.jpg", context));
        informationDao.insert(i6c1);

        InformationCategory ic2 = new InformationCategory();
        ic2.setName("Kropp og psyke");
        ic2.setImage(getImage("c2.jpg", context));
        ic2.setOrderNumber(1);
        ic2.setVersionNumber(0);
        informationCategoryDao.insert(ic2);

        Information i1c2 = new Information();
        i1c2.setName("Lykkepromille");
        i1c2.setContent("Det er ikke uvanlig å drikke litt i sosiale sammenhenger, for å slappe mer av og for hyggens\n" +
                "\n" +
                "skyld; det er ikke uten grunn at alkohol kalles et «sosialt lim». Alkohol er vanlig som sosialt\n" +
                "\n" +
                "lim. «Lykkepromillen» ligger på omkring 0.6 – 0.8. Men når promillen overstiger et visst\n" +
                "\n" +
                "nivå (ca. 0,5) vil tanker og følelser endres og forsterkes, og sosiale ferdigheter avta. Du\n" +
                "\n" +
                "risikerer å miste flere hemninger enn du i ettertid vil være komfortabel med.");
        i1c2.setImage(getImage("i1c2.jpg", context));
        i1c2.setCategoryId(2);
        informationDao.insert(i1c2);

        Information i2c2 = new Information();
        i2c2.setName("Angst og depresjoner");
        i2c2.setContent("Det er ikke uvanlig å drikke litt alkohol for å lette litt på vanskelige følelser som stress eller\n" +
                "\n" +
                "angst. Om du har angst eller andre psykiske plager, bør du tenke over om du alltid skal bruke\n" +
                "\n" +
                "alkohol for å lette på disse plagene. Det kan i lengden gjøre at du må ha alkohol for å føle deg\n" +
                "\n" +
                "bra og for å fungere sosialt. Om du har selvmordstanker i perioder, bør du også være forsiktig\n" +
                "\n" +
                "med alkohol. Det er større sjanse for at du selvskader eller forsøker å ta ditt liv i fylla. Faktisk\n" +
                "\n" +
                "er det slik at hvert fjerde tilfelle av selvskading blant norsk ungdom er gjort i alkoholrus.");
        i2c2.setImage(getImage("i2c2.jpg", context));
        i2c2.setCategoryId(2);
        informationDao.insert(i2c2);

        Information i3c2 = new Information();
        i3c2.setName("Reptilhjernen");
        i3c2.setContent("Hjernens evne til innlæring og tenkning reduseres ved promille fra 0.4 og oppover. Skal du\n" +
                "\n" +
                "lære noe eller ønsker å prestere maksimalt i forhold til dine egne kognitive evner, bør du\n" +
                "\n" +
                "derfor holde deg under denne grensen. Hjernens evne til innlæring er også redusert dagen etter\n" +
                "\n" +
                "en fyllekule.\n" +
                "\n" +
                "Når du er alkoholberuset, svekker du hjernens evne til å ta gode beslutninger. Dette kan gjøre\n" +
                "\n" +
                "at du blir mindre kritisk til hva du sier og gjør – det går mer på refleks, slik som hos reptiler.\n" +
                "\n" +
                "Dette kan føre til lite gjennomtenkte replikker og handlinger. Det kan også føre til at din evne\n" +
                "\n" +
                "til å tolke omgivelsene/forskjellige situasjoner blir dårlige slik at misforståelser lettere\n" +
                "\n" +
                "oppstår. Derfor er det ikke uvanlig at folk oftere er involvert i tilfeldig sex, krangling,\n" +
                "\n" +
                "voldshandlinger eller ulykker når de er fulle. Å bli aggressiv eller i overkant dramatisk, er\n" +
                "\n" +
                "heller ikke uvanlig.");
        i3c2.setImage(getImage("i3c2.jpg", context));
        i3c2.setCategoryId(2);
        informationDao.insert(i3c2);

        Information i4c2 = new Information();
        i4c2.setName("Overvekt og Ernæring");
        i4c2.setContent("Alkohol inneholder mye energi (kalorier) men få viktige næringsstoffer. Når du drikker\n" +
                "\n" +
                "alkohol bruker kroppen all tilgjengelig energi på å bryte ned alkoholen. Dette innebærer at\n" +
                "\n" +
                "fettforbrenning reduseres. Alkohol kan også øke kortisolnivået i kroppen, noe som kan\n" +
                "\n" +
                "redusere nedbrytning av fett i fettcellene. I tillegg er det lett å bli fristet til å spise usunn og fet\n" +
                "\n" +
                "mat når du drikker og dagen etter. Er du ofte på fylla over tid, øker risikoen for overvekt og\n" +
                "\n" +
                "ernæringsmangler.");
        i4c2.setImage(getImage("i4c2.jpg", context));
        i4c2.setCategoryId(2);
        informationDao.insert(i4c2);

        Information i5c2 = new Information();
        i5c2.setName("Huden din");
        i5c2.setContent("Alkohol er dehydrerende, noe som også påvirker kroppens største organ - huden din. Hvis du\n" +
                "\n" +
                "drikker mye og ofte kan det gå på bekostning av hudens evne til å ta til seg viktige vitaminer\n" +
                "\n" +
                "og næringsstoffer. For utseendet ditt vil det bety større risiko for tørr hud, rødming og\n" +
                "\n" +
                "misfarging av huden. Pløsete hud kan også forårsakes av alkohol, både i ansiktet og på\n" +
                "\n" +
                "magen. Dessuten inneholder alkohol mye kalorier og karbohydrater, som kan føre til uren\n" +
                "\n" +
                "hud.");
        i5c2.setImage(getImage("i5c2.jpg", context));
        i5c2.setCategoryId(2);
        informationDao.insert(i5c2);

        Information i6c2 = new Information();
        i6c2.setName("sex");
        i6c2.setContent("Alkohol frigjør dopamin og serotonin i hjernen, og dette kan gjøre at du føler deg mindre\n" +
                "\n" +
                "nervøs og mister litt hemminger - også seksuelt. Serotonin øker også lykkefølelsen, som kan\n" +
                "\n" +
                "gjøre at du får mer lyst på sex. Dette kan utspille seg i at du opplever å tørre å prøve ut ting du\n" +
                "\n" +
                "ellers ikke ville gjort/hadde turt. Men du kan også bli mindre kritisk/likegyldig og ende opp\n" +
                "\n" +
                "med å ha sex uten egentlig å ville det, og uten å beskytte deg.\n" +
                "\n" +
                "Gutten kan få problemer med ereksjon og utløsning om han er for full. Jenter kan også få\n" +
                "\n" +
                "problemer med å få orgasme.\n" +
                "\n" +
                "- Tenk en gang ekstra på om dette er en person du vil ha sex med\n" +
                "\n" +
                "- Tenk over om dette er en person du er trygg på vil forholde seg til dine grenser\n" +
                "\n" +
                "- Tenk gjennom om du sårer noen ved å ha sex med denne personen\n" +
                "\n" +
                "- Bruk prevensjon som beskytter mot kjønnssykdommer\n" +
                "\n" +
                "- Blir du med en person hjem, vær trygg på denne personen, og fortell alltid en venn\n" +
                "\n" +
                "hvor du skal\n" +
                "\n" +
                "- ALDRI gjennomfør seksuelle handlinger på en person som er for full til å si ja eller\n" +
                "\n" +
                "nei. Det er voldtekt");
        i6c2.setImage(getImage("i6c2.jpg", context));
        i6c2.setCategoryId(2);
        informationDao.insert(i6c2);

        InformationCategory ic3 = new InformationCategory();
        ic3.setName("Trening");
        ic3.setImage(getImage("c3.jpg", context));
        ic3.setOrderNumber(2);
        ic3.setVersionNumber(0);
        informationCategoryDao.insert(ic3);

        Information i1c3 = new Information();
        i1c3.setName("Dårligere treningseffekt");
        i1c3.setContent("Alkohol kan redusere den positive effekten av trening. Alkohol kan virke hemmende på\n" +
                "\n" +
                "treningsprestasjoner, særlig påvirker det restitusjon på en negativ måte. Fordi både alkohol og\n" +
                "\n" +
                "trening har vanndrivende effekt øker risikoen for dehydrering. Dette gjelder særlig dersom du\n" +
                "\n" +
                "drikker alkohol etter trening. Dehydrering kan redusere blodgjennomstrømning til musklene,\n" +
                "\n" +
                "noe som er uheldig i restitusjonsfasen. Belastningen under trening bryter muskulaturen ned,\n" +
                "\n" +
                "og i hvileperioden etter trening forbereder musklene seg til å tåle liknende påkjenninger\n" +
                "\n" +
                "senere («restitusjon»). Resultatet er at du blir litt sterkere hver gang du trener. Når du drikker\n" +
                "\n" +
                "alkohol kan dette hemme nydannelse av glykogen og glykosefrigjøring fra leveren, en prosess\n" +
                "\n" +
                "som er viktig for å oppnå optimal restitusjon. Hvis du starter med ny trening uten god\n" +
                "\n" +
                "restitusjon, vil ikke muskulaturen ha klart å gjenoppbygge seg i mellomtiden, og resultatet kan\n" +
                "\n" +
                "bli at du bryter ned en allerede nedbrutt muskulatur. Alkohol påvirker også hjerte og blodkar\n" +
                "\n" +
                "slik at den aerobe kapasiteten blir redusert. Alkohol kan derfor gi dårligere treningseffekt\n" +
                "\n" +
                "både på kort og lang sikt.");
        i1c3.setImage(getImage("i1c3.jpg", context));
        i1c3.setCategoryId(3);
        informationDao.insert(i1c3);

        Information i2c3 = new Information();
        i2c3.setName("Redusert prestasjonsevne");
        i2c3.setContent("Drikker du alkohol etter trening, tar det lengre tid å erstatte væsketapet. Hvis kroppen ikke\n" +
                "\n" +
                "klarer å gjenopprette væskebalanse før du trener på nytt, vil det øke risikoen for dehydrering.\n" +
                "\n" +
                "Væsketap gir redusert prestasjonsevne blant annet fordi blodgjennomstrømningen til\n" +
                "\n" +
                "musklene reduseres og kroppstemperaturen øker.");
        i2c3.setImage(getImage("i2c3.jpg", context));
        i2c3.setCategoryId(3);
        informationDao.insert(i2c3);

        Information i3c3 = new Information();
        i3c3.setName("Påvirkning av hormoner");
        i3c3.setContent("Hvis du drikker relativt mye og jevnlig kan produksjonen av mannlig kjønnshormon,\n" +
                "\n" +
                "testosteron, synke. Samtidig kan konsentrasjonen av stresshormonet kortisol øke. Denne\n" +
                "\n" +
                "kombinasjonen kan over tid medføre nedbrytning av muskelmasse og/eller forhindre at\n" +
                "\n" +
                "muskelmasse utvikles. Andre stresshormoner kan også øke; symptomer på dette kan være\n" +
                "\n" +
                "uregelmessig hjerterytme og søvnproblemer.");
        i3c3.setImage(getImage("i3c3.jpg", context));
        i3c3.setCategoryId(3);
        informationDao.insert(i3c3);

        InformationCategory ic4 = new InformationCategory();
        ic4.setName("Tips");
        ic4.setOrderNumber(3);
        ic4.setVersionNumber(0);
        ic4.setImage(getImage("c4.jpg", context));
        informationCategoryDao.insert(ic4);

        Information i1c4 = new Information();
        i1c4.setName("Dagen derpå");
        i1c4.setContent("Hodepine, kvalme, mageforstyrrelser og uro (fylleangst), er vanlige symptomer på bakrus.\n" +
                "\n" +
                "Bakrus eller fyllesyke kan forklares som en slags abstinensstilstand der hjernen sliter med å\n" +
                "\n" +
                "venne seg til at alkoholen blir borte. Dehydrering, søvnmangel, lite matinntak, og allergi mot\n" +
                "\n" +
                "noen av tilsetningsstoffene i alkoholen, har skylden for hvordan formen din er dagen derpå. Er\n" +
                "\n" +
                "skaden allerede skjedd, er det dessverre ingen vidunderkurer som kan kurere.\n" +
                "\n" +
                "\uF0B7 Det er en utbredt misforståelse/myte at kraftig mosjon, badstuopphold, kaffe,\n" +
                "\n" +
                "legemidler eller kjemiske vidunderkurer kan øke alkoholforbrenningen.\n" +
                "\n" +
                "\uF0B7 Store inntak av fruktsukker (fruktose) kan derimot øke alkoholforbrenningen, men da\n" +
                "\n" +
                "skjer det samtidig også biokjemiske endringer i leveren som kan være uheldige og gi\n" +
                "\n" +
                "akutte plager.\n" +
                "\n" +
                "\uF0B7 Det beste du kan gjøre når du er fyllesyk er å la kroppen få hvile, samt sørge for å få i\n" +
                "\n" +
                "deg nok væske (alkoholfri).\n" +
                "\n" +
                "\uF0B7 Noen synes det hjelper å spise. Er du veldig kvalm kan tørre kjeks være lettere å få i\n" +
                "\n" +
                "seg.\n" +
                "\n" +
                "\uF0B7 Noen kan oppleve å få til dels sterke «fyllenerver». Det finnes ingen mirakelkur mot\n" +
                "\n" +
                "dette, men det kan hjelpe med avspenningsøvelser som meditasjon og lett yoga (hvis\n" +
                "\n" +
                "du orker). Poenget er å klare å fokusere på pust/mantra slik at kroppen blir mindre\n" +
                "\n" +
                "anspent!\n" +
                "\n" +
                "\uF0B7 Vær snill med deg selv. Forsøk å unngå de store selvransakelsene akkurat denne\n" +
                "\n" +
                "dagen, og fokuser heller på hva du kan gjøre for å unngå å bli SÅ full neste gang.");
        i1c4.setImage(getImage("i1c4.jpg", context));
        i1c4.setCategoryId(4);
        informationDao.insert(i1c4);

        Information i2c4 = new Information();
        i2c4.setName("Ikke sov på stigende rus");
        i2c4.setContent("Det anbefales å slutte å drikke i god tid før du skal legge deg for å lindre symptomer på\n" +
                "\n" +
                "bakrus dagen derpå. Da er sjansene for at du får en roligere søvn større. Det kan dessuten\n" +
                "\n" +
                "være farlig å legge seg på stigende rus, om promillen er høy.");
        i2c4.setImage(getImage("i2c4.jpg", context));
        i2c4.setCategoryId(4);
        informationDao.insert(i2c4);

        Information i3c4 = new Information();
        i3c4.setName("Drikkevettsreglene");
        i3c4.setContent("Av og til er det fort gjort å gå på en smell og bli fullere enn du hadde tenkt. For noen skjer\n" +
                "\n" +
                "dette oftere enn for andre. Og av og til kan det være slik i perioder. Om du liker å ta deg en\n" +
                "\n" +
                "fest, men også å ha kontroll på promillen og påfølgende handlinger, finnes det enkle grep du\n" +
                "\n" +
                "kan gjøre for å unngå for høy promille.\n" +
                "\n" +
                "Det finnes ingen trylleformularer for å begrense drikkingen, og den eneste sikre metoden er å\n" +
                "\n" +
                "drikke mindre. Men her kommer noen drikkevettsregler som kan hjelpe deg til å ha mer\n" +
                "\n" +
                "kontroll på promillen:\n" +
                "\n" +
                "1. Planlegg før du skal ut og drikke. Tenk gjennom hvor mye du vil drikke og hva du vil\n" +
                "\n" +
                "drikke, og forsøk å holde deg til det. Appen du har lastet ned kan hjelpe deg med dette.\n" +
                "\n" +
                "2. Begrens inntak av brennevin hvis du drikker det. Dropp shotting.\n" +
                "\n" +
                "3. Spis godt før du drikker og ikke drikk på tom mave. Drikker du på tom mave økes\n" +
                "\n" +
                "risikoen for at du skal miste kontrollen.\n" +
                "\n" +
                "4. Ta kontroll over promillen. Drikke et glass vann, juice, brus eller annen alkoholfri\n" +
                "\n" +
                "drikke mellom hver enhet alkohol du drikker.\n" +
                "\n" +
                "5. Bruk erfaringen din. Vi er alle forskjellige og tåler ulike mengder alkohol.\n" +
                "\n" +
                "6. Husk at kvinner vanligvis tåler mindre alkohol enn menn.\n" +
                "\n" +
                "7. Ikke gå alene, allier deg på forhånd med en venn. Kjenner du at du blir så beruset at du\n" +
                "\n" +
                "vil ha problemer med å ta vare på det selv er det greit å vite at noen holder et øye med\n" +
                "\n" +
                "deg, evt. følger deg trygt hjem.\n" +
                "\n" +
                "8. Det er ingen skam å tåle minst. Ikke la deg bli revet med i konkurransen om å tåle\n" +
                "\n" +
                "mest. Drikkekonkurranser kan få fatale utfall.\n" +
                "\n" +
                "9. Vis respekt for rus og rusens virkning. Vold, aggresjon, hemningsløs oppførsel og\n" +
                "\n" +
                "ulykker skjer ofte i beruset tilstand.\n" +
                "\n" +
                "10. Vær rustet til å tåle alkohol. Er du syk, stresset, sover dårlig, eller bruker medisiner e.l.\n" +
                "\n" +
                "tåler du mindre alkohol, enn når du er frisk og uthvilt.\n" +
                "\n" +
                "11. Lytt til erfarne folk. Har du spørsmål om rus kontak" +
                "t RUStelefonen på tlf 08588, chat\n" +
                "\n" +
                "med oss eller send oss spørsmål.\n" +
                "\n" +
                "12. Sett deg et langsiktig mål. Får du ikke til første gang, forsøk igjen.");
        i3c4.setCategoryId(4);
        i3c4.setImage(getImage("i3c4.jpg", context));
        informationDao.insert(i3c4);

        superDao.close();

    }
}
