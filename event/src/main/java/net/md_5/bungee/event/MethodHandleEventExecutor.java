package net.md_5.bungee.event;

import lombok.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.WrongMethodTypeException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.google.common.base.Preconditions;

@RequiredArgsConstructor
public class MethodHandleEventExecutor implements EventExecutor {
    private final MethodHandle handle;

    public MethodHandleEventExecutor(Method m) {
        Preconditions.checkArgument(!Modifier.isStatic(m.getModifiers()), "Static method: %s", m);
        try {
            m.setAccessible(true);
            this.handle = MethodHandles.lookup().unreflect(m);
        } catch (IllegalAccessException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public void invoke(Object listener, Object event) throws Throwable {
        try {
            handle.invoke(listener, event);
        } catch (ClassCastException | WrongMethodTypeException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
