package lk.ijse.dep12.server;

import lk.ijse.dep12.shared.to.Media;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerAppInitializer {

    //private static final List<Socket> CLIENTS_LIST = new ArrayList<>();
    private static final List<Socket> CLIENTS_LIST = new CopyOnWriteArrayList<>();

    public static void main(String[] args) throws IOException {

        try (ServerSocket serverSocket = new ServerSocket(5050)) {
            while (true) {
                Socket localSocket = serverSocket.accept();
                CLIENTS_LIST.add(localSocket);

                new Thread(()->{
                    try {
                        try (InputStream is = localSocket.getInputStream();
                        /*ObjectInputStream ois = new ObjectInputStream(is)*/) {

                            while (true) {
                               /* if(ois.available()>0){*/

                                byte[] buffer = new byte[1024];
                                if (is.read(buffer) == -1) break;
                                //Media media = (Media) ois.readObject();
                                broadcastMessage(localSocket, buffer);

                               /* try {
                                    Media media = (Media) ois.readObject();
                                    broadcastMessage(localSocket, media);
                                } catch (EOFException e) {
                                    break;
                                }*/
                           // }
                            }
                            System.out.println("Came here" + Thread.currentThread().getName());
                        }
                    } catch (IOException  e) {
                        System.out.println(localSocket.getRemoteSocketAddress());
                        throw new RuntimeException(e);
                    }
                }, CLIENTS_LIST.size() + "").start();
                System.out.println(CLIENTS_LIST.size() + "");
            }
        }

    }

    private static void broadcastMessage(Socket client, byte[] buffer) throws IOException {

        new Thread(()->{

            for (Socket socket : CLIENTS_LIST) {
                if (socket==client) continue;

                try{
                    if(socket.isConnected()){
                        socket.getOutputStream().write(buffer);
                        //new ObjectOutputStream(socket.getOutputStream()).writeObject(media);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }

        }).start();

    }
}
