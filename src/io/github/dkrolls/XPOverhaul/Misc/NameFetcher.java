package io.github.dkrolls.XPOverhaul.Misc;

import com.google.common.collect.ImmutableList;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * 
 * @author evilmidget38 (https://gist.github.com/evilmidget38/a5c971d2f2b2c3b3fb37)
 * I've adapted it some.
 */

public class NameFetcher implements Callable<Map<UUID, String>> {
    private static final String PROFILE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
    private final JSONParser jsonParser = new JSONParser();
    private final List<UUID> uuids;
    
    private static HashMap<UUID, String> cachedIDs = new HashMap<UUID, String>();
    
    public NameFetcher(List<UUID> uuids) {
        this.uuids = ImmutableList.copyOf(uuids);
    }

    @Override
    public Map<UUID, String> call() throws Exception {
        Map<UUID, String> uuidStringMap = new HashMap<UUID, String>();
        for (UUID uuid: uuids) {
            HttpURLConnection connection = (HttpURLConnection) new URL(PROFILE_URL+uuid.toString().replace("-", "")).openConnection();
            JSONObject response = (JSONObject) jsonParser.parse(new InputStreamReader(connection.getInputStream()));
            String name = (String) response.get("name");
            if (name == null) {
                continue;
            }
            String cause = (String) response.get("cause");
            String errorMessage = (String) response.get("errorMessage");
            if (cause != null && cause.length() > 0) {
                throw new IllegalStateException(errorMessage);
            }
            uuidStringMap.put(uuid, name);
        }
        return uuidStringMap;
    }
    
    public static String getName(UUID uuid) throws Exception{
    	OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
    	if(player.getName() != null){ //small optimization prevents accessing server
    		return player.getName();
    	}
    	else if(cachedIDs.get(uuid) != null){
    		return cachedIDs.get(uuid);
    	}
    	NameFetcher f = new NameFetcher(Arrays.asList(uuid));
    	String name = f.call().get(uuid);
    	cachedIDs.put(uuid, name);
    	return name;
    }
}
