package com.mabasasi.iwc.main;


import com.mabasasi.iwc.data.SiteClass;
import com.mabasasi.iwc.data.UrlClass;
import MB.lib.MBUtility;
import static MB.lib.MBUtility.changeMetrixPrefix;
import java.awt.Desktop;
import java.io.*;
import java.net.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.*;
import javax.swing.*;
import mabasasi.library.logger.MBLogger;
import org.apache.commons.io.FileUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class FileIO {

    
    /**ファイルの上書き許可*/
    public static boolean FILE_OVERWRITE = true;
    /**ファイルのパス*/
    public static String SAVE_FILE_PATH = "C:\\iwc";
    
    /**接続許可*/
    public static boolean CONNECT_PERMISSON = true;
    /**サーバー接続許可*/
    public static boolean CONNECT_SSL_PERMISSON = true;
    /**ユーザーエージェント*/
    public static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36";
    /**パケットサイズ*/
    public static int PACKET_SIZE = 1024;
    /**除外するキーワード*/
    public static String URL_EXCLUSE_CONDITION = "\\.css|\\.js|\\.ico|\\.php|\\.xml|\\.rss|b.h atena.ne.jp|twitter.com|www.dmm.co.jp|#|%%??";
    
    /**サムネイルサイズ*/
    public static int SITE_THUMBNAIL_SIZE = 100;
    /**jpeg画質*/
    public static int SITE_THUMBNAIL_QUALITY = 80;
    
    
    
    
    /**フォルダー作成例外*/
    static class MakeFolderException extends Exception {
        public MakeFolderException(String str) { super(str); }
    }
    
    /**アクセス例外*/
    static class IllegalAccessException extends Exception {
        public IllegalAccessException(String str) { super(str); }
    }
    

    /**
     * ファイルのディレクトリ検証.
     * <p>親ディレクトリをチェック -> 参照先にファイルが存在するか</p>
     * @param filePath ファイル名（ディレクトリでやるとバグるかも
     * @return 重複していたらtrue（但し、書き換えられるファイルの時のみ）
     * @throws NetIO.MakeFolderException フォルダー作成失敗
     * @throws NetIO.IllegalAccessException ファイルアクセス失敗
     */
    public static boolean FileVerify(File filePath) throws MakeFolderException, IllegalAccessException{
        //絶対パスに変更
        filePath = filePath.getAbsoluteFile();
        
        //親パスを作成
        File parentPath = filePath.getParentFile();
        
        //絶対パスが存在しなかったら作成
        if (!parentPath.exists()){
            if (parentPath.isDirectory()){
                MBLogger.Fine("File_I/O", "Make Directory. path=\"%s\"", parentPath);
                boolean makeResult = parentPath.mkdirs();
                if (!makeResult){
                    throw new MakeFolderException("フォルダが作成できませんでした。 path="+parentPath.getPath());
                }
            } else {
                throw new IllegalAccessException("親のパスがディレクトリではありません。 path="+parentPath.getPath());
            }
        }
        
        //パス検証
        boolean exist = filePath.exists();
        boolean file = filePath.isFile();
        boolean dir = filePath.isDirectory();
        boolean write = filePath.canWrite();
        MBLogger.Fine("File_I/O", "Verify FilePath.  path=\"%s\" exist=%s isFile=%s isDirectory=%s canWrite=%s", filePath, exist, file, dir, write);
        
        //参照先ファイルが存在したら、移動チェック
        if (exist){
            if (file && write){
                return true;
            } else {
                throw new IllegalAccessException("ファイルが存在しますが、アクセスできませんでした。");
            }
        } else {
            return false;
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    //<editor-fold defaultstate="collapsed" desc="HTTPコネクション">
    /**
     * SSLContextの作成.
     */
    private static SSLContext getSSLContext() throws Exception{
        MBLogger.Fine("Connecrion", "Create SSL Context.");
        
        //接続許可がないなら終了
        if (!CONNECT_SSL_PERMISSON){
            throw new IllegalAccessException("接続許可がありません。");
        }        
        
        //証明書情報　全て空を返す
        TrustManager[] tm = { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            
            @Override
            public void checkClientTrusted(X509Certificate[] chain,
                    String authType) throws CertificateException {
            }
            
            @Override
            public void checkServerTrusted(X509Certificate[] chain,
                    String authType) throws CertificateException {
            }
        }};
        SSLContext sslcontext = SSLContext.getInstance("SSL");
        sslcontext.init(null, tm, null);
        
        //ホスト名の検証ルール　何が来てもtrueを返す
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname,
                    SSLSession session) {
                return true;
            }
        });
        
        return sslcontext;
    }
    
    /**
     * HTTP・HTTPSコネクションの確立.
     * @param url URL
     * @return HTTPコネクション or HTTPSコネクション
     * @throws Exception
     */
    private static HttpURLConnection establishHttpConenction(URL url) throws Exception{
        MBLogger.Fine("Connection", "HTTP(s) Connection...");
        MBLogger.Fine("Connection", "url=\"%s\"", url);
        
        //もし接続許可がないならエラー
        if (!CONNECT_PERMISSON){
            throw new IllegalAccessException("オフラインモードです。アクセス許可がありません。");
        }
        
        //スキーマの取得
        String scheme = url.toURI().getScheme();
        
        //コネクションベース作成
        HttpURLConnection conn = null;
        switch(scheme){
            case "http":
                conn = (HttpURLConnection) url.openConnection();
                break;
            case "https":
                conn = (HttpsURLConnection) url.openConnection();
                ((HttpsURLConnection) conn).setSSLSocketFactory(getSSLContext().getSocketFactory());
                break;
            default:
                throw new IllegalArgumentException("スキーマが不明な値です。 scheme="+scheme);
        }
        
        //接続設定
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setAllowUserInteraction(false);
        conn.setInstanceFollowRedirects(true);
        conn.setRequestMethod("GET");
        
        //URLコネクト
        conn.connect();
        MBLogger.Fine("Connection", "Scheme = %s", scheme);
        MBLogger.Fine("Connection", "UserAgent = %s", USER_AGENT);
        MBLogger.Fine("Connection", "Method = %s", conn.getRequestMethod());
        
        //ステータスチェック
        int httpStatusCode = conn.getResponseCode();
        MBLogger.Fine("Connecrion", "Status = %s", conn.getResponseMessage());
        if (httpStatusCode != HttpURLConnection.HTTP_OK){
            conn.disconnect();
            throw new HttpStatusException("HTTP statusCode error.", httpStatusCode, null);
        }
        
        return conn;
    }
    
    /**
     * HTTP接続からのファイルの書き出し.
     * @param conn HTTPコネクション
     * @param file 保存先ファイルパス
     * @return DLバイト数（nullならエラー　ファイル保存時は戻り値なし）
     * @throws Exception
     */
    private static byte[] HTTPConnectionFileOutputStream(HttpURLConnection conn, File file) throws IOException{
        DataInputStream in = null;
        OutputStream out = null;
        int size = -1;
        
        try {
            //ストリーム作成
            MBLogger.Fine("File_I/O", "Create data stream...");
            in = new DataInputStream(conn.getInputStream());
            if (file != null){
                out = new DataOutputStream( new BufferedOutputStream( new FileOutputStream(file)));
            } else {
                out = new ByteArrayOutputStream();
            }
            
            //バイトパケット読み込み
            MBLogger.Fine("File_I/O", "Read byte packets...");
            byte[] b = new byte[PACKET_SIZE];
            int readByte = 0;
            while(-1 != (readByte = in.read(b))){
                out.write(b, 0, readByte);
            }
            
            size = (file != null) ? ((DataOutputStream)out).size() : ((ByteArrayOutputStream)out).size();
            MBLogger.Debug("File_I/O", "ReadSize = %s.", changeMetrixPrefix(size , false, "###.00"));
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (in != null)    try { in.close();  } catch(Exception e){ };
            if (out != null)   try { out.close(); } catch(Exception e){ };
            MBLogger.Fine("File_I/O", "Close data stream.");
        }
        
        if (file != null){
            return null;
        } else {
            if (out != null){
                return ((ByteArrayOutputStream) out).toByteArray();
            } else {
                return null;
            }
        }
    }
    
    /**
     * HTTP・HTTPSコネクションの切断.
     * @param conn HTTPコネクション
     */
    private static void HttpDisconnection(HttpURLConnection conn){
        MBLogger.Fine("Connection", "HTTP(s) Disonnecrion.");
        conn.disconnect();
    }
    
    //</editor-fold>

    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * ネット上からファイル（主に画像）をダウンロードする.
     * @param urlStr URL
     * @param savePath 保存パス
     * @return ダウンロード成功かどうか
     */
    public static boolean FileDownload(String urlStr, String savePath){
        try {
            //String変換
            File path = new File(savePath);
            URL url = new URL(urlStr);
            
            //パスチェック(trueなら重複)
            boolean verify = FileVerify(path);
            if (verify){
                if (FILE_OVERWRITE){
                    MBLogger.Debug("File_I/O", "File overwrite mode.");
                } else {
                    MBLogger.Warn("File_I/O", "File is already exists. path=\t%d\t", path.getAbsolutePath());
                    return false;
                }
            }
            
            //コネクション確立
            HttpURLConnection conn = establishHttpConenction(url);
            
            //ファイルDL
            HTTPConnectionFileOutputStream(conn, path);
            
            //コネクション切断
            HttpDisconnection(conn);
            
            //終了処理
            MBLogger.Fine("File_I/O", "Completion.");
            return true;
        } catch(Exception ex){
            MBLogger.Exception("File_I/O", ex);
            return false;
        }
    }
    
    /**
     * URL取得.
     * @param uc urlClass
     * @return URL一覧
     */
    public static String[] getContainUrl(UrlClass uc){
        //格納先の作成
        List<String> urlList = new ArrayList<>();
        String url = uc.getUrl();
        
        try{
            //コネクション確立
            HttpURLConnection conn = establishHttpConenction(new URL(url));

            //ファイルDL
            byte[] htmlByte = HTTPConnectionFileOutputStream(conn, null);

            //コネクション切断
            HttpDisconnection(conn);



            //HTML解析
            Document document = Jsoup.parse(new String(htmlByte));
            Elements eHref = document.getElementsByAttribute("href");
            Elements eSrc  = document.getElementsByAttribute("src");
            uc.setTitle(document.title());

            urlList.addAll(urlMatcher(eHref, URL_EXCLUSE_CONDITION));
            urlList.addAll(urlMatcher(eSrc, URL_EXCLUSE_CONDITION));

            //終了処理
            MBLogger.Fine("File_I/O", "Completion.");
            return (String[]) urlList.toArray(new String[0]);

        } catch (Exception ex){
            MBLogger.Exception("File_I/O", ex);
        }
        return null;
    }

    /**
     * サイトのサムネイルを取得.
     * @param path サイトパス
     * @param size サムネイルサイズ
     * @param quality jpeg画質(0-100)
     * @return アイコン（nullは失敗）
     */
    public synchronized static ImageIcon getSiteThumbnail(String path){
        int size = SITE_THUMBNAIL_SIZE;
        int quality = SITE_THUMBNAIL_QUALITY;
        
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("https://api.thumbalizr.com/?url=");
            sb.append(URLEncoder.encode(path, "UTF-8"));
            sb.append("&api_key=&width=").append(size).append("&quality=").append(quality).append("&encoding=jpg");
            
            //コネクション確立
            HttpURLConnection conn = establishHttpConenction(new URL(sb.toString()));
            
            MBUtility.noThrowSleep(1000);
            
            //ファイルDL
            byte[] htmlByte = HTTPConnectionFileOutputStream(conn, null);
            
            //コネクション切断
            HttpDisconnection(conn);
            
            
            return new ImageIcon(htmlByte);
        } catch (Exception ex) {
            MBLogger.Exception("File_I/O", ex);
            return null;
        }
    }

    /**
     * サイトのファビコンを取得.
     * @param path サイトパス
     * @return アイコン（nullは失敗）
     */
    public synchronized static ImageIcon getSiteFavicon(String path){        
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("http://www.google.com/s2/favicons?domain=");
            sb.append(path);
            
            //コネクション確立
            HttpURLConnection conn = establishHttpConenction(new URL(sb.toString()));
            
            //ファイルDL
            byte[] htmlByte = HTTPConnectionFileOutputStream(conn, null);
            
            //コネクション切断
            HttpDisconnection(conn);
            
            return new ImageIcon(htmlByte);
        } catch (Exception ex) {
            MBLogger.Exception("File_I/O", ex);
            return null;
        }
    }

    /**
     * ブラウザでネットにアクセス.
     * @param path URL
     */
    public static void brawseAccess(String path){
        try {        
            Desktop desktop = Desktop.getDesktop();
            URI uri = new URI(path);
            MBLogger.Info("File_I/O", "Access to \"%s\"", path);
            
            desktop.browse(uri);
        } catch (Exception ex) {
            MBLogger.Exception("File_I/O", ex);
        }
    }
    
    
    /**一致するURLのみを抜き出す*/
    private static List<String> urlMatcher(Elements elements, String regex){
        List<String> urlList = new ArrayList<>();
        
        for (Element element : elements) {
            String str = element.attr("href");
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(str);
            
            if (!m.find()){
                urlList.add(str);
            }
        }
        return urlList;        
    }
    
    ////////////////////////////////////////////////////////////////////////////

    
    /**
     * 指定フォルダのサイトバイナリをすべて読み込む.
     * @return SiteClass配列、nullなら失敗
     */
    public static SiteClass[] loadSiteData(){
        ArrayList<SiteClass> scs = new ArrayList<>();
        
        //ファイルパスを検索
        Collection<File> files;
        try {
            File f = new File(SAVE_FILE_PATH);
            files = FileUtils.listFiles(f, new String[]{"bin"}, false);
        } catch (Exception ex){
            MBLogger.Exception("File_I/O", ex);
            files = new ArrayList<>();
        }
        
        //存在したファイルすべてを対象
        for (File file : files) {
            try {
                //ロード
                MBLogger.Fine("File_I/O", "Loading... \"%s\"", file);
                SiteClass sc = loadSiteData(file);
                System.out.println(sc);
                //初期化（＆画像抜き出し）
                sc.Normalization();

                //サイト情報追加                
                scs.add(sc);
            } catch (Exception ex) {
                MBLogger.Exception(null, ex);
            }
        }
        
        return (scs.isEmpty()) ? null : scs.toArray(new SiteClass[0]);
    }
    
    /**
     * パスのサイトバイナリを読み込む.
     * @param file 保存先パス
     * @return サイト情報
     * @throws Exception
     */
    public static SiteClass loadSiteData(File file) throws Exception {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(file));
            SiteClass sc = (SiteClass) ois.readObject();

            System.out.println("loadBinaryFile - "+file);
            return sc;
        } catch (Exception ex){
            Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        } finally {
            ois.close();
        }
    }
    
    /**
     * サイトバイナリを保存する.
     * @param sc 保存するサイト情報
     * @throws Exception
     */
    public static void saveSiteData(SiteClass sc) throws Exception{        
        //初期化する
        sc.Normalization();
        
        //ファイル名作成
        String path = SAVE_FILE_PATH + File.separator + sc.getPageName() + ".bin";
        
        //直列化保存
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(path));
            oos.writeObject(sc);
        } catch (Exception ex){
            throw ex;
        } finally {
            oos.close();
        }
        
        System.out.println("saveBinaryFile - " + path);
    }
}