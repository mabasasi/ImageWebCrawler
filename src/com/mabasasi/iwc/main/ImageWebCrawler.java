package com.mabasasi.iwc.main;


import com.mabasasi.iwc.swing.MainView;
import com.mabasasi.iwc.data.DataManager;
import java.util.*;
import java.util.concurrent.*;
import mabasasi.library.logger.LEVEL;
import mabasasi.library.logger.MBLogger;
import mabasasi.library.utility.SynchronizedCounter;

public class ImageWebCrawler {
    /**タイトル*/
    public static String APPLET_TITLE = "ImageDownloader";
    /**バージョン*/
    public static String APPLET_VERSION = "3.0";
    

    private final MainView applet;      //アプレット作成クラス
    private final DataManager manager;    //データ管理クラス
    
    private ExecutorService exec;    //マルチスレッド
    private SynchronizedCounter counter;    //同調カウンタ
    
    private boolean threadRun;                  //スレッドを動かすか
    private int threadNum = 1;                  //スレッド数
    private ArrayList<Future> threadFutures;    //スレッドの戻り値格納

    
    public static void main(String[] args) {
        new MBLogger(LEVEL.FINE);
        //MBLogger.putConsole = true;
        new ImageWebCrawler();
    }

    public ImageWebCrawler() {
        MBLogger.ChangeLogger("Initilize");
        MBLogger.Info("Initialize", "%s ver%s", APPLET_TITLE, APPLET_VERSION);
        
        //各変数の初期化
        manager = new DataManager();
        applet = new MainView(manager);
        manager.setApplet(applet);
        
        threadFutures = new ArrayList<>();
        
        //サイトデータ読み込み
        
        MBLogger.ChangeLogger("Idle");
    }

    /**スレッドを走らせる*/
    public void ThreadRun() {
//        if (threadRun){
//            MBLogger.Warn("Runnable", "Thread is already Running.");
//            return;
//        }
//        
//        MBLogger.ChangeLogger("Runnable");
//        MBLogger.Info("Runnable", "ThreadNum = %d.", threadNum);
//        MBLogger.Info("Runnable", "SiteListNum = %d.", siteList.size());
//        
//        //マルチスレッドの設定
//        threadFutures.clear();  //初期化
//        exec = Executors.newFixedThreadPool(threadNum);     //スレッド作成
//        counter = new SynchronizedCounter(siteList.size()); //カウンタ新規生成
//        
//        threadRun = true;   //RUN設定
//        for (int i = 0; i < threadNum; i++) {
//            //threadFutures.add(exec.submit(new Task()));
//        }
//        
//        MBLogger.Info("Runnable", "Thread submit.");
//        
//        MBUtility.noThrowSleep(5000);
//        waitThreadFinish();
    }
    
