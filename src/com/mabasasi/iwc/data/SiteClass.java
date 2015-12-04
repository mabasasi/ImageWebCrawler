package com.mabasasi.iwc.data;


import com.mabasasi.iwc.main.FileIO;
import com.mabasasi.iwc.main.Utility;
import MB.lib.MBArrayString;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import mabasasi.library.logger.MBLogger;

/**サイト管理クラス*/
public class SiteClass extends BaseSiteClass implements Serializable{
    private static final long serialVersionUID = -7872888528674060472L;

    public SiteClass(String title, String url){
        super();
        setPageName(title);
        setPageUrl(url);
    }
    
    

    
    
    /**
     * 正規化用関数.
     * <p>解析の前、保存の前に実行する。</p>
     */
    public synchronized void Normalization(){
        //一時数値を保存して、初期化する
        resetStatisticsNum();
        
        //各リストを初期化する
        recreateUrlQueues();
        
        //リストに自分自身を載せる
        putSiteData();
        
        //一つ載せる
        this.addQueueToParentUrl();
    }

    
    /**
     * ページの最上位URLを処理に追加する.
     * <p>但し、追加できるのは待ち行列が空の場合のみ。<br>
     * キューが空になった場合の対処用</p>
     */
    public void addQueueToParentUrl(){
        //キューが空でないなら終了
        if (isEmptyOfQueue()){
            //親URLを追加
            String name = getPageName();
            String url = getPageUrl();
            UrlClass uc = new UrlClass(name, url);
            System.out.println("out:"+uc);
            this.enque(uc);
        }
    }
    
    /**
     * URLの保存データを初期化する.
     * @param isClearImage trueで画像履歴を残す、falseですべてを消す
     */
    public synchronized void clearUrlData(boolean isClearImage){
        MBLogger.Info(null, "Clear Url Data...  isClearImage = %s", isClearImage);
        
        //データの保持
        UrlClass[] queue = this.getImageUrlList(getIteratorOfQueue(), isClearImage);
        UrlClass[] record = this.getImageUrlList(getIteratorOfRecord(), isClearImage);
        UrlClass[] except = this.getImageUrlList(getIteratorOfExcept(), isClearImage);
        
        //初期化
        clearQueue();
        clearRecord();
        clearExcept();
        
        //もし、画像をとっといてるならそれを戻す
        if (isClearImage){
            enque(queue);
            addRecord(queue);
            addExcept(queue);
        }
        
        //一つ載せる
        this.addQueueToParentUrl();
    }
    
    
    
    /**
     * リストから画像要素をすべて抜き出す.
     * @param isPicture trueで画像要素、falseでそれ以外を取得
     * @return UrlClass[] 取得できたurl、nullあり
     */
    private UrlClass[] getImageUrlList(Iterator it, boolean isPicture){
        ArrayList<UrlClass> ucs = new ArrayList<>();
        while (it.hasNext()){
            UrlClass uc = (UrlClass) it.next();
            if (uc.isPicture() == isPicture){
                ucs.add(uc);
                it.remove();
            }
        }
        
        return (!ucs.isEmpty()) ? ucs.toArray(new UrlClass[0]) : null;
    }
    
    /**
     * UrlClass全てに自分自身を載せる.
     * <p>実質更新作業</p>
     */
    private synchronized void putSiteData(){
        for (Iterator it : getAllIterator()){
            while(it.hasNext()){
                UrlClass uc = (UrlClass) it.next();
                uc.setSiteClass(this);
            }
        }
    }
    
    /**
     *　サイト情報を保存する.
     * @throws Exception 例外
     */
    public void saveSiteData() throws Exception{
        Normalization();
        FileIO.saveSiteData(this);
    }
    
    @Override
    public String toString(){
        return super.toString();
    }

}
