package com.mabasasi.iwc.swing;


import com.mabasasi.iwc.main.FileIO;
import com.mabasasi.iwc.data.SiteClass;
import MB.lib.MBArrayString;
import MB.lib.MBGridBagConstraints;
import static MB.lib.MBGridBagConstraints.ANCHOR.*;
import static MB.lib.MBGridBagConstraints.FILL.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import mabasasi.library.logger.MBLogger;


public class EditView implements ActionListener{
    private final MainView outer;    //使わないなら消してもいいかも
    
    
    private SiteClass sc;
    
    private JDialog dialog;
    private JPopupMenu listPopup;
    
    
    
    private JButton save;    
    private JLabel thumb;
    private JLabel fav;
    
    private JTextField name;
    private JTextField url;
    
    private JTextField replaceString;
    private JComboBox replaceWay;
    private JList regex;
    private JScrollPane regexPane;
    
    private JLabel analysis;
    private JLabel download;
    
    private JLabel queue;
    private JLabel record;
    private JLabel excluse;
    
    
    private JButton nameTest;
    private JButton urlTest;
    private JButton link;
    
    private JButton imageUpdate;
    private JButton statusDelete;
    private JButton initialize;
    private JButton reAnalysis;
    private JButton Adapt;
    private JButton Cancel;    
    
    public EditView(SiteClass sc, JComponent location, MainView outer){
        this.sc = sc;
        this.outer = outer;
        
        
        thumb = new JLabel();
        thumb.setHorizontalAlignment(JLabel.CENTER);
        thumb.setVerticalAlignment(JLabel.CENTER);
        int thumbSize = MainView.SITE_THUMBNAIL_SIZE;
        thumb.setPreferredSize(new Dimension(thumbSize, thumbSize));

        fav = new JLabel();
        fav.setHorizontalAlignment(JLabel.CENTER);
        fav.setVerticalAlignment(JLabel.CENTER);
        int favSize = MainView.SITE_FAVICON_SIZE;
        fav.setPreferredSize(new Dimension(favSize, favSize));
        
        save = new JButton("save");
        save.setActionCommand("save");
        save.addActionListener(this);
        
        name = new JTextField();
        url = new JTextField();
        
        replaceString = new JTextField();
        replaceWay = new JComboBox(new String[]{"none", "once", "after", "before"});
        
        regex = new JList(new DefaultListModel());
        regex.addMouseListener(new MouseAdapter() {            
            @Override
            public void mouseClicked(MouseEvent e) {
                // 座標をインデックスに変換
                Point p = e.getPoint();
                int index = regex.locationToIndex(p);
                
                if (e.getButton() == MouseEvent.BUTTON3){
                    if (index != -1)    regex.setSelectedIndex(index);
                    listPopup.show(regex, e.getX(), e.getY());
                }
            }
        });
        
        regexPane = new JScrollPane(regex);
        regexPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        
        analysis = new JLabel("0");
        analysis.setHorizontalAlignment(JLabel.RIGHT);
        download = new JLabel("0");
        download.setHorizontalAlignment(JLabel.RIGHT);
        
        queue = new JLabel("0");
        queue.setHorizontalAlignment(JLabel.RIGHT);
        record = new JLabel("0");
        record.setHorizontalAlignment(JLabel.RIGHT);
        excluse = new JLabel("0");
        excluse.setHorizontalAlignment(JLabel.RIGHT);
        
        nameTest = new JButton("テスト");
        nameTest.setActionCommand("nameTest");
        nameTest.addActionListener(this);
        
        urlTest = new JButton("テスト");
        urlTest.setActionCommand("urlTest");
        urlTest.addActionListener(this);
        
        link = new JButton("リンク");
        link.setActionCommand("link");
        link.addActionListener(this);
        
        imageUpdate = new JButton("サムネ\n更新");
        imageUpdate.setActionCommand("imageUpdate");
        imageUpdate.addActionListener(this);
        
        statusDelete = new JButton("統計削除");
        statusDelete.setActionCommand("valueClear");
        statusDelete.addActionListener(this);
        
        initialize = new JButton("初期化");
        initialize.setActionCommand("initialize");
        initialize.addActionListener(this);
        
        reAnalysis = new JButton("再解析");
        reAnalysis.setActionCommand("re-analysis");
        reAnalysis.addActionListener(this);
        
        Adapt = new JButton("適用");
        Adapt.setActionCommand("adapt");
        Adapt.addActionListener(this);
        
        Cancel = new JButton("閉じる");
        Cancel.setActionCommand("close");
        Cancel.addActionListener(this);
        
        
        dialog = create(location);
        createListPopup();
        
        attachSiteData(sc);
    }
    
