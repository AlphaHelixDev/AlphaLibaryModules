package de.alphahelix.almreflections.nms.REnums;


import de.alphahelix.almreflections.reflection.ReflectionUtil;

import java.io.Serializable;

public enum REnumAction implements Serializable {

    INTERACT(0),
    ATTACK(1),
    INTERACT_AT(2);

    private int c;

    REnumAction(int c) {
        this.c = c;
    }

    public Object getEnumAction() {
        return ReflectionUtil.getNmsClass("PacketPlayInUseEntity$EnumEntityUseAction").getEnumConstants()[c];
    }

    @Override
    public String toString() {
        return "REnumAction{" +
                "c=" + c +
                '}';
    }
}
