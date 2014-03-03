package com.sohu.smc.hystrix.util;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPClient {
    static UDPSocketManager socks = new UDPSocketManager();
    public static long counter = 0;
    public static int Timeout = 0;
    public static long TotalTime = 0;
    public static int TimeLimit = 0;
    public static int TimeCount = 0;

    public static void memIntcpy(byte[] buf, int offset, int val, int len) {
        if (len > 4)
            return;
        try {
            for (int x1 = 0; x1 < len; x1++) {
                buf[offset + x1] = (byte) (val % 256);
                // System.out.println(buf[offset+x1]);
                val = val >> 8;
            }
            // System.out.println();
        } catch (Exception E) {
        }
    }

    public static void memLongcpy(byte[] buf, int offset, long val, int len) {
        if (len > 8)
            return;
        try {
            for (int x1 = 0; x1 < len; x1++) {
                buf[offset + x1] = (byte) (val % 256);
                // System.out.println(buf[offset+x1]);
                val = val >> 8;
            }
            // System.out.println();
        } catch (Exception E) {
        }
    }

    public static int intMemcpy(byte[] buf, int offset, int len) {
        int ret = 0;

        if (len > 4)
            return 0;
        try {
            for (int x1 = len - 1; x1 >= 0; x1--) {

                ret *= 256;
                ret += (buf[offset + x1] >= 0 ? buf[offset + x1]
                        : (256 + buf[offset + x1]));
            }
        } catch (Exception E) {
        }
        return ret;
    }

    public static int longMemcpy(byte[] buf, int offset, int len) {
        int ret = 0;

        if (len > 8)
            return 0;
        try {
            for (int x1 = len - 1; x1 >= 0; x1--) {

                ret *= 256;
                ret += (buf[offset + x1] >= 0 ? buf[offset + x1]
                        : (256 + buf[offset + x1]));
            }
        } catch (Exception E) {
        }
        return ret;
    }

    public static void memcpy(byte[] buf, int offset, byte[] inbuf, int off2,
                              int len) {
        try {
            for (int x1 = 0; x1 < len; x1++) {
                buf[x1 + offset] = inbuf[x1 + off2];
            }
        } catch (Exception E) {
        }
    }

    public static int send(String ServerIP, int port, byte[] L2PkgIn, byte[] L2PkgOut) {
        counter++;
        TimeCount++;
        if (TimeCount > TimeLimit)
            TimeCount = 0;
        byte[] outBuffer = new byte[L2PkgIn.length];
        memcpy(outBuffer, 0, L2PkgIn, 0, L2PkgIn.length);

        DatagramSocket ds = socks.getSock();
        DatagramPacket dpin = socks.getInPackage();

        if (ds == null || dpin == null)
            return -1;

        DatagramPacket dp = new DatagramPacket(outBuffer, outBuffer.length,
                socks.getAddr(ServerIP), port);

        try {
            ds.send(dp);

        } catch (Exception E) {
            System.out.println("LogUDPClient->UDPIO:" + E.toString() + " To:" + ServerIP);
        }

        socks.putSock(ds);
        socks.putInPackage(dpin);

        return -1;
    }
}
