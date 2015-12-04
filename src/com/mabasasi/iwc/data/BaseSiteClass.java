package com.mabasasi.iwc.data;


import com.mabasasi.iwc.main.FileIO;
import com.mabasasi.iwc.main.Utility;
import MB.lib.MBArrayString;
import java.io.Serializable;
import java.net.URI;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import mabasasi.library.logger.MBLogger;

/**SiteClassのベース*/
public class BaseSiteClass implements Serializable{
    private static final long serialVersionUID = -1427574052458249095L;
    
    private ImageIcon thumbnail;//サムネイル
    private ImageIcon favicon;  //ファビコン
    private String pageName;    //ページタイトル
    private String pageURL;     //ページのURL
    
    private String replaceStr;  //タイトルから削除する文字
    private String replaceWay;  //┗その削除の方法
    
    private MBArrayString regexUrl; //解析するURL（正規表現）
    
    private int analysisHold;   //統計の解析数
    private int downloadHold;   //統計のＤＬ数
    
    private int analysisNow;    //今のセッションでの解析数
    private int downloadNow;    //今のセッションでのＤＬ数
    
    private LinkedBlockingQueue<UrlClass>   urlQueue;   //URL待ち行列
    private Set<UrlClass>                   urlRecord;  //URLの取得履歴
    private Set<UrlClass>                   urlExcept; //例外スタック
    
    public BaseSiteClass(){
        this.initilize();
    }
    
    /**初期値代入関数*/
    public void initilize(){
        this.thumbnail = null;
        this.favicon = null;
        
        this.pageName = "";
        this.pageURL = "";
        
        this.replaceStr = "";
        this.replaceWay = "none";
        
        this.regexUrl = new MBArrayString();
        regexUrl.setSeparator("|");
        
        this.analysisNow = 0;
        this.analysisHold = 0;
        this.downloadNow = 0;
        this.downloadHold = 0;
        
        this.urlQueue = new LinkedBlockingQueue<>();
        this.urlRecord = Collections.synchronizedSet(new HashSet<UrlClass>());
        this.urlExcept = Collections.synchronizedSet(new HashSet<UrlClass>());
    }
    
    /**三つの配列を作り直しをする*/
    public void recreateUrlQueues(){
        this.urlQueue = new LinkedBlockingQueue<>(this.urlQueue);
        this.urlRecord = Collections.synchronizedSet(new HashSet<>(this.urlRecord));
        this.urlExcept = Collections.synchronizedSet(new HashSet<>(this.urlExcept));
    }
    
    
    
    
//    
//    /**
//     * 自身のクラスを再作成する.
//     */
//    public void reInitilize(){
//        //自分を保存して、初期化する
//        BaseSiteClass bsc = this;
//        initilize();
//        
//        //すべての値をセットしていく
//        
//        
//    }
    
    
    //<editor-fold defaultstate="collapsed" desc="setter">
    public void setThumbnailImage(ImageIcon icon){
        this.thumbnail = icon;
    }
    
    public void setFaviconImage(ImageIcon icon){
        this.favicon = icon;
    }
    
    public void setPageName(String name) throws IllegalArgumentException{
        if (Utility.isSignificantString(name)){
            this.pageName = name;
            return;
        }
        
        throw new IllegalArgumentException("タイトルは任意の文字列である必要があります。");
    }
    
    public void setPageUrl(String url) throws IllegalArgumentException{
        try {
            if (Utility.isSignificantString(url)){
                new URI(url);
                this.pageURL = url;
                return;
            }
        } catch (Exception ex){ }
        
        throw new IllegalArgumentException("URLは任意の文字列である必要があります。もしくはURLが正しくありません。");
    }
    
    public void setReplaceString(String repStr){
        this.replaceStr = repStr;
    }
    
    public void setReplaceWay(String repWay){
        this.replaceWay = repWay;
    }
    
    public void setProcessUrlRegex(String... regex){
        for (String str : regex){
            if (Utility.isSignificantString(str)){
                this.regexUrl.add(str);
            }
        }
    }
    
    public void setAnalysisNum(int num){
        this.analysisNow = num;
    }
    
    public void setDownloadNow(int num){
        this.downloadNow = num;
    }
    
    public void setAnalysisHold(int num){
        this.analysisHold = num;
    }
    