    /**スレッドを終了させる*/
    public void waitThreadFinish(){
//        if (!threadRun){
//            MBLogger.Warn("Runnable", "Thread is not Running.");
//            return;
//        }
//        
//        MBLogger.Info("Runnable", "Thread is Finishing...  Wait For at most 30sec.");
//        
//        //30sec待機
//        threadRun = false;
//        boolean finFlg = true;
//        for (int i=0; i<30; i++){
//            //Futureの状況調査
//            finFlg = true;
//            for (Future future : threadFutures) {
//                if (!future.isDone()){
//                    finFlg = false;
//                }
//            }
//            if (finFlg) break;
//            
//            //一秒待機
//            if (i+1 % 10 == 0)    MBLogger.Fine("Runnable", "%ssec Passage...", i);
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException ex) {
//                MBLogger.Exception("Runnable", ex);
//            }
//        }
//        
//        //正常終了かどうか判定
//        if (finFlg){
//            MBLogger.Info("Runnable", "Thread Finished.");
//        } else {
//            MBLogger.Error("Runnable", "Thread is not Complection.");
//            MBLogger.Error("Runnable", "I'll recommend to end a program.");
//        }
//        
//        
    }

//    /**サイト情報読み込み*/
//    public void loadSiteList() {
//        MBLogger.Info("Runnable", "Loading site list...");
//        
//        //初期化
//        siteList.clear();
//        
//        
////<editor-fold defaultstate="collapsed" desc="comment">
//
////</editor-fold>
//     
//        MBLogger.Fine("Runnable", "Succeed.");
//        
//        
//        //swingに関連付ける
//        MBLogger.Info("Runnable", "Atatch swing component...");
//        attachSiteList();
//        
//        
//        MBLogger.Fine("Runnable", "Succeed.");
//        MBLogger.Info("Runnable", "SiteListNum = %d.", siteList.size());
//    }

    
    
//    
//    /**サイト情報保存*/
//    public void saveSiteList() {
//        System.out.println("Data Serializable...");
//        
//        //スレッドを終了させる
//        waitThreadFinish();
//        
//        //保存する
//        for (SiteClass sc : siteList) {
//            try {
//                sc.saveSiteData();
//            } catch (Exception ex) {
//                Logger.getLogger(ImageWebCrawler.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//    }
//    
//    /**サイトリストをswingに関連付ける*/
//    public void attachSiteList(){
//        applet.setSiteData(siteList.toArray(new SiteClass[0]));
//    }
//    
//
//    /**
//     * siteListの探索.
//     * @param name 探索する名前
//     * @return サイト情報（見つからなかったらnull）
//     */
//    public static synchronized SiteClass searchSiteList(String name) {
//        for (SiteClass sc : siteList) {
//            if (name.equals(sc.getPageName())) {
//                return sc;
//            }
//        }
//        return null;
//    }
//    
//    /**
//     * siteListの削除.
//     * @param sc 削除するサイト情報
//     * @return 成功可否
//     */
//    public synchronized boolean deleteSiteList(SiteClass sc){
//        boolean val = siteList.remove(sc);
//        attachSiteList();
//        return val;
//    }
//    
//    /**
//     * siteListの追加.
//     * @param sc 追加するサイト情報
//     */
//    public synchronized void addSiteList(SiteClass sc){
//        siteList.add(sc);
//        attachSiteList();
//    } 
//    
//    /**
//     * siteListの置き換え.
//     * @param sc 置き換えるサイト情報
//     * @return 成功可否
//     */
//    public synchronized boolean setSiteList(SiteClass sc){
//        boolean val = false;
//        for (int i=0; i<siteList.size(); i++){
//            if (siteList.get(i).getPageName().equals(sc.getPageName())){
//                siteList.set(i, sc);
//                val = true;
//                attachSiteList();
//                break;
//            }
//        }
//        return val;
//    }
//    
//
//        
////
//
//    /**
//     * タスク
//     */
//    public class Task implements Runnable {
//        private long id;    //スレッドID
//
//        public Task() {
//            id = Thread.currentThread().getId();
//            MBLogger.Info("ID:"+id, "Created Thread. ID = %d.", id);
//        }
//
//        /**仮の終了処理*/
//        private boolean finCheck() {
//            int n = 0;
//            if (urlQueue.isEmpty()) {
//                n++;
//            } else {
//                return false;
//            }
//
//            for (SiteClass sc : siteList) {
//                if (sc.isEmpty()) {
//                    n++;
//                }
//            }
//
//            if (n >= siteList.size() + 1) {
//                return true;
//            }
//
//            return false;
//        }
//
//
//
//        @Override
//        public void run() {
//            MBLogger.Info("ID:"+id, "Running Thread...", id);
//            
//            for (int i=0; threadRun; i++) {
//                try {
//                    //最上位のキューを取得
//                    UrlClass uc = (UrlClass) urlQueue.poll(1, TimeUnit.SECONDS);
//                    
//                    if (uc != null) {
//                        //nullでないならDL開始
//                        this.download(uc);
//                    } else {
//                        //nullなら新規解析（カウンターを回す）
//                        int cnt = counter.getCounter();
//                        this.analysis(siteList.get(cnt), cnt);
//                    }
//                } catch (InterruptedException ex) {
//                    MBLogger.Exception(null, ex);
//                }
//                
//                break;
//            }
//
//            saveSiteList();
//            MBLogger.Info("ID:"+id, "Thread Completion...", id);
//        }
//
//        /**ダウンロード*/
//        private void download(UrlClass uc) {
//            MBLogger.Fine("ID:"+id, "Download : %s", uc);
//
//            String path = MakeFileName(uc);
//            MBLogger.Fine("ID:"+id, "path = %s", path);
//            if (path != null) {
//                //ダウンロード
//                boolean result = FileIO.FileDownload(uc.getUrl(), path);
//                if (result){
//                    MBLogger.Info("ID:"+id, "DownloadResult : path=\"%s\"", path);
//                } else {
//                    MBLogger.Warn("ID:"+id, "Dawnload Fallure.  %s", uc);
//                }
//                
//                //サイトリストに情報追加
//                SiteClass sc = searchSiteList(uc.getSiteTitle());
//                if (sc != null) {
//                    if (result){
//                        sc.downloadIncrement();
//                        sc.addRecord(uc);
//                    } else {
//                        sc.addExcept(uc);
//                    }
//                } else {
//                    MBLogger.Error("ID:"+id, "Site class was not found. site = %s", uc.getSiteTitle());
//                    MBLogger.Error("Runnable", "I'll recommend to end a program.");
//                }
//            }
//        }
//
//        /**
//         * ページ解析.
//         * @return これ以上取得できない場合、false
//         */
//        private boolean analysis(SiteClass site, int cnt) {
//            MBLogger.Fine("ID:"+id, "Analysis : %s", site);
//            
//            UrlClass uc = null;
//            try {
//                //サイトのURLキューを取得
//                uc = site.dequeue();
//                MBLogger.Fine("ID:"+id, "Deque : %s", uc);
//                
//                //キューが空なら終了
//                if (uc == null){
//                    MBLogger.Fine("ID:"+id, "UrlQueue is Empty.");
//                    return false;
//                }
//                
//                //画像かどうかで、動作を変更
//                if (uc.isPicture()) {
//                    //画像URLならスタックに入れる
//                    urlQueue.offer(uc, 1, TimeUnit.SECONDS);
//                    MBLogger.Fine("ID:"+id, "Added ImageQueue. URL = %s", uc.getUrl());
//                } else {
//                    //違うならページのURLを解析＋タイトル取得
//                    String[] urlList = FileIO.getContainUrl(uc);
//                    MBLogger.Fine("ID:"+id, "AnalysisUrlNum = %d.", urlList.length);
//                    
//                    //取得した全てを検索
//                    int addCnt = 0;
//                    for (String url : urlList) {
//                        //<editor-fold defaultstate="collapsed" desc="//URL解析">
//                        //絶対パスに変換
//                        url = ConverseToAbsoludePath(url, uc.getUrl());
//                        if (url == null) {
//                            MBLogger.Debug("ID:"+id, "Excluse. reason = Format Error [ url = %s]", url);
//                            continue;
//                        }
//                        
//                        //処理対象かどうか判定
//                        if (!isAnalysisTarget(url, site.getUrlRegexString())) {
//                            MBLogger.Debug("ID:"+id, "Excluse. reason = Is not Matching [ url = %s]", url);
//                            continue;
//                        }
//                        
//                        //レコードにあったらスキップ（親と同じパス+nullは消去）
//                        if ((uc.getUrl().equals(url)) || (site.isExist(url))) {
//                            MBLogger.Debug("ID:"+id, "Excluse. reason = Already Exist [ url = %s]", url);
//                            continue;
//                        }
//                        
//                        //【特殊】オーバー処理
//                        boolean isOver = isOtherHostPage(url, uc.getUrl());
//                        if ((isOver) && (uc.getOverNum() > SystemVariable.SEARCH_MAX_OVER.getInteger())) {
//                            MBLogger.Debug("ID:"+id, "Excluse. reason = Most Over num = %d [ url = %s]", uc.getOverNum()+1, url);
//                            continue;
//                        }
//                        
//                        //【特殊】ネスト処理
//                        if (uc.getNestNum() + 1 > SystemVariable.SEARCH_MAX_NEST.getInteger()) {
//                            MBLogger.Debug("ID:"+id, "Excluse. reason = Most Nest num = %d [ url = %s]", uc.getNestNum()+1, url);
//                            continue;
//                        }
//                        
//                        //【特殊】このページのみ対象の場合、画像以外をスキップ
//                        if ((SystemVariable.SEARCH_PAGE_ONCE.getBoolean()) && (!uc.isPicture(url))) {
//                            MBLogger.Debug("ID:"+id, "Excluse. reason = Different Parent [ url = %s]", url);
//                            continue;
//                        }
//                        //</editor-fold>
//                        
//                        //追加処理
//                        UrlClass newuc = new UrlClass(url, site.ReplaceTitleString(uc.getPageTitle()), site.ReplaceTitleString(site.getPageName()));
//                        newuc.setNestNum(uc.getNestNum() + 1);
//                        newuc.setOverNum(uc.getOverNum() + ((isOver) ? 1 : 0));
//                        
//                        //画像キューに存在するか
//                        if (urlQueue.contains(newuc)){
//                            MBLogger.Debug("ID:"+id, "Excluse. reason = Already Exist [ url = %s]", url);
//                            continue;
//                        }
//                        
//                        //キューに追加
//                        site.enqueue(newuc);
//                        
//                        //数値処理
//                        site.analysisIncrement();
//                        addCnt ++;
//                        MBLogger.Fine("ID:"+id, "Enque : %s", newuc);
//                    }
//                    
//                    //取得したデータを履歴に格納する
//                    site.addRecord(uc);
//                    MBLogger.Info("ID:"+id, "AnalysisResult : [%s] -> AddedUrlNum = %d/%d.", site.getPageName(), addCnt, urlList.length);
//                }
//            } catch (Exception ex) {
//                MBLogger.Exception("ID:"+id, ex);
//                if (uc != null) {
//                    //例外に追加する
//                    site.addExcept(uc);
//                    MBLogger.Warn("ID:"+id, "Exception : %d", uc);
//                }
//            }
//            
//            MBLogger.Fine("ID:"+id, "Completion.");
//            return true;
//        }
//
//        
//        //<editor-fold defaultstate="collapsed" desc="//特殊メソッド一覧">
//        /**
//         * 親基準で絶対パス化
//         */
//        private String ConverseToAbsoludePath(String url, String parent) {
//            try {
//                //絶対パスに変換
//                url = new URL(new URL(parent), url).toString();
//            } catch (Exception ex) {
//                url = null;
//            }
//
//            return url;
//        }
//
//        /**
//         * 処理対象かどうか判定
//         */
//        private boolean isAnalysisTarget(String url, String regex) {
//            Pattern p = Pattern.compile(regex);
//            Matcher m = p.matcher(url);
//
//            return m.find();
//        }
//
//        /**
//         * 別ホストのページか
//         */
//        private boolean isOtherHostPage(String url, String parent) {
//            try {
//                URL p = new URL(parent);
//                URL c = new URL(url);
//                return !p.getHost().equals(c.getHost());
//            } catch (MalformedURLException ex) {
//                return false;
//            }
//        }
//
//        /**
//         * 保存パスの作成
//         */
//        private String MakeFileName(UrlClass uc) {
//            try {
//                //画像パス作成
//                String parent = uc.getSiteTitle();
//                String folder = uc.getPageTitle();
//                String file = new URI(uc.getUrl()).getRawPath();
//
//                if ("".equals(parent)) {
//                    parent = "_blank";
//                }
//                if ("".equals(folder)) {
//                    folder = "_blank";
//                }
//
//                //パス作成
//                String path = SystemVariable.SAVE_IMAGE_PATH.getString()
//                        + File.separator + parent
//                        + File.separator + folder
//                        + File.separator + file.substring(file.lastIndexOf("/") + 1);
//
//                return path;
//            } catch (URISyntaxException ex) {
//                Logger.getLogger(ImageWebCrawler.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//            return null;
//        }
//
//        //</editor-fold>
//    }
//    
//    /**(仮)データ情報を出力*/
//    private synchronized void PrintDataTrace(){
//        if (true)return;
//        
//        MBLogger.ChangeLogger("DataTrace");
//        MBLogger.Info("DataTrace", "The present state is acquiring...");
//        
//        if (siteList.isEmpty()){
//            MBLogger.Info("DataTrace", "Datatable is Empty.");
//        }
//        
//        for (SiteClass sc : siteList) {
//            MBLogger.Info("DataTrace", sc.toString());
//            
//            UrlClass[] queue = sc.getQueue();
//            MBLogger.Info("DataTrace", "Dump queue... num = %d", queue.length);
//            for (UrlClass uc : queue){
//                MBLogger.Debug("DataTrace", uc.toString());
//            }
//            
//            UrlClass[] record = sc.getRecord();
//            MBLogger.Info("DataTrace", "Dump record... num = %d", record.length);
//            for (UrlClass uc : record){
//                MBLogger.Debug("DataTrace", uc.toString());
//            }
//
//            UrlClass[] except = sc.getExcept();
//            MBLogger.Info("DataTrace", "Dump except... num = %d", except.length);
//            for (UrlClass uc : except){
//                MBLogger.Debug("DataTrace", uc.toString());
//            }
//        }
//        
//        MBLogger.Fine("DataTrace", "Completion.");
//    }
//    
}
