package com.mabasasi.iwc.data;

import com.mabasasi.iwc.main.Utility;
import java.io.Serializable;
import java.net.URI;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**URL管理クラス*/
public class UrlClass implements Serializable {
    private static final long serialVersionUID = 20002989552168605L;
    
    /**処理対象拡張子*/
    public static String IMAGE_EXTENSION = "\\.jpeg$|\\.jpg$|\\.png$|\\.bmp$|\\.gif$";
    
    
    private transient SiteClass sc;       //サイト情報を保持しておく（シリアライズはしない）
    
    private String  url;        //URL
    private String  title;  //ページのタイトル（ひとつ前が代入される）
    
    private int nest;           //ネスト数
    private int over;           //オーバー数
    
    private boolean isPicture;  //画像かどうか
                
    
    public UrlClass(String title, String url){
        this.initilize();
        this.setTitle(title);
        this.setUrl(url);
        
    }
    
    /**初期値代入関数*/
    public void initilize(){
        this.sc = null;
        
        this.url = "";
        this.title = "";
        
        this.nest = 0;
        this.over = 0;
        
        this.isPicture = false;
    }
    
    /**
     * URLが画像かどうか判定.
     * @param url URL
     * @return 画像かどうか
     */
    private boolean isPicture(String url){        
        //末尾がこれなら終了
        Pattern p = Pattern.compile(IMAGE_EXTENSION);
        Matcher m = p.matcher(url);

        return m.find();
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="setter">
    public void setSiteClass(SiteClass sc) throws NullPointerException{
        if (sc != null){
            this.sc = sc;
            return;
        }
        
        throw new NullPointerException("所属するサイトはnullではない必要があります。");
    }
    
    public void setTitle(String title) throws NullPointerException{
        if (Utility.isSignificantString(title)){
            this.title = title;
            return;
        }
        
        throw new NullPointerException("タイトルは任意の文字列である必要があります。");
    }
    
    public void setUrl(String url) throws NullPointerException{
        try {
            if (Utility.isSignificantString(url)){
                new URI(url);
                this.url = url;
                return;
            }
        } catch (Exception ex){ }
        
        throw new NullPointerException("URLは任意の文字列である必要があります。");
    }
    
    public void setNestNum(int num){
        this.nest = nest;
    }
    
    public void setOverNum(int num){
        this.over = over;
    }
    
    public void setIsPicture(boolean bool){
        this.isPicture = isPicture(getUrl());
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="getter">
    public SiteClass getSiteClass(){
        return this.sc;
    }
    
    public String getUrl(){
        return url;
    }
    
    public String getTitle(){
        return title;
    }
    
    public int getNest(){
        return nest;
    }
    
    public int getOver(){
        return over;
    }
    
    public boolean isPicture(){
        return isPicture;
    }
    //</editor-fold>
    
    @Override
    public String toString(){
        String site = (sc == null) ? "null" : sc.getPageName();
        
        StringBuilder sb = new StringBuilder();
        sb.append("UrlClass[").append("site=").append(site).append(" page=\"").append(title).append("\" url=\"").append(url).append("\"]");
        
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object obj){
        //そもそも自信とクラスが違ったらfalse
        if (!obj.getClass().equals(this.getClass())){
            return false;
        }
        
        //比較（タイトルとURLのみ）
        UrlClass uc = (UrlClass) obj;
        return ((uc.getTitle().equals(this.getTitle())) && (uc.getUrl().equals(this.getUrl())));
    }    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.url);
        hash = 17 * hash + Objects.hashCode(this.title);
        return hash;
    }

}
