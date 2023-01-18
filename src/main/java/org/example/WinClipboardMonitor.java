package org.example;


import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 监听剪切板，发送到客户端
 */
public class WinClipboardMonitor implements ClipboardOwner {
    Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    private ObjectOutputStream objectOutputStream;
    private Socket accept;

    public WinClipboardMonitor() {
        systemClipboard.setContents(systemClipboard.getContents(null), this);
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {


        try {
            Thread.sleep(1000 * 1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Transferable transferable = systemClipboard.getContents(null);

        String transferData = null;
        try {
            transferData = (String) transferable.getTransferData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(transferData);

        sendMsg(transferData);

        // 修改剪切板所有者
        systemClipboard.setContents(transferable, this);
    }

    private void sendMsg(String strMsg) {
        Msg msg = new Msg();
        msg.setValue(strMsg);
        try {
            objectOutputStream.writeObject(msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        WinClipboardMonitor winClipboardMonitor = new WinClipboardMonitor();
        winClipboardMonitor.init();
        Thread thread = Thread.currentThread();
        try {
            Thread.sleep(1000 * 60 * 60);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void init() {
        if (this.objectOutputStream == null || accept == null || accept.isClosed()) {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(9001);
                accept = serverSocket.accept();
                OutputStream outputStream = accept.getOutputStream();
                this.objectOutputStream = new ObjectOutputStream(outputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
