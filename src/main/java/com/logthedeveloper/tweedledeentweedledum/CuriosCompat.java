package com.logthedeveloper.tweedledeentweedledum;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public final class CuriosCompat {
    private static final String CURIOS_API = "top.theillusivec4.curios.api.CuriosApi";
    private static final String CURIOS_HANDLER = "top.theillusivec4.curios.api.type.capability.ICuriosItemHandler";
    private static final String STACKS_HANDLER = "top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler";
    private static final String DYNAMIC_STACK_HANDLER = "top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler";

    private static volatile Accessors accessors;

    private CuriosCompat() {}


    public static List<ItemStack> getTweedleStacks(LivingEntity entity) {
        if (entity == null || !ModList.get().isLoaded("curios")) {
            return List.of();
        }

        try {
            Accessors api = getAccessors();
            Object optionalHandler = api.getInventory.invoke(null, entity);
            if (!(optionalHandler instanceof Optional<?> handlerOptional)) {
                return List.of();
            }
            Object handler = handlerOptional.orElse(null);
            if (handler == null) {
                return List.of();
            }

            Object optionalStacks = api.getStacksHandler.invoke(handler, "tweedle");
            if (!(optionalStacks instanceof Optional<?> stacksOptional)) {
                return List.of();
            }
            Object stacksHandler = stacksOptional.orElse(null);
            if (stacksHandler == null) {
                return List.of();
            }

            Object stacks = api.getStacks.invoke(stacksHandler);
            int slots = (int) api.getSlots.invoke(stacks);
            Object rendersValue = api.getRenders.invoke(stacksHandler);
            List<?> renders = rendersValue instanceof List<?> list ? list : List.of();

            List<ItemStack> result = new ArrayList<>(Math.min(slots, 2));
            for (int slot = 0; slot < slots && result.size() < 2; slot++) {
                if (slot < renders.size() && Boolean.FALSE.equals(renders.get(slot))) {
                    continue;
                }
                Object value = api.getStackInSlot.invoke(stacks, slot);
                if (value instanceof ItemStack stack && !stack.isEmpty() && stack.getItem() instanceof PlushItem) {
                    result.add(stack);
                }
            }
            return result;
        } catch (ReflectiveOperationException | LinkageError exception) {
            TweedleMod.LOGGER.debug("Curios is installed, but its inventory could not be read for shoulder rendering/effects.", exception);
            return List.of();
        }
    }

    private static Accessors getAccessors() throws ReflectiveOperationException {
        Accessors result = accessors;
        if (result != null) {
            return result;
        }
        synchronized (CuriosCompat.class) {
            result = accessors;
            if (result == null) {
                ClassLoader loader = CuriosCompat.class.getClassLoader();
                Class<?> apiClass = Class.forName(CURIOS_API, false, loader);
                Class<?> handlerClass = Class.forName(CURIOS_HANDLER, false, loader);
                Class<?> stacksHandlerClass = Class.forName(STACKS_HANDLER, false, loader);
                Class<?> dynamicStacksClass = Class.forName(DYNAMIC_STACK_HANDLER, false, loader);
                result = new Accessors(
                        apiClass.getMethod("getCuriosInventory", LivingEntity.class),
                        handlerClass.getMethod("getStacksHandler", String.class),
                        stacksHandlerClass.getMethod("getStacks"),
                        dynamicStacksClass.getMethod("getSlots"),
                        dynamicStacksClass.getMethod("getStackInSlot", int.class),
                        stacksHandlerClass.getMethod("getRenders")
                );
                accessors = result;
            }
        }
        return result;
    }

    private record Accessors(Method getInventory, Method getStacksHandler, Method getStacks,
                             Method getSlots, Method getStackInSlot, Method getRenders) {}
}
