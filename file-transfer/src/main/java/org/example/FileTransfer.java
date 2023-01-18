package org.example;

import org.example.msg.FileMsg;
import org.example.msg.Msg;
import org.example.msg.SystemMsg;
import org.example.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class FileTransfer extends JFrame {


    private static final Logger logger = LoggerFactory.getLogger(FileTransfer.class);
    private final JTextField selectedFile;
    private final FileDropArea logArea;
    private final JPanel mainPanel;
    private final JPanel settingsPanel;

    /**
     * 0，建立连接失败
     * 1，作为客户端存在
     * 2，作为服务器存在
     */
    private int status;

    public boolean isFileSendEnable() {
        return fileSendEnable;
    }

    public void setFileSendEnable(boolean fileSendEnable) {
        this.fileSendEnable = fileSendEnable;
    }

    /**
     * 发送文件限制条件
     */
    private boolean fileSendEnable;

    /**
     * 连接
     */
    private Socket connectSocket;

    public FileTransfer() throws HeadlessException {


//        setComponentZOrder(this, 0);

        setTitle("File Drop Client");
        setAlwaysOnTop(true);


        JMenuBar jMenuBar = new JMenuBar();
        setJMenuBar(jMenuBar);

        JMenu jMenu = new JMenu("设置");
        jMenuBar.add(jMenu);

        JMenuItem config = new JMenuItem("配置信息");
        jMenu.add(config);

        config.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });


        Container contentPane = getContentPane();
        selectedFile = new JTextField();

        mainPanel = new JPanel();
        contentPane.add(mainPanel);
        logArea = new FileDropArea(selectedFile);
        logArea.setColumns(5);
        logArea.setRows(5);
        mainPanel.setLayout(new BorderLayout());


        settingsPanel = getSettingsPanel();


        JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout());
        panel1.setBackground(Color.GRAY);
        JPanel panel2 = new JPanel();
        panel2.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JButton selectBtn = new JButton("选择");

        JFrame that = this;
        selectBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                int openDialog = jFileChooser.showOpenDialog(that);
                if (openDialog == JFileChooser.APPROVE_OPTION) {

                    File selectedFile1 = jFileChooser.getSelectedFile();
                    String absolutePath = selectedFile1.getAbsolutePath();
                    selectedFile.setText(absolutePath);
                }
            }
        });
        JButton clearBtn = new JButton("清空");
        JButton sendBtn = new JButton("发送");
        sendBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (connectSocket == null || !connectSocket.isConnected()) {
                    logger.info("连接断开，无法发送");
                    JOptionPane.showMessageDialog(that, "连接断开，请重新连接");
                    return;
                }
                String text = selectedFile.getText();
                if (text == null || "".equals(text)) {
                    logger.info("发送文件不能为空");
                    JOptionPane.showMessageDialog(that, "选中文件不存在");
                    return;
                }
                File file = new File(text);
                if (!file.exists()) {
                    logger.info("文件[{}]不存在", file.getAbsolutePath());
                    JOptionPane.showMessageDialog(that, "选中文件不存在");
                    return;
                }
                FileMsg fileMsg = new FileMsg();
                fileMsg.setFileName(file.getName());

                byte[] bytes = FileUtils.readFile(file);
                if (bytes == null || bytes.length == 0) {
                    JOptionPane.showMessageDialog(that, "文件读取失败");
                    return;
                }

                fileMsg.setFileBytes(bytes);

                OutputStream outputStream = null;
                try {
                    outputStream = connectSocket.getOutputStream();
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                    objectOutputStream.writeObject(fileMsg);
                } catch (Exception ex) {
                    logger.error("文件发送失败:{}", ex.getMessage());
//                    throw new RuntimeException(ex);
                    JOptionPane.showMessageDialog(that, "选中文件不存在");
                }
            }
        });
        panel2.add(selectBtn);
        panel2.add(sendBtn);
        panel2.add(clearBtn);
        panel1.add(panel2, BorderLayout.SOUTH);


        JPanel panel3 = new JPanel();
        panel3.setLayout(new BorderLayout());
        JLabel label1 = new JLabel("文件");
        panel3.add(label1, BorderLayout.WEST);

        panel3.add(selectedFile);
        panel1.add(panel3);


        JPanel panel4 = new JPanel();
        panel4.setBorder(new EtchedBorder());
        panel4.setLayout(new BorderLayout());
        JLabel label = new JLabel("拖拽到这里", JLabel.CENTER);
        panel4.add(label);
        mainPanel.add(panel4);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(logArea);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);


