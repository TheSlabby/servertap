package io.servertap.api.v1;

import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import io.javalin.openapi.*;
import org.bukkit.Keyed;
import org.bukkit.Registry;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class RegistryApi {

    @OpenApi(
            path = "/v1/registries",
            summary = "Get list of all available registries",
            methods = {HttpMethod.GET},
            tags = {"Server"},
            headers = {
                    @OpenApiParam(name = "key")
            },
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/json"))
            }
    )
    public void getRegistries(Context ctx) {
        List<String> registries = new ArrayList<>();
        for (Field field : Registry.class.getFields()) {
            if (Modifier.isStatic(field.getModifiers()) && Registry.class.isAssignableFrom(field.getType())) {
                registries.add(field.getName().toLowerCase());
            }
        }
        ctx.json(registries);
    }

    @OpenApi(
            path = "/v1/registries/{registryName}",
            summary = "Get all keys in a registry",
            methods = {HttpMethod.GET},
            tags = {"Server"},
            headers = {
                    @OpenApiParam(name = "key")
            },
            pathParams = {
                    @OpenApiParam(name = "registryName", description = "The name of the registry, e.g. material, biome, sound_event")
            },
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/json")),
                    @OpenApiResponse(status = "404", description = "Registry not found")
            }
    )
    public void getRegistryKeys(Context ctx) {
        String registryName = ctx.pathParam("registryName");
        Registry<? extends Keyed> registry = getRegistryByName(registryName);
        if (registry == null) {
            throw new NotFoundResponse("Registry not found: " + registryName);
        }

        List<String> keys = new ArrayList<>();
        for (Keyed item : registry) {
            if (item != null && item.getKey() != null) {
                keys.add(item.getKey().toString());
            }
        }
        ctx.json(keys);
    }

    private Registry<? extends Keyed> getRegistryByName(String name) {
        String upperName = name.toUpperCase();

        // Some common aliases for convenience
        if (upperName.equals("BLOCKS")) upperName = "BLOCK";
        if (upperName.equals("ITEMS")) upperName = "ITEM";
        if (upperName.equals("LOOT_TABLE")) upperName = "LOOT_TABLES";
        if (upperName.equals("POTION_EFFECTS")) upperName = "POTION_EFFECT_TYPE";
        if (upperName.equals("SOUNDS")) upperName = "SOUND_EVENT";

        try {
            Field field = Registry.class.getField(upperName);
            if (Registry.class.isAssignableFrom(field.getType())) {
                return (Registry<? extends Keyed>) field.get(null);
            }
        } catch (Exception e) {
            // Fallback: search for case-insensitive matching fields
            for (Field field : Registry.class.getFields()) {
                if (field.getName().equalsIgnoreCase(name) || field.getName().equalsIgnoreCase(upperName)) {
                    try {
                        if (Registry.class.isAssignableFrom(field.getType())) {
                            return (Registry<? extends Keyed>) field.get(null);
                        }
                    } catch (Exception ex) {
                        // Ignore
                    }
                }
            }
        }
        return null;
    }
}
