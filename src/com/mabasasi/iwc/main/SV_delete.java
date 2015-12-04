package com.mabasasi.iwc.main;


public enum SV_delete {   
    //ツール変数
    APPLET_TITLE("ImageDownloader"),
    APPLET_VERSION("2.0"),
    
    SAVE_IMAGE_PATH("C:\\iwc"),
    SAVE_FILE_PATH("C:\\iwc"),
    
    
    
    IMAGE_REGEX("\\.jpeg$|\\.jpg$|\\.png$|\\.bmp$|\\.gif$"),
    
    
    XML_CHARCODE("UTF-8"),
    
    
    /**ネットアクセス許可*/
    CONNECT_PERMISSION("true"),
    /**接続時UserAgent*/
    USER_AGENT("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36"),
    /**接続タイムアウト時(ms)*/
    CONNECT_TIME_OUT("5000"),
    /**パケットサイズ(byte)*/
    PACKET_SIZE("1024"),
    /**上書きを許容する*/
    FILE_OVERWRITE("true"),
    
    
    /**指定のページの画像のみ処理対象とする*/
    SEARCH_PAGE_ONCE("false"),
    /**ネスト最大数を指定する*/
    SEARCH_MAX_NEST("2"),
    /**ページ跨ぎの最大数を指定する*/
    SEARCH_MAX_OVER("1"), //ページ跨ぎ最大許容数
    
    
    /**サイトのサムネイルサイズ*/
    SITE_THUMBNAIL_SIZE("100"),
    /**サイトのサムネイルの画質(0-100)*/
    SITE_THUMBNAIL_QUALITY("80"),
    
    
    ;
    
    
    //定義式
    private String name;
    
    
    //コンストラクタ
    private SV_delete(String name) {
        this.name = name;
    }
    
    
    
    
    
    public void setString(String val){
        System.out.println("[" + this + "]" + this.name + " -> " + val);
        
        this.name = val;
    }
    
    
    
    
    
    
    
    public String getString() {
        return this.name;
    }
    
    public int getInteger(){
        return Integer.parseInt(this.name);
    }
    
    public boolean getBoolean(){
        return Boolean.parseBoolean(this.name);
    }
}
