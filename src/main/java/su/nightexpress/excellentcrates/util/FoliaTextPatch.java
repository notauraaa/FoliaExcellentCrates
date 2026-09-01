package su.nightexpress.excellentcrates.util;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;
import org.bukkit.Bukkit;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;

public class FoliaTextPatch {

    private static boolean patched = false;

    public static boolean isPatched() {
        return patched;
    }

    public static void apply() {
        if (patched) return;
        try {
            Instrumentation inst = ByteBuddyAgent.install();

            new AgentBuilder.Default()
                .disableClassFormatChanges()
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .type((typeDescription, classLoader, module, classBeingRedefined, protectionDomain) -> {
                    String name = typeDescription.getName();
                    return name.equals("com.notauraaa.folianightcore.bridge.text.FoliaAbstractComponent")
                        || name.equals("com.notauraaa.folianightcore.bridge.text.impl.FoliaTextComponent")
                        || name.equals("com.notauraaa.folianightcore.bridge.text.impl.FoliaKeybindComponent")
                        || name.equals("com.notauraaa.folianightcore.bridge.text.impl.FoliaObjectComponent")
                        || name.equals("com.notauraaa.folianightcore.bridge.text.impl.FoliaTranslatableComponent");
                })
                .transform((builder, typeDescription, classLoader, module, protectionDomain) ->
                    builder.visit(
                        Advice.to(ToStringAdvice.class)
                            .on(ElementMatchers.named("toString")
                                .and(ElementMatchers.takesArguments(0)))
                    )
                )
                .installOn(inst);

            patched = true;
            Bukkit.getLogger().info("[FoliaExcellentCrates] FoliaTextComponent toString() patched successfully!");
        } catch (Exception e) {
            Bukkit.getLogger().warning("[FoliaExcellentCrates] Failed to patch FoliaTextComponent toString(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static class ToStringAdvice {
        @Advice.OnMethodExit(onThrowable = Throwable.class)
        public static void exit(@Advice.This Object thiz,
                               @Advice.Return(readOnly = false) String returnValue) {
            try {
                Method toLegacy = thiz.getClass().getMethod("toLegacy");
                Object result = toLegacy.invoke(thiz);
                if (result instanceof String str && !str.isEmpty()) {
                    returnValue = str;
                }
            } catch (Exception ignored) {
            }
        }
    }
}
