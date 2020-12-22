package client;

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;



public class Main {


    public static void main(String[] args) {
        TaskArgs targs = new TaskArgs();
        JCommander taskArgs = JCommander.newBuilder().addObject(targs).build();
        taskArgs.parse(args);


        if (targs.getInput() != null) {
            System.out.println("get from file: " + targs.getInput());
            try {
                JsonReader reader = new JsonReader(new FileReader("src/client/data/" + targs.getInput()));//need to fix the path
                targs = parseTaskArgs(reader);
            } catch (IOException e) {
                System.err.println("Could not create targs from file");
                e.printStackTrace();
            }

        }


        String address = "127.0.0.1";
        int port = 23456;
        try (Socket socket = new Socket(InetAddress.getByName(address), port);
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream())
        ) {
            System.out.println("Client started!");

            Gson gson = new Gson();
            System.out.println("Sent: " + targs.getJSONCommand());
            output.writeUTF(gson.toJson(targs));
            String receivedMsg = input.readUTF();

            System.out.println("Received: " + receivedMsg);


        } catch (Exception e) {

        }


    }

    private static TaskArgs parseTaskArgs(JsonReader reader) {
        TaskArgs taskArgs = new TaskArgs();
        String fieldName = null;
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                JsonToken token = reader.peek();

                if (token.equals(JsonToken.END_OBJECT)) {
                    reader.endObject();
                    break;
                }

                fieldName = reader.nextName();

                switch (fieldName) {
                    case "type" : taskArgs.setType(reader.nextString());
                        break;
                    case "key" :
                        token = reader.peek();
                        if (token.equals(JsonToken.BEGIN_ARRAY)) {
                            String value = parseArray(reader);
                            taskArgs.setKey(value);
                        } else {
                            taskArgs.setKey(reader.nextString());
                        }
                        break;
                    case "value" :
                        token = reader.peek();
                        if (token.equals(JsonToken.BEGIN_OBJECT)) {
                            String value = parseObject(reader);
                            taskArgs.setValue(value.substring(0, value.length() - 1));
                        } else {
                            taskArgs.setValue(reader.nextString());
                        }
                        break;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return taskArgs;
    }

    private static String parseArray(JsonReader reader) {
        String arr = "[";

        try {
            reader.beginArray();

            while (true) {
                JsonToken token = reader.peek();

                if (token.equals(JsonToken.END_ARRAY)) {
                    reader.endArray();
                    arr = arr.substring(0, arr.length() - 2) + "]";
                    break;
                }

                arr += "\"" + reader.nextString() + "\", ";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return arr;
    }

    private static String parseObject(JsonReader reader) {
        String obj = "{";
        try {
            reader.beginObject();

            while (true) {
                JsonToken token = reader.peek();
                if (token.equals(JsonToken.BEGIN_OBJECT)) {
                    obj += parseObject(reader);
                } else if (token.equals(JsonToken.END_OBJECT)) {
                    obj = obj.substring(0, obj.length() - 1);
                    obj += "},";
                    reader.endObject();
                    break;
                } else {
                    if (token.equals(JsonToken.NAME)) {
                        obj += "\"" + reader.nextName() + "\":";
                    }
                    token = reader.peek();

                    switch (token) {
                        case NUMBER:
                        case STRING:
                            obj += "\"" + reader.nextString() + "\",";
                            break;
                        case BOOLEAN:
                            obj += "\"" + reader.nextBoolean() + "\",";
                            break;
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return obj;
    }
}
