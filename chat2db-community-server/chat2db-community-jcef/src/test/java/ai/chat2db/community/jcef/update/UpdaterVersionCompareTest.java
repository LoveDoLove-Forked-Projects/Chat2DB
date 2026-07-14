package ai.chat2db.community.jcef.update;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdaterVersionCompareTest {

    @Test
    void shouldTreatLowerRemoteVersionAsOlder() {
        assertTrue(Updater.compareVersions("4.0.13", "4.1.1") < 0);
    }

    @Test
    void shouldTreatSameVersionAsEqual() {
        assertEquals(0, Updater.compareVersions("4.1.1", "4.1.1"));
        assertEquals(0, Updater.compareVersions("v4.1.1", "4.1.1"));
        assertEquals(0, Updater.compareVersions("4.1", "4.1.0"));
    }

    @Test
    void shouldTreatHigherRemoteVersionAsNewer() {
        assertTrue(Updater.compareVersions("4.1.2", "4.1.1") > 0);
        assertTrue(Updater.compareVersions("4.2", "4.1.99") > 0);
    }
}