//        jTextField.setPreferredSize(new Dimension(300,30));
        mainPanel.add(panel1, BorderLayout.NORTH);

        new DropTarget(label, DnDConstants.ACTION_COPY_OR_MOVE, logArea, true);


        setMinimumSize(new Dimension(400, 300));
        setLocationRelativeTo(null);
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

    }

    private JPanel getSettingsPanel() {
        final JPanel settingsPanel;
        settingsPanel = new JPanel();
        return settingsPanel;
    }

    public static void main(String[] args) {

        String property = System.getProperty("user.dir");
        System.out.println(property);

        FileTransfer fileDropSend = new FileTransfer();
        boolean success = fileDropSend.connectServer();
        if (!success) {
            fileDropSend.runAsServer();
        }

        // 接收文件
        new Thread(new Runnable() {


            @Override
            public void run() {

                while (true) {

                    Socket connectSocket1 = fileDropSend.getConnectSocket();
                    if (connectSocket1 != null && connectSocket1.isConnected()) {
                        InputStream inputStream = null;
                        try {
                            inputStream = connectSocket1.getInputStream();
                            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                            Msg readObject = (Msg) objectInputStream.readObject();
                            if (readObject.getType() == Msg.TYPE_FILE) {
                                logger.info("接收到一个文件类型消息");
                                FileMsg fileMsg = (FileMsg) readObject;
                                String fileName = fileMsg.getFileName();
                                logger.info("文件名字:{}", fileName);
                                byte[] fileBytes = fileMsg.getFileBytes();
                                logger.info("文件大小:{}", fileBytes.length);

                                String receiveFilePath = ConfigUtils.getReceiveFilePath();
                                logger.info("文件被下载在目录[{}]", receiveFilePath);

                                FileUtils.saveFile(receiveFilePath, fileName, fileBytes);


                            } else if (readObject.getType() == Msg.TYPE_STR) {

                            } else if (readObject.getType() == Msg.TYPE_SYSTEM) {
                                SystemMsg systemMsg = (SystemMsg) readObject;
                                int status1 = systemMsg.getStatus();
                                if (status1 == SystemMsg.SYSTEM_MSG_STATUS_FILE_SAVE) {
                                    // 对方文件已经接受完成
                                    fileDropSend.setFileSendEnable(true); // 当前客户端可以发送文件了
                                }
                            }

                        } catch (Exception e) {
                            logger.info("读取消息失败");
                            logger.info(e.getMessage());
//                            throw new RuntimeException(e);
                        }
                    }
                }


            }
        }).start();

    }

    private void runAsServer() {

        int serverPort = ConfigUtils.getMyPort();
        Socket socket = null;
        long s = System.currentTimeMillis();
        int connectStatus = 0;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(serverPort);
        } catch (IOException e) {
            connectStatus = 1;
            log(e.getMessage());
            log(String.format("服务器在端口%s启动失败", serverPort));
        }
        if (connectStatus == 1) {
            // 连接失败
            this.status = 0;
        }
        this.status = 2;
        log("等待客户端接入");
        try {
            socket = serverSocket.accept();
        } catch (IOException e) {
            log(e.getMessage());
            log("客户端接入失败");
//            throw new RuntimeException(e);
        }
        if (socket != null) {

            log(String.format("客户端%s:%s接入", socket.getInetAddress().getHostAddress(), socket.getPort()));
        }
        long end = System.currentTimeMillis();
        log("连接服务器耗时:" + (end - s));

        this.connectSocket = socket;

    }

    private void log(String log) {
        logArea.append(log + "\n");
    }

    private boolean connectServer() {
        String serverHost = ConfigUtils.getServerHost();
        int serverPort = ConfigUtils.getSErverPort();
        Socket socket = null;
        long s = System.currentTimeMillis();
        int connectStatus = 0;
        try {
//            socket = new Socket(serverHost, serverPort);
            socket = new Socket();
            socket.connect(new InetSocketAddress(serverHost, serverPort), 1000 * 2);
        } catch (IOException e) {
            connectStatus = 1;
            log(e.getMessage());
            log(String.format("连接服务器%s:%s失败", serverHost, serverPort));
        }
        long end = System.currentTimeMillis();
        log("连接服务器耗时:" + (end - s));
        if (connectStatus == 1) {
            // 连接失败
            this.status = 0;
            return false;
        }
        this.connectSocket = socket;
        return true;
    }

    public Socket getConnectSocket() {
        return connectSocket;
    }

}