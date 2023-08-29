package com.hnalovski.parks.util;

public class Util {
    public static final String PARKS_URL = "https://developer.nps.gov/api/v1/parks?stateCode=NV&api_key=w6DZoECsm1fZxt9J4nxLFUMRZ3NVgFqbD7c5mNJp";

    public static String getParksUrl(String stateCode) {
        return "https://developer.nps.gov/api/v1/parks?stateCode="+stateCode+"&api_key=w6DZoECsm1fZxt9J4nxLFUMRZ3NVgFqbD7c5mNJp";
    }
}
