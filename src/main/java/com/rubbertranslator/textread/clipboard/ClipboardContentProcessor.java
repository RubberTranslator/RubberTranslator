package com.rubbertranslator.textread.clipboard;

import com.rubbertranslator.textread.clipboard.ocr.OCRUtils;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/7 15:36
 * 剪切板内容处理
 */
public class ClipboardContentProcessor {
    /**
     * 处理剪切板内容，返回最终文本
     * @param t 剪切板内容
     * @return 处理后的文本
     */
    public String process(Transferable t){
        String copyText = null;
        try {
            if (t == null) return null;
            if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) { // 文本类型
                 copyText = (String) t.getTransferData(DataFlavor.stringFlavor);
            }else if(t.isDataFlavorSupported(DataFlavor.imageFlavor)){  // 图片类型
                Image image = (Image) t.getTransferData(DataFlavor.imageFlavor);
                // OCR
                copyText = OCRUtils.ocr(image);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING,"获取剪切板内容出错");
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING,e.getMessage());
        }
        return copyText;
    }
}
