package websocket.client;

import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;
import java.util.stream.IntStream;

/**
 * Example of a simple Echo Client.
 */
public class SimpleEchoClient {

    static int max = 50;

    public static void main(String[] args) {
//        long startTime = System.currentTimeMillis();
////        IntStream.range(0, 1).parallel().forEach(i ->
////            createRequest("ws://127.0.0.1:800" + i + "/", "log" + i + ".txt")
////        );
//
        for (int i = 0; i < max; ++i) {
            if (i < 10) {
                createRequest("ws://127.0.0.1:800" + i + "/", "log" + i + ".txt");
            } else {
                createRequest("ws://127.0.0.1:80" + i + "/", "log" + i + ".txt");
            }
        }
//        long stopTime = System.currentTimeMillis();
//        System.out.println("diff: " + (stopTime - startTime));
                    createRequest("ws://127.0.0.1:8000/", "log" + 0 + ".txt");

    }


    private static void createRequest(String destUri, String logFile) {
        WebSocketClient client = new WebSocketClient();

        SimpleEchoSocket socket = new SimpleEchoSocket(logFile);
        try {
            client.start();
            URI echoUri = new URI(destUri);
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            client.connect(socket, echoUri, request);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}