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
            String token = "", value = "";
            Boolean getToken = true, ignoreCommas = false;
            String[] kvp;
            while (tokenizer.hasMoreElements())
            {
                if (getToken)
                {
                    String delims = "\\{\\}";
                    token = tokenizer.nextToken(ignoreCommas ? delims : delims+",");
                    if  (token.startsWith(","))
                    {
                        token = token.substring(1);
                    }
                    ignoreCommas = false;
                }
                else
                {
                    getToken = true;
                }
                if(!token.endsWith(":"))
                {
                    kvp = getJsonKVP(token);
                    token = kvp[0];
                    value = kvp[1];
                    switch (token)
                    {
                        case "mainName":
                            parsedSandwich.setMainName(value);
                            ignoreCommas = true;
                            break;
                        case "alsoKnownAs":
                            List<String> aliases = parseJsonArray(value);
                            parsedSandwich.setAlsoKnownAs(aliases);
                            break;
                        case "placeOfOrigin":
                            String location = getFullValue(value,tokenizer,true);
                            parsedSandwich.setPlaceOfOrigin(value.isEmpty() ? "Unknown": location);
                            getToken = location.isEmpty();
                            if (!getToken)
                                token = "description"+tokenizer.nextToken(",");
                            break;
                        case "description":
                            String fullDescription = getFullValue(value, tokenizer,false);
                            parsedSandwich.setDescription(fullDescription);
                            getToken = false;
                            token = tokenizer.nextToken(",");
                            if (!token.startsWith("\"image\""))
                                token = "image"+token;
                            break;
                        case "image":
                            parsedSandwich.setImage(value);
                            ignoreCommas = true;
                            break;
                        case "ingredients":
                            List<String> ingredients = parseJsonArray(value);
                            parsedSandwich.setIngredients(ingredients);
                            break;
                        default:
                            Log.e("ERROR", "Attribute: "+token+" not used.");
                    }

                }
            }
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

    public static String getFullValue(String partialValue, StringTokenizer tokenizer, boolean gettingOrigin)
    {
        if (partialValue.isEmpty())
        {
            return "";
        }
        else if(partialValue.endsWith("."))
        {
            return partialValue;
        }
        StringBuilder sb = new StringBuilder(partialValue);
        String token = tokenizer.nextToken(":");
        if ((gettingOrigin && countChar(token, ",") >= 2) || !gettingOrigin)
        {
            token = token.substring(0,token.lastIndexOf(","));
            token = token.substring(0,token.length()-1);
            sb.append(token);
        }
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

    private static int countChar(String s, String c)
    {
        int count = 0;
        for (int i = 0; i < s.length(); ++i)
        {
            count += c.equals(s.charAt(i)) ? 1 : 0;
        }
        return count;
    }
}
