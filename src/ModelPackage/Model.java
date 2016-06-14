package ModelPackage;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by Y50 on 4/15/2016.
 */
public class Model {



    private Story story;

    public Model(){
        GameObjectsHolder gameObjectsHolder = new GameObjectsHolder();
        loadGame(gameObjectsHolder);
        story = new Story(gameObjectsHolder);



        for (Hero hero : story.getGameObjectsHolder().getPlayer().getHeroes()) {
            hero.init(story);
        }
    }


    public void loadGame(GameObjectsHolder gameObjectsHolder) {
        loadItems(gameObjectsHolder , "items.txt");
        loadSoldierTypes(gameObjectsHolder , "soldierTypes.txt");
        loadHeroes(gameObjectsHolder , "heroes.txt");
    }

    private ArrayList loadArrayListObject(String fileName, Type listType) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(fileName)));
            String json = bufferedReader.readLine();
            return new Gson().fromJson(json , listType);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void loadHeroes(GameObjectsHolder gameObjectsHolder, String fileName)
    {
        Type listType = new TypeToken<ArrayList<SoldierType>>() {}.getType();
        gameObjectsHolder.getPlayer().setHeroes(loadArrayListObject(fileName , listType));
    }

    private void loadSoldierTypes(GameObjectsHolder gameObjectsHolder , String fileName)
    {
        Type listType = new TypeToken<ArrayList<SoldierType>>() {}.getType();
        gameObjectsHolder.setSoldierTypes(loadArrayListObject(fileName , listType));
    }

    private void loadItems(GameObjectsHolder gameObjectsHolder, String fileName) {
        Type listType = new TypeToken<ArrayList<Item>>() {}.getType();
        gameObjectsHolder.setItems(loadArrayListObject(fileName , listType));
    }

    public static Object deepClone(Object object)
    {
        try{
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            return objectInputStream.readObject();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static void writeJsonEncodedToFile(String fileName , Object object)
    {
        Gson gson = new Gson();
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            byte[] shit = gson.toJson(object).getBytes();
            fileOutputStream.write(shit);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Story getStory() {
        return story;
    }

}



//    public static ArrayList<Ability> loadAbility()
//    {
//        Ability ability = null;
//        BufferedReader br;
//        ArrayList<Ability> abilities = new ArrayList<>();
//        Map mapDataAbility = new HashMap<String,ArrayList<String>>();
//        try
//        {
//            String line;
//            br = new BufferedReader(new FileReader("ability.txt"));
//            try
//            {
//                while((line = br.readLine()) != null)
//                {
//                    if(!line.equals("****"))
//                    {
//                        ArrayList<String> value = new ArrayList<>();
//                        StringTokenizer tokenizer = new StringTokenizer(line , ":");
//                        String input = tokenizer.nextToken();
//                        while(tokenizer.hasMoreElements())
//                        {
//                            value.add(tokenizer.nextToken());
//                        }
//                        mapDataAbility.put(input,value);
//                    }
//                    else
//                    {
//                        ArrayList<String> temp = new ArrayList<>();
//                        temp = (ArrayList<String>)mapDataAbility.get("isCastable");
//                        if(temp.get(0).equals("true"))
//                        {
////                            ability = new CastableAbility();
//                            ability.setCastable(true);
//                        }
//                        else{
////                            ability = new Ability();
//                            ability.setCastable(false);
//                        }
//                        temp = (ArrayList<String>)mapDataAbility.get("abilityName");
//                        ability.setName(temp.get(0));
//                        temp=(ArrayList<String>)mapDataAbility.get("price");
//                        for (String s : temp) {
//                            Price price = new Price(Integer.parseInt(s));
//                            ability.setAcquirePrices(price);
//                        }
//                        temp = (ArrayList<String>)mapDataAbility.get("abilityInfo");
//                        ability.setAbilityInfo(temp.get(0));
//
//                        temp = (ArrayList<String>)mapDataAbility.get("prerequisiteAbility");
//                        for (int i = 0; i <temp.size() ; i+=2) {
//                            PrerequisiteAbility prerequisiteAbility = new PrerequisiteAbility(temp.get(i),Integer.parseInt(temp.get(i+1)));
//                            ability.setPrerequisiteAbilities(prerequisiteAbility);
//                        }
//                        if(ability.isCastable())
//                        {
//                            temp = (ArrayList<String>) mapDataAbility.get("castPrice");
//                            for (int i = 0; i < temp.size(); i += 5) {
//                                Price price = new Price(Integer.parseInt(temp.get(i)), Integer.parseInt(temp.get(i + 1)), Integer.parseInt(temp.get(i + 2)), Integer.parseInt(temp.get(i + 3)), Integer.parseInt(temp.get(i + 4)));
//                                ability.addToCastPrices(price);
//                            }
//                        }
//                        else
//                        {
//                            temp = (ArrayList<String>) mapDataAbility.get("buffs");
//                            for (int i = 0; i < temp.size(); i+= 8) {
//                                Buff buff = new Buff(temp.get(i),Integer.parseInt(temp.get(i+1)),Integer.parseInt(temp.get(i+2)),Integer.parseInt(temp.get(i+3)),Integer.parseInt(temp.get(i+4)),Integer.parseInt(temp.get(i+5)),Integer.parseInt(temp.get(i+6)),Integer.parseInt(temp.get(i+7)));
//                                ability.addToBuffs(buff);
//                            }
//                        }
//                        abilities.add(ability);
//                        mapDataAbility.clear();
//
//
//                    }
//                }
//                br.close();
//            }
//            catch (IOException e)
//            {
//                e.printStackTrace();
//            }
//        }
//        catch (FileNotFoundException e)
//        {
//            e.printStackTrace();
//        }
//        return abilities;
//    }
//    public ArrayList<Hero> loadHero()
//    {
//        Hero hero = new Hero(new SoldierType() , story);
//        ArrayList<Hero> heroes = new ArrayList<>();
//        BufferedReader br;
//        Map<String,String> mapingData = new HashMap<>();
//        try{
//            br = new BufferedReader(new FileReader("heroes.txt"));
//            String line;
//            try {
//                while ((line = br.readLine()) != null)
//                {
//                    if (!line.equals("****"))
//                    {
//                        StringTokenizer temp = new StringTokenizer(line, ":");
//                        String key = temp.nextToken();
//                        String value = temp.nextToken();
//                        mapingData.put(key, value);
//                    }
//                    else
//                    {
//                        hero.setName(mapingData.get("heroName"));
//                        hero.setEnergyPoints(Integer.parseInt( mapingData.get("energy points")));
////                        hero.getType().setMaximumHealth(Integer.parseInt(mapingData.get("maximum health")));
////                        hero.getType().setHealthRefillRatePercentage(Integer.parseInt(mapingData.get("health refill rate")));
////                        hero.getType().setMaximumMagic(Integer.parseInt(mapingData.get("maximum magic")));
////                        hero.getType().setMagicRefillRatePercentage(Integer.parseInt(mapingData.get("magic refill rate")));
////                        hero.getType().setAttackPower(Integer.parseInt(mapingData.get("attack power")));
////                        hero.getType().setInventorySize(Integer.parseInt(mapingData.get("inventory size")));
//                        heroes.add(hero);
//                        mapingData.clear();
//                    }
//                }
//                br.close();
//            }
//            catch(IOException e)
//            {
//                e.printStackTrace();
//            }
//        }
//        catch (FileNotFoundException e)
//        {
//            e.printStackTrace();
//        }
//        return heroes;
//    }