    public void setDownloadHold(int num){
        this.downloadHold = num;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="getter">
    public ImageIcon getThumbnailImage(){
        ImageIcon icon = this.thumbnail;
        
        if (icon != null){
            return icon;
        } else {
            return this.thumbnail = FileIO.getSiteThumbnail(this.pageURL);
        }
    }
    
    public ImageIcon getFaviconImage(){
        ImageIcon icon = this.favicon;
        
        if (icon != null){
            return icon;
        } else {
            return this.favicon = FileIO.getSiteFavicon(this.pageURL);
        }
    }
    
    public String getPageName(){
        return this.pageName;
    }
    
    public String getPageUrl(){
        return this.pageURL;
    }
    
    public String getReplaceString(){
        return this.replaceStr;
    }
    
    public String getReplaceWay(){
        return this.replaceWay;
    }
    
    public String getProcessUrlRegex(){
        return this.regexUrl.toString();
    }
    
    public String[] getArrayProcessUrlRegex(){
        return (String[]) this.regexUrl.toArray(new String[0]);
    }
    
    public int getAnalysisNow(){
        return this.analysisNow;
    }
    
    public int getDownloadNow(){
        return this.downloadNow;
    }
    
    public int getAnalysisHold(){
        return this.analysisHold;
    }
    
    public int getDownloadHold(){
        return this.downloadHold;
    }
    
    public int getAnalysisNum(){
        return this.analysisHold + this.analysisNow;
    }
    
    public int getDownloadNum(){
        return this.downloadHold + this.downloadNow;
    }
    //</editor-fold>
 
    //<editor-fold defaultstate="collapsed" desc="other">
    /**処理対象URLの正規表現を削除する*/
    public void clearProcessUrlRegex(){
        regexUrl.clear();
    }
    
    /**解析情報を削除する*/
    public void clearStatisticsNum(){
        this.setAnalysisHold(0);
        this.setAnalysisNum(0);
        
        this.setDownloadHold(0);
        this.setDownloadNow(0);
    }
    
    /**一時保存解析情報を移動する*/
    public void resetStatisticsNum(){
        int download = this.getDownloadHold();
        this.setDownloadHold(download+this.getDownloadNow());
        this.setDownloadNow(0);
        
        int analysis = this.getAnalysisHold();
        this.setAnalysisHold(analysis+this.getDownloadNow());
        this.setDownloadNow(0);
    }
    
    /**ダウンロード数をインクリする*/
    public void incrementDownloadNum(){
        int num = getAnalysisNow();
        this.setAnalysisNum(num+1);
    }
    
    /**アナライズ数をインクリする*/
    public void incrementAnalysisNum(){
        int num = getDownloadNow();
        this.setDownloadNow(num+1);
    }
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="urlQueue">
    public boolean contains(UrlClass uc){
        if (uc == null) return false;
        
        return urlQueue.contains(uc) && urlRecord.contains(uc) && urlExcept.contains(uc);
    }
    
    
    
    public void enque(UrlClass... ucs){
        if (ucs == null)    return;
        
        for (UrlClass uc : ucs){
            if (!this.contains(uc)){
                try {
                    urlQueue.offer(uc, 1, TimeUnit.SECONDS);
                    System.out.println("enqueue - " + uc);
                } catch (Exception ex){
                    MBLogger.Exception("SiteManager", ex);
                }
            } else {
                System.out.println("overlap - " + uc);
            }
        }
    }
    
    public UrlClass deque(){
        if (urlQueue.isEmpty()) return null;
        
        try {
            UrlClass uc = (UrlClass) urlQueue.poll(1, TimeUnit.SECONDS);
            System.out.println("dequeue - " + uc);
            return uc;
        } catch (Exception ex){
            MBLogger.Exception("SiteManager", ex);
            return null;
        }
    }
    
    public void clearQueue(){
        System.out.println("url queue is cleared.");
        urlQueue.clear();
    }
    
    public UrlClass[] getQueue(){
        return urlQueue.toArray(new UrlClass[0]);
    }
    
    synchronized Iterator getIteratorOfQueue(){
        return urlQueue.iterator();
    }
    
    public int getQueueSize(){
        return urlQueue.size();
    }
    
    public boolean isEmptyOfQueue(){
        return urlQueue.isEmpty();
    }
    
    
    
    public void addRecord(UrlClass... ucs){
        if (ucs == null)    return;
        
        for (UrlClass uc : ucs){
            if (!this.contains(uc)){
                urlRecord.add(uc);
                System.out.println("record - " + uc);
            } else {
                System.out.println("overlap - " + uc);
            }
        }
    }
    
    public void clearRecord(){
        System.out.println("url record is cleared.");
        urlQueue.clear();
    }
    
    public UrlClass[] getRecord(){
        return urlRecord.toArray(new UrlClass[0]);
    }

    synchronized Iterator getIteratorOfRecord(){
        return urlRecord.iterator();
    }
    
    public int getRecordSize(){
        return urlRecord.size();
    }
        
    public boolean isEmptyOfRecord(){
        return urlRecord.isEmpty();
    }
    
    
    
    public void addExcept(UrlClass... ucs){
        if (ucs == null)    return;
        
        for (UrlClass uc : ucs){
            if (!this.contains(uc)){
                urlRecord.add(uc);
                System.out.println("except - " + uc);
            } else {
                System.out.println("overlap - " + uc);
            }
        }
    }
    
    public void clearExcept(){
        System.out.println("url except is cleared.");
        urlQueue.clear();
    }
    
    public UrlClass[] getExcept(){
        return urlExcept.toArray(new UrlClass[0]);
    }
    
   synchronized Iterator getIteratorOfExcept(){
        return urlExcept.iterator();
    }
   
   public int getExcluseSize(){
       return urlExcept.size();
   }
    
    public boolean isEmptyOfExcept(){
        return urlExcept.isEmpty();
    }
    
    
    public Iterator[] getAllIterator(){
        Iterator[] its = new Iterator[3];
        its[0] = getIteratorOfQueue();
        its[1] = getIteratorOfRecord();
        its[2] = getIteratorOfExcept();
        
        return its;
    }
    //</editor-fold>
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("SiteClass[").append(pageName).append(":\"").append(pageURL).append("\"");
        sb.append(" Queue=").append(urlQueue.size()).append(" Record=").append(urlRecord.size()).append(" Expect=").append(urlExcept.size());
        sb.append(" analysis=").append(analysisNow).append("/").append(getAnalysisNum()).append(" download=").append(downloadNow).append("/").append(getDownloadNum());
        sb.append(" rep=\"").append(replaceStr).append("\" way=").append(replaceWay).append("]");
        
        return sb.toString();
    }
    