    private JDialog create(JComponent location){
        final JDialog d = new JDialog();
        d.setTitle("edit");
        
        d.add(initPahel());
        
        d.setSize(640, 480);
        d.setLocationRelativeTo(location);
        d.setResizable(false);
        
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        d.setModal(true);
        
        d.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(dialog, "保存しますか？", "選択", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.YES_OPTION){                
                    boolean val = createSiteData();
                    if (val)    d.dispose();
                } else {
                    d.dispose();
                }
            }
        });
        
        return d;
    }
    

    public SiteClass show(){
        dialog.setVisible(true);
        
        return sc;
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * サイトクラスを更新する.
     * @return 更新できたか
     */
    public boolean createSiteData(){
        String n = name.getText();
        String u = url.getText();

        //SiteClassの初期化
        SiteClass nsc = null;
        try {            
            //初期化
            nsc = new SiteClass(n, u);
            nsc.setThumbnailImage((ImageIcon) thumb.getIcon());
            nsc.setFaviconImage((ImageIcon) fav.getIcon());

            nsc.setReplaceString(replaceString.getText());
            nsc.setReplaceWay((String) replaceWay.getSelectedItem());

            DefaultListModel model = (DefaultListModel) regex.getModel();
            nsc.setProcessUrlRegex(Arrays.toString(model.toArray()));

            nsc.setAnalysisHold(Integer.valueOf(analysis.getText()));
            nsc.setDownloadNow(Integer.valueOf(download.getText()));
            
            //置き換え
            if (sc != null){
                nsc.enque(sc.getQueue());
                nsc.addRecord(sc.getRecord());
                nsc.addExcept(sc.getExcept());
            }
            
            //サイトデータを置き換えて、情報を更新
            outer.updateSiteData(nsc, (this.sc != null));
            this.sc = nsc;
            return true;
        } catch (Exception ex){
            JOptionPane.showMessageDialog(dialog, ex.getMessage(), "警告", JOptionPane.WARNING_MESSAGE);
            MBLogger.Exception("Swing", ex);
        }
        
        return false;
    }
    
    
    
    /**
     * サイトリストを編集する.
     * @param sc サイトクラス
     */
    public void attachSiteData(SiteClass sc){        
        //空なら名前を編集OKにする
        if (sc == null){
            name.setEditable(true);
            return;
        } else {
            name.setEditable(false);
            sc.Normalization();
        }
        
        thumb.setIcon(sc.getThumbnailImage());
        fav.setIcon(sc.getFaviconImage());
        
        name.setText(sc.getPageName());
        replaceString.setText(sc.getReplaceString());
        replaceWay.setSelectedItem(sc.getReplaceWay());
        
        url.setText(sc.getPageUrl());
        DefaultListModel model = (DefaultListModel)regex.getModel();
        for (Object obj : sc.getArrayProcessUrlRegex()){
            model.addElement(obj);
        }
        
        analysis.setText(String.valueOf(sc.getAnalysisNum()));
        download.setText(String.valueOf(sc.getDownloadNum()));
        queue.setText(String.valueOf(sc.getQueueSize()));
        record.setText(String.valueOf(sc.getRecordSize()));
        excluse.setText(String.valueOf(sc.getExcluseSize()));
    }
    


    @Override
    public void actionPerformed(ActionEvent e) {
        MBLogger.Debug("Swing", "ActionCommand = %s.", e.getActionCommand());
        
        String tips = "\n[.]：任意の一文字、[^]：行の先頭、[$]：行の末尾\n"
                + "[*]：直前の文字の繰り返し（0～）、[+]：（1～）、[?]：（0～1）\n"
                + "[.*]：任意の文字数、[|]：いずれかの文字（or）\n"
                + "[（）]：グループ化、[\\d]、半角数字、[\\w]：半角英数字";
        
        
        DefaultListModel model = (DefaultListModel)regex.getModel();
        
        String value = null;
        int option = -1;
        switch(e.getActionCommand()){
//            case "New":
//                    value = JOptionPane.showInputDialog(dialog, "新規："+tips, "");
//                    if ((value != null)&&(!"".equals(value))){
//                        model.addElement(value);
//                    }
//                break;
//            case "Edit":
//                if (regex.getSelectedIndex() != -1){
//                    value = JOptionPane.showInputDialog(dialog, "編集："+tips, regex.getSelectedValue());
//                    if ((value != null)&&(!"".equals(value))){
//                        model.set(regex.getSelectedIndex(), value);
//                    }
//                }
//                break;
//            case "Delete":
//                if (regex.getSelectedIndex() != -1){
//                    option = JOptionPane.showConfirmDialog(dialog, "本当に削除しますか？\n"+regex.getSelectedValue(), "選択", JOptionPane.OK_CANCEL_OPTION);
//                    if (option == JOptionPane.YES_OPTION){
//                        model.remove(regex.getSelectedIndex());
//                    }
//                }
//                break;
//                
            case "imageUpdate":
                imageDownload();
                break;
            case "valueClear":
                option = JOptionPane.showConfirmDialog(dialog, "本当に削除しますか？\n"+regex.getSelectedValue(), "選択", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.YES_OPTION){
                    analysis.setText(String.valueOf(0));
                    download.setText(String.valueOf(0));
                }
                break;
            case "initialize":
                option = JOptionPane.showConfirmDialog(dialog, "本当に削除しますか？\n"+regex.getSelectedValue(), "選択", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.YES_OPTION){
                    queue.setText(String.valueOf(0));
                    record.setText(String.valueOf(0));
                    excluse.setText(String.valueOf(0));
                }
                break;
            case "re-analysis":
                option = JOptionPane.showConfirmDialog(dialog, "再解析しますか？\n"+regex.getSelectedValue(), "選択", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.YES_OPTION){
                    queue.setText("analysis");
                    record.setText("analysis");
                    excluse.setText("analysis");
                }
                break;
            case "adapt":
                createSiteData();
                
                break;
            case "close":
                option = JOptionPane.showConfirmDialog(dialog, "保存しますか？", "選択", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.YES_OPTION){                
                    boolean val = createSiteData();
                    if (val)    dialog.dispose();
                } else {
                    dialog.dispose();
                }
                break;
                
            case "link":
                FileIO.brawseAccess(url.getText());
                break;
            case "nameTest":
                value = JOptionPane.showInputDialog(dialog, "解析対象候補のタイトルを入力してください。", "");
                if ((value != null)&&(!"".equals(value))){
                    String result = SiteClass.ReplaceTitle(value, replaceString.getText(), replaceWay.getSelectedItem().toString());                    
                    JOptionPane.showMessageDialog(dialog, "結果："+result, "テスト", JOptionPane.INFORMATION_MESSAGE);
                }
                break;
            case "urlTest":
                value = JOptionPane.showInputDialog(dialog, "解析対象候補のURLを入力してください。", "");
                if ((value != null)&&(!"".equals(value))){
                    MBArrayString as = new MBArrayString();
                    as.setSeparator("|");
                    for (Object obj : model.toArray()){
                        as.add(obj);
                    }
                    
                    System.out.println("ptr="+as.toString());
                    Pattern p = Pattern.compile(as.toString());
                    Matcher m = p.matcher(value);
                    
                    JOptionPane.showMessageDialog(dialog, "結果："+m.find(), "テスト", JOptionPane.INFORMATION_MESSAGE);
                }
                break;
            case "save":
                try {
                    sc.saveSiteData();
                } catch (Exception ex) {
                    Logger.getLogger(EditView.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
        }
    }
    
    /**サムネとかを取ってくる*/
    private void imageDownload(){
        ImageIcon thumbIcon = FileIO.getSiteThumbnail(url.getText());
        ImageIcon favIcon = FileIO.getSiteFavicon(url.getText());
        if (thumbIcon != null)  thumb.setIcon(thumbIcon);
        if (favIcon != null)    fav.setIcon(favIcon);

        JOptionPane.showMessageDialog(dialog, "完了", "更新", JOptionPane.INFORMATION_MESSAGE);
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="AppletComponent">
    private JPanel initPahel(){
        JPanel panel = new JPanel();
        
        GridBagLayout gbl = new GridBagLayout();
        panel.setLayout(gbl);
        MBGridBagConstraints gbc = new MBGridBagConstraints();
        gbc.setInset(5);
        
        //左
        JPanel lp = makeLeftPanel();
        gbc.setGridBounds(0, 0, 1, 3, 0.0, 0.0);
        gbc.setAnchorAndFill(TOP, HORIZONTAL);
        gbl.addLayoutComponent(lp, gbc);
        panel.add(lp);
        
        //セパレーター
        JSeparator lrSep = new JSeparator(SwingConstants.VERTICAL);
        gbc.setGridBounds(1, 0, 1, 3, 0.0, 0.0);
        gbc.setAnchorAndFill(CENTER, BOTH);
        gbl.addLayoutComponent(lrSep, gbc);
        panel.add(lrSep);
        
        //名前
        JPanel np = makeNamePanel();
        gbc.setGridBounds(2, 0, 1, 1, 1.0, 0.0);
        gbc.setAnchorAndFill(TOP, HORIZONTAL);
        gbl.addLayoutComponent(np, gbc);
        panel.add(np);
        
        //セパレーター
        JSeparator nuSep = new JSeparator(SwingConstants.HORIZONTAL);
        gbc.setGridBounds(2, 1, 1, 1, 0.0, 0.0);
        gbc.setAnchorAndFill(CENTER, BOTH);
        gbl.addLayoutComponent(nuSep, gbc);
        panel.add(nuSep);
        
        //URL
        JPanel up = makeUrlPanel();
        gbc.setGridBounds(2, 2, 1, 1, 1.0, 1.0);
        gbc.setAnchorAndFill(CENTER, BOTH);
        gbl.addLayoutComponent(up, gbc);
        panel.add(up);
        
        //セパレーター
        JSeparator ucSep = new JSeparator(SwingConstants.HORIZONTAL);
        gbc.setGridBounds(0, 3, 3, 1, 0.0, 0.0);
        gbc.setAnchorAndFill(CENTER, BOTH);
        gbl.addLayoutComponent(ucSep, gbc);
        panel.add(ucSep);
        
        //コントロール
        JPanel cp = makeControlPanel();
        gbc.setGridBounds(0, 4, 3, 1, 1.0, 0.0);
        gbc.setAnchorAndFill(CENTER, HORIZONTAL);
        gbl.addLayoutComponent(cp, gbc);
        panel.add(cp);
        
        return panel;
    }
    
    private JPanel makeLeftPanel(){
        JPanel panel = new JPanel();
        
        GridBagLayout gbl = new GridBagLayout();
        panel.setLayout(gbl);
        MBGridBagConstraints gbc = new MBGridBagConstraints();
        
        
        //アイコン
        gbc.setGridBounds(0, 0, 2, 1, 0.0, 0.0);
        gbc.setAnchorAndFill(CENTER, BOTH);
        gbl.addLayoutComponent(thumb, gbc);
        panel.add(thumb);
        
        //ファビコンラベル
        JLabel favLabel = new JLabel("favicon：");
        gbc.setGridBounds(0, 1, 1, 1, 0.0, 0.0);
        gbc.setAnchorAndFill(LEFT, BOTH);
        gbl.addLayoutComponent(favLabel, gbc);
        panel.add(favLabel);
        
        //ファビコン
        gbc.setGridBounds(1, 1, 1, 1, 0.0, 0.0);
        gbc.setAnchorAndFill(LEFT, BOTH);
        gbl.addLayoutComponent(fav, gbc);
        panel.add(fav);
        
        
        
        //解析ラベル
        JLabel anlLabel = new JLabel("解析数：");
        gbc.setGridBounds(0, 3, 1, 1, 0.0, 0.0);
        gbc.setAnchorAndFill(LEFT, BOTH);
        gbl.addLayoutComponent(anlLabel, gbc);
        panel.add(anlLabel);
        
        //DLラベル
        JLabel dlLabel = new JLabel("DL数：");
        gbc.setGridBounds(0, 4, 1, 1, 0.0, 0.0);
        gbc.setAnchorAndFill(LEFT, BOTH);
        gbl.addLayoutComponent(dlLabel, gbc);
        panel.add(dlLabel);
        
        //キューラベル
        JLabel queLabel = new JLabel("queue：");
        gbc.setGridBounds(0, 5, 1, 1, 0.0, 0.0);
        gbc.setAnchorAndFill(LEFT, BOTH);
        gbl.addLayoutComponent(queLabel, gbc);
        panel.add(queLabel);
        
        //レコードラベル
        JLabel recLabel = new JLabel("record：");
        gbc.setGridBounds(0, 6, 1, 1, 0.0, 0.0);
        gbc.setAnchorAndFill(LEFT, BOTH);
        gbl.addLayoutComponent(recLabel, gbc);
        panel.add(recLabel);
        
        //除外ラベル
        JLabel exLabel = new JLabel("except：");
        gbc.setGridBounds(0, 7, 1, 1, 0.0, 0.0);
        gbc.setAnchorAndFill(LEFT, BOTH);
        gbl.addLayoutComponent(exLabel, gbc);
        panel.add(exLabel);
        
        
        
        //解析数
        gbc.setGridBounds(1, 3, 1, 1, 0.0, 0.0);
        gbc.setAnchorAndFill(RIGHT, BOTH);
        gbl.addLayoutComponent(analysis, gbc);
        panel.add(analysis);
        
        //DL数
        gbc.setGridBounds(1, 4, 1, 1, 0.0, 0.0);
        gbc.setAnchorAndFill(RIGHT, BOTH);
        gbl.addLayoutComponent(download, gbc);
        panel.add(download);
        
        //キュー数
        gbc.setGridBounds(1, 5, 1, 1, 0.0, 0.0);
        gbc.setAnchorAndFill(RIGHT, BOTH);
        gbl.addLayoutComponent(queue, gbc);
        panel.add(queue);
        
        //レコード数
        gbc.setGridBounds(1, 6, 1, 1, 0.0, 0.0);
        gbc.setAnchorAndFill(RIGHT, BOTH);
        gbl.addLayoutComponent(record, gbc);
        panel.add(record);
        
        //除外数
        gbc.setGridBounds(1, 7, 1, 1, 0.0, 0.0);
        gbc.setAnchorAndFill(RIGHT, BOTH);
        gbl.addLayoutComponent(excluse, gbc);
        panel.add(excluse);
        
        
        
        
        
        //セパレーター
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        gbc.setGridBounds(0, 2, 2, 1, 0.0, 0.0);
        gbc.setAnchorAndFill(CENTER, BOTH);
        gbc.setInset(new Insets(10, 0, 10, 0));
        gbl.addLayoutComponent(sep, gbc);
        panel.add(sep);
        
        
        
        
        
        
        return panel;
        
    }
    
    private JPanel makeNamePanel(){
        JPanel panel = new JPanel();
        
        GridBagLayout gbl = new GridBagLayout();
        panel.setLayout(gbl);
        MBGridBagConstraints gbc = new MBGridBagConstraints();
        
        
        //名前ラベル
        JLabel nameLabel = new JLabel("サイト名：");
        gbc.setGridBounds(0, 0, 1, 1, 0.0, 0.0);
        gbc.setAnchorAndFill(LEFT, BOTH);
        gbl.addLayoutComponent(nameLabel, gbc);
        panel.add(nameLabel);
        
        //置換文字列ラベル
        JLabel repStrLabel = new JLabel("┗ 置換文字列：");
        gbc.setGridBounds(0, 1, 1, 1, 0.0, 0.0);
        gbc.setAnchorAndFill(LEFT, BOTH);
        gbl.addLayoutComponent(repStrLabel, gbc);
        panel.add(repStrLabel);
        
        //置換方法ラベル
        JLabel repWayLabel = new JLabel("┗ 置換方法：");
        gbc.setGridBounds(0, 2, 1, 1, 0.0, 0.0);
        gbc.setAnchorAndFill(LEFT, BOTH);
        gbl.addLayoutComponent(repWayLabel, gbc);
        panel.add(repWayLabel);
        
        
        
        //名前
        gbc.setGridBounds(1, 0, 1, 1, 1.0, 0.0);
        gbc.setAnchorAndFill(LEFT, HORIZONTAL);
        gbl.addLayoutComponent(name, gbc);
        panel.add(name);
        
        //置換文字列
        gbc.setGridBounds(1, 1, 1, 1, 1.0, 0.0);
        gbc.setAnchorAndFill(LEFT, HORIZONTAL);
        gbl.addLayoutComponent(replaceString, gbc);
        panel.add(replaceString);
        
        //置換方法
        gbc.setGridBounds(1, 2, 1, 1, 1.0, 0.0);
        gbc.setAnchorAndFill(LEFT, NONE);
        gbl.addLayoutComponent(replaceWay, gbc);
        panel.add(replaceWay);
        
        
        
        //重複チェック用
        gbc.setGridBounds(2, 0, 1, 1, 0.0, 0.0);
        gbc.setAnchorAndFill(CENTER, BOTH);
        gbc.setInset(5);
        gbl.addLayoutComponent(save, gbc);
        panel.add(save);
        
        //テストボタン
        gbc.setGridBounds(2, 2, 1, 1, 0.0, 0.0);
        gbc.setAnchorAndFill(CENTER, BOTH);
        gbc.setInset(5);
        gbl.addLayoutComponent(nameTest, gbc);
        panel.add(nameTest);
        
        
        
        
        
        
        
        return panel;
    }
    
    private JPanel makeUrlPanel(){
        JPanel panel = new JPanel();
        
        GridBagLayout gbl = new GridBagLayout();
        panel.setLayout(gbl);
        MBGridBagConstraints gbc = new MBGridBagConstraints();
        
        
        //URLラベル
        JLabel urlLabel = new JLabel("ＵＲＬ：");
        gbc.setGridBounds(0, 0, 1, 1, 0.0, 0.0);
        gbc.setAnchorAndFill(LEFT, BOTH);
        gbl.addLayoutComponent(urlLabel, gbc);
        panel.add(urlLabel);
        
        //正規表現ラベル
        JLabel regexLabel = new JLabel("正規表現：");
        gbc.setGridBounds(0, 1, 1, 1, 0.0, 0.0);
        gbc.setAnchorAndFill(TOP, HORIZONTAL);
        gbl.addLayoutComponent(regexLabel, gbc);
        panel.add(regexLabel);
        
        
        //URL
        gbc.setGridBounds(1, 0, 1, 1, 1.0, 0.0);
        gbc.setAnchorAndFill(LEFT, HORIZONTAL);
        gbl.addLayoutComponent(url, gbc);
        panel.add(url);
        
        //正規表現
        gbc.setGridBounds(1, 1, 1, 1, 1.0, 1.0);
        gbc.setAnchorAndFill(LEFT, BOTH);
        gbl.addLayoutComponent(regexPane, gbc);
        panel.add(regexPane);
        
        
        //リンク
        gbc.setGridBounds(2, 0, 1, 1, 0.0, 0.0);
        gbc.setAnchorAndFill(CENTER, BOTH);
        gbc.setInset(5);
        gbl.addLayoutComponent(link, gbc);
        panel.add(link);
        
        //テスト
        gbc.setGridBounds(2, 1, 1, 1, 0.0, 0.0);
        gbc.setAnchorAndFill(BOTTOM, HORIZONTAL);
        gbc.setInset(5);
        gbl.addLayoutComponent(urlTest, gbc);
        panel.add(urlTest);
        
        return panel;
    }
    
    private JPanel makeControlPanel(){
        JPanel panel = new JPanel();
        
        GridLayout gl = new GridLayout(1, 6, 2, 2);
        panel.setLayout(gl);
        
        panel.add(imageUpdate);
        panel.add(statusDelete);
        panel.add(initialize);
        panel.add(reAnalysis);
        panel.add(Adapt);
        panel.add(Cancel);
        
        return panel;
    }
    
    private  void createListPopup(){
        listPopup = new JPopupMenu();
        
        JMenuItem editItem = new JMenuItem("編集");
        editItem.addActionListener(this);
        editItem.setActionCommand("Edit");
        listPopup.add(editItem);
        
        JMenuItem deleteItem = new JMenuItem("削除");
        deleteItem.addActionListener(this);
        deleteItem.setActionCommand("Delete");
        listPopup.add(deleteItem);
        
        JMenuItem newItem = new JMenuItem("新規");
        newItem.addActionListener(this);
        newItem.setActionCommand("New");
        listPopup.add(newItem);
    }
//</editor-fold>
        
}
