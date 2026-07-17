package com.system.wash.controller;

import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class PetsController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/ajax/pages/Pets/find")
    public String find(@RequestBody(required = false) String body) {
        JSONObject responseJson = new JSONObject();

        String sqlPets = "SELECT id, name, breed, user_id, gender, birthday, weight_kg, body_size, personality, is_active FROM pets";
        List<Map<String, Object>> petsList = jdbcTemplate.queryForList(sqlPets);

        String sqlUsers = "SELECT user_id, nickname FROM user_profiles";
        List<Map<String, Object>> usersList = jdbcTemplate.queryForList(sqlUsers);

        java.util.Map<Long, String> userMap = new java.util.HashMap<>();
        for (Map<String, Object> userRow : usersList) {
            Number userIdNum = (Number) userRow.get("user_id");
            if (userIdNum != null) {
                userMap.put(userIdNum.longValue(), (String) userRow.get("nickname"));
            }
        }

        long count = petsList != null ? petsList.size() : 0;
        responseJson.put("count", count);

        JSONArray array = new JSONArray();
        if (petsList != null) {
            for (Map<String, Object> petRow : petsList) {
                JSONObject petJson = new JSONObject();
                
                Number petId = (Number) petRow.get("id");
                String name = (String) petRow.get("name");
                String breed = (String) petRow.get("breed");
                Number userId = (Number) petRow.get("user_id");
                
                petJson.put("id", petId != null ? petId.longValue() : JSONObject.NULL);
                petJson.put("petId", petId != null ? petId.longValue() : JSONObject.NULL);
                petJson.put("name", name != null ? name : "");
                petJson.put("petName", name != null ? name : "");
                petJson.put("breed", breed != null ? breed : "");
                
                petJson.put("gender", petRow.get("gender") != null ? petRow.get("gender").toString() : JSONObject.NULL);
                petJson.put("birthday", petRow.get("birthday") != null ? petRow.get("birthday").toString() : JSONObject.NULL);
                petJson.put("weightKg", petRow.get("weight_kg"));
                petJson.put("bodySize", petRow.get("body_size") != null ? petRow.get("body_size").toString() : JSONObject.NULL);
                petJson.put("personality", petRow.get("personality") != null ? petRow.get("personality").toString() : JSONObject.NULL);
                petJson.put("isActive", petRow.get("is_active") != null ? petRow.get("is_active") : true);

                JSONObject userJson = new JSONObject();
                if (userId != null) {
                    long uId = userId.longValue();
                    userJson.put("id", uId);
                    userJson.put("nickname", userMap.containsKey(uId) ? userMap.get(uId) : "");
                }
                petJson.put("user", userJson);
                
                array.put(petJson);
            }
        }

        responseJson.put("list", array);
        return responseJson.toString();
    }

    @GetMapping("/ajax/pages/Pets/{id}")
    public String findById(@PathVariable("id") Long id) {
        JSONObject responseJson = new JSONObject();
        JSONArray array = new JSONArray();

        String sqlPets = "SELECT id, name, breed, user_id, gender, birthday, weight_kg, body_size, personality, is_active FROM pets WHERE id = ?";
        List<Map<String, Object>> petsList = jdbcTemplate.queryForList(sqlPets, id);

        if (petsList != null && !petsList.isEmpty()) {
            Map<String, Object> petRow = petsList.get(0);
            JSONObject petJson = new JSONObject();
            
            Number petId = (Number) petRow.get("id");
            String name = (String) petRow.get("name");
            String breed = (String) petRow.get("breed");
            Number userId = (Number) petRow.get("user_id");
            
            petJson.put("id", petId != null ? petId.longValue() : JSONObject.NULL);
            petJson.put("petId", petId != null ? petId.longValue() : JSONObject.NULL);
            petJson.put("name", name != null ? name : "");
            petJson.put("petName", name != null ? name : "");
            petJson.put("breed", breed != null ? breed : "");
            
            petJson.put("gender", petRow.get("gender") != null ? petRow.get("gender").toString() : JSONObject.NULL);
            petJson.put("birthday", petRow.get("birthday") != null ? petRow.get("birthday").toString() : JSONObject.NULL);
            petJson.put("weightKg", petRow.get("weight_kg"));
            petJson.put("bodySize", petRow.get("body_size") != null ? petRow.get("body_size").toString() : JSONObject.NULL);
            petJson.put("personality", petRow.get("personality") != null ? petRow.get("personality").toString() : JSONObject.NULL);
            petJson.put("isActive", petRow.get("is_active") != null ? petRow.get("is_active") : true);

            JSONObject userJson = new JSONObject();
            if (userId != null) {
                long uId = userId.longValue();
                userJson.put("id", uId);
                
                String sqlUser = "SELECT nickname FROM user_profiles WHERE user_id = ?";
                List<Map<String, Object>> userProfiles = jdbcTemplate.queryForList(sqlUser, uId);
                if (userProfiles != null && !userProfiles.isEmpty()) {
                    userJson.put("nickname", userProfiles.get(0).get("nickname"));
                } else {
                    userJson.put("nickname", "");
                }
            }
            petJson.put("user", userJson);
            array.put(petJson);
        }

        responseJson.put("list", array);
        return responseJson.toString();
    }
}
