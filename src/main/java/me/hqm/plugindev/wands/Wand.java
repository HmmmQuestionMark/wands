package me.hqm.plugindev.wands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Wand {
    String name();

    String[] lore();

    public static class Meta {
        String name;
        List<String> lore;
        Method method;

        public Meta(Wand wand, Method method) {
            this.name = wand.name();
            this.lore = Arrays.asList(wand.lore());
            this.method = method;
        }

        public String getName() {
            return name;
        }

        public List<String> getLore() {
            return lore;
        }

        public Method getMethod() {
            return method;
        }
    }
}
