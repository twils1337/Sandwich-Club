package com.udacity.sandwichclub.utils;

import android.util.Log;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import com.udacity.sandwichclub.model.Sandwich;

public class JsonUtils {

    public static Sandwich parseSandwichJson(String json)
    {
        if(json.charAt(0) != '{' || json.charAt(json.length()-1) != '}')
        {
            return null;
        }
        else
        {
            StringTokenizer tokenizer = new StringTokenizer(json);
            Sandwich parsedSandwich = new Sandwich();
            String token = tokenizer.nextToken("{}");
            String value = "";
            Boolean getToken = true, ignoreCommas = false;
            String[] kvp;
            while (tokenizer.hasMoreElements())
            {
                if(!token.endsWith(":"))
                {
                    kvp = getJsonKVP(token);
                    token = kvp[0];
                    value = kvp[1];
                    switch (token)
                    {
                        case "mainName":
                            parsedSandwich.setMainName(value);
                            break;
                        case "alsoKnownAs":
                            List<String> aliases = parseJsonArray(value);
                            parsedSandwich.setAlsoKnownAs(aliases);
                            break;
                        case "placeOfOrigin":
                            parsedSandwich.setPlaceOfOrigin(value.equals("") ? "Unknown": value);
                            break;
                        case "description":
                            String fullDescription = getFullDescription(value, tokenizer);
                            parsedSandwich.setDescription(fullDescription);
                            getToken = false;
                            token = "image"+tokenizer.nextToken(",");
                            break;
                        case "image":
                            parsedSandwich.setImage(value);
                            ignoreCommas = true;
                            break;
                        case "ingredients":
                            List<String> ingredients = parseJsonArray(value);
                            break;
                        default:
                            Log.i("Info", "Attribute: "+token+" not used.");
                    }

                }
                if (getToken)
                {
                    String delims = "\\{\\}";
                    token = tokenizer.nextToken(ignoreCommas ? delims : delims+",");
                    if (ignoreCommas)
                    {
                        ignoreCommas = false;
                        token = token.substring(1);
                    }
                }
                else
                {
                  getToken = true;
                }

            }
            kvp = getJsonKVP(token);
            List<String> ingredients = parseJsonArray(kvp[1]);
            parsedSandwich.setIngredients(ingredients);
            return parsedSandwich;
        }
    }

    public static List<String> parseJsonArray(String jsonArray)
    {
        String arr = jsonArray.replaceAll("\\[","").replaceAll("\\]","");
        if (arr.length() == 0){
            return Collections.emptyList();
        }
        else
        {
            return Arrays.asList(arr.split(","));
        }
    }

    public static String getFullDescription(String partialDescription, StringTokenizer tokenizer)
    {
        StringBuilder sb = new StringBuilder(partialDescription);
        String token = tokenizer.nextToken(":");
        token = token.substring(0,token.lastIndexOf(","));
        token = token.substring(0,token.length()-1);
        sb.append(token);
        return sb.toString();
    }

    public static String[] getJsonKVP(String token)
    {
        String[] splitter = token.split(":",2);
        token = splitter[0].replaceAll("\"","");
        String value = splitter[1].substring(0,splitter[1].length()).replaceAll("\"","");
        String[] kvp = {token, value};
        return kvp;
    }
}
