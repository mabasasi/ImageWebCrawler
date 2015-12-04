package com.mabasasi.iwc.swing;


import com.mabasasi.iwc.main.FileIO;
import com.mabasasi.iwc.main.ImageWebCrawler;
import com.mabasasi.iwc.data.SiteClass;
import MB.lib.MBGridBagConstraints;
import com.mabasasi.iwc.data.DataManager;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import mabasasi.library.logger.MBLogger;

public class MainView implements ActionListener{
    /**サムネイルサイズ*/
    public static int SITE_THUMBNAIL_SIZE = 100;
    /**ファビコンサイズ*/
    public static int SITE_FAVICON_SIZE = 16;
    /**表示するサムネイル数*/
    public static int SHOW_THUMBNAIL_NUM = 100;

    //private JFrame main;
    
    int siteThumb = 200;
    Color bgColor = new Color(0xf5f5f5);
    
    private JList imageList;
    private JList siteList;
    
    
    private final DataManager manager;
    
    
    /**
     * コンストラクタ.
     * @param manager データマネージャー
     */
    public MainView(final DataManager manager) {
        this.manager = manager;
        
        MBLogger.Info("Swing", "Making swing components...");
        
        //メイン画面作成
        JFrame frame = initMainFrame();
        frame.setVisible(true);
        frame.repaint();
        
        MBLogger.Fine("Swing", "Succeed.");
    }
    
    /**
     * サイトデータを関連付ける.
     * <p>一度初期化してから、すべてをアタッチする</p>
     * @param scs
     */
    public void setSiteData(Collection<SiteClass> scs){
        DefaultListModel model = (DefaultListModel) siteList.getModel();
        model.removeAllElements();
            
        for (SiteClass sc : scs){
            model.addElement(new SiteDataManager(sc));
            MBLogger.Fine("Swing","Attach. site=\"%s\"", sc.getPageName());
        }
    }
    
    /**
     * 画像データを追加する.
     * <p>最大数を超えたら、FIFO</p>
     * @param path 表示する画像パス
     */
    public void setImageData(String path){
        DefaultListModel model = (DefaultListModel) imageList.getModel();
        
        try {
            ImageIcon icon = new ImageIcon(path);
            model.add(0, icon);
            
            int removeNum = model.size() - SHOW_THUMBNAIL_NUM;
            if (removeNum > 0){
                model.removeRange(model.size()-removeNum, model.size()-1);
            }
        } catch (Exception ex){
            MBLogger.Exception("Swing", ex);
        }
    }
    
    
    /**
     * サイト情報を更新する（パッケージ向け）.
     * @param sc サイト情報
     * @param overload trueで上書き、falseで重複時例外
     */
    void updateSiteData(SiteClass sc, boolean overload) throws Exception{
        if (overload){
            manager.addForciblySiteData(sc);
        } else {
            manager.addSiteData(sc);
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        MBLogger.Debug("Swing", "ActionCommand = %s.", command);
        
        String name = null;
        int index = siteList.getSelectedIndex();
        if (index != -1){
            name = ((SiteDataManager) siteList.getSelectedValue()).getSiteName();
        }
        
        switch(command){
            case "siteEdit":
                siteClassEdit(name);
                break;
            case "siteDelete":
                //siteClassDelete(name, index);
                break;
            case "siteNew":
                siteClassNew();
                break;
            default:
                MBLogger.Warn("Swing", "Not supported yet.  command=\"%s\"", command);
        }
    }
    
    private void siteClassNew(){
        //新規作成
        EditView edit = new EditView(null, null, this);
        edit.show();
    }
    
    private void siteClassEdit(String name){
        if (name == null)   return;
        
        //編集
        SiteClass sc = manager.searchSiteData(name);
        EditView edit = new EditView(sc, null, this);
        edit.show();
    }
    
    
    
    
    ////////////////////////////////////////////////////////////////////////////
    
    //<editor-fold defaultstate="collapsed" desc="makeApplet">
    private JFrame initMainFrame(){
        JFrame f = new JFrame();
        f.setTitle(ImageWebCrawler.APPLET_TITLE + " v" + ImageWebCrawler.APPLET_VERSION);
        f.add(initPanel());
        f.setJMenuBar(makeMenuBar());
        
        f.setBounds(10, 10, 1200, 800);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    MBLogger.OutputLogFile();
                } catch (IOException ex) {
                    Logger.getLogger(ImageWebCrawler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        return f;
    }
    
    private JPopupMenu initSitePopupMenu(int index){
        JPopupMenu sitePopup = new JPopupMenu();
        
        JMenuItem newItem = new JMenuItem("新規");
        newItem.addActionListener(this);
        newItem.setActionCommand("siteNew");
        sitePopup.add(newItem);
        
        if (index != -1){
            JMenuItem editItem = new JMenuItem("編集");
            editItem.addActionListener(this);
            editItem.setActionCommand("siteEdit");
            sitePopup.add(editItem);
            
            JMenuItem deleteItem = new JMenuItem("削除");
            deleteItem.addActionListener(this);
            deleteItem.setActionCommand("siteDelete");
            sitePopup.add(deleteItem);
        }
        
        return sitePopup;
    }
    
    private JMenuBar makeMenuBar(){
        JMenuBar menu = new JMenuBar();
        
        JMenu menu1 = new JMenu("File");
        JMenu menu2 = new JMenu("Edit");
        JMenu menu3 = new JMenu("Tool");
        JMenu menu4 = new JMenu("Help");
        
        menu.add(menu1);
        menu.add(menu2);
        menu.add(menu3);
        menu.add(menu4);
        
        return menu;
    }
    
    private JComponent initPanel(){
        final JSplitPane spLeft = new JSplitPane();
        spLeft.setContinuousLayout(true);
        spLeft.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                siteList.setFixedCellWidth(spLeft.getDividerLocation());
            }
        });
        
