
package com.mabasasi.iwc.data;

import com.mabasasi.iwc.swing.MainView;
import com.mabasasi.iwc.main.FileIO;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import mabasasi.library.logger.MBLogger;

/**データ一括管理クラス*/
public class DataManager {
    private static final String LOGNAME = "DataManager";
    
    private MainView applet;
    
    private List<SiteClass> siteList;                   //サイトデータ
    private LinkedBlockingQueue<UrlClass> urlQueue;     //画像キュー
    
    public DataManager(){
        siteList = Collections.synchronizedList(new ArrayList<SiteClass>());
        urlQueue = new LinkedBlockingQueue<>();
        
        //サイト情報を読み込む
        this.loadSiteData();
    }  
    
    public void setApplet(MainView applet){
        this.applet = applet;
    }
    
    
    
    
    /**
     * サイト情報を読み込む.
     * <p>サイトリストを初期化して、binファイルを読み込みなおす</p>
     */
    public void loadSiteData(){
        siteList.clear();
        
        MBLogger.Info(LOGNAME, "Loading sitelist...");
        SiteClass[] scs = FileIO.loadSiteData();
        if (scs != null){
            for (SiteClass sc : scs){
                addSiteData(sc);
            }
        }
        
        MBLogger.Info(LOGNAME, "Completion.  num=%d", this.getSiteNum());
    }
    
    /**
     * サイト情報を保存する.
     * <p>ノーマライズして直列化</p>
     */
    public void saveSiteData(){
        MBLogger.Info(LOGNAME, "Saving sitelist...");
        
        moveFromQueuetoSiteClass();
        
        for (SiteClass sc : siteList){
            try {
                sc.saveSiteData();
                MBLogger.Fine(LOGNAME, "\"%s\" is saved.", sc.getPageName());
            } catch (Exception ex) {
                MBLogger.Exception(LOGNAME, ex);
            }
        }
        MBLogger.Info(LOGNAME, "Completion.");
    }
    
    /**
     * サイト情報を検索する.
     * @param title サイト名
     * @return サイト情報　nullで失敗
     */
    public SiteClass searchSiteData(String title){
        System.out.println("DM-search: "+title);
        
        for (SiteClass sc : siteList){
            if (sc.getPageName().equals(title)){
                return sc;
            }
        }
        
        return null;
    }
    
    
    /**
     * サイト情報を追加する.
     * <p>キー値：名前。重複は禁止です。</p>
     * @param sc サイト情報
     */
    public void addSiteData(SiteClass sc) throws IllegalArgumentException{
        System.out.println("DM-add: "+sc);
        if (!siteList.contains(sc)){
            siteList.add(sc);
            updateView();
            return;
        }
        
        throw new IllegalArgumentException("このサイト情報は既に存在します。");
    }
    
    /**
     * サイト情報を削除する.
     * @param sc サイト情報
     */
    public void deleteSiteData(SiteClass sc) throws IllegalArgumentException{
        System.out.println("DM-delete: "+sc);
        
        if (siteList.contains(sc)){
            siteList.remove(sc);
            updateView();
            return;
        }
        
        throw new IllegalArgumentException("このサイト情報は存在しません。");
    }
    
    /**
     * サイト情報を更新する
     * @param sc サイト情報
     */
    public void setSiteData(SiteClass sc) throws IllegalArgumentException{
        System.out.println("DM-set: "+sc);
        
        for (int i=0; i<siteList.size(); i++){
            if (siteList.get(i).equals(sc)){
                siteList.set(i, sc);
                updateView();
                return;
            }
        }
        
        throw new IllegalArgumentException("このサイト情報は存在しません。");
    }
    
    /**
     * サイト情報を追加する.
     * <p>但し、サイト情報が存在したら上書きする.</p>
     * @param sc
     */
    public void addForciblySiteData(SiteClass sc) throws Exception{
        System.out.println("DM-forceAdd: "+sc);
        
        try {
            setSiteData(sc);
        } catch (Exception ex){
            try {
                addSiteData(sc);
            } catch (Exception exx){
                throw exx;
            }
        }
    }
    
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * 画面情報を更新する.
     */
    private void updateView(){
        if (applet != null){
            System.out.println("DM-update");
            applet.setSiteData(siteList);
        }
    }
    
    
    /**
     * URLキューから各サイトに戻す.
     */
    private void moveFromQueuetoSiteClass(){
        MBLogger.Fine(LOGNAME, "Move from Queue to SiteClass.");
        
        //移動出来たら、キューから削除する
        Iterator it = urlQueue.iterator();
        while (it.hasNext()){
            UrlClass uc = (UrlClass) it.next();
            try {
                SiteClass sc = uc.getSiteClass();
                sc.enque(uc);
                
                it.remove();
            } catch (Exception ex){ }
        }
        
        MBLogger.Fine(LOGNAME, "urlQueueNum = %d", getQueueSize());
    }
    
    
    
    
    
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * 登録サイトの数.
     * @return サイト数
     */
    public int getSiteNum(){
        return siteList.size();
    }
    
    public int getQueueSize(){
        return urlQueue.size();
    }


}
