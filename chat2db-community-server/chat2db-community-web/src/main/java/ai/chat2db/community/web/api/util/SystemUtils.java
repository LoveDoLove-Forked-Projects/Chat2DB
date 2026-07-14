package ai.chat2db.community.web.api.util;


import lombok.extern.slf4j.Slf4j;


@Slf4j
public class SystemUtils {


    public static void stop() {
        stop(null);
    }

    public static void stop(Runnable closeRuntime) {
        new Thread(() -> {
            log.info("Application will stop in one second");
            if (closeRuntime != null) {
                try {
                    closeRuntime.run();
                } catch (Exception e) {
                    log.error("runtime close error", e);
                }
            }
            log.info("Start stopping system application");

            try {
                Thread.sleep(200);
            } catch (Exception ignore) {

            }

        }).start();
    }
}