        final JSplitPane spRight = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        spRight.setContinuousLayout(true);
        
        spRight.setLeftComponent(makeThumbnailPanel());
        spRight.setRightComponent(makeLogPanel());
        
        spLeft.setLeftComponent(makeSiteScroll());
        spLeft.setRightComponent(spRight);
        
        
        
        
        spLeft.setDividerLocation(300);
        spRight.setDividerLocation(300);
        return spLeft;
    }
    
    private JScrollPane makeThumbnailPanel(){
        //サムネイルの作成
        DefaultListModel listModel = new DefaultListModel();
        imageList = new JList(listModel);
        imageList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        //siteInfoList.setCellRenderer(new SiteCellRenderer());
        
        JScrollPane sp = new JScrollPane(imageList);
        
        
        MBLogger.Fine("Swing", "Make thumbnail panel.");
        return sp;
    }
    
    private JScrollPane makeLogPanel() {
        JComponent log = MBLogger.getJLogPane();
        
        JScrollPane sp = new JScrollPane(log);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        
        MBLogger.Fine("Swing", "Make log panel.");
        return sp;
    }
    
    private JScrollPane makeSiteScroll(){
        //swingの作成
        DefaultListModel listModel = new DefaultListModel();
        siteList = new JList(listModel){
//            private boolean pointOutsidePrefSize(Point p) {
//                int index = locationToIndex(p);
//                if (index != -1){
//                    DefaultListModel m = (DefaultListModel) getModel();
//                    SiteDataManager n = (SiteDataManager) m.get(index);
//                    Component c = getCellRenderer().getListCellRendererComponent(
//                            this, n, index, false, false);
//                    //c.doLayout();
//                    Dimension d = c.getPreferredSize();
//                    Rectangle rect = getCellBounds(index, index);
//                    rect.width = d.width;
//                    return index < 0 || !rect.contains(p);
//                }
//
//                return false;
//            }
//
//            @Override
//            protected void processMouseEvent(MouseEvent e) {
//                if (!pointOutsidePrefSize(e.getPoint())) {
//                    super.processMouseEvent(e);
//                }
//            }
//            @Override
//            protected void processMouseMotionEvent(MouseEvent e) {
//                if (!pointOutsidePrefSize(e.getPoint())) {
//                    super.processMouseMotionEvent(e);
//                } else {
//                    e = new MouseEvent(
//                            (Component) e.getSource(), MouseEvent.MOUSE_EXITED, e.getWhen(),
//                            e.getModifiers(), e.getX(), e.getY(),
//                            e.getXOnScreen(), e.getYOnScreen(),
//                            e.getClickCount(), e.isPopupTrigger(), MouseEvent.NOBUTTON);
//                    super.processMouseEvent(e);
//                }
//            }
        };
        siteList.setBackground(bgColor);
        siteList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        siteList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        siteList.setCellRenderer(new SiteCellRenderer());
        siteList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {                                                                    ///////再調整！！！
                // 座標をインデックスに変換
                Point p = e.getPoint();
                int index = siteList.locationToIndex(p);
                
                //もしインデックスが-1なら選択解除
                if (index == -1)    siteList.clearSelection();
                
                // 左クリック＆ダブルクリック
                if ((e.getButton() == MouseEvent.BUTTON1)&&(e.getClickCount() >= 2)){
                    if (index != -1)    FileIO.brawseAccess(((MainView.SiteDataManager) siteList.getModel().getElementAt(index)).getSiteURL());
                } else if (e.getButton() == MouseEvent.BUTTON3){
                    //右クリックでも選択する
                    if (index != -1)    siteList.setSelectedIndex(index);
                    initSitePopupMenu(index).show(siteList, p.x, p.y);
                }
            }
        });
        
        
        
        JScrollPane sp = new JScrollPane(siteList);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        MBLogger.Fine("Swing", "Make site panel.");
        return sp;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**サイトセルレンダラー*/
    private class SiteCellRenderer extends JPanel implements ListCellRenderer{
        public SiteCellRenderer(){
            setOpaque(true);
        }
        
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus){
            SiteDataManager si = (SiteDataManager) value;
            
            JPanel p = si.getJComponent(list.getSize().width);
            p.setBorder(new EtchedBorder());
            
            if (isSelected){
                p.setBackground(new Color(0xe0ffff));
            } else {
                p.setBackground(Color.white);
            }
            
            return p;
        }
    }
    
    /**サイト情報管理クラス*/
    private class SiteDataManager {
        private boolean isSmallMode;    //小さいときはtrue
        private JPanel panel;
        
        private JLabel thumbLabel;
        private JLabel favLabel;
        private JLabel titleLabel;
        private JLabel urlLabel;
        
        private JLabel analysisLabel;
        private JLabel downloadLabel;
        private JLabel dataLabel;
        
        private int threshold = 300;    //しきい値
        
        public SiteDataManager(SiteClass sc){
            //初期設定
            isSmallMode = false;
            panel = null;
            
            //コンポーネントの作成
            thumbLabel = new JLabel();
            int thumbSize = SITE_THUMBNAIL_SIZE;
            thumbLabel.setPreferredSize(new Dimension(thumbSize, thumbSize));
            
            favLabel = new JLabel();
            int favSize = SITE_FAVICON_SIZE;
            favLabel.setPreferredSize(new Dimension(favSize, favSize));
            
            titleLabel = new JLabel();
            titleLabel.setBackground(Color.red);
            
            urlLabel = new JLabel();
            urlLabel.setBackground(Color.WHITE);
            urlLabel.setForeground(Color.BLUE);
            
            analysisLabel = new JLabel();
            downloadLabel = new JLabel();
            dataLabel = new JLabel();
            
            //データも入れとく
            setSiteData(sc);
        }
        
        /**
         * コンポーネントの取得.
         * @param width 置く場所の幅
         */
        public JPanel getJComponent(int width){
            boolean wishSmall = (width < threshold);
            
            if ((panel != null) && (wishSmall && isSmallMode)){
                //希望と実測が同じならそのまま返す
                return panel;
            } else {
                //違うなら作り出す
                if (wishSmall){
                    panel = initSmallLayout();
                    return panel;
                } else {
                    panel = initBigLayout();
                    return panel;
                }
            }
        }
        
        
        /**レイアウトの作成（大サイズ）*/
        private JPanel initBigLayout(){
            JPanel panel = new JPanel();
            
            GridBagLayout gbl = new GridBagLayout();
            panel.setLayout(gbl);
            MBGridBagConstraints gbc = new MBGridBagConstraints();
            
            
            //--------------
            gbc.setFill(GridBagConstraints.BOTH);
            gbc.setAnchor(GridBagConstraints.CENTER);
            gbc.setWeight(0.0, 0.0);
            gbc.setInset(new Insets(5, 5, 5, 10));
            
            //アイコン
            gbc.setGridBounds(0, 0, 1, 5);
            gbl.addLayoutComponent(thumbLabel, gbc);
            panel.add(thumbLabel);
            
            
            //--------------
            gbc.setFill(GridBagConstraints.NONE);
            gbc.setAnchor(GridBagConstraints.WEST);
            gbc.setWeight(1.0, 1.0);
            gbc.setInset(new Insets(0, 0, 0, 0));
            
            //タイトル
            gbc.setGridBounds(1, 0, 1, 1);
            gbl.addLayoutComponent(titleLabel, gbc);
            panel.add(titleLabel);
            
            //URL
            gbc.setGridBounds(1, 1, 1, 1);
            gbl.addLayoutComponent(urlLabel, gbc);
            panel.add(urlLabel);
            
            //解析数
            gbc.setGridBounds(1, 2, 1, 1);
            gbl.addLayoutComponent(analysisLabel, gbc);
            panel.add(analysisLabel);
            
            //DL数
            gbc.setGridBounds(1, 3, 1, 1);
            gbl.addLayoutComponent(downloadLabel, gbc);
            panel.add(downloadLabel);
            
            //データ数
            gbc.setGridBounds(1, 4, 1, 1);
            gbl.addLayoutComponent(dataLabel, gbc);
            panel.add(dataLabel);
            
            
            isSmallMode = false;
            return panel;
        }
        
        /**レイアウトの作成（小サイズ）*/
        private JPanel initSmallLayout(){
            JPanel panel = new JPanel();
            
            GridBagLayout gbl = new GridBagLayout();
            panel.setLayout(gbl);
            MBGridBagConstraints gbc = new MBGridBagConstraints();
            
            
            //--------------
            gbc.setFill(GridBagConstraints.BOTH);
            gbc.setAnchor(GridBagConstraints.CENTER);
            gbc.setWeight(0.0, 0.0);
            gbc.setInset(new Insets(5, 5, 5, 10));
            
            //アイコン
            gbc.setGridBounds(0, 0, 1, 1);
            gbl.addLayoutComponent(favLabel, gbc);
            panel.add(favLabel);
            
            
            //--------------
            gbc.setFill(GridBagConstraints.NONE);
            gbc.setAnchor(GridBagConstraints.WEST);
            gbc.setWeight(1.0, 1.0);
            gbc.setInset(new Insets(0, 0, 0, 0));
            
            //タイトル
            gbc.setGridBounds(1, 0, 1, 1);
            gbl.addLayoutComponent(titleLabel, gbc);
            panel.add(titleLabel);
            
            
            isSmallMode = true;
            return panel;
        }
        
        
        /**
         * サイトリストを編集する.
         * @param sc サイトクラス
         */
        public void setSiteData(SiteClass sc){
            System.out.println("lenderer:"+sc);
            thumbLabel.setIcon(sc.getThumbnailImage());
            favLabel.setIcon(sc.getFaviconImage());
            
            titleLabel.setText(sc.getPageName());
            urlLabel.setText(String.format("<html><u>%s</u></html>", sc.getPageUrl()));
            
            analysisLabel.setText(String.format("Analysis : %d (%d)", sc.getAnalysisNow(), sc.getAnalysisNum()));
            downloadLabel.setText(String.format("Download : %d (%d)", sc.getDownloadNow(), sc.getDownloadNum()));
            dataLabel.setText(String.format("queue: %d  record: %d  except: %d", sc.getQueueSize(), sc.getRecordSize(), sc.getExcluseSize()));
            
            //再描画
            siteList.repaint();
        }
        
        public String getSiteName(){
            return titleLabel.getText();
        }
        
        public String getSiteURL(){
            String str = urlLabel.getText();
            return str.substring(9, str.length()-11);
        }
    }
    
//</editor-fold>

}
