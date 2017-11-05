package websocket.client;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * Basic Echo Client Socket
 */
@WebSocket(maxTextMessageSize = 1024)
public class SimpleEchoSocket {
    private final CountDownLatch closeLatch;
    @SuppressWarnings("unused")
    private Session session;
    private Long startTime;
    private Long stopTime;
    private Integer i = 0, j = 0;
    private static final int MAX = 240;
    private RemoteEndpoint remoteEndpoint;
    private PrintWriter writer;

    public SimpleEchoSocket(String logFile) {
        this.closeLatch = new CountDownLatch(1);
        try {
            File f = new File("C:\\Users\\Khanh Nguyen\\Desktop\\test\\" + logFile);
            if (!f.exists() && !f.isDirectory()) {
                f.createNewFile();
            }
            writer = new PrintWriter(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
        return this.closeLatch.await(duration, unit);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.printf("Connection closed: %d - %s%n", statusCode, reason);
        this.session = null;
        this.closeLatch.countDown(); // trigger latch
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.printf("Got connect: %s%n", session);
        this.session = session;
        this.remoteEndpoint = session.getRemote();
        startTime = System.currentTimeMillis();

//        IntStream.range(0, MAX).parallel().forEach(i -> {
//            try {
//                Future<Void> fut = remoteEndpoint.sendStringByFuture("first msg");
//                fut.get(10, TimeUnit.SECONDS); // wait for send to complete.
//            } catch (Throwable e) {
//                e.printStackTrace();
//            }
//        });

        try {
            int i = 0;
            while (i < MAX) {
                Future<Void> fut = remoteEndpoint.sendStringByFuture("first msg");
                fut.get(10, TimeUnit.SECONDS); // wait for send to complete.
                ++i;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

//        new Thread(new Runnable() {
//            public void run() {
//                try {
//                    int i = 0;
//                    while (i < MAX) {
//                        Future<Void> fut = remoteEndpoint.sendStringByFuture("first msg");
//                        fut.get(10, TimeUnit.SECONDS); // wait for send to complete.
//                        ++i;
//                    }
//                } catch (Throwable e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();

    }

    @OnWebSocketMessage
    public void onMessage(String msg) {
        if (j < MAX) {
            writer.println(j + ": " + msg);
            ++j;
            if (j == MAX) {
                System.out.println("elapsed time: " + (System.currentTimeMillis() - startTime) + " | j = " + j);
                writer.close();
                session.close();
            }
        }
    }

}
