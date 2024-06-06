package io.github.sakurawald.config.model;

import io.github.sakurawald.module.initializer.teleport_warmup.Position;

import java.util.HashMap;
import java.util.Map;

public class HomeModel extends AbstractModel {

    public Map<String, Map<String, Position>> homes = new HashMap<>();
}
