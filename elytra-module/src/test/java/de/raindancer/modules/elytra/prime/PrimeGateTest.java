package de.raindancer.modules.elytra.prime;

import de.raindancer.modules.elytra.util.PermissionNodes;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PrimeGateTest {

    /** Static: Bukkit takes its server once per JVM, and Permission.setDefault recalculates through it. */
    private static final PluginManager manager = mock(PluginManager.class);
    private final Map<String, Permission> registered = new HashMap<>();

    @BeforeEach
    void fakeManager() throws ReflectiveOperationException {
        if (Bukkit.getServer() == null) {
            Server server = mock(Server.class);
            when(server.getPluginManager()).thenReturn(manager);
            // Paper's Bukkit.setServer reads build info a test has none of; the field is all that is needed.
            java.lang.reflect.Field field = Bukkit.class.getDeclaredField("server");
            field.setAccessible(true);
            field.set(null, server);
        }
        org.mockito.Mockito.reset(manager);
        when(manager.getPermission(anyString())).thenAnswer(call -> registered.get(call.<String>getArgument(0)));
        doAnswer(call -> {
            Permission permission = call.getArgument(0);
            registered.put(permission.getName(), permission);
            return null;
        }).when(manager).addPermission(any(Permission.class));
        when(manager.getPermissionSubscriptions(anyString())).thenReturn(Set.of());
        registered.put(PermissionNodes.LOOKS, PermissionNodes.declared().getFirst());
    }

    @Test
    @DisplayName("Prime-only: nobody has the looks by default, Prime, staff perks and free cosmetics do")
    void primeOnly() {
        PrimeGate.apply(manager, true);

        assertThat(registered.get(PermissionNodes.LOOKS).getDefault()).isEqualTo(PermissionDefault.FALSE);
        for (String parent : PrimeGate.PARENTS) {
            assertThat(registered.get(parent).getChildren()).as(parent).containsEntry(PermissionNodes.LOOKS, true);
        }
    }

    @Test
    @DisplayName("parents nobody registered yet are made with the defaults their owners give them")
    void parentsMadeLikeTheirOwners() {
        PrimeGate.apply(manager, true);

        assertThat(registered.get(PrimeGate.PRIME).getDefault()).as("Core's rains.prime").isEqualTo(PermissionDefault.FALSE);
        assertThat(registered.get(PrimeGate.COSMETICS_FREE).getDefault()).isEqualTo(PermissionDefault.OP);
        assertThat(registered.get(PrimeGate.COSMETICS_PERKS).getDefault()).isEqualTo(PermissionDefault.OP);
    }

    @Test
    @DisplayName("switched off, the looks are everybody's again and no parent carries them")
    void off() {
        PrimeGate.apply(manager, true);
        PrimeGate.apply(manager, false);

        assertThat(registered.get(PermissionNodes.LOOKS).getDefault()).isEqualTo(PermissionDefault.TRUE);
        for (String parent : PrimeGate.PARENTS) {
            assertThat(registered.get(parent).getChildren()).as(parent).doesNotContainKey(PermissionNodes.LOOKS);
        }
    }
}
