package net.minecraft.src;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventPacketReceive;
import net.skidcode.gh.maybeaclient.events.impl.EventPacketSend;

public class NetworkManager {
    public static final Object threadSyncObject = new Object();
    public static int numReadThreads;
    public static int numWriteThreads;
    private Object sendQueueLock = new Object();
    private Socket networkSocket;
    public final SocketAddress remoteSocketAddress;
    private DataInputStream socketInputStream;
    private DataOutputStream socketOutputStream;
    private boolean isRunning = true;
    private List readPackets = Collections.synchronizedList(new ArrayList());
    private List dataPackets = Collections.synchronizedList(new ArrayList());
    private List chunkDataPackets = Collections.synchronizedList(new ArrayList());
    private NetHandler netHandler;
    private boolean isServerTerminating = false;
    private Thread writeThread;
    private Thread readThread;
    private boolean isTerminating = false;
    private String terminationReason = "";
    private Object[] field_20101_t;
    public int timeSinceLastRead = 0;
    private int sendQueueByteLength = 0;
    public int chunkDataSendCounter = 0;
    private int field_20100_w = 50;

    public NetworkManager(Socket var1, String var2, NetHandler var3) throws IOException {
        this.networkSocket = var1;
        this.remoteSocketAddress = var1.getRemoteSocketAddress();
        this.netHandler = var3;

        try {
            var1.setSoTimeout(30000);
            var1.setTrafficClass(24);
        } catch (SocketException var5) {
            System.err.println(var5.getMessage());
        }

        this.socketInputStream = new DataInputStream(new BufferedInputStream(var1.getInputStream()));
        this.socketOutputStream = new DataOutputStream(new BufferedOutputStream(var1.getOutputStream()));
        this.readThread = new NetworkReaderThread(this, var2 + " read thread");
        this.writeThread = new NetworkWriterThread(this, var2 + " write thread");
        this.readThread.start();
        this.writeThread.start();
    }

    public void addToSendQueue(Packet var1) {
        if (!this.isServerTerminating) {
        	EventPacketSend ev = new EventPacketSend(var1);
        	EventRegistry.handleEvent(ev);
        	if(ev.cancelled) return;
            synchronized(this.sendQueueLock) {
                this.sendQueueByteLength += var1.getPacketSize() + 1;
                if (var1.isChunkDataPacket) {
                    this.chunkDataPackets.add(var1);
                } else {
                    this.dataPackets.add(var1);
                }

            }
        }
    }

    private void sendPacket() {
        try {
            boolean var1 = true;
            Packet var2;
            if (!this.dataPackets.isEmpty() && (this.chunkDataSendCounter == 0 || System.currentTimeMillis() - ((Packet)this.dataPackets.get(0)).creationTimeMillis >= (long)this.chunkDataSendCounter)) {
                var1 = false;
                synchronized(this.sendQueueLock) {
                    var2 = (Packet)this.dataPackets.remove(0);
                    this.sendQueueByteLength -= var2.getPacketSize() + 1;
                }

                Packet.writePacket(var2, this.socketOutputStream);
            }

            if ((var1 || this.field_20100_w-- <= 0) && !this.chunkDataPackets.isEmpty() && (this.chunkDataSendCounter == 0 || System.currentTimeMillis() - ((Packet)this.chunkDataPackets.get(0)).creationTimeMillis >= (long)this.chunkDataSendCounter)) {
                var1 = false;
                synchronized(this.sendQueueLock) {
                    var2 = (Packet)this.chunkDataPackets.remove(0);
                    this.sendQueueByteLength -= var2.getPacketSize() + 1;
                }

                Packet.writePacket(var2, this.socketOutputStream);
                this.field_20100_w = 50;
            }

            if (var1) {
                Thread.sleep(10L);
            } else {
                this.socketOutputStream.flush();
            }
        } catch (InterruptedException var8) {
        } catch (Exception var9) {
            if (!this.isTerminating) {
                this.onNetworkError(var9);
            }
        }

    }

    private void readPacket() {
        try {
            Packet var1 = Packet.readPacket(this.socketInputStream, this.netHandler.func_27247_c());
            if (var1 != null) {
                this.readPackets.add(var1);
            } else {
                this.networkShutdown("disconnect.endOfStream");
            }
        } catch (Exception var2) {
            if (!this.isTerminating) {
                this.onNetworkError(var2);
            }
        }

    }

    private void onNetworkError(Exception var1) {
        var1.printStackTrace();
        this.networkShutdown("disconnect.genericReason", "Internal exception: " + var1.toString());
    }

    public void networkShutdown(String var1, Object... var2) {
        if (this.isRunning) {
            this.isTerminating = true;
            this.terminationReason = var1;
            this.field_20101_t = var2;
            (new NetworkMasterThread(this)).start();
            this.isRunning = false;

            try {
                this.socketInputStream.close();
                this.socketInputStream = null;
            } catch (Throwable var6) {
            }

            try {
                this.socketOutputStream.close();
                this.socketOutputStream = null;
            } catch (Throwable var5) {
            }

            try {
                this.networkSocket.close();
                this.networkSocket = null;
            } catch (Throwable var4) {
            }

        }
    }

    public void processReadPackets() {
        if (this.sendQueueByteLength > 1048576) {
            this.networkShutdown("disconnect.overflow");
        }

        if (this.readPackets.isEmpty()) {
            if (this.timeSinceLastRead++ == 1200) {
                this.networkShutdown("disconnect.timeout");
            }
        } else {
            this.timeSinceLastRead = 0;
        }

        int var1 = 100;

        while(!this.readPackets.isEmpty() && var1-- >= 0) {
            Packet var2 = (Packet)this.readPackets.remove(0);
            
            EventPacketReceive ev = new EventPacketReceive(var2);
        	EventRegistry.handleEvent(ev);
        	if(!ev.cancelled) var2.processPacket(this.netHandler);
        }

        if (this.isTerminating && this.readPackets.isEmpty()) {
            this.netHandler.handleErrorMessage(this.terminationReason, this.field_20101_t);
        }

    }

    // $FF: synthetic method
    static boolean isRunning(NetworkManager var0) {
        return var0.isRunning;
    }

    // $FF: synthetic method
    static boolean isServerTerminating(NetworkManager var0) {
        return var0.isServerTerminating;
    }

    // $FF: synthetic method
    static void readNetworkPacket(NetworkManager var0) {
        var0.readPacket();
    }

    // $FF: synthetic method
    static void sendNetworkPacket(NetworkManager var0) {
        var0.sendPacket();
    }

    // $FF: synthetic method
    static Thread getReadThread(NetworkManager var0) {
        return var0.readThread;
    }

    // $FF: synthetic method
    static Thread getWriteThread(NetworkManager var0) {
        return var0.writeThread;
    }
}
