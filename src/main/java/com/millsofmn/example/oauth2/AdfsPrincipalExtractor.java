package com.millsofmn.example.oauth2;

import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;

import java.util.Map;

/**
 * Principal Extractor to find the principal from the Oauth2 JWT
 * Created by M108491 on 4/5/2017.
 */
public class AdfsPrincipalExtractor implements PrincipalExtractor {
    private static final String[] PRINCIPAL_KEYS = new String[]{"lan_id"};

    public AdfsPrincipalExtractor() {
    }

    public Object extractPrincipal(Map<String, Object> map) {
        String[] var2 = PRINCIPAL_KEYS;
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String key = var2[var4];
            if(map.containsKey(key)) {
                return map.get(key);
            }
        }

        return null;
    }
}