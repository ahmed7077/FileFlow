package com.fileflow.web;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public final class FileFLowWebServer {
    private final int port;

    public FileFlowWebServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.createContext("/", new StaticFileHttpHandler());
        httpServer.createContext("/api/files/organise", new FileOrganiseHttpHandler());
        httpServer.setExecutor(Executors.newFixedThreadPool(4));
        httpServer.start();
    }
}
