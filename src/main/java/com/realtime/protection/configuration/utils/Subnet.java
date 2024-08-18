package com.realtime.protection.configuration.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Subnet {
    public final String ip;
    public final String subnetMask;
    public final long startIp;
    public final long endIp;
    public final int numberOfHosts;

    public Subnet(String ip, String subnetMask) throws UnknownHostException {
        this.ip = ip;
        this.subnetMask = subnetMask;
        this.startIp = calculateStartIp(ip, subnetMask);
        this.endIp = calculateEndIp(startIp, subnetMask);
        this.numberOfHosts = getNumberOfHosts(subnetMask);
    }

    private static long ipToLong(String ip) throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getByName(ip);
        byte[] bytes = inetAddress.getAddress();
        long result = 0;
        for (byte b : bytes) {
            result = (result << 8) | (b & 0xFF);
        }
        return result;
    }

    private long calculateStartIp(String ip, String subnetMask) throws UnknownHostException {
        long ipLong = ipToLong(ip);
        long maskLong = ipToLong(subnetMask);
        return ipLong & maskLong;
    }

    private long calculateEndIp(long startIp, String subnetMask) throws UnknownHostException {
        long maskLong = ipToLong(subnetMask);
        long hostPart = ~maskLong & 0xFFFFFFFFL;
        return startIp + hostPart;
    }

    public static int getNumberOfHosts(String subnetMask) throws UnknownHostException {
        long maskLong = ipToLong(subnetMask);
        long hostPart = ~maskLong & 0xFFFFFFFFL;
        return (int) (hostPart + 1);
    }

    public boolean overlapsWith(Subnet other) {
        return this.startIp <= other.endIp && other.startIp <= this.endIp;
    }

    public Subnet mergeWith(Subnet other) throws UnknownHostException {
        long newStartIp = Math.min(this.startIp, other.startIp);
        long newEndIp = Math.max(this.endIp, other.endIp);
        long newRange = newEndIp - newStartIp + 1;
        int newPrefixLength = 32 - Integer.numberOfTrailingZeros((int) newRange);
        long mask = 0xFFFFFFFFL << (32 - newPrefixLength);
        long newNetworkIp = newStartIp & mask;
        String newSubnetMask = longToIp(mask);
        return new Subnet(longToIp(newNetworkIp), newSubnetMask);
    }

    private String longToIp(long value) {
        return ((value >> 24) & 0xFF) + "." + ((value >> 16) & 0xFF) + "." + ((value >> 8) & 0xFF) + "." + (value & 0xFF);
    }

    @Override
    public String toString() {
        return "Subnet{" +
                "ip='" + ip + '\'' +
                ", subnetMask='" + subnetMask + '\'' +
                ", startIp=" + longToIp(startIp) +
                ", endIp=" + longToIp(endIp) +
                ", numberOfHosts=" + numberOfHosts +
                '}';
    }
}