    /**ダンプ用（仮実装）*/
    public String Dump(){
        StringBuilder sb = new StringBuilder();
        sb.append("SiteClass : ").append("\n");
        sb.append("name=").append(getPageName()).append("\n");
        sb.append("url=").append(getPageUrl()).append("\n");
        sb.append("replaceString=").append(getReplaceString()).append("\n");
        sb.append("replaceWay=").append(getReplaceWay()).append("\n");
        sb.append("urlRegex=").append(getProcessUrlRegex()).append("\n");
        sb.append("analysis=").append(getAnalysisNow()).append("(").append(getAnalysisNum()).append(")").append("\n");
        sb.append("download=").append(getDownloadNow()).append("(").append(getDownloadNum()).append(")").append("\n");
        sb.append("queue=").append(getQueueSize()).append("\n");
        sb.append("record=").append(getRecordSize()).append("\n");
        sb.append("excluse=").append(getExcluseSize()).append("\n");
        sb.append("thumbNail=").append(thumbnail).append("\n");
        sb.append("favicon=").append(favicon).append("\n");
        return sb.toString();
    }
    
    /**
     * タイトル文字列を整形.
     * <p>UrlClass中のタイトル文字列を綺麗に</p>
     * @param title タイトル文字列
     * @param repStr 置換文字列
     * @param repWay 置換方法(before, after, none, once)
     * @return 処理結果
     */
    public String ReplaceTitleString(String title){
        if ("".equals(title))  return title;
        return ConverseString(ReplaceTitle(title, replaceStr, replaceWay));
    }

    /**タイトルの文字を置換する.
     * @param title 文字列
     * @param repStr 置換文字列
     * @param repWay 置換方法
     */
    public static String ReplaceTitle(String title, String repStr, String repWay){
        if (("".equals(repStr))||("".equals(repWay))){
            return title;
        }
        String str = "";

        System.out.println("title:"+title);
        System.out.println("reps:"+repStr);
        System.out.println("repw:"+repWay);
        try {
            //タイトルの文字を除外する
            int i = repStr.length();
            int n = title.indexOf(repStr);
            System.out.println(title.contains(repStr));
            System.out.println("i="+i+" n="+n);
            if (n == -1)    return title;
            
            //置換開始
            switch(repWay){
                case "before":
                    str = title.substring(i+n, title.length());
                    break;
                case "after":
                    str = title.substring(0, n);
                    break;
                case "none":
                    str = title.replace(repStr, "");
                    break;
                default:
                    str = title;
            }
        } catch(Exception ex){
            return title;
        }

        return str;
    }
    
    /**windowsフォルダに使用できない文字全角化*/
    private String ConverseString(String title){
        if ("".equals(title))   return title;
        String str = title;
        
        try {
            str = str.replaceAll(Pattern.quote("\\"), "￥");
            str = str.replaceAll(Pattern.quote("/"), "／");
            str = str.replaceAll(Pattern.quote(":"), "：");
            str = str.replaceAll(Pattern.quote("*"), "＊");
            str = str.replaceAll(Pattern.quote("?"), "？");
            str = str.replaceAll(Pattern.quote("\""), "”");
            str = str.replaceAll(Pattern.quote("<"), "＜");
            str = str.replaceAll(Pattern.quote(">"), "＞");
            str = str.replaceAll(Pattern.quote("|"), "｜");
            str = str.replaceAll(Pattern.quote("."), "．");
        } catch(Exception ex){
            return title;
        }
        return str;
    }
    
        
    @Override
    public boolean equals(Object obj){
        //そもそも自信とクラスが違ったらfalse
        if (!obj.getClass().equals(this.getClass())){
            return false;
        }
        
        //比較（タイトルのみ）
        SiteClass sc = (SiteClass) obj;
        return sc.getPageName().equals(this.getPageName());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.pageName);
        return hash;
    }
}