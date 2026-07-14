package ai.chat2db.community.jcef.utils;

import lombok.Getter;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

public class MacAddressUtil {


    public static Optional<String> findBestMacAddress() {
        try {
            List<NetworkInterface> allInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            List<Candidate> candidates = new ArrayList<>();

            for (NetworkInterface networkInterface : allInterfaces) {
                byte[] mac = networkInterface.getHardwareAddress();
                if (!isValidCandidate(networkInterface, mac)) {
                    continue;
                }
                int score = calculateScore(networkInterface);
                candidates.add(new Candidate(mac, score, networkInterface.getDisplayName()));
            }
            if (!candidates.isEmpty()) {
                candidates.sort(Comparator.comparingInt(Candidate::getScore).reversed());
                return Optional.of(formatMacAddress(candidates.get(0).getMac()));
            }

        } catch (SocketException e) {
            System.err.println("Error accessing network interfaces: " + e.getMessage());
        }

        return Optional.empty();
    }


    private static boolean isValidCandidate(NetworkInterface ni, byte[] mac) throws SocketException {
        if (mac == null || mac.length != 6 || isAllZeros(mac)) {
            return false;
        }
        if (ni.isLoopback()) {
            return false;
        }
        if (ni.isVirtual() || ni.isPointToPoint()) {
            return false;
        }
        String name = ni.getName().toLowerCase();
        String displayName = ni.getDisplayName().toLowerCase();
        return !isVirtualName(name) && !isVirtualName(displayName);
    }


    private static boolean isVirtualName(String name) {
        return name.contains("virtual") || name.contains("vmnet") || name.contains("vmware") ||
               name.contains("vbox") || name.contains("hyper-v") || name.contains("vpn") ||
               name.contains("tunnel") || name.contains("tap") || name.startsWith("docker") ||
               name.startsWith("br-") || name.startsWith("veth");
    }


    private static int calculateScore(NetworkInterface ni) throws SocketException {
        int score = 0;
        String name = ni.getName().toLowerCase();
        if (ni.isUp()) {
            score += 1000;
        }
        if (name.startsWith("en") || name.startsWith("eth")) {
            score += 100;
        }
        if (name.startsWith("wl") || name.startsWith("wlan")) {
            score += 50;
        }

        return score;
    }


    private static boolean isAllZeros(byte[] mac) {
        for (byte b : mac) {
            if (b != 0) {
                return false;
            }
        }
        return true;
    }


    private static String formatMacAddress(byte[] mac) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : ""));
        }
        return sb.toString();
    }


    private static class Candidate {
        @Getter
        private final byte[] mac;
        @Getter
        private final int score;
        private final String displayName;

        public Candidate(byte[] mac, int score, String displayName) {
            this.mac = mac;
            this.score = score;
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return "Candidate{" +
                   "mac=" + formatMacAddress(mac) +
                   ", score=" + score +
                   ", displayName='" + displayName + '\'' +
                   '}';
        }
    }
}
