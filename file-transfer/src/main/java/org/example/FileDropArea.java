package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileDropArea extends JTextArea implements DropTargetListener {


    private final JTextField selectedFile;


    private static final Logger logger = LoggerFactory.getLogger(FileDropArea.class);

    public FileDropArea(JTextField selectedFile) {
        this.selectedFile = selectedFile;
    }


    @Override
    public void dragEnter(DropTargetDragEvent dtde) {

    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {

    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {

    }

    @Override
    public void dragExit(DropTargetEvent dte) {

    }

    @Override
    public void drop(DropTargetDropEvent dtde) {



        if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){

            dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

            List<File> transferData = null;
            try {
                transferData = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
            } catch (UnsupportedFlavorException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (transferData != null && transferData.size() > 0){

                if (transferData.size() > 1){
                    JOptionPane.showMessageDialog(this,"只能选择一个文件");
                    return;
                }

                for (File transferDatum : transferData) {
                    String absolutePath = transferDatum.getAbsolutePath();
                    System.out.println(absolutePath);
                    this.append("选择文件:"+absolutePath+"\n");
                    selectedFile.setText(absolutePath);
                }
            }

        }

    }
}